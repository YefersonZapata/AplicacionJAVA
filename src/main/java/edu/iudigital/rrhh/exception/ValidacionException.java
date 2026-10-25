package edu.iudigital.rrhh.exception;

/** Representa un dato ingresado que no cumple las reglas de validación. */
public class ValidacionException extends Exception {
    public ValidacionException(String message) {
        super(message);
    }
}
