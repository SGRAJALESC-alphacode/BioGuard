package org.BioGuard.exceptions;

/*
 *  // Objetivo //
 *     Excepción lanzada cuando hay un error al acceder o manipular archivos del sistema.
 *  // Casos de Uso //
 *     - Cuando no se puede crear un archivo JSON de paciente.
 *     - Cuando no se puede leer un archivo FASTA.
 *     - Cuando no se puede escribir en un archivo de resultados.
 *     - Cuando hay permisos insuficientes para acceder a un archivo.
 *  // Atributos //
 *     filePath : La ruta del archivo que causó el error (almacenada en el mensaje).
 */
public class PatientFileException extends BioGuardException {

    /*
     *  // Objetivo //
     *     Constructor que recibe la ruta del archivo problemático.
     *  // Entradas //
     *     filePath : String con la ruta del archivo que causó el error.
     */
    public PatientFileException(String filePath) {
        super("Error al acceder al archivo: " + filePath);
    }

    /*
     *  // Objetivo //
     *     Constructor con ruta y operación que falló.
     *  // Entradas //
     *     filePath  : String con la ruta del archivo.
     *     operation : String describiendo la operación (crear, leer, actualizar, eliminar).
     */
    public PatientFileException(String filePath, String operation) {
        super("Error al " + operation + " archivo: " + filePath);
    }

    /*
     *  // Objetivo //
     *     Constructor con ruta, operación y causa.
     *  // Entradas //
     *     filePath  : String con la ruta del archivo.
     *     operation : String describiendo la operación.
     *     cause     : Throwable que originó este error.
     */
    public PatientFileException(String filePath, String operation, Throwable cause) {
        super("Error al " + operation + " archivo: " + filePath, cause);
    }
}

