package org.BioGuard.exceptions;

/*
 *  // Objetivo //
 *     Excepción lanzada cuando hay un error en la comunicación TCP/SSL con el cliente.
 *  // Casos de Uso //
 *     - Cuando hay error al aceptar una conexión cliente.
 *     - Cuando hay error al leer datos del cliente.
 *     - Cuando hay error al enviar respuesta al cliente.
 *     - Cuando hay timeout en la comunicación.
 *  // Atributos //
 *     clientInfo : Información sobre el cliente (dirección IP, puerto, etc).
 */
public class ServerCommunicationException extends BioGuardException {

    /*
     *  // Objetivo //
     *     Constructor con información general del error.
     *  // Entradas //
     *     message : String describiendo el error de comunicación.
     */
    public ServerCommunicationException(String message) {
        super("Error en comunicación del servidor: " + message);
    }

    /*
     *  // Objetivo //
     *     Constructor con información del cliente y razón del error.
     *  // Entradas //
     *     clientInfo : String con información del cliente (IP, puerto).
     *     reason     : String explicando el error.
     */
    public ServerCommunicationException(String clientInfo, String reason) {
        super("Error en comunicación con cliente '" + clientInfo + "': " + reason);
    }

    /*
     *  // Objetivo //
     *     Constructor con información, razón y causa.
     *  // Entradas //
     *     clientInfo : String con información del cliente.
     *     reason     : String explicando el error.
     *     cause      : Throwable que originó este error.
     */
    public ServerCommunicationException(String clientInfo, String reason, Throwable cause) {
        super("Error en comunicación con cliente '" + clientInfo + "': " + reason, cause);
    }
}

