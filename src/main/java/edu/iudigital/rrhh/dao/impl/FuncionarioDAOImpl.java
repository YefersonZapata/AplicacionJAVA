package edu.iudigital.rrhh.dao.impl;

import edu.iudigital.rrhh.config.ConexionBD;
import edu.iudigital.rrhh.dao.FuncionarioDAO;
import edu.iudigital.rrhh.exception.AccesoDatosException;
import edu.iudigital.rrhh.model.Funcionario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FuncionarioDAOImpl implements FuncionarioDAO {
    private static final String SELECT =
            "SELECT f.id_funcionario, f.id_tipo_documento, td.nombre AS tipo_documento_nombre,"
                + " td.sigla AS tipo_documento_sigla, f.numero_identificacion, f.nombres,"
                + " f.apellidos, f.id_estado_civil, ec.nombre AS estado_civil_nombre, f.sexo,"
                + " f.direccion, f.telefono, f.fecha_nacimiento FROM funcionarios f JOIN"
                + " tipo_documento td ON td.id_tipo_documento=f.id_tipo_documento JOIN estado_civil"
                + " ec ON ec.id_estado_civil=f.id_estado_civil";

    @Override
    public List<Funcionario> listar() {
        List<Funcionario> datos = new ArrayList<>();
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps =
                        c.prepareStatement(SELECT + " ORDER BY f.apellidos, f.nombres");
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) datos.add(mapear(rs));
            return datos;
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudieron listar los funcionarios: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Funcionario> buscarPorId(long id) {
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(SELECT + " WHERE f.id_funcionario=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapear(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo consultar el funcionario: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertar(Funcionario f) {
        String sql =
                "INSERT INTO"
                    + " funcionarios(id_tipo_documento,numero_identificacion,nombres,apellidos,id_estado_civil,sexo,direccion,telefono,fecha_nacimiento)"
                    + " VALUES(?,?,?,?,?,?,?,?,?)";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(ps, f);
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) f.setIdFuncionario(keys.getLong(1));
            }
        } catch (SQLException e) {
            throw traducir("No se pudo insertar el funcionario", e);
        }
    }

    @Override
    public void actualizar(Funcionario f) {
        String sql =
                "UPDATE funcionarios SET"
                    + " id_tipo_documento=?,numero_identificacion=?,nombres=?,apellidos=?,id_estado_civil=?,sexo=?,direccion=?,telefono=?,fecha_nacimiento=?"
                    + " WHERE id_funcionario=?";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql)) {
            asignar(ps, f);
            ps.setLong(10, f.getIdFuncionario());
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException("No existe el funcionario que intentas actualizar.");
        } catch (SQLException e) {
            throw traducir("No se pudo actualizar el funcionario", e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps =
                        c.prepareStatement("DELETE FROM funcionarios WHERE id_funcionario=?")) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException("No existe el funcionario que intentas eliminar.");
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo eliminar el funcionario: " + e.getMessage(), e);
        }
    }

    private static void asignar(PreparedStatement ps, Funcionario f) throws SQLException {
        ps.setLong(1, f.getIdTipoDocumento());
        ps.setString(2, limpiar(f.getNumeroIdentificacion()));
        ps.setString(3, limpiar(f.getNombres()));
        ps.setString(4, limpiar(f.getApellidos()));
        ps.setLong(5, f.getIdEstadoCivil());
        ps.setString(6, limpiar(f.getSexo()));
        ps.setString(7, vacioANull(f.getDireccion()));
        ps.setString(8, vacioANull(f.getTelefono()));
        if (f.getFechaNacimiento() == null) ps.setNull(9, Types.DATE);
        else ps.setDate(9, Date.valueOf(f.getFechaNacimiento()));
    }

    private static Funcionario mapear(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.setIdFuncionario(rs.getLong("id_funcionario"));
        f.setIdTipoDocumento(rs.getLong("id_tipo_documento"));
        f.setTipoDocumentoNombre(rs.getString("tipo_documento_nombre"));
        f.setTipoDocumentoSigla(rs.getString("tipo_documento_sigla"));
        f.setNumeroIdentificacion(rs.getString("numero_identificacion"));
        f.setNombres(rs.getString("nombres"));
        f.setApellidos(rs.getString("apellidos"));
        f.setIdEstadoCivil(rs.getLong("id_estado_civil"));
        f.setEstadoCivilNombre(rs.getString("estado_civil_nombre"));
        f.setSexo(rs.getString("sexo"));
        f.setDireccion(rs.getString("direccion"));
        f.setTelefono(rs.getString("telefono"));
        Date fecha = rs.getDate("fecha_nacimiento");
        f.setFechaNacimiento(fecha == null ? null : fecha.toLocalDate());
        return f;
    }

    private static String limpiar(String s) {
        return s == null ? null : s.trim();
    }

    private static String vacioANull(String s) {
        return s == null || s.isBlank() ? null : s.trim();
    }

    private static AccesoDatosException traducir(String inicio, SQLException e) {
        if ("23000".equals(e.getSQLState()))
            return new AccesoDatosException(
                    inicio
                            + ": verifica el tipo de documento, el estado civil y que no exista"
                            + " otro funcionario con ese tipo y número de identificación.",
                    e);
        return new AccesoDatosException(inicio + ": " + e.getMessage(), e);
    }
}
