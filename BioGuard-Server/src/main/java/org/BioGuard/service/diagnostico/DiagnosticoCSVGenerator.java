package org.BioGuard.service.diagnostico;

import org.BioGuard.model.Diagnostico;

import java.io.*;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;

/**
 * Generador de archivos CSV para diagnósticos.
 *
 * <p>Responsabilidad Única: Crear archivos CSV con los resultados
 * de los diagnósticos, siguiendo el formato requerido:
 * virus,posicion_inicio,posicion_fin</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class DiagnosticoCSVGenerator {

    private static final String MUESTRAS_DIR = "data/muestras/";

    /**
     * Genera un archivo CSV con los hallazgos del diagnóstico.
     *
     * @param diagnostico Diagnóstico a exportar
     * @return Ruta del archivo CSV generado
     * @throws IOException Si hay error de escritura
     */
    public String generarCSV(Diagnostico diagnostico) throws IOException {
        // Crear carpeta del paciente
        Path pacienteDir = Paths.get(MUESTRAS_DIR, diagnostico.getDocumentoPaciente());
        Files.createDirectories(pacienteDir);

        // Extraer la parte de fecha del ID (formato: documento_yyyyMMdd_HHmmss)
        String[] partes = diagnostico.getId().split("_");
        String fechaStr = partes.length > 1 ? partes[1] : "";

        String nombreCSV = "diagnóstico_" + fechaStr + ".csv";
        Path csvPath = pacienteDir.resolve(nombreCSV);

        // Escribir CSV
        try (BufferedWriter writer = Files.newBufferedWriter(csvPath)) {
            // Escribir cabecera
            writer.write("virus,posicion_inicio,posicion_fin");
            writer.newLine();

            // Escribir cada hallazgo
            for (Diagnostico.HallazgoVirus hallazgo : diagnostico.getVirusDetectados()) {
                writer.write(hallazgo.toString());
                writer.newLine();
            }
        }

        return csvPath.toString();
    }

    /**
     * Genera un archivo CSV con formato alternativo (incluye fecha).
     *
     * @param diagnostico Diagnóstico a exportar
     * @return Ruta del archivo CSV generado
     * @throws IOException Si hay error de escritura
     */
    public String generarCSVConFecha(Diagnostico diagnostico) throws IOException {
        Path pacienteDir = Paths.get(MUESTRAS_DIR, diagnostico.getDocumentoPaciente());
        Files.createDirectories(pacienteDir);

        String fechaCSV = diagnostico.getFecha()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreCSV = "diagnóstico_" + fechaCSV + ".csv";
        Path csvPath = pacienteDir.resolve(nombreCSV);

        try (BufferedWriter writer = Files.newBufferedWriter(csvPath)) {
            writer.write("fecha_diagnostico," + diagnostico.getFecha());
            writer.newLine();
            writer.write("virus,posicion_inicio,posicion_fin");
            writer.newLine();

            for (Diagnostico.HallazgoVirus hallazgo : diagnostico.getVirusDetectados()) {
                writer.write(hallazgo.toString());
                writer.newLine();
            }
        }

        return csvPath.toString();
    }
}