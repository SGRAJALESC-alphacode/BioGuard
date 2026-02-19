package org.BioGuard;

import org.BioGuard.network.client.ClientConfig;
import org.BioGuard.network.client.TCPClient;
import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;

import java.io.IOException;

/**
 * Clase principal del cliente BioGuard.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class Main {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("=== CLIENTE BioGuard ===");

        // 1. Configuración
        ClientConfig config = new ClientConfig.Builder()
                .withHost(SERVER_HOST)
                .withPort(SERVER_PORT)
                .withConnectionTimeoutMs(5000)
                .build();

        // 2. Protocolo
        IMessageProtocol protocol = new LengthPrefixedProtocol();

        // 3. Cliente
        TCPClient client = new TCPClient(config, protocol);

        try {
            // 4. Conectar
            client.connect();

            // 5. Enviar mensajes
            String respuesta1 = client.sendMessage("HOLA SERVIDOR");
            System.out.println("Respuesta 1: " + respuesta1);

            String respuesta2 = client.sendMessage("MENSAJE DE PRUEBA");
            System.out.println("Respuesta 2: " + respuesta2);

        } catch (IOException e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 6. Desconectar
            client.disconnect();
        }
    }
}