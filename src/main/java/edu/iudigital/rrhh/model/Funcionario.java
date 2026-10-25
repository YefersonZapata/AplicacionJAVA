package edu.iudigital.rrhh.model;

import java.time.LocalDate;

public class Funcionario {
    private long idFuncionario;
    private long idTipoDocumento;
    private long idEstadoCivil;
    private String numeroIdentificacion;
    private String nombres;
    private String apellidos;
    private String tipoDocumentoNombre;
    private String tipoDocumentoSigla;
    private String estadoCivilNombre;
    private String sexo;
    private String direccion;
    private String telefono;
    private LocalDate fechaNacimiento;

    public Funcionario() {}

    public long getIdFuncionario() {
        return idFuncionario;
    }

    public void setIdFuncionario(long v) {
        idFuncionario = v;
    }

    public long getIdTipoDocumento() {
        return idTipoDocumento;
    }

    public void setIdTipoDocumento(long v) {
        idTipoDocumento = v;
    }

    public long getIdEstadoCivil() {
        return idEstadoCivil;
    }

    public void setIdEstadoCivil(long v) {
        idEstadoCivil = v;
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

    public String getEstadoCivilNombre() {
        return estadoCivilNombre;
    }

    public void setEstadoCivilNombre(String v) {
        estadoCivilNombre = v;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String v) {
        sexo = v;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String v) {
        direccion = v;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String v) {
        telefono = v;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate v) {
        fechaNacimiento = v;
    }
}
