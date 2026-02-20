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
 * <p>Principios aplicados:</p>
 * <ul>
 *   <li><b>Inversión de Dependencias:</b> Las clases de alto nivel dependen
 *       de esta abstracción, no de implementaciones concretas</li>
 *   <li><b>Segregación de Interfaces:</b> Interfaz cohesiva con métodos
 *       específicos para codificación/decodificación</li>
 * </ul>
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
     * (por ejemplo, mediante una cabecera con longitud) y extraer su contenido.
     * Debe bloquearse hasta recibir el mensaje completo o hasta que ocurra
     * un error de conexión.</p>
     *
     * @param inputStream Flujo de entrada del cual leer el mensaje.
     *                    No debe ser null.
     * @return El mensaje decodificado como String.
     * @throws IOException Si ocurre un error de lectura, el formato es inválido,
     *                     o la conexión se cierra inesperadamente.
     * @throws IllegalArgumentException Si inputStream es null.
     */
    String decode(InputStream inputStream) throws IOException;

    /**
     * Codifica y escribe un mensaje en el flujo de salida.
     *
     * <p>El método debe agregar la información necesaria (longitud, delimitadores, etc.)
     * para que el receptor pueda reconstruir el mensaje original. Garantiza que
     * todos los datos se escriben antes de retornar.</p>
     *
     * @param message Mensaje a enviar. No debe ser null ni estar vacío.
     * @param outputStream Flujo de salida donde escribir el mensaje codificado.
     *                     No debe ser null.
     * @throws IOException Si ocurre un error de escritura.
     * @throws IllegalArgumentException Si message es null o vacío, o outputStream es null.
     */
    void encode(String message, OutputStream outputStream) throws IOException;
}