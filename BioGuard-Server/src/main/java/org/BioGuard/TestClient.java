package org.BioGuard;

import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;

import java.io.*;
import java.net.Socket;

/**
 * Cliente de prueba simple para verificar la comunicación con el servidor.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class TestClient {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        IMessageProtocol protocol = new LengthPrefixedProtocol();

        System.out.println("=== CLIENTE DE PRUEBA ===");
        System.out.println("Conectando a " + SERVER_HOST + ":" + SERVER_PORT + "...");

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            System.out.println("✅ Conectado al servidor");

            // Mensaje de prueba
            String mensaje = "HOLA SERVIDOR, SOY UN CLIENTE DE PRUEBA";
            System.out.println("📤 Enviando: " + mensaje);

            // Enviar mensaje usando el protocolo
            protocol.encode(mensaje, out);

            // Recibir respuesta
            String respuesta = protocol.decode(in);
            System.out.println("📥 Respuesta: " + respuesta);

            System.out.println("✅ Prueba exitosa");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}