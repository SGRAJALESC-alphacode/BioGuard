package org.BioGuard.controller;

import org.BioGuard.network.client.ClientConfig;
import org.BioGuard.network.client.SSLClient;
import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;

import java.io.IOException;

/**
 * Controlador de comunicación con el servidor.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class ClienteController {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8443;

    private SSLClient client;
    private boolean conectado = false;

    /**
     * Establece la conexión con el servidor.
     *
     * @return true si la conexión fue exitosa
     */
    public boolean conectar() {
        try {
            ClientConfig config = new ClientConfig.Builder()
                    .withHost(SERVER_HOST)
                    .withPort(SERVER_PORT)
                    .withSSL(true)
                    .build();

            IMessageProtocol protocol = new LengthPrefixedProtocol();
            client = new SSLClient(config, protocol);

            System.out.print("Conectando a " + SERVER_HOST + ":" + SERVER_PORT + "... ");
            client.connect();
            conectado = true;
            System.out.println("Conectado.");
            return true;

        } catch (IOException e) {
            System.err.println("\nError de conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envía un comando al servidor y retorna la respuesta.
     *
     * @param comando Comando a enviar
     * @return Respuesta del servidor
     * @throws IOException Si hay error de comunicación
     */
    public String enviarComando(String comando) throws IOException {
        if (!conectado || client == null) {
            throw new IOException("No conectado al servidor");
        }
        return client.sendMessage(comando);
    }

    /**
     * Desconecta del servidor.
     */
    public void desconectar() {
        if (client != null) {
            client.disconnect();
            conectado = false;
        }
    }

    /**
     * Verifica si está conectado.
     *
     * @return true si está conectado
     */
    public boolean isConectado() {
        return conectado;
    }
}