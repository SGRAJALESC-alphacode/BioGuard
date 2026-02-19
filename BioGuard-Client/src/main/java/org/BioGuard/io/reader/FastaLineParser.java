package org.BioGuard.io.reader;

import org.BioGuard.model.Patient;
import org.BioGuard.exception.FileReadException;

/**
 * Parsea líneas de archivo FASTA a objetos Patient.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class FastaLineParser {

    /**
     * Parsea una línea FASTA a Patient.
     * Formato esperado: >id|nombre|edad|genero|telefono
     *
     * @param linea Línea a parsear
     * @param numLinea Número de línea (para errores)
     * @return Patient parseado
     * @throws FileReadException Si el formato es inválido
     */
    public Patient parsear(String linea, int numLinea) throws FileReadException {
        if (linea == null || linea.trim().isEmpty()) {
            throw new FileReadException("Línea vacía", "línea " + numLinea);
        }

        if (!linea.startsWith(">")) {
            throw new FileReadException("Línea no comienza con '>'", "línea " + numLinea);
        }

        String sinMayor = linea.substring(1);
        String[] partes = sinMayor.split("\\|");

        if (partes.length < 5) {
            throw new FileReadException(
                    "Formato inválido. Se esperaban 5 campos separados por |",
                    "línea " + numLinea
            );
        }

        try {
            String id = partes[0].trim();
            String nombre = partes[1].trim();
            int edad = Integer.parseInt(partes[2].trim());
            String genero = partes[3].trim();
            String telefono = partes[4].trim();

            return new Patient(id, nombre, edad, genero, telefono);

        } catch (NumberFormatException e) {
            throw new FileReadException("Edad debe ser un número", "línea " + numLinea);
        }
    }
}