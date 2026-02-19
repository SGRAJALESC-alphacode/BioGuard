package org.BioGuard.exception;

/**
 * Excepción para errores de lectura de archivos.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class FileReadException extends Exception {

    private final String archivo;

    public FileReadException(String mensaje) {
        super(mensaje);
        this.archivo = "desconocido";
    }

    public FileReadException(String mensaje, String archivo) {
        super(String.format("%s (archivo: %s)", mensaje, archivo));
        this.archivo = archivo;
    }

    public FileReadException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        this.archivo = "desconocido";
    }

    public String getArchivo() {
        return archivo;
    }
}