package edu.iudigital.rrhh.dao.impl;

import edu.iudigital.rrhh.config.ConexionBD;
import edu.iudigital.rrhh.dao.EstudioDAO;
import edu.iudigital.rrhh.exception.AccesoDatosException;
import edu.iudigital.rrhh.model.Estudio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EstudioDAOImpl implements EstudioDAO {
    private static final String SELECT =
            """
            SELECT id_estudio, id_funcionario, universidad, nivel_estudio, titulo_estudio
            FROM estudio
            """;

    @Override
    public List<Estudio> listar() {
        List<Estudio> resultado = new ArrayList<>();
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta =
                        conexion.prepareStatement(
                                SELECT + " ORDER BY id_funcionario, nivel_estudio");
                ResultSet resultados = consulta.executeQuery()) {
            while (resultados.next()) {
                resultado.add(mapear(resultados));
            }
            return resultado;
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudieron listar los estudios: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Estudio> buscarPorId(long id) {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta =
                        conexion.prepareStatement(SELECT + " WHERE id_estudio = ?")) {
            consulta.setLong(1, id);
            try (ResultSet resultados = consulta.executeQuery()) {
                return resultados.next() ? Optional.of(mapear(resultados)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo consultar el estudio.", e);
        }
    }

    @Override
    public void insertar(Estudio estudio) {
        String sql =
                """
                INSERT INTO estudio (id_funcionario, universidad, nivel_estudio, titulo_estudio)
                VALUES (?, ?, ?, ?)
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta =
                        conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(consulta, estudio);
            consulta.executeUpdate();
            try (ResultSet claves = consulta.getGeneratedKeys()) {
                if (claves.next()) {
                    estudio.setIdEstudio(claves.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo insertar el estudio: " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizar(Estudio estudio) {
        String sql =
                """
                UPDATE estudio
                SET id_funcionario = ?, universidad = ?, nivel_estudio = ?, titulo_estudio = ?
                WHERE id_estudio = ?
                """;
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta = conexion.prepareStatement(sql)) {
            asignar(consulta, estudio);
            consulta.setLong(5, estudio.getIdEstudio());
            if (consulta.executeUpdate() == 0) {
                throw new AccesoDatosException("No existe el estudio que intentas actualizar.");
            }
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo actualizar el estudio: " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(long id) {
        try (Connection conexion = ConexionBD.obtenerConexion();
                PreparedStatement consulta =
                        conexion.prepareStatement("DELETE FROM estudio WHERE id_estudio = ?")) {
            consulta.setLong(1, id);
            if (consulta.executeUpdate() == 0) {
                throw new AccesoDatosException("No existe el estudio que intentas eliminar.");
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo eliminar el estudio: " + e.getMessage(), e);
        }
    }

    private static void asignar(PreparedStatement consulta, Estudio estudio) throws SQLException {
        consulta.setLong(1, estudio.getIdFuncionario());
        consulta.setString(2, estudio.getUniversidad());
        consulta.setString(3, estudio.getNivelEstudio());
        consulta.setString(4, estudio.getTituloEstudio());
    }

    private static Estudio mapear(ResultSet resultados) throws SQLException {
        return new Estudio(
                resultados.getLong("id_estudio"),
                resultados.getLong("id_funcionario"),
                resultados.getString("universidad"),
                resultados.getString("nivel_estudio"),
                resultados.getString("titulo_estudio"));
    }
}
