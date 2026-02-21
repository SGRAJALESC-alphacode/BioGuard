package org.BioGuard.network.protocol;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interfaz que define el contrato para los protocolos de comunicación
 * entre cliente y servidor en el sistema BioGuard.
 *
 * <p>Esta interfaz permite que la capa de red sea independiente del formato
 * específico de los mensajes. Diferentes implementaciones pueden usar
 * distintos formatos (longitud fija, delimitadores, etc.) sin afectar
 * las capas superiores.</p>
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
     * @param inputStream Flujo de entrada del cual leer el mensaje
     * @return El mensaje decodificado como String
     * @throws IOException Si ocurre un error de lectura
     * @throws IllegalArgumentException Si el InputStream es null
     */
    String decode(InputStream inputStream) throws IOException;

    /**
     * Codifica y escribe un mensaje en el flujo de salida.
     *
     * @param message Mensaje a enviar
     * @param outputStream Flujo de salida donde escribir
     * @throws IOException Si ocurre un error de escritura
     * @throws IllegalArgumentException Si message es null o vacío
     */
    void encode(String message, OutputStream outputStream) throws IOException;
}