package edu.iudigital.rrhh.dao.impl;

import edu.iudigital.rrhh.config.ConexionBD;
import edu.iudigital.rrhh.dao.EstadoCivilDAO;
import edu.iudigital.rrhh.exception.AccesoDatosException;
import edu.iudigital.rrhh.model.EstadoCivil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstadoCivilDAOImpl implements EstadoCivilDAO {
    @Override
    public List<EstadoCivil> listar() {
        List<EstadoCivil> datos = new ArrayList<>();
        String sql = "SELECT id_estado_civil, nombre FROM estado_civil ORDER BY nombre";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                datos.add(new EstadoCivil(rs.getLong("id_estado_civil"), rs.getString("nombre")));
            return datos;
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudieron consultar los estados civiles: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertar(EstadoCivil estado) {
        String sql = "INSERT INTO estado_civil(nombre) VALUES(?)";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, requerido(estado.getNombre()));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw convertir("No se pudo guardar el estado civil", e);
        }
    }

    @Override
    public void actualizar(EstadoCivil estado) {
        String sql = "UPDATE estado_civil SET nombre=? WHERE id_estado_civil=?";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, requerido(estado.getNombre()));
            ps.setLong(2, estado.getIdEstadoCivil());
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException("El estado civil seleccionado ya no existe.");
        } catch (SQLException e) {
            throw convertir("No se pudo actualizar el estado civil", e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps =
                        c.prepareStatement("DELETE FROM estado_civil WHERE id_estado_civil=?")) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException("El estado civil seleccionado no existe.");
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState()))
                throw new AccesoDatosException(
                        "No se puede eliminar: hay funcionarios que usan ese estado civil.", e);
            throw new AccesoDatosException(
                    "No se pudo eliminar el estado civil: " + e.getMessage(), e);
        }
    }

    private static String requerido(String s) {
        if (s == null || s.isBlank())
            throw new AccesoDatosException("El nombre del estado civil es obligatorio.");
        return s.trim();
    }

    private static AccesoDatosException convertir(String msg, SQLException e) {
        if ("23000".equals(e.getSQLState()))
            return new AccesoDatosException(
                    "" + msg + ": ya existe un estado civil con ese nombre.", e);
        return new AccesoDatosException(msg + ": " + e.getMessage(), e);
    }
}
