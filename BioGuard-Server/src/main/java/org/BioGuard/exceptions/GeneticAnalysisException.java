package org.BioGuard.exceptions;

/*
 *  // Objetivo //
 *     Excepción lanzada cuando hay un error en el análisis genético del paciente.
 *  // Casos de Uso //
 *     - Cuando no se puede encontrar el archivo FASTA del paciente.
 *     - Cuando el archivo FASTA está corrupto o en formato inválido.
 *     - Cuando no se encuentran los archivos de enfermedades de referencia.
 *     - Cuando la secuencia genética es inválida.
 *  // Atributos //
 *     patientId : El ID del paciente cuyo análisis falló.
 *     reason    : La razón específica del fallo en el análisis.
 */
public class GeneticAnalysisException extends BioGuardException {

    /*
     *  // Objetivo //
     *     Constructor con el ID del paciente que falló en análisis.
     *  // Entradas //
     *     patientId : String con el identificador del paciente.
     */
    public GeneticAnalysisException(String patientId) {
        super("Error en el análisis genético del paciente: " + patientId);
    }

    /*
     *  // Objetivo //
     *     Constructor con ID del paciente y razón del error.
     *  // Entradas //
     *     patientId : String con el identificador del paciente.
     *     reason    : String describiendo qué falló.
     */
    public GeneticAnalysisException(String patientId, String reason) {
        super("Error en el análisis genético del paciente '" + patientId + "': " + reason);
    }

    /*
     *  // Objetivo //
     *     Constructor con ID, razón y causa.
     *  // Entradas //
     *     patientId : String con el identificador del paciente.
     *     reason    : String describiendo el error.
     *     cause     : Throwable que originó este error.
     */
    public GeneticAnalysisException(String patientId, String reason, Throwable cause) {
        super("Error en el análisis genético del paciente '" + patientId + "': " + reason, cause);
    }
}

