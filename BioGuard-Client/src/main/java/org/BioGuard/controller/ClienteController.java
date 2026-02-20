package org.BioGuard.controller;

import org.BioGuard.network.client.ClientConfig;
import org.BioGuard.network.client.SSLClient;
import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;

import java.io.IOException;

/**
 * Controlador que maneja la comunicación con el servidor.
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
     * Conecta al servidor.
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

            System.out.println("Conectando a " + SERVER_HOST + ":" + SERVER_PORT + "...");
            client.connect();
            conectado = true;
            System.out.println(" Conectado al servidor");
            return true;

        } catch (IOException e) {
            System.err.println(" Error de conexión: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envía un mensaje al servidor y retorna la respuesta.
     */
    public String enviarComando(String comando) {
        if (!conectado || client == null) {
            return "ERROR: No conectado al servidor";
        }

        try {
            System.out.println("> Enviando: " + comando);
            String respuesta = client.sendMessage(comando);
            return respuesta;
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
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

    public boolean isConectado() {
        return conectado;
    }
}