package edu.iudigital.rrhh.model;

public class EstadoCivil {
    private long idEstadoCivil;
    private String nombre;

    public EstadoCivil() {}

    public EstadoCivil(long idEstadoCivil, String nombre) {
        this.idEstadoCivil = idEstadoCivil;
        this.nombre = nombre;
    }

    public long getIdEstadoCivil() {
        return idEstadoCivil;
    }

    public void setIdEstadoCivil(long idEstadoCivil) {
        this.idEstadoCivil = idEstadoCivil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return nombre == null ? "" : nombre;
    }
}
