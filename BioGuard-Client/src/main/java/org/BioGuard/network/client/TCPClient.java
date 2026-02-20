package org.BioGuard.network.client;

import org.BioGuard.network.protocol.IMessageProtocol;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Cliente TCP mejorado para múltiples mensajes.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class TCPClient implements ITCPClient {

    private final ClientConfig config;
    private final IMessageProtocol protocol;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private volatile boolean connected = false;

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
            System.out.println(" Conexión establecida");

        } catch (IOException e) {
            disconnect();
            throw new IOException("Error conectando al servidor: " + e.getMessage(), e);
        }
    }

    @Override
    public synchronized String sendMessage(String message) throws IOException {
        if (!connected || socket == null || socket.isClosed()) {
            throw new IllegalStateException("Cliente no está conectado");
        }

        if (message == null) {
            throw new IllegalArgumentException("El mensaje no puede ser null");
        }

        try {
            // Enviar mensaje
            protocol.encode(message, out);

            // Recibir respuesta
            String response = protocol.decode(in);

            return response;

        } catch (SocketTimeoutException e) {
            // Timeout en lectura - la conexión puede seguir viva
            throw new IOException("Timeout esperando respuesta del servidor", e);

        } catch (SocketException e) {
            // Error grave de socket - desconectar
            disconnect();
            throw new IOException("Error de conexión: " + e.getMessage(), e);

        } catch (IOException e) {
            // Otro error de E/S - desconectar para limpiar estado
            disconnect();
            throw e;
        }
    }

    @Override
    public synchronized void disconnect() {
        if (!connected) {
            return;
        }

        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}

        out = null;
        in = null;
        socket = null;
        connected = false;

        System.out.println("🔌 Conexión cerrada");
    }

    @Override
    public boolean isConnected() {
        if (!connected) return false;
        if (socket == null || socket.isClosed()) {
            connected = false;
            return false;
        }
        return true;
    }
}