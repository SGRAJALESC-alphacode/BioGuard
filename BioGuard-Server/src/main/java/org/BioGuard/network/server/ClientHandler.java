package org.BioGuard.network.server;

import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.network.protocol.IMessageProtocol;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Manejador de clientes para el servidor TCP.
 * Permite múltiples mensajes por conexión.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final IMessageProtocol protocol;
    private final IMessageProcessor processor;
    private volatile boolean running = true;

    /**
     * Constructor del manejador de clientes.
     *
     * @param clientSocket Socket del cliente conectado
     * @param protocol Protocolo de comunicación
     * @param processor Procesador de mensajes
     */
    public ClientHandler(Socket clientSocket, IMessageProtocol protocol, IMessageProcessor processor) {
        this.clientSocket = clientSocket;
        this.protocol = protocol;
        this.processor = processor;
    }

    @Override
    public void run() {
        String clientAddress = clientSocket.getInetAddress().getHostAddress();
        int clientPort = clientSocket.getPort();
        String clientId = clientAddress + ":" + clientPort;

        System.out.println("[" + clientId + "] Cliente conectado");

        try {
            // Configurar timeout para no bloquear indefinidamente
            clientSocket.setSoTimeout(60000); // 60 segundos

            try (InputStream inputStream = new BufferedInputStream(clientSocket.getInputStream());
                 OutputStream outputStream = new BufferedOutputStream(clientSocket.getOutputStream())) {

                // Bucle para recibir múltiples mensajes del mismo cliente
                while (running && !clientSocket.isClosed()) {
                    try {
                        // Leer mensaje del cliente usando el protocolo
                        String clientMessage = protocol.decode(inputStream);

                        if (clientMessage == null || clientMessage.trim().isEmpty()) {
                            continue; // Mensaje vacío, seguir esperando
                        }

                        System.out.println("[" + clientId + "] Mensaje recibido: " + clientMessage);

                        // Procesar mensaje (lógica de negocio)
                        String response = processor.process(clientMessage);

                        // Enviar respuesta usando el protocolo
                        protocol.encode(response, outputStream);

                        System.out.println("[" + clientId + "] Respuesta enviada: " + response);

                    } catch (SocketTimeoutException e) {
                        // Timeout sin datos - el cliente sigue conectado pero inactivo
                        if (!clientSocket.isClosed()) {
                            System.out.println("[" + clientId + "] Timeout - esperando más mensajes...");
                        }
                        continue;

                    } catch (EOFException e) {
                        // Cliente cerró la conexión normalmente
                        System.out.println("[" + clientId + "] Cliente cerró conexión");
                        break;

                    } catch (SocketException e) {
                        // Error de socket (conexión rota)
                        if (!clientSocket.isClosed()) {
                            System.err.println("[" + clientId + "] Error de socket: " + e.getMessage());
                        }
                        break;

                    } catch (IOException e) {
                        // Otros errores de E/S
                        if (!clientSocket.isClosed()) {
                            System.err.println("[" + clientId + "] Error de E/S: " + e.getMessage());
                        }
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[" + clientId + "] Error configurando socket: " + e.getMessage());
        } finally {
            // Cerrar socket si está abierto
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // Ignorar error al cerrar
            }
            System.out.println("[" + clientId + "] Conexión cerrada");
        }
    }

    /**
     * Detiene el manejador del cliente.
     */
    public void stop() {
        running = false;
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        } catch (IOException e) {
            // Ignorar
        }
    }
}