package org.BioGuard;

/*
 * // Objetivo
 *    Servicio responsable de la gestión del catálogo de virus: validación,
 *    almacenamiento en archivos FASTA y lectura de virus registrados.
 *
 * // Responsabilidades
 *    - validar datos de virus recibidos (nombre, nivel, secuencia)
 *    - guardar virus en `data/virus/` como archivos FASTA
 *    - cargar todos los virus registrados para su uso en diagnósticos
 */

import org.BioGuard.exception.FormatoFastaInvalidoException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class VirusService {

    private static final String VIRUS_FOLDER = "data/virus/";

    public VirusService() {
        new File(VIRUS_FOLDER).mkdirs();
    }

    /**
     * Guarda un virus desde datos enviados por el cliente.
     *
     * @param virusData Mapa con nombre, nivel y secuencia
     * @return El virus guardado
     * @throws FormatoFastaInvalidoException Si los datos son inválidos
     * @throws IOException Si hay error de escritura
     */
    public Virus guardarVirus(Map<String, String> virusData) throws FormatoFastaInvalidoException, IOException {
        String nombre = virusData.get("nombre");
        String nivel = virusData.get("nivel");
        String secuencia = virusData.get("secuencia");

        // Validaciones
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new FormatoFastaInvalidoException("El nombre del virus es obligatorio");
        }

        if (nivel == null || (!nivel.equals("Poco Infeccioso") &&
                !nivel.equals("Normal") && !nivel.equals("Altamente Infeccioso"))) {
            throw new FormatoFastaInvalidoException("Nivel de infecciosidad inválido. Debe ser: Poco Infeccioso, Normal o Altamente Infeccioso");
        }

        if (secuencia == null || secuencia.trim().isEmpty()) {
            throw new FormatoFastaInvalidoException("La secuencia de ADN es obligatoria");
        }

        if (!secuencia.matches("^[ATCG]+$")) {
            throw new FormatoFastaInvalidoException("La secuencia contiene caracteres inválidos. Solo se permiten A, T, C, G");
        }

        Virus virus = new Virus(nombre, nivel, secuencia);
        guardarVirusEnArchivo(virus);
        return virus;
    }

    /**
     * Guarda un virus en el sistema de archivos (formato FASTA).
     *
     * @param virus El virus a guardar
     * @throws IOException Si hay error de escritura
     */
    private void guardarVirusEnArchivo(Virus virus) throws IOException {
        String nombreArchivo = virus.getNombre().replaceAll("[^a-zA-Z0-9]", "_") + ".fasta";
        Path ruta = Paths.get(VIRUS_FOLDER + nombreArchivo);

        String header = ">" + virus.getNombre() + "|" + virus.getNivelInfecciosidad();
        String contenido = header + "\n" + virus.getSecuencia() + "\n";

        Files.createDirectories(ruta.getParent());
        Files.writeString(ruta, contenido);
    }

    /**
     * Carga todos los virus registrados en el sistema.
     *
     * @return Lista de virus
     * @throws IOException Si hay error de lectura
     */
    public List<Virus> cargarTodosLosVirus() throws IOException {
        List<Virus> virusList = new ArrayList<>();
        File folder = new File(VIRUS_FOLDER);

        if (!folder.exists()) {
            return virusList;
        }

        File[] virusFiles = folder.listFiles((dir, name) -> name.endsWith(".fasta"));
        if (virusFiles == null) {
            return virusList;
        }

        for (File file : virusFiles) {
            try {
                Virus virus = cargarVirusDeArchivo(file);
                if (virus != null) {
                    virusList.add(virus);
                }
            } catch (Exception e) {
                System.err.println("Error cargando virus del archivo: " + file.getName() + " - " + e.getMessage());
            }
        }

        return virusList;
    }

    /**
     * Carga un virus desde un archivo FASTA.
     *
     * @param file El archivo a cargar
     * @return El virus cargado o null si hay error
     * @throws IOException Si hay error de lectura
     */
    private Virus cargarVirusDeArchivo(File file) throws IOException {
        String contenido = Files.readString(file.toPath()).trim();
        String[] lineas = contenido.split("\n");

        if (lineas.length < 2) {
            return null;
        }

        String header = lineas[0].trim();
        if (!header.startsWith(">")) {
            return null;
        }

        // Parsear header: >nombre_virus|nivel
        String headerSinMayor = header.substring(1);
        String[] partes = headerSinMayor.split("\\|");

        String nombre = partes[0];
        String nivel = partes.length > 1 ? partes[1] : "Normal";

        // Unir todas las líneas de secuencia
        StringBuilder secuencia = new StringBuilder();
        for (int i = 1; i < lineas.length; i++) {
            secuencia.append(lineas[i].trim());
        }

        return new Virus(nombre, nivel, secuencia.toString());
    }

    /**
     * Busca un virus por su nombre.
     *
     * @param nombre El nombre del virus a buscar
     * @return El virus encontrado o null si no existe
     * @throws IOException Si hay error de lectura
     */
    public Virus buscarVirusPorNombre(String nombre) throws IOException {
        List<Virus> virusList = cargarTodosLosVirus();

        for (Virus v : virusList) {
            if (v.getNombre().equalsIgnoreCase(nombre)) {
                return v;
            }
        }

        return null;
    }

    /**
     * Elimina un virus del sistema.
     *
     * @param nombre El nombre del virus a eliminar
     * @return true si se eliminó correctamente
     * @throws IOException Si hay error de eliminación
     */
    public boolean eliminarVirus(String nombre) throws IOException {
        String nombreArchivo = nombre.replaceAll("[^a-zA-Z0-9]", "_") + ".fasta";
        Path ruta = Paths.get(VIRUS_FOLDER + nombreArchivo);

        return Files.deleteIfExists(ruta);
    }

    /**
     * Obtiene la cantidad de virus registrados.
     *
     * @return Número de virus
     * @throws IOException Si hay error de lectura
     */
    public int contarVirus() throws IOException {
        File folder = new File(VIRUS_FOLDER);

        if (!folder.exists()) {
            return 0;
        }

        File[] virusFiles = folder.listFiles((dir, name) -> name.endsWith(".fasta"));
        return virusFiles != null ? virusFiles.length : 0;
    }
}