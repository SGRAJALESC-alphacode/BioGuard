package org.BioGuard.service.diagnostico;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.model.Muestra;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Repositorio para gestionar la persistencia de diagnósticos y muestras.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class DiagnosticoRepository {

    private final Map<String, Diagnostico> diagnosticos = new ConcurrentHashMap<>();
    private static final String MUESTRAS_DIR = "data/muestras/";

    public DiagnosticoRepository() {
        crearDirectorioMuestras();
    }

    private void crearDirectorioMuestras() {
        try {
            Files.createDirectories(Paths.get(MUESTRAS_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio de muestras: " + e.getMessage());
        }
    }

    /**
     * Guarda un diagnóstico en memoria.
     */
    public void guardarDiagnostico(Diagnostico diagnostico) {
        diagnosticos.put(diagnostico.getId(), diagnostico);
    }

    /**
     * Busca un diagnóstico por ID.
     */
    public Optional<Diagnostico> buscarPorId(String id) {
        return Optional.ofNullable(diagnosticos.get(id));
    }

    /**
     * Busca diagnósticos por documento de paciente.
     */
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

    /**
     * Lista todos los diagnósticos.
     */
    public List<Diagnostico> listarTodos() {
        return new ArrayList<>(diagnosticos.values());
    }

    /**
     * Guarda una muestra en el sistema de archivos.
     */
    public Muestra guardarMuestra(Muestra muestra) throws IOException {
        Path pacienteDir = Paths.get(MUESTRAS_DIR, muestra.getDocumentoPaciente());
        Files.createDirectories(pacienteDir);

        String nombreArchivo = muestra.getNombreArchivo();
        Path archivoPath = pacienteDir.resolve(nombreArchivo);

        String contenido = ">" + muestra.getDocumentoPaciente() + "|" +
                muestra.getFecha().format(DateTimeFormatter.ISO_DATE_TIME) + "\n" +
                muestra.getSecuencia() + "\n";

        Files.writeString(archivoPath, contenido);
        muestra.setArchivoPath(archivoPath.toString());

        System.out.println("[Repository] Muestra guardada en: " + archivoPath);
        return muestra;
    }

    /**
     * Carga diagnósticos desde archivos CSV existentes.
     */
    public void cargarDiagnosticosDesdeArchivos() {
        Path muestrasDir = Paths.get(MUESTRAS_DIR);
        if (!Files.exists(muestrasDir)) {
            return;
        }

        System.out.println("[Repository] Cargando diagnósticos desde archivos...");
        int contador = 0;

        try (DirectoryStream<Path> pacientesDirs = Files.newDirectoryStream(muestrasDir, Files::isDirectory)) {
            for (Path pacienteDir : pacientesDirs) {
                String documento = pacienteDir.getFileName().toString();

                // Ignorar la carpeta "documento" si existe
                if (documento.equals("documento")) {
                    continue;
                }

                try (DirectoryStream<Path> archivos = Files.newDirectoryStream(pacienteDir, "*.csv")) {
                    for (Path csvPath : archivos) {
                        try {
                            Diagnostico diagnostico = parsearCSV(csvPath, documento);
                            if (diagnostico != null) {
                                diagnosticos.put(diagnostico.getId(), diagnostico);
                                contador++;
                                System.out.println("  Cargado: " + diagnostico.getId());
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

        System.out.println("[Repository] " + contador + " diagnósticos cargados");
    }

    /**
     * Parsea un archivo CSV para reconstruir un diagnóstico.
     */
    private Diagnostico parsearCSV(Path csvPath, String documento) {
        try {
            String nombreArchivo = csvPath.getFileName().toString();
            if (!nombreArchivo.startsWith("diagnóstico_") && !nombreArchivo.startsWith("diagnostico_")) {
                return null;
            }

            // Extraer fecha del nombre (formato yyyyMMdd_HHmmss)
            String fechaStr = nombreArchivo
                    .replace("diagnóstico_", "")
                    .replace("diagnostico_", "")
                    .replace(".csv", "");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
            LocalDateTime fecha = LocalDateTime.parse(fechaStr, formatter);

            // Crear ID basado en documento y fecha
            String id = documento + "_" + fechaStr;

            Diagnostico diagnostico = new Diagnostico();
            diagnostico.setId(id);
            diagnostico.setDocumentoPaciente(documento);
            diagnostico.setFecha(fecha);

            // Leer hallazgos del CSV
            List<String> lines = Files.readAllLines(csvPath);
            boolean primeraLinea = true;

            for (String line : lines) {
                if (primeraLinea) {
                    primeraLinea = false;
                    continue; // Saltar cabecera
                }

                line = line.trim();
                if (line.isEmpty()) continue;

                String[] partes = line.split(",");
                if (partes.length >= 3) {
                    String nombreVirus = partes[0];
                    int inicio = Integer.parseInt(partes[1]);
                    int fin = Integer.parseInt(partes[2]);

                    Diagnostico.HallazgoVirus hallazgo =
                            new Diagnostico.HallazgoVirus(nombreVirus, inicio, fin);
                    diagnostico.agregarHallazgo(hallazgo);
                }
            }

            return diagnostico;

        } catch (Exception e) {
            System.err.println("Error parseando CSV " + csvPath.getFileName() + ": " + e.getMessage());
            return null;
        }
    }
}