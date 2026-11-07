package edu.iudigital.rrhh.dao;

import edu.iudigital.rrhh.auth.Rol;
import edu.iudigital.rrhh.auth.SesionUsuario;
import edu.iudigital.rrhh.config.ConexionBD;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class AutenticacionDAO {
    private static final int ITERACIONES = 210_000;
    private static final int TAMANO_SAL = 16;
    private static final int TAMANO_HASH = 256;
    private static final SecureRandom ALEATORIO = new SecureRandom();

    public record CuentaUsuario(long id, String nombreFuncionario, String usuario, Rol rol) {}

    public void inicializarEsquema() {
        String sql =
                """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id_usuario BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
                    id_funcionario BIGINT UNSIGNED NULL,
                    nombre_usuario VARCHAR(60) NOT NULL,
                    sal VARBINARY(16) NOT NULL,
                    hash_contrasena VARBINARY(32) NOT NULL,
                    rol VARCHAR(20) NOT NULL,
                    PRIMARY KEY (id_usuario),
                    UNIQUE KEY uq_usuarios_nombre (nombre_usuario),
                    UNIQUE KEY uq_usuarios_funcionario (id_funcionario),
                    CONSTRAINT chk_usuarios_rol CHECK (rol IN ('ADMIN_RRHH', 'FUNCIONARIO')),
                    CONSTRAINT fk_usuarios_funcionario FOREIGN KEY (id_funcionario)
                        REFERENCES funcionarios (id_funcionario)
                        ON UPDATE CASCADE ON DELETE CASCADE
                ) ENGINE=InnoDB
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
                Statement sentencia = conexion.createStatement()) {
            sentencia.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo preparar la tabla de usuarios: " + e.getMessage(), e);
        }
    }

    public boolean existeAdministrador() {
        String sql = "SELECT 1 FROM usuarios WHERE rol = 'ADMIN_RRHH' LIMIT 1";
        try (Connection conexion = ConexionBD.obtenerConexion();
                Statement sentencia = conexion.createStatement();
                ResultSet resultado = sentencia.executeQuery(sql)) {
            return resultado.next();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo consultar la cuenta administradora: " + e.getMessage(), e);
        }
    }

    public void crearAdministradorInicial(String usuario, char[] contrasena) {
        validarCredenciales(usuario, contrasena);
        String sql =
                "INSERT INTO usuarios "
                        + "(id_funcionario, nombre_usuario, sal, hash_contrasena, rol) "
                        + "VALUES (NULL, ?, ?, ?, 'ADMIN_RRHH')";
        crearCuenta(sql, usuario, contrasena, null);
    }

    public Optional<SesionUsuario> autenticar(String usuario, char[] contrasena) {
        String sql =
                "SELECT sal, hash_contrasena, rol FROM usuarios WHERE nombre_usuario = ?";
        String nombreUsuario = usuario == null ? "" : usuario.trim();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setString(1, nombreUsuario);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (!resultado.next()) return Optional.empty();
                byte[] sal = resultado.getBytes("sal");
                byte[] hashGuardado = resultado.getBytes("hash_contrasena");
                if (!MessageDigest.isEqual(hashGuardado, derivar(contrasena, sal))) {
                    return Optional.empty();
                }
                return Optional.of(
                        new SesionUsuario(nombreUsuario, Rol.desdeBD(resultado.getString("rol"))));
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo validar el inicio de sesión: " + e.getMessage(), e);
        }
    }

    public List<CuentaUsuario> listarCuentas() {
        String sql =
                """
                SELECT u.id_usuario, u.id_funcionario, f.nombres, f.apellidos,
                       u.nombre_usuario, u.rol
                FROM usuarios u
                LEFT JOIN funcionarios f ON f.id_funcionario = u.id_funcionario
                WHERE u.rol IN ('ADMIN_RRHH', 'FUNCIONARIO')
                ORDER BY u.rol, f.apellidos, f.nombres, u.nombre_usuario
                """;
        List<CuentaUsuario> cuentas = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                Statement sentencia = conexion.createStatement();
                ResultSet resultado = sentencia.executeQuery(sql)) {
            while (resultado.next()) {
                String nombres = resultado.getString("nombres");
                String apellidos = resultado.getString("apellidos");
                cuentas.add(
                        new CuentaUsuario(
                                resultado.getLong("id_usuario"),
                                nombres == null
                                        ? "Administrador inicial"
                                        : nombres + " " + apellidos,
                                resultado.getString("nombre_usuario"),
                                Rol.desdeBD(resultado.getString("rol"))));
            }
            return cuentas;
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudieron consultar las cuentas: " + e.getMessage(), e);
        }
    }

    public void crearCuentaFuncionario(
            long idFuncionario, String usuario, char[] contrasena, Rol rol) {
        validarCredenciales(usuario, contrasena);
        if (rol == null) {
            throw new IllegalArgumentException("Selecciona un rol para la cuenta.");
        }
        String sql =
                "INSERT INTO usuarios "
                        + "(id_funcionario, nombre_usuario, sal, hash_contrasena, rol) "
                        + "VALUES (?, ?, ?, ?, ?)";
        crearCuenta(sql, usuario, contrasena, idFuncionario, rol);
    }

    public void otorgarRolAdmin(long idCuenta) {
        String sql =
                "UPDATE usuarios SET rol = 'ADMIN_RRHH' "
                        + "WHERE id_usuario = ? AND rol = 'FUNCIONARIO' "
                        + "AND id_funcionario IS NOT NULL";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, idCuenta);
            if (sentencia.executeUpdate() != 1) {
                throw new IllegalStateException(
                        "La cuenta ya no es de funcionario o no se pudo actualizar.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo otorgar el rol de Admin RRHH: " + e.getMessage(), e);
        }
    }

    public void cambiarContrasenaFuncionario(long idCuenta, char[] contrasena) {
        validarContrasena(contrasena);
        byte[] sal = nuevaSal();
        String sql =
                "UPDATE usuarios SET sal = ?, hash_contrasena = ? "
                        + "WHERE id_usuario = ? AND rol = 'FUNCIONARIO'";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setBytes(1, sal);
            sentencia.setBytes(2, derivar(contrasena, sal));
            sentencia.setLong(3, idCuenta);
            if (sentencia.executeUpdate() != 1) {
                throw new IllegalStateException("La cuenta seleccionada ya no existe.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo cambiar la contraseña: " + e.getMessage(), e);
        }
    }

    public void eliminarCuentaFuncionario(long idCuenta) {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ? AND rol = 'FUNCIONARIO'";
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            sentencia.setLong(1, idCuenta);
            if (sentencia.executeUpdate() != 1) {
                throw new IllegalStateException("La cuenta seleccionada ya no existe.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo eliminar la cuenta: " + e.getMessage(), e);
        }
    }

    private void crearCuenta(String sql, String usuario, char[] contrasena, Long idFuncionario) {
        crearCuenta(sql, usuario, contrasena, idFuncionario, null);
    }

    private void crearCuenta(
            String sql, String usuario, char[] contrasena, Long idFuncionario, Rol rol) {
        byte[] sal = nuevaSal();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            if (idFuncionario == null) {
                sentencia.setString(1, usuario.trim());
                sentencia.setBytes(2, sal);
                sentencia.setBytes(3, derivar(contrasena, sal));
            } else {
                sentencia.setLong(1, idFuncionario);
                sentencia.setString(2, usuario.trim());
                sentencia.setBytes(3, sal);
                sentencia.setBytes(4, derivar(contrasena, sal));
                if (rol != null) sentencia.setString(5, rol.getValorBD());
            }
            sentencia.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException(
                    "No se pudo crear la cuenta. Verifica que el usuario y el funcionario no tengan ya una cuenta. "
                            + e.getMessage(),
                    e);
        }
    }

    private static void validarCredenciales(String usuario, char[] contrasena) {
        if (usuario == null || usuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario es obligatorio.");
        }
        if (usuario.trim().length() > 60) {
            throw new IllegalArgumentException(
                    "El nombre de usuario no puede superar 60 caracteres.");
        }
        validarContrasena(contrasena);
    }

    private static void validarContrasena(char[] contrasena) {
        if (contrasena == null || contrasena.length < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres.");
        }
    }

    private static byte[] nuevaSal() {
        byte[] sal = new byte[TAMANO_SAL];
        ALEATORIO.nextBytes(sal);
        return sal;
    }

    private static byte[] derivar(char[] contrasena, byte[] sal) {
        PBEKeySpec especificacion = new PBEKeySpec(contrasena, sal, ITERACIONES, TAMANO_HASH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(especificacion)
                    .getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo proteger la contraseña.", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
