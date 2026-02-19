package org.BioGuard.io.reader;

import org.BioGuard.model.Patient;
import org.BioGuard.exception.FileReadException;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Lector de archivos FASTA que convierte a objetos Patient.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class FastaReader implements IFileReader<Patient> {

    private final FastaLineParser parser;

    public FastaReader(FastaLineParser parser) {
        this.parser = parser;
    }

    @Override
    public List<Patient> leer(String ruta) throws FileReadException {
        List<Patient> pacientes = new ArrayList<>();
        int numLinea = 0;

        try (BufferedReader reader = Files.newBufferedReader(Paths.get(ruta))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                numLinea++;
                if (linea.trim().isEmpty()) continue;

                Patient patient = parser.parsear(linea, numLinea);
                pacientes.add(patient);
            }

        } catch (NoSuchFileException e) {
            throw new FileReadException("Archivo no encontrado: " + ruta, ruta);
        } catch (IOException e) {
            throw new FileReadException("Error leyendo archivo: " + e.getMessage(), ruta);
        }

        return pacientes;
    }
}