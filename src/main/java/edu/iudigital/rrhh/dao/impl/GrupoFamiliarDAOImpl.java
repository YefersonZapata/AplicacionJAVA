package edu.iudigital.rrhh.dao.impl;

import edu.iudigital.rrhh.config.ConexionBD;
import edu.iudigital.rrhh.dao.GrupoFamiliarDAO;
import edu.iudigital.rrhh.exception.AccesoDatosException;
import edu.iudigital.rrhh.model.GrupoFamiliar;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GrupoFamiliarDAOImpl implements GrupoFamiliarDAO {
    private static final String SELECT =
            "SELECT gf.id_familiar,gf.id_funcionario, CONCAT(f.nombres,' ',f.apellidos) AS"
                + " funcionario_nombre, f.numero_identificacion AS"
                + " funcionario_documento,gf.id_tipo_documento,td.nombre AS"
                + " tipo_documento_nombre,td.sigla AS tipo_documento_sigla,"
                + " gf.numero_identificacion,gf.nombres,gf.apellidos,gf.rol,gf.fecha_nacimiento,gf.telefono"
                + " FROM grupo_familiar gf JOIN funcionarios f ON"
                + " f.id_funcionario=gf.id_funcionario LEFT JOIN tipo_documento td ON"
                + " td.id_tipo_documento=gf.id_tipo_documento";

    @Override
    public List<GrupoFamiliar> listar() {
        List<GrupoFamiliar> datos = new ArrayList<>();
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps =
                        c.prepareStatement(
                                SELECT
                                        + " ORDER BY"
                                        + " f.apellidos,f.nombres,gf.apellidos,gf.nombres");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) datos.add(mapear(rs));
            return datos;
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo listar el grupo familiar: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<GrupoFamiliar> buscarPorId(long id) {
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(SELECT + " WHERE gf.id_familiar=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo consultar el familiar: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertar(GrupoFamiliar f) {
        String sql =
                "INSERT INTO"
                    + " grupo_familiar(id_funcionario,id_tipo_documento,numero_identificacion,nombres,apellidos,rol,fecha_nacimiento,telefono)"
                    + " VALUES(?,?,?,?,?,?,?,?)";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, f);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) f.setIdFamiliar(k.getLong(1));
            }
        } catch (SQLException e) {
            throw traducir("No se pudo insertar el familiar", e);
        }
    }

    @Override
    public void actualizar(GrupoFamiliar f) {
        String sql =
                "UPDATE grupo_familiar SET"
                    + " id_funcionario=?,id_tipo_documento=?,numero_identificacion=?,nombres=?,apellidos=?,rol=?,fecha_nacimiento=?,telefono=?"
                    + " WHERE id_familiar=?";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, f);
            ps.setLong(9, f.getIdFamiliar());
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException("No existe el familiar que intentas actualizar.");
        } catch (SQLException e) {
            throw traducir("No se pudo actualizar el familiar", e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps =
                        c.prepareStatement("DELETE FROM grupo_familiar WHERE id_familiar=?")) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException("No existe el familiar que intentas eliminar.");
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo eliminar el familiar: " + e.getMessage(), e);
        }
    }

    private static void asignar(PreparedStatement ps, GrupoFamiliar f) throws SQLException {
        ps.setLong(1, f.getIdFuncionario());
        if (f.getIdTipoDocumento() == null) ps.setNull(2, Types.BIGINT);
        else ps.setLong(2, f.getIdTipoDocumento());
        ps.setString(3, vacioANull(f.getNumeroIdentificacion()));
        ps.setString(4, requerido(f.getNombres(), "nombres"));
        ps.setString(5, requerido(f.getApellidos(), "apellidos"));
        ps.setString(6, requerido(f.getRol(), "rol familiar"));
        if (f.getFechaNacimiento() == null) ps.setNull(7, Types.DATE);
        else ps.setDate(7, Date.valueOf(f.getFechaNacimiento()));
        ps.setString(8, vacioANull(f.getTelefono()));
    }

    private static GrupoFamiliar mapear(ResultSet rs) throws SQLException {
        GrupoFamiliar f = new GrupoFamiliar();
        f.setIdFamiliar(rs.getLong("id_familiar"));
        f.setIdFuncionario(rs.getLong("id_funcionario"));
        f.setFuncionarioNombre(rs.getString("funcionario_nombre"));
        f.setNumeroDocumentoFuncionario(rs.getString("funcionario_documento"));
        long tipo = rs.getLong("id_tipo_documento");
        f.setIdTipoDocumento(rs.wasNull() ? null : tipo);
        f.setTipoDocumentoNombre(rs.getString("tipo_documento_nombre"));
        f.setTipoDocumentoSigla(rs.getString("tipo_documento_sigla"));
        f.setNumeroIdentificacion(rs.getString("numero_identificacion"));
        f.setNombres(rs.getString("nombres"));
        f.setApellidos(rs.getString("apellidos"));
        f.setRol(rs.getString("rol"));
        Date fecha = rs.getDate("fecha_nacimiento");
        f.setFechaNacimiento(fecha == null ? null : fecha.toLocalDate());
        f.setTelefono(rs.getString("telefono"));
        return f;
    }

    private static String requerido(String s, String campo) {
        if (s == null || s.isBlank())
            throw new AccesoDatosException("El campo " + campo + " es obligatorio.");
        return s.trim();
    }

    private static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static AccesoDatosException traducir(String msg, SQLException e) {
        if ("23000".equals(e.getSQLState()))
            return new AccesoDatosException(
                    msg
                            + ": verifica el funcionario, el tipo y número de documento. "
                            + e.getMessage(),
                    e);
        return new AccesoDatosException(msg + ": " + e.getMessage(), e);
    }
}
