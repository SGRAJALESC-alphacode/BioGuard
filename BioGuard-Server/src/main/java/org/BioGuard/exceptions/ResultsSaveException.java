package org.BioGuard.exceptions;

/*
 *  // Objetivo //
 *     Excepción lanzada cuando hay un error al procesar o guardar resultados del análisis.
 *  // Casos de Uso //
 *     - Cuando no se puede crear el archivo de resultados.
 *     - Cuando no se puede escribir en el archivo de resultados.
 *     - Cuando no existen permisos para crear carpetas de resultados.
 *     - Cuando hay error de I/O al guardar los resultados.
 *  // Atributos //
 *     patientId : El ID del paciente cuyos resultados no se pudieron guardar.
 */
public class ResultsSaveException extends BioGuardException {

    /*
     *  // Objetivo //
     *     Constructor con el ID del paciente.
     *  // Entradas //
     *     patientId : String con el identificador del paciente.
     */
    public ResultsSaveException(String patientId) {
        super("No se pueden guardar los resultados para el paciente: " + patientId);
    }

    /*
     *  // Objetivo //
     *     Constructor con ID del paciente y razón del error.
     *  // Entradas //
     *     patientId : String con el identificador del paciente.
     *     reason    : String explicando qué no se pudo guardar.
     */
    public ResultsSaveException(String patientId, String reason) {
        super("No se pueden guardar los resultados para el paciente '" + patientId + "': " + reason);
    }

    /*
     *  // Objetivo //
     *     Constructor con ID, razón y causa.
     *  // Entradas //
     *     patientId : String con el identificador del paciente.
     *     reason    : String explicando el error.
     *     cause     : Throwable que originó este error.
     */
    public ResultsSaveException(String patientId, String reason, Throwable cause) {
        super("No se pueden guardar los resultados para el paciente '" + patientId + "': " + reason, cause);
    }
}

