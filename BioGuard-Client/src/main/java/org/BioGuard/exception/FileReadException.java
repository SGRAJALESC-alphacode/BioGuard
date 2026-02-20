package org.BioGuard.exception;

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