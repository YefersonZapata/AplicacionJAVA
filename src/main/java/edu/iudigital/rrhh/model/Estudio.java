package edu.iudigital.rrhh.model;

public class Estudio {
    private long idEstudio;
    private long idFuncionario;
    private String universidad, nivelEstudio, tituloEstudio;

    public Estudio() {}

    public Estudio(
            long idEstudio,
            long idFuncionario,
            String universidad,
            String nivelEstudio,
            String tituloEstudio) {
        this.idEstudio = idEstudio;
        this.idFuncionario = idFuncionario;
        this.universidad = universidad;
        this.nivelEstudio = nivelEstudio;
        this.tituloEstudio = tituloEstudio;
    }

    public long getIdEstudio() {
        return idEstudio;
    }

    public void setIdEstudio(long idEstudio) {
        this.idEstudio = idEstudio;
    }

    public long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(long idFuncionario) {
        this.idFuncionario = idFuncionario;
    }

    public String getUniversidad() {
        return universidad;
    }

    public void setUniversidad(String universidad) {
        this.universidad = universidad;
    }

    public String getNivelEstudio() {
        return nivelEstudio;
    }

    public void setNivelEstudio(String nivelEstudio) {
        this.nivelEstudio = nivelEstudio;
    }

    public String getTituloEstudio() {
        return tituloEstudio;
    }

    public void setTituloEstudio(String tituloEstudio) {
        this.tituloEstudio = tituloEstudio;
    }
}
