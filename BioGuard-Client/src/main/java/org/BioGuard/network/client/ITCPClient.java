package org.BioGuard.network.client;

import java.io.IOException;

/**
 * Interfaz que define el contrato para los clientes TCP.
 *
 * <p>Define las operaciones básicas que cualquier cliente de red debe implementar:
 * conectar, enviar mensajes, recibir respuestas y cerrar conexión.</p>
 *
 * <p>Sigue el principio de inversión de dependencias: las clases de alto nivel
 * dependen de esta abstracción, no de implementaciones concretas.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 */
public interface ITCPClient {

    /**
     * Establece la conexión con el servidor.
     *
     * @throws IOException Si no se puede conectar al servidor
     * @throws IllegalStateException Si el cliente ya está conectado
     */
    void connect() throws IOException;

    /**
     * Envía un mensaje al servidor y espera la respuesta.
     *
     * @param message Mensaje a enviar (no debe ser null ni vacío)
     * @return Respuesta del servidor
     * @throws IOException Si ocurre un error en la comunicación
     * @throws IllegalStateException Si el cliente no está conectado
     * @throws IllegalArgumentException Si el mensaje es inválido
     */
    String sendMessage(String message) throws IOException;

    /**
     * Cierra la conexión con el servidor.
     *
     * <p>Este método debe ser idempotente: llamarlo varias veces no debe causar errores.</p>
     */
    void disconnect();

    /**
     * Verifica si el cliente está actualmente conectado.
     *
     * @return true si está conectado, false en caso contrario
     */
    boolean isConnected();
}