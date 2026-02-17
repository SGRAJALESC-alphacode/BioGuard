package org.BioGuard;

public class Diagnostico {
    private String virus;
    private String nivel;
    private int inicio;
    private int fin;

    public Diagnostico(String virus, String nivel, int inicio, int fin) {
        this.virus = virus;
        this.nivel = nivel;
        this.inicio = inicio;
        this.fin = fin;
    }

    public String getNombreVirus() { return virus; }
    public String toCsvString() { return virus + "," + nivel + "," + inicio + "," + fin; }
}