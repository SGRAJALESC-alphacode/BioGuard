package org.BioGuard;

import org.BioGuard.exception.FormatoFastaInvalidoException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Servicio para gestión de virus.
 * Persistencia exclusiva en archivos FASTA.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class VirusService {

    private static final String VIRUS_FOLDER = "data/virus/";

    public VirusService() {
        new File(VIRUS_FOLDER).mkdirs();
    }

    /**
     * Guarda un virus.
     *
     * @param nombre Nombre del virus
     * @param nivel Nivel de infecciosidad
     * @param secuencia Secuencia de ADN
     * @throws FormatoFastaInvalidoException Si los datos son inválidos
     * @throws IOException Si hay error de escritura
     */
    public void guardarVirus(String nombre, String nivel, String secuencia)
            throws FormatoFastaInvalidoException, IOException {

        if (nombre == null || nombre.trim().isEmpty())
            throw new FormatoFastaInvalidoException("El nombre del virus es obligatorio");

        if (nivel == null || (!nivel.equals("Poco Infeccioso") &&
                !nivel.equals("Normal") && !nivel.equals("Altamente Infeccioso")))
            throw new FormatoFastaInvalidoException("Nivel de infecciosidad inválido");

        if (secuencia == null || secuencia.trim().isEmpty())
            throw new FormatoFastaInvalidoException("La secuencia es obligatoria");

        if (!secuencia.matches("^[ATCG]+$"))
            throw new FormatoFastaInvalidoException("Caracteres inválidos en secuencia");

        String nombreArchivo = nombre.replaceAll("[^a-zA-Z0-9]", "_") + ".fasta";
        Path ruta = Paths.get(VIRUS_FOLDER + nombreArchivo);

        String header = ">" + nombre + "|" + nivel;
        String contenido = header + "\n" + secuencia + "\n";

        Files.writeString(ruta, contenido);
    }

    /**
     * Carga todos los virus registrados.
     *
     * @return Lista de virus
     * @throws IOException Si hay error de lectura
     */
    public List<Virus> cargarTodosLosVirus() throws IOException {
        List<Virus> virusList = new ArrayList<>();
        File folder = new File(VIRUS_FOLDER);

        if (!folder.exists()) return virusList;

        File[] virusFiles = folder.listFiles((dir, name) -> name.endsWith(".fasta"));
        if (virusFiles == null) return virusList;

        for (File file : virusFiles) {
            Virus virus = cargarVirusDeArchivo(file);
            if (virus != null) virusList.add(virus);
        }
        return virusList;
    }

    /**
     * Carga un virus desde archivo FASTA.
     */
    private Virus cargarVirusDeArchivo(File file) throws IOException {
        String contenido = Files.readString(file.toPath()).trim();
        String[] lineas = contenido.split("\n");

        if (lineas.length < 2) return null;

        String header = lineas[0].trim();
        if (!header.startsWith(">")) return null;

        String headerSinMayor = header.substring(1);
        String[] partes = headerSinMayor.split("\\|");

        String nombre = partes[0];
        String nivel = partes.length > 1 ? partes[1] : "Normal";

        StringBuilder secuencia = new StringBuilder();
        for (int i = 1; i < lineas.length; i++) {
            secuencia.append(lineas[i].trim());
        }

        return new Virus(nombre, nivel, secuencia.toString());
    }

    /**
     * Busca virus por nombre.
     */
    public Virus buscarVirusPorNombre(String nombre) throws IOException {
        for (Virus v : cargarTodosLosVirus()) {
            if (v.getNombre().equalsIgnoreCase(nombre)) return v;
        }
        return null;
    }
}