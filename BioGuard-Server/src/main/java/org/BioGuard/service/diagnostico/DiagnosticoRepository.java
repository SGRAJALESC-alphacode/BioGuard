package org.BioGuard.service.diagnostico;

import org.BioGuard.model.Diagnostico;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Repositorio para diagnósticos.
 *
 * <p>Responsabilidad Única: Gestionar la persistencia de diagnósticos
 * en memoria y en archivos CSV.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class DiagnosticoRepository {

    private final Map<String, Diagnostico> diagnosticos = new ConcurrentHashMap<>();
    private static final String MUESTRAS_DIR = "data/muestras/";

    public void guardar(Diagnostico diagnostico) {
        diagnosticos.put(diagnostico.getId(), diagnostico);
    }

    public Optional<Diagnostico> buscarPorId(String id) {
        return Optional.ofNullable(diagnosticos.get(id));
    }

    public List<Diagnostico> buscarPorPaciente(String documento) {
        List<Diagnostico> resultados = new ArrayList<>();
        for (Diagnostico d : diagnosticos.values()) {
            if (documento.equals(d.getDocumentoPaciente())) {
                resultados.add(d);
            }
        }
        resultados.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));
        return resultados;
    }

    public List<Diagnostico> listarTodos() {
        return new ArrayList<>(diagnosticos.values());
    }

    public void cargarDiagnosticosDesdeArchivos() {
        Path muestrasDir = Paths.get(MUESTRAS_DIR);
        if (!Files.exists(muestrasDir)) return;

        System.out.println("[DiagnosticoRepository] Cargando diagnósticos...");
        int contador = 0;

        try (Stream<Path> pacientesDirs = Files.list(muestrasDir)) {
            for (Path pacienteDir : pacientesDirs.filter(Files::isDirectory).toList()) {
                String documento = pacienteDir.getFileName().toString();

                try (DirectoryStream<Path> archivos = Files.newDirectoryStream(pacienteDir, "*.csv")) {
                    for (Path csvPath : archivos) {
                        try {
                            Diagnostico diagnostico = parsearCSV(csvPath, documento);
                            if (diagnostico != null) {
                                diagnosticos.put(diagnostico.getId(), diagnostico);
                                contador++;
                            }
                        } catch (Exception e) {
                            System.err.println("  Error cargando " + csvPath.getFileName() + ": " + e.getMessage());
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error cargando diagnósticos: " + e.getMessage());
        }

        System.out.println("[DiagnosticoRepository] " + contador + " diagnósticos cargados");
    }

    private Diagnostico parsearCSV(Path csvPath, String documento) {
        try {
            String nombre = csvPath.getFileName().toString();
            if (!nombre.startsWith("diagnóstico_") && !nombre.startsWith("diagnostico_")) {
                return null;
            }

            String fechaStr = nombre.replace("diagnóstico_", "")
                    .replace("diagnostico_", "")
                    .replace(".csv", "");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime fecha = LocalDateTime.parse(fechaStr, formatter);

            String id = documento + "_" + fechaStr;

            Diagnostico diagnostico = new Diagnostico(documento, id);
            diagnostico.setFecha(fecha);

            List<String> lines = Files.readAllLines(csvPath);
            boolean primera = true;

            for (String line : lines) {
                if (primera) { primera = false; continue; }
                if (line.trim().isEmpty()) continue;

                String[] partes = line.split(",");
                if (partes.length >= 3) {
                    Diagnostico.HallazgoVirus h = new Diagnostico.HallazgoVirus(
                            partes[0], Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
                    diagnostico.agregarHallazgo(h);
                }
            }

            return diagnostico;

        } catch (Exception e) {
            System.err.println("Error parseando CSV: " + e.getMessage());
            return null;
        }
    }
}