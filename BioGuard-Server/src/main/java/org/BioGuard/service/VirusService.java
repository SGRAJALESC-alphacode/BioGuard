package org.BioGuard.service;

import org.BioGuard.model.Virus;
import org.BioGuard.exception.FormatoFastaInvalidoException;
import org.BioGuard.exception.VirusNotFoundException;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de virus con persistencia en archivos FASTA.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class VirusService implements IVirusService {  // ← IMPLEMENTA LA INTERFAZ

    private static final String VIRUS_FOLDER = "data/virus/";

    public VirusService() {
        new File(VIRUS_FOLDER).mkdirs();
    }

    @Override
    public Virus registrarVirus(Virus virus) {
        try {
            guardarVirus(virus.getNombre(), virus.getNivel(), virus.getSecuencia());
            return virus;
        } catch (IOException | FormatoFastaInvalidoException e) {
            throw new RuntimeException("Error registrando virus: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Virus> buscarPorId(String id) {
        // Como los virus se guardan por nombre, ID es el nombre
        try {
            Virus virus = buscarVirusPorNombre(id);
            return Optional.ofNullable(virus);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Virus> buscarPorNombre(String nombre, boolean exacta) {
        try {
            if (exacta) {
                Virus virus = buscarVirusPorNombre(nombre);
                return virus != null ? List.of(virus) : Collections.emptyList();
            } else {
                // Búsqueda parcial
                return cargarTodosLosVirus().stream()
                        .filter(v -> v.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                        .collect(Collectors.toList());
            }
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public List<Virus> buscarPorNivelPeligrosidad(int nivel) {
        try {
            String nivelStr = nivelToString(nivel);
            return cargarTodosLosVirus().stream()
                    .filter(v -> v.getNivel().equals(nivelStr))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public Virus actualizarVirus(Virus virus) throws VirusNotFoundException {
        try {
            // Verificar si existe
            Virus existente = buscarVirusPorNombre(virus.getNombre());
            if (existente == null) {
                throw new VirusNotFoundException("nombre", virus.getNombre());
            }

            // Eliminar archivo viejo
            String nombreArchivo = virus.getNombre().replaceAll("[^a-zA-Z0-9]", "_") + ".fasta";
            Path ruta = Paths.get(VIRUS_FOLDER + nombreArchivo);
            Files.deleteIfExists(ruta);

            // Guardar nuevo
            guardarVirus(virus.getNombre(), virus.getNivel(), virus.getSecuencia());
            return virus;

        } catch (IOException | FormatoFastaInvalidoException e) {
            throw new RuntimeException("Error actualizando virus: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean eliminarVirus(String id) {
        try {
            String nombreArchivo = id.replaceAll("[^a-zA-Z0-9]", "_") + ".fasta";
            Path ruta = Paths.get(VIRUS_FOLDER + nombreArchivo);
            return Files.deleteIfExists(ruta);
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public List<Virus> listarTodos() {
        try {
            return cargarTodosLosVirus();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    // ===== MÉTODOS EXISTENTES (sin cambios) =====

    public void guardarVirus(String nombre, String nivel, String secuencia)
            throws FormatoFastaInvalidoException, IOException {
        // ... (tu código existente)
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

    public Virus buscarVirusPorNombre(String nombre) throws IOException {
        for (Virus v : cargarTodosLosVirus()) {
            if (v.getNombre().equalsIgnoreCase(nombre)) return v;
        }
        return null;
    }

    private String nivelToString(int nivel) {
        switch (nivel) {
            case 1: return "Poco Infeccioso";
            case 2: return "Normal";
            case 3: return "Altamente Infeccioso";
            default: return "Normal";
        }
    }
}