package org.BioGuard.exception;

public class VirusNotFoundException extends Exception {

    private final String criterio;
    private final String valor;

    public VirusNotFoundException(String criterio, String valor) {
        super(String.format("Virus no encontrado con %s: '%s'", criterio, valor));
        this.criterio = criterio;
        this.valor = valor;
    }

    public VirusNotFoundException(String mensaje) {
        super(mensaje);
        this.criterio = "desconocido";
        this.valor = "desconocido";
    }

    public String getCriterio() { return criterio; }
    public String getValor() { return valor; }
}