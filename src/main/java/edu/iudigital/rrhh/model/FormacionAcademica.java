package edu.iudigital.rrhh.model;

public class FormacionAcademica {
    private long idFormacion;
    private long idFuncionario;
    private String funcionarioNombre;
    private String numeroDocumentoFuncionario;
    private String universidad;
    private String nivelEstudio;
    private String tituloEstudio;

    public FormacionAcademica() {}

    public long getIdFormacion() {
        return idFormacion;
    }

    public void setIdFormacion(long v) {
        idFormacion = v;
    }

    public long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(long v) {
        idFuncionario = v;
    }

    public String getFuncionarioNombre() {
        return funcionarioNombre;
    }

    public void setFuncionarioNombre(String v) {
        funcionarioNombre = v;
    }

    public String getNumeroDocumentoFuncionario() {
        return numeroDocumentoFuncionario;
    }

    public void setNumeroDocumentoFuncionario(String v) {
        numeroDocumentoFuncionario = v;
    }

    public String getUniversidad() {
        return universidad;
    }

    public void setUniversidad(String v) {
        universidad = v;
    }

    public String getNivelEstudio() {
        return nivelEstudio;
    }

    public void setNivelEstudio(String v) {
        nivelEstudio = v;
    }

    public String getTituloEstudio() {
        return tituloEstudio;
    }

    public void setTituloEstudio(String v) {
        tituloEstudio = v;
    }
}
