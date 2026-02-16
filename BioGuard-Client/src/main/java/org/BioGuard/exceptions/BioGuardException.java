package org.BioGuard.exceptions;

/*
 *  // Objetivo //
 *     Clase base para todas las excepciones personalizadas del sistema BioGuard.
 *     Proporciona una excepción genérica que puede ser extendida para casos específicos.
 *  // Atributos //
 *     Hereda de Exception y mantiene los mensajes de error estándar de Java.
 *  // Uso //
 *     Sirve como clase padre para todas las excepciones del dominio de BioGuard,
 *     permitiendo una captura específica de errores relacionados con el sistema.
 */
public class BioGuardException extends Exception {

    /*
     *  // Objetivo //
     *     Constructor que recibe un mensaje de error descriptivo.
     *  // Entradas //
     *     message : String con la descripción del error ocurrido.
     */
    public BioGuardException(String message) {
        super(message);
    }

    /*
     *  // Objetivo //
     *     Constructor que recibe un mensaje de error y una causa (excepción anterior).
     *  // Entradas //
     *     message : String con la descripción del error.
     *     cause   : Throwable que originó este error (para encadenamiento).
     */
    public BioGuardException(String message, Throwable cause) {
        super(message, cause);
    }
}

