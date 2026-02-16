package org.BioGuard.exceptions;

/*
 *  // Objetivo //
 *     Excepción lanzada cuando hay un error al leer un archivo FASTA.
 *  // Casos de Uso //
 *     - Cuando no se encuentra el archivo FASTA especificado.
 *     - Cuando el archivo FASTA está corrupto o no es legible.
 *     - Cuando hay permisos insuficientes para acceder al archivo.
 *     - Cuando hay error de I/O al procesar el archivo.
 *  // Atributos //
 *     fastaPath : La ruta del archivo FASTA que causó el error.
 */
public class FastaFileException extends BioGuardException {

    /*
     *  // Objetivo //
     *     Constructor que recibe la ruta del archivo FASTA problemático.
     *  // Entradas //
     *     fastaPath : String con la ruta del archivo FASTA.
     */
    public FastaFileException(String fastaPath) {
        super("Error al leer archivo FASTA: " + fastaPath);
    }

    /*
     *  // Objetivo //
     *     Constructor con ruta y tipo de error.
     *  // Entradas //
     *     fastaPath : String con la ruta del archivo FASTA.
     *     errorType : String describiendo el tipo de error (no encontrado, corrupto, etc).
     */
    public FastaFileException(String fastaPath, String errorType) {
        super("Error al leer archivo FASTA '" + fastaPath + "': " + errorType);
    }

    /*
     *  // Objetivo //
     *     Constructor con ruta, error y causa.
     *  // Entradas //
     *     fastaPath : String con la ruta del archivo FASTA.
     *     errorType : String describiendo el error.
     *     cause     : Throwable que originó este error.
     */
    public FastaFileException(String fastaPath, String errorType, Throwable cause) {
        super("Error al leer archivo FASTA '" + fastaPath + "': " + errorType, cause);
    }
}

