package edu.iudigital.rrhh.exception;

/** Excepción de ejecución para errores de conexión o consultas SQL. */
public class AccesoDatosException extends RuntimeException {
    public AccesoDatosException(String message) {
        super(message);
    }

    public AccesoDatosException(String message, Throwable cause) {
        super(message, cause);
    }
}
