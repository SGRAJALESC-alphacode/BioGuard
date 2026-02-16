package org.BioGuard.exceptions;

/*
 *  // Objetivo //
 *     Excepción lanzada cuando hay un error de validación en los datos del paciente.
 *  // Casos de Uso //
 *     - Cuando un paciente tiene un ID nulo o vacío.
 *     - Cuando un paciente tiene una edad inválida (negativa o mayor a 150 años).
 *     - Cuando un paciente tiene un email en formato incorrecto.
 *     - Cuando falta información requerida del paciente.
 *  // Atributos //
 *     fieldName : El nombre del campo que falló la validación.
 *     reason    : La razón específica del fallo de validación.
 */
public class InvalidPatientDataException extends BioGuardException {

    /*
     *  // Objetivo //
     *     Constructor que recibe el nombre del campo inválido.
     *  // Entradas //
     *     fieldName : String con el nombre del campo que falló.
     */
    public InvalidPatientDataException(String fieldName) {
        super("Datos inválidos en el campo: " + fieldName);
    }

    /*
     *  // Objetivo //
     *     Constructor con campo y razón de validación.
     *  // Entradas //
     *     fieldName : String con el nombre del campo.
     *     reason    : String explicando por qué es inválido.
     */
    public InvalidPatientDataException(String fieldName, String reason) {
        super("Datos inválidos en '" + fieldName + "': " + reason);
    }

    /*
     *  // Objetivo //
     *     Constructor con campo, razón y causa.
     *  // Entradas //
     *     fieldName : String con el nombre del campo.
     *     reason    : String explicando el error.
     *     cause     : Throwable que originó este error.
     */
    public InvalidPatientDataException(String fieldName, String reason, Throwable cause) {
        super("Datos inválidos en '" + fieldName + "': " + reason, cause);
    }
}

