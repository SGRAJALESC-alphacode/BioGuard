package org.BioGuard.network.client;

import org.BioGuard.network.protocol.IMessageProtocol;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Implementación de un cliente TCP para comunicación con el servidor BioGuard.
 *
 * <p>Esta clase proporciona una implementación del cliente usando sockets TCP estándar.
 * Gestiona la conexión, el envío y recepción de mensajes, y el manejo de errores
 * de red. Utiliza un protocolo de comunicación para la serialización de mensajes.</p>
 *
 * <p>Características:</p>
 * <ul>
 *   <li>Conexión TCP estándar (no SSL)</li>
 *   <li>Timeouts configurables para lectura</li>
 *   <li>Manejo de errores con desconexión automática</li>
 *   <li>Operaciones thread-safe mediante sincronización</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * ClientConfig config = new ClientConfig.Builder()
 *     .withHost("localhost")
 *     .withPort(8080)
 *     .build();
 * IMessageProtocol protocol = new LengthPrefixedProtocol();
 * TCPClient client = new TCPClient(config, protocol);
 *
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
 * @see ITCPClient
 * @see ClientConfig
 * @see IMessageProtocol
 */
public class TCPClient implements ITCPClient {

    private final ClientConfig config;
    private final IMessageProtocol protocol;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private volatile boolean connected = false;

    /**
     * Constructor del cliente TCP.
     *
     * @param config Configuración del cliente (host, puerto, timeouts)
     * @param protocol Protocolo para codificar/decodificar mensajes
     * @throws IllegalArgumentException Si algún parámetro es null
     */
    public TCPClient(ClientConfig config, IMessageProtocol protocol) {
        if (config == null) {
            throw new IllegalArgumentException("La configuración no puede ser null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("El protocolo no puede ser null");
        }

        this.config = config;
        this.protocol = protocol;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Establece la conexión TCP con el servidor configurado.
     * Configura los timeouts según la configuración proporcionada.</p>
     *
     * @throws IOException Si no se puede establecer la conexión
     */
    @Override
    public synchronized void connect() throws IOException {
        if (connected) {
            return;
        }

        try {
            System.out.println("Conectando a " + config.getServerHost() + ":" + config.getServerPort());

            socket = new Socket(config.getServerHost(), config.getServerPort());
            socket.setSoTimeout(config.getReadTimeoutMs());

            out = socket.getOutputStream();
            in = socket.getInputStream();

            connected = true;
            System.out.println("Conexión establecida");

        } catch (IOException e) {
            disconnect();
            throw new IOException("Error conectando al servidor: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Envía un mensaje al servidor y espera la respuesta.
     * El método es sincrónico y bloquea hasta recibir respuesta o timeout.</p>
     *
     * @param message Mensaje a enviar
     * @return Respuesta del servidor
     * @throws IOException Si hay error de comunicación
     * @throws IllegalStateException Si el cliente no está conectado
     */
    @Override
    public synchronized String sendMessage(String message) throws IOException {
        if (!connected || socket == null || socket.isClosed()) {
            throw new IllegalStateException("Cliente no está conectado");
        }

        if (message == null) {
            throw new IllegalArgumentException("El mensaje no puede ser null");
        }

        try {
            // Enviar mensaje usando el protocolo
            protocol.encode(message, out);

            // Recibir respuesta
            String response = protocol.decode(in);

            return response;

        } catch (SocketTimeoutException e) {
            disconnect();
            throw new IOException("Timeout esperando respuesta del servidor", e);
        } catch (SocketException e) {
            disconnect();
            throw new IOException("Error de conexión: " + e.getMessage(), e);
        } catch (IOException e) {
            disconnect();
            throw new IOException("Error en comunicación: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>Cierra la conexión y libera todos los recursos asociados.
     * Es seguro llamar a este método múltiples veces.</p>
     */
    @Override
    public synchronized void disconnect() {
        if (!connected) {
            return;
        }

        // Cerrar streams
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (in != null) in.close(); } catch (IOException ignored) {}

        // Cerrar socket
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}

        // Liberar recursos
        out = null;
        in = null;
        socket = null;
        connected = false;

        System.out.println("Conexión cerrada");
    }

    /**
     * {@inheritDoc}
     *
     * <p>Verifica si el cliente está actualmente conectado y el socket es válido.</p>
     *
     * @return true si está conectado, false en caso contrario
     */
    @Override
    public boolean isConnected() {
        if (!connected) return false;
        if (socket == null || socket.isClosed()) {
            connected = false;
            return false;
        }
        return true;
    }

    /**
     * Obtiene la configuración actual del cliente.
     *
     * @return Configuración del cliente
     */
    public ClientConfig getConfig() {
        return config;
    }

    /**
     * Obtiene la dirección del servidor al que está conectado.
     *
     * @return Dirección del servidor o null si no está conectado
     */
    public String getServerAddress() {
        if (socket != null && socket.isConnected()) {
            return socket.getInetAddress().getHostAddress();
        }
        return null;
    }

    /**
     * Obtiene el puerto local usado por la conexión.
     *
     * @return Puerto local o -1 si no está conectado
     */
    public int getLocalPort() {
        if (socket != null && socket.isConnected()) {
            return socket.getLocalPort();
        }
        return -1;
    }
}