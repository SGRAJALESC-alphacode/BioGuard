package org.BioGuard.network.client;

import org.BioGuard.network.protocol.IMessageProtocol;

import java.io.*;
import java.net.Socket;

/**
 * Implementación de cliente TCP.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class TCPClient {

    private final ClientConfig config;
    private final IMessageProtocol protocol;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private boolean connected = false;

    public TCPClient(ClientConfig config, IMessageProtocol protocol) {
        this.config = config;
        this.protocol = protocol;
    }

    public void connect() throws IOException {
        if (connected) {
            return;
        }

        System.out.println("Conectando a " + config.getServerHost() + ":" + config.getServerPort());
        socket = new Socket(config.getServerHost(), config.getServerPort());
        out = socket.getOutputStream();
        in = socket.getInputStream();
        connected = true;
        System.out.println("✅ Conectado");
    }

    public String sendMessage(String message) throws IOException {
        if (!connected) {
            throw new IllegalStateException("No conectado");
        }

        System.out.println("📤 Enviando: " + message);
        protocol.encode(message, out);

        String response = protocol.decode(in);
        System.out.println("📥 Respuesta: " + response);

        return response;
    }

    public void disconnect() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando: " + e.getMessage());
        }
        connected = false;
        System.out.println("🔌 Desconectado");
    }

    public boolean isConnected() {
        return connected && socket != null && socket.isConnected();
    }
}