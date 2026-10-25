package edu.iudigital.rrhh.dao.impl;

import edu.iudigital.rrhh.config.ConexionBD;
import edu.iudigital.rrhh.dao.TipoDocumentoDAO;
import edu.iudigital.rrhh.exception.AccesoDatosException;
import edu.iudigital.rrhh.model.TipoDocumento;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipoDocumentoDAOImpl implements TipoDocumentoDAO {
    @Override
    public List<TipoDocumento> listar() {
        List<TipoDocumento> datos = new ArrayList<>();
        String sql = "SELECT id_tipo_documento, nombre, sigla FROM tipo_documento ORDER BY nombre";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                datos.add(
                        new TipoDocumento(
                                rs.getLong("id_tipo_documento"),
                                rs.getString("nombre"),
                                rs.getString("sigla")));
            return datos;
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudieron consultar los tipos de documento: " + e.getMessage(), e);
        }
    }

    @Override
    public void insertar(TipoDocumento tipo) {
        String sql = "INSERT INTO tipo_documento(nombre, sigla) VALUES(?, ?)";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, requerido(tipo.getNombre(), "nombre"));
            ps.setString(2, requerido(tipo.getSigla(), "sigla").toUpperCase());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw convertir("No se pudo guardar el tipo de documento", e);
        }
    }

    @Override
    public void actualizar(TipoDocumento tipo) {
        String sql = "UPDATE tipo_documento SET nombre=?, sigla=? WHERE id_tipo_documento=?";
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, requerido(tipo.getNombre(), "nombre"));
            ps.setString(2, requerido(tipo.getSigla(), "sigla").toUpperCase());
            ps.setLong(3, tipo.getIdTipoDocumento());
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException("El tipo de documento seleccionado ya no existe.");
        } catch (SQLException e) {
            throw convertir("No se pudo actualizar el tipo de documento", e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection c = ConexionBD.obtenerConexion();
                PreparedStatement ps =
                        c.prepareStatement(
                                "DELETE FROM tipo_documento WHERE id_tipo_documento=?")) {
            ps.setLong(1, id);
            if (ps.executeUpdate() == 0)
                throw new AccesoDatosException("El tipo de documento seleccionado no existe.");
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState()))
                throw new AccesoDatosException(
                        "No se puede eliminar: hay funcionarios o familiares que usan ese tipo de"
                                + " documento.",
                        e);
            throw new AccesoDatosException(
                    "No se pudo eliminar el tipo de documento: " + e.getMessage(), e);
        }
    }

    private static String requerido(String s, String campo) {
        if (s == null || s.isBlank())
            throw new AccesoDatosException("El campo " + campo + " es obligatorio.");
        return s.trim();
    }

    private static AccesoDatosException convertir(String msg, SQLException e) {
        if ("23000".equals(e.getSQLState()))
            return new AccesoDatosException(
                    msg + ": el nombre o la sigla ya están registrados.", e);
        return new AccesoDatosException(msg + ": " + e.getMessage(), e);
    }
}
