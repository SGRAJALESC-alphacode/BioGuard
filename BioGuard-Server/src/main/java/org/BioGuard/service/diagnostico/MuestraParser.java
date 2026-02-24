package org.BioGuard.service.diagnostico;

import org.BioGuard.model.Muestra;
import org.BioGuard.exception.FileReadException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Parser para archivos FASTA de muestras.
 *
 * <p>Responsabilidad Única: Leer archivos FASTA y convertirlos
 * en objetos Muestra.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MuestraParser {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * Parsea un archivo FASTA y reconstruye una muestra.
     *
     * @param rutaArchivo Ruta del archivo FASTA
     * @return Muestra reconstruida
     * @throws IOException Si hay error de lectura
     * @throws FileReadException Si el formato es inválido
     */
    public Muestra parsear(Path rutaArchivo) throws IOException, FileReadException {
        List<String> lineas = Files.readAllLines(rutaArchivo);

        if (lineas.isEmpty()) {
            throw new FileReadException("Archivo vacío: " + rutaArchivo);
        }

        // Primera línea: >documento|fecha
        String header = lineas.get(0).trim();
        if (!header.startsWith(">")) {
            throw new FileReadException("Formato FASTA inválido: falta '>'");
        }

        String headerSinMayor = header.substring(1);
        String[] partes = headerSinMayor.split("\\|", 2);

        if (partes.length < 2) {
            throw new FileReadException("Header FASTA inválido. Formato esperado: >documento|fecha");
        }

        String documento = partes[0].trim();
        String fechaStr = partes[1].trim();

        LocalDateTime fecha;
        try {
            fecha = LocalDateTime.parse(fechaStr, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new FileReadException("Fecha inválida en header: " + fechaStr);
        }

        // Concatenar líneas restantes como secuencia
        StringBuilder secuencia = new StringBuilder();
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (!linea.isEmpty()) {
                secuencia.append(linea);
            }
        }

        if (secuencia.length() == 0) {
            throw new FileReadException("Secuencia vacía en archivo: " + rutaArchivo);
        }

        // Crear ID a partir del documento y fecha
        String id = documento + "_" + fecha.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        Muestra muestra = new Muestra();
        muestra.setId(id);
        muestra.setDocumentoPaciente(documento);
        muestra.setFecha(fecha);
        muestra.setSecuencia(secuencia.toString());
        muestra.setArchivoPath(rutaArchivo.toString());

        return muestra;
    }
}