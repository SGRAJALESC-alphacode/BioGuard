package org.BioGuard.network.client;

import java.io.IOException;

/**
 * Interfaz que define el contrato para los clientes TCP del sistema BioGuard.
 *
 * <p>Esta interfaz establece las operaciones fundamentales que cualquier
 * cliente TCP debe implementar, siguiendo el principio de inversión de
 * dependencias. Las implementaciones concretas pueden variar (con o sin SSL,
 * diferentes timeouts, etc.) pero todas deben cumplir con este contrato.</p>
 *
 * <h2>Ciclo de vida típico:</h2>
 * <ol>
 *   <li>Crear instancia con configuración específica</li>
 *   <li>Invocar {@link #connect()} para establecer conexión</li>
 *   <li>Invocar {@link #sendMessage(String)} una o más veces</li>
 *   <li>Invocar {@link #disconnect()} para liberar recursos</li>
 * </ol>
 *
 * <h2>Ejemplo de uso:</h2>
 * <pre>
 * ITCPClient client = new TCPClient(config, protocol);
 * try {
 *     client.connect();
 *     String respuesta = client.sendMessage("HOLA");
 *     System.out.println("Respuesta: " + respuesta);
 * } finally {
 *     client.disconnect();
 * }
 * </pre>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 * @see TCPClient
 * @see ClientConfig
 */
public interface ITCPClient {

    /**
     * Establece la conexión con el servidor.
     *
     * <p>Este método debe ser invocado antes de cualquier intento de
     * enviar mensajes. Si la conexión ya está establecida, algunas
     * implementaciones pueden lanzar IllegalStateException o simplemente
     * ignorar la llamada.</p>
     *
     * @throws IOException Si no se puede establecer la conexión (servidor no disponible,
     *                     timeout, error de red, etc.)
     * @throws IllegalStateException Si el cliente ya está conectado (opcional)
     */
    void connect() throws IOException;

    /**
     * Envía un mensaje al servidor y espera la respuesta.
     *
     * <p>El método bloquea hasta recibir la respuesta completa o hasta
     * que ocurra un error. La serialización del mensaje depende del
     * protocolo configurado.</p>
     *
     * @param message Mensaje a enviar. No debe ser null ni estar vacío.
     * @return Respuesta del servidor como String.
     * @throws IOException Si ocurre un error en la comunicación (timeout,
     *                     conexión perdida, error de protocolo, etc.)
     * @throws IllegalStateException Si el cliente no está conectado.
     * @throws IllegalArgumentException Si el mensaje es inválido.
     */
    String sendMessage(String message) throws IOException;

    /**
     * Cierra la conexión con el servidor y libera todos los recursos.
     *
     * <p>Este método debe ser idempotente: invocarlo múltiples veces
     * no debe causar errores. Después de llamar a este método, el
     * cliente ya no puede enviar mensajes hasta una nueva conexión.</p>
     */
    void disconnect();

    /**
     * Verifica si el cliente está actualmente conectado al servidor.
     *
     * @return true si la conexión está activa y puede enviar mensajes,
     *         false en caso contrario.
     */
    boolean isConnected();
}