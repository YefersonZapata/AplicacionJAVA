package edu.iudigital.rrhh.dao.impl;

import edu.iudigital.rrhh.config.ConexionBD;
import edu.iudigital.rrhh.dao.FormacionAcademicaDAO;
import edu.iudigital.rrhh.exception.AccesoDatosException;
import edu.iudigital.rrhh.model.FormacionAcademica;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormacionAcademicaDAOImpl implements FormacionAcademicaDAO {
    private static final String SELECT =
            "SELECT fa.id_formacion,fa.id_funcionario,CONCAT(f.nombres,' ',f.apellidos) AS"
                + " funcionario_nombre, f.numero_identificacion AS funcionario_documento,"
                + " fa.universidad,fa.nivel_estudio,fa.titulo_estudio FROM formacion_academica fa"
                + " JOIN funcionarios f ON f.id_funcionario=fa.id_funcionario";

    @Override
    public List<FormacionAcademica> listar() {
        List<FormacionAcademica> datos = new ArrayList<>();
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps =
                        c.prepareStatement(
                                SELECT + " ORDER BY f.apellidos,f.nombres,fa.nivel_estudio");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) datos.add(mapear(rs));
            return datos;
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo listar la formación académica: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FormacionAcademica> buscarPorId(long id) {
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(SELECT + " WHERE fa.id_formacion=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo consultar la formación académica: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertar(FormacionAcademica f) {
        String sql =
                "INSERT INTO"
                    + " formacion_academica(id_funcionario,universidad,nivel_estudio,titulo_estudio)"
                    + " VALUES(?,?,?,?)";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, f);
            ps.executeUpdate();
            try (ResultSet k = ps.getGeneratedKeys()) {
                if (k.next()) f.setIdFormacion(k.getLong(1));
            }
        } catch (SQLException e) {
            throw traducir("No se pudo insertar la formación académica", e);
        }
    }

    @Override
    public void actualizar(FormacionAcademica f) {
        String sql =
                "UPDATE formacion_academica SET"
                        + " id_funcionario=?,universidad=?,nivel_estudio=?,titulo_estudio=? WHERE"
                        + " id_formacion=?";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, f);
            ps.setLong(5, f.getIdFormacion());
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException(
                        "No existe el registro académico que intentas actualizar.");
        } catch (SQLException e) {
            throw traducir("No se pudo actualizar la formación académica", e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps =
                        c.prepareStatement(
                                "DELETE FROM formacion_academica WHERE id_formacion=?")) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException(
                        "No existe el registro académico que intentas eliminar.");
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo eliminar la formación académica: " + e.getMessage(), e);
        }
    }

    private static void asignar(PreparedStatement ps, FormacionAcademica f) throws SQLException {
        ps.setLong(1, f.getIdFuncionario());
        ps.setString(2, requerido(f.getUniversidad(), "universidad"));
        ps.setString(3, requerido(f.getNivelEstudio(), "nivel de estudio"));
        ps.setString(4, requerido(f.getTituloEstudio(), "título"));
    }

    private static FormacionAcademica mapear(ResultSet rs) throws SQLException {
        FormacionAcademica f = new FormacionAcademica();
        f.setIdFormacion(rs.getLong("id_formacion"));
        f.setIdFuncionario(rs.getLong("id_funcionario"));
        f.setFuncionarioNombre(rs.getString("funcionario_nombre"));
        f.setNumeroDocumentoFuncionario(rs.getString("funcionario_documento"));
        f.setUniversidad(rs.getString("universidad"));
        f.setNivelEstudio(rs.getString("nivel_estudio"));
        f.setTituloEstudio(rs.getString("titulo_estudio"));
        return f;
    }

    private static String requerido(String s, String campo) {
        if (s == null || s.isBlank())
            throw new AccesoDatosException("El campo " + campo + " es obligatorio.");
        return s.trim();
    }

    private static AccesoDatosException traducir(String msg, SQLException e) {
        if ("23000".equals(e.getSQLState()))
            return new AccesoDatosException(
                    msg
                            + ": verifica que el funcionario exista y no estés duplicando el mismo"
                            + " estudio. "
                            + e.getMessage(),
                    e);
        return new AccesoDatosException(msg + ": " + e.getMessage(), e);
    }
}
