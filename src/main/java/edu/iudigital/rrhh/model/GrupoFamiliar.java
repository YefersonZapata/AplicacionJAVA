package edu.iudigital.rrhh.model;

import java.time.LocalDate;

public class GrupoFamiliar {
    private long idFamiliar;
    private long idFuncionario;
    private Long idTipoDocumento;
    private String funcionarioNombre;
    private String numeroDocumentoFuncionario;
    private String tipoDocumentoNombre;
    private String tipoDocumentoSigla;
    private String numeroIdentificacion;
    private String nombres;
    private String apellidos;
    private String rol;
    private LocalDate fechaNacimiento;
    private String telefono;

    public GrupoFamiliar() {}

    public long getIdFamiliar() {
        return idFamiliar;
    }

    public void setIdFamiliar(long v) {
        idFamiliar = v;
    }

    public long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(long v) {
        idFuncionario = v;
    }

    public Long getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(Long v) {
        idTipoDocumento = v;
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
    public String getTipoDocumentoNombre() {
        return tipoDocumentoNombre;
    }
    public void setTipoDocumentoNombre(String v) {
        tipoDocumentoNombre = v;
    }

    public String getTipoDocumentoSigla() {
        return tipoDocumentoSigla;
    }

    public void setTipoDocumentoSigla(String v) {
        tipoDocumentoSigla = v;
    }

    public String getNumeroIdentificacion() {
        return numeroIdentificacion;
    }

    public void setNumeroIdentificacion(String v) {
        numeroIdentificacion = v;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String v) {
        nombres = v;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String v) {
        apellidos = v;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String v) {
        rol = v;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate v) {
        fechaNacimiento = v;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String v) {
        telefono = v;
    }
}
