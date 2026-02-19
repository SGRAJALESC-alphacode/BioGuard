package org.BioGuard.io.reader;

import org.BioGuard.exception.FileReadException;
import java.util.List;

/**
 * Interfaz para lectores de archivos.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public interface IFileReader<T> {

    /**
     * Lee un archivo y lo convierte en una lista de objetos.
     *
     * @param ruta Ruta del archivo
     * @return Lista de objetos parseados
     * @throws FileReadException Si hay error de lectura
     */
    List<T> leer(String ruta) throws FileReadException;
}