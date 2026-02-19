package org.BioGuard;

import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;

import java.io.*;
import java.net.Socket;

/**
 * Cliente de prueba que envía múltiples mensajes al servidor.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class TestClientMultiple {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    private static final int NUM_MENSAJES = 5;

    public static void main(String[] args) {
        IMessageProtocol protocol = new LengthPrefixedProtocol();

        System.out.println("=== CLIENTE DE PRUEBA MÚLTIPLE ===");
        System.out.println("Conectando a " + SERVER_HOST + ":" + SERVER_PORT + "...");

        try (Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
             OutputStream out = socket.getOutputStream();
             InputStream in = socket.getInputStream()) {

            System.out.println("✅ Conectado al servidor\n");

            for (int i = 1; i <= NUM_MENSAJES; i++) {
                String mensaje = "MENSAJE DE PRUEBA #" + i;
                System.out.println("📤 [" + i + "] Enviando: " + mensaje);

                protocol.encode(mensaje, out);

                String respuesta = protocol.decode(in);
                System.out.println("📥 [" + i + "] Respuesta: " + respuesta);
                System.out.println();

                // Pequeña pausa entre mensajes
                Thread.sleep(500);
            }

            System.out.println("✅ Prueba múltiple exitosa");

        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}