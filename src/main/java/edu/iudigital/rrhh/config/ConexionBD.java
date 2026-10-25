package edu.iudigital.rrhh.config;

import edu.iudigital.rrhh.exception.AccesoDatosException;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Crea conexiones JDBC. Cada DAO obtiene y cierra su propia conexión. */
public final class ConexionBD {
    private static final Properties PROPERTIES = cargarPropiedades();

    private ConexionBD() {}

    private static Properties cargarPropiedades() {
        Properties p = new Properties();
        try (InputStream in =
                ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) p.load(in);
        } catch (IOException e) {
            throw new AccesoDatosException("No se pudo leer db.properties.", e);
        }
        return p;
    }

    public static Connection obtenerConexion() {
        String url =
                System.getProperty(
                        "db.url",
                        PROPERTIES.getProperty(
                                "db.url",
                                "jdbc:mysql://localhost:3306/rrhh_iudigital?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&characterEncoding=UTF-8"));
        String user = System.getProperty("db.user", PROPERTIES.getProperty("db.user", "root"));
        String password =
                System.getProperty(
                        "db.password", PROPERTIES.getProperty("db.password", ""));
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new AccesoDatosException(
                    "No se encontró MySQL Connector/J. Abre el proyecto como Maven para descargar"
                            + " la dependencia.",
                    e);
        } catch (SQLException e) {
            throw new AccesoDatosException(
                    "No se pudo conectar a MySQL. Verifica el servidor, la base de datos y"
                            + " src/main/resources/db.properties. Detalle: "
                            + e.getMessage(),
                    e);
        }
    }
}
