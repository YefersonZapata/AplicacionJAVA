package edu.iudigital.rrhh.auth;

public enum Rol {
    ADMIN_RRHH("ADMIN_RRHH", "Admin RRHH"),
    FUNCIONARIO("FUNCIONARIO", "Funcionario");

    private final String valorBD;
    private final String etiqueta;

    Rol(String valorBD, String etiqueta) {
        this.valorBD = valorBD;
        this.etiqueta = etiqueta;
    }

    public String getValorBD() {
        return valorBD;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }

    public static Rol desdeBD(String valor) {
        for (Rol rol : values()) {
            if (rol.valorBD.equals(valor)) return rol;
        }
        throw new IllegalArgumentException("Rol de usuario desconocido: " + valor);
    }
}
