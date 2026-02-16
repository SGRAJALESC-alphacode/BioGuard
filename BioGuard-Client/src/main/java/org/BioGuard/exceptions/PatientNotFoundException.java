package org.BioGuard.exceptions;

/*
 *  // Objetivo //
 *     Excepción lanzada cuando no se encuentra un paciente en el sistema.
 *  // Casos de Uso //
 *     - Cuando se intenta leer un paciente con un ID que no existe.
 *     - Cuando se intenta actualizar un paciente que fue eliminado.
 *     - Cuando se intenta enviar un paciente inexistente al servidor.
 *  // Atributos //
 *     patientId : El ID del paciente que no fue encontrado (almacenado en el mensaje).
 */
public class PatientNotFoundException extends BioGuardException {

    /*
     *  // Objetivo //
     *     Constructor que recibe el ID del paciente no encontrado.
     *  // Entradas //
     *     patientId : String con el identificador del paciente buscado.
     */
    public PatientNotFoundException(String patientId) {
        super("Paciente con ID '" + patientId + "' no encontrado en el sistema.");
    }

    /*
     *  // Objetivo //
     *     Constructor que recibe el ID y un mensaje adicional personalizado.
     *  // Entradas //
     *     patientId : String con el identificador del paciente.
     *     message   : String con mensaje adicional de contexto.
     */
    public PatientNotFoundException(String patientId, String message) {
        super("Paciente con ID '" + patientId + "' no encontrado. " + message);
    }

    /*
     *  // Objetivo //
     *     Constructor que recibe el ID y una causa (excepción anterior).
     *  // Entradas //
     *     patientId : String con el identificador del paciente.
     *     cause     : Throwable que originó este error.
     */
    public PatientNotFoundException(String patientId, Throwable cause) {
        super("Paciente con ID '" + patientId + "' no encontrado en el sistema.", cause);
    }
}

