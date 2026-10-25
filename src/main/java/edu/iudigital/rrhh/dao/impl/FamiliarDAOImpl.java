package edu.iudigital.rrhh.dao.impl;

import edu.iudigital.rrhh.config.ConexionBD;
import edu.iudigital.rrhh.dao.FamiliarDAO;
import edu.iudigital.rrhh.exception.AccesoDatosException;
import edu.iudigital.rrhh.model.Familiar;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FamiliarDAOImpl implements FamiliarDAO {
    private static final String SELECT =
            """
            SELECT id_familiar, id_funcionario, tipo_identificacion, numero_identificacion,
                   nombres, apellidos, rol, fecha_nacimiento, telefono
            FROM familiar
            """;

    @Override
    public List<Familiar> listar() {
        List<Familiar> resultado = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta =
                        conexion.prepareStatement(
                                SELECT + " ORDER BY id_funcionario, apellidos, nombres");
                ResultSet resultados = consulta.executeQuery()) {
            while (resultados.next()) {
                resultado.add(mapear(resultados));
            }
            return resultado;
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudieron listar los familiares: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Familiar> buscarPorId(long id) {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta =
                        conexion.prepareStatement(SELECT + " WHERE id_familiar = ?")) {
            consulta.setLong(1, id);
            try (ResultSet resultados = consulta.executeQuery()) {
                return resultados.next() ? Optional.of(mapear(resultados)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo consultar el familiar.", e);
        }
    }

    @Override
    public void insertar(Familiar familiar) {
        String sql =
                """
                INSERT INTO familiar (
                    id_funcionario, tipo_identificacion, numero_identificacion, nombres,
                    apellidos, rol, fecha_nacimiento, telefono
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta =
                        conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(consulta, familiar);
            consulta.executeUpdate();
            try (ResultSet claves = consulta.getGeneratedKeys()) {
                if (claves.next()) {
                    familiar.setIdFamiliar(claves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException(mensajeSql("No se pudo insertar el familiar", e), e);
        }
    }

    @Override
    public void actualizar(Familiar familiar) {
        String sql =
                """
                UPDATE familiar
                SET id_funcionario = ?, tipo_identificacion = ?, numero_identificacion = ?,
                    nombres = ?, apellidos = ?, rol = ?, fecha_nacimiento = ?, telefono = ?
                WHERE id_familiar = ?
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta = conexion.prepareStatement(sql)) {
            asignar(consulta, familiar);
            consulta.setLong(9, familiar.getIdFamiliar());
            if (consulta.executeUpdate() == 0) {
                throw new AccesoDatosException("No existe el familiar que intentas actualizar.");
            }
        } catch (SQLException e) {
            throw new AccesoDatosException(mensajeSql("No se pudo actualizar el familiar", e), e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta =
                        conexion.prepareStatement("DELETE FROM familiar WHERE id_familiar = ?")) {
            consulta.setLong(1, id);
            if (consulta.executeUpdate() == 0) {
                throw new AccesoDatosException("No existe el familiar que intentas eliminar.");
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo eliminar el familiar: " + e.getMessage(), e);
        }
    }

    private static void asignar(PreparedStatement consulta, Familiar familiar) throws SQLException {
        consulta.setLong(1, familiar.getIdFuncionario());
        consulta.setString(2, vacioANull(familiar.getTipoIdentificacion()));
        consulta.setString(3, vacioANull(familiar.getNumeroIdentificacion()));
        consulta.setString(4, familiar.getNombres());
        consulta.setString(5, familiar.getApellidos());
        consulta.setString(6, familiar.getRol());
        if (familiar.getFechaNacimiento() == null) {
            consulta.setNull(7, Types.DATE);
        } else {
            consulta.setDate(7, Date.valueOf(familiar.getFechaNacimiento()));
        }
        consulta.setString(8, vacioANull(familiar.getTelefono()));
    }

    private static Familiar mapear(ResultSet resultados) throws SQLException {
        Date fechaNacimiento = resultados.getDate("fecha_nacimiento");
        return new Familiar(
                resultados.getLong("id_familiar"),
                resultados.getLong("id_funcionario"),
                resultados.getString("tipo_identificacion"),
                resultados.getString("numero_identificacion"),
                resultados.getString("nombres"),
                resultados.getString("apellidos"),
                resultados.getString("rol"),
                fechaNacimiento == null ? null : fechaNacimiento.toLocalDate(),
                resultados.getString("telefono"));
    }

    private static String vacioANull(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private static String mensajeSql(String inicio, SQLException e) {
        if ("23000".equals(e.getSQLState())) {
            return inicio
                    + ": revisa el documento del familiar y que el funcionario seleccionado exista."
                    + " "
                    + e.getMessage();
        }
        return inicio + ": " + e.getMessage();
    }
}
