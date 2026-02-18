package org.BioGuard;

/*
 * // Objetivo
 *    Representar un hallazgo (diagnóstico) dentro de una muestra de ADN: qué virus
 *    fue detectado, su nivel de infecciosidad y la posición dentro de la secuencia.
 *
 * // Atributos
 *    virus : Nombre del virus detectado (String)
 *    nivel : Nivel de infecciosidad (String) - p.ej. "Normal" / "Altamente Infeccioso"
 *    inicio: Posición de inicio en la secuencia donde se detectó el virus (int)
 *    fin   : Posición de final en la secuencia donde se detectó el virus (int)
 */

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