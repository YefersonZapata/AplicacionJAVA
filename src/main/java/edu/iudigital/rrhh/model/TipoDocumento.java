package edu.iudigital.rrhh.model;

public class TipoDocumento {
    private long idTipoDocumento;
    private String nombre;
    private String sigla;

    public TipoDocumento() {}

    public TipoDocumento(long idTipoDocumento, String nombre, String sigla) {
        this.idTipoDocumento = idTipoDocumento;
        this.nombre = nombre;
        this.sigla = sigla;
    }

    public long getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(long idTipoDocumento) {
        this.idTipoDocumento = idTipoDocumento;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }

    @Override
    public String toString() {
        return sigla == null || sigla.isBlank() ? nombre : sigla + " — " + nombre;
    }
}
