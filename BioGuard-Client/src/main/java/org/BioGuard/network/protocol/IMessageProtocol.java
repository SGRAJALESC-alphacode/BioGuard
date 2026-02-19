package org.BioGuard.network.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interfaz que define el contrato para los protocolos de comunicación
 * entre cliente y servidor. Permite la serialización y deserialización
 * de mensajes de forma independiente al transporte subyacente.
 *
 * <p>Los protocolos concretos deben implementar esta interfaz garantizando
 * que el emisor y receptor puedan interpretar los mensajes correctamente.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 */
public interface IMessageProtocol {

    /**
     * Lee y decodifica un mensaje desde el flujo de entrada.
     *
     * <p>El método debe ser capaz de identificar los límites del mensaje
     * (por ejemplo, mediante una cabecera con longitud) y extraer su contenido.</p>
     *
     * @param inputStream Flujo de entrada del cual leer el mensaje (no debe ser null)
     * @return El mensaje decodificado como String
     * @throws IOException Si ocurre un error de lectura o el formato es inválido
     * @throws IllegalArgumentException Si el InputStream es null
     */
    String decode(InputStream inputStream) throws IOException;

    /**
     * Codifica y escribe un mensaje en el flujo de salida.
     *
     * <p>El método debe agregar la información necesaria (longitud, delimitadores, etc.)
     * para que el receptor pueda reconstruir el mensaje original.</p>
     *
     * @param message Mensaje a enviar (no debe ser null ni vacío)
     * @param outputStream Flujo de salida donde escribir el mensaje codificado
     * @throws IOException Si ocurre un error de escritura
     * @throws IllegalArgumentException Si el mensaje es null o vacío, o el OutputStream es null
     */
    void encode(String message, OutputStream outputStream) throws IOException;
}