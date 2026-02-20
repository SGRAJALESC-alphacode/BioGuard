package org.BioGuard.service.diagnostico;

import org.BioGuard.model.Diagnostico;

import java.io.*;
import java.nio.file.*;

/**
 * Generador de archivos CSV para diagnósticos.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class DiagnosticoCSVGenerator {

    private static final String MUESTRAS_DIR = "data/muestras/";

    /**
     * Genera un archivo CSV con los hallazgos del diagnóstico.
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