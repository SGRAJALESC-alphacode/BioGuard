package org.BioGuard.service.reporte;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.model.Virus;
import org.BioGuard.service.IVirusService;
import org.BioGuard.service.diagnostico.IDiagnosticoService;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Generador de reporte de pacientes de alto riesgo.
 *
 * <p>Identifica pacientes con más de 3 virus altamente infecciosos
 * y genera un archivo CSV en la carpeta data/reportes/.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class AltoRiesgoReporter {

    private final IDiagnosticoService diagnosticoService;
    private final IVirusService virusService;

    // Ruta absoluta a la carpeta data en la raíz del proyecto BioGuard
    private static final String REPORTES_DIR = "C:/Users/jhona/OneDrive/Escritorio/Backend-2026/BioGuard/data/reportes/";

    /**
     * Constructor del reporter de alto riesgo.
     *
     * @param diagnosticoService Servicio de diagnósticos
     * @param virusService Servicio de virus
     */
    public AltoRiesgoReporter(IDiagnosticoService diagnosticoService, IVirusService virusService) {
        this.diagnosticoService = diagnosticoService;
        this.virusService = virusService;
        crearDirectorioReportes();
    }

    /**
     * Crea el directorio de reportes si no existe.
     */
    private void crearDirectorioReportes() {
        try {
            Files.createDirectories(Paths.get(REPORTES_DIR));
            System.out.println("[Reporter] Directorio de reportes: " + REPORTES_DIR);
        } catch (IOException e) {
            System.err.println("[Reporter] Error creando directorio de reportes: " + e.getMessage());
        }
    }

    /**
     * Genera el reporte de pacientes de alto riesgo.
     *
     * @return Ruta del archivo CSV generado
     * @throws IOException Si hay error de escritura
     */
    public String generarReporte() throws IOException {
        System.out.println("[Reporter] Generando reporte de alto riesgo...");

        // 1. Obtener todos los diagnósticos
        List<Diagnostico> todosDiagnosticos = diagnosticoService.listarTodos();
        System.out.println("[Reporter] Total diagnósticos procesados: " + todosDiagnosticos.size());

        // 2. Agrupar por paciente y procesar datos
        Map<String, ReporteData> datosPorPaciente = new HashMap<>();

        // Obtener mapa de niveles de virus (nombre -> nivel)
        Map<String, Integer> nivelesVirus = obtenerNivelesVirus();

        for (Diagnostico diag : todosDiagnosticos) {
            String documento = diag.getDocumentoPaciente();
            ReporteData data = datosPorPaciente.computeIfAbsent(documento, ReporteData::new);

            for (Diagnostico.HallazgoVirus hallazgo : diag.getVirusDetectados()) {
                Integer nivel = nivelesVirus.get(hallazgo.getNombreVirus());
                if (nivel != null) {
                    data.agregarVirus(hallazgo.getNombreVirus(), nivel);
                }
            }
        }

        // 3. Filtrar solo pacientes de alto riesgo (>3 virus altamente infecciosos)
        List<ReporteData> altoRiesgo = datosPorPaciente.values().stream()
                .filter(ReporteData::esAltoRiesgo)
                .toList();

        System.out.println("[Reporter] Pacientes de alto riesgo encontrados: " + altoRiesgo.size());

        // 4. Generar nombre de archivo con timestamp
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "pacientes_alto_riesgo_" + timestamp + ".csv";
        Path rutaArchivo = Paths.get(REPORTES_DIR, nombreArchivo);

        // 5. Escribir CSV
        try (BufferedWriter writer = Files.newBufferedWriter(rutaArchivo)) {
            writer.write("documento,total_virus,virus_altamente_infecciosos,virus_normales,virus_altamente_infecciosos_list");
            writer.newLine();

            for (ReporteData data : altoRiesgo) {
                writer.write(data.toString());
                writer.newLine();
            }
        }

        System.out.println("[Reporter] Reporte generado: " + rutaArchivo);
        return rutaArchivo.toString();
    }

    /**
     * Obtiene un mapa con nombre del virus y su nivel de peligrosidad.
     */
    private Map<String, Integer> obtenerNivelesVirus() {
        Map<String, Integer> niveles = new HashMap<>();
        for (Virus virus : virusService.listarTodos()) {
            niveles.put(virus.getNombre(), virus.getNivelPeligrosidad());
            System.out.println("[Reporter] Virus cargado: " + virus.getNombre() + " (nivel " + virus.getNivelPeligrosidad() + ")");
        }
        return niveles;
    }

    /**
     * Genera el reporte y retorna el contenido como String para enviar al cliente.
     *
     * @return Mensaje con la ruta del reporte o error
     */
    public String generarReporteComoString() {
        try {
            String ruta = generarReporte();
            return "REPORTE_ALTO_RIESGO: Archivo generado en " + ruta;
        } catch (IOException e) {
            System.err.println("[Reporter] Error: " + e.getMessage());
            return "ERROR: No se pudo generar el reporte - " + e.getMessage();
        }
    }
}