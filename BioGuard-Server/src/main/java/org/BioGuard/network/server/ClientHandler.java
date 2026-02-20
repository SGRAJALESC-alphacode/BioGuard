package org.BioGuard.network.server;

import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.network.protocol.IMessageProtocol;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final IMessageProtocol protocol;
    private final IMessageProcessor processor;
    private volatile boolean running = true;

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
            clientSocket.setSoTimeout(60000);

            try (InputStream inputStream = clientSocket.getInputStream();
                 OutputStream outputStream = clientSocket.getOutputStream()) {

                while (running && !clientSocket.isClosed()) {
                    try {
                        String clientMessage = protocol.decode(inputStream);

                        if (clientMessage == null || clientMessage.trim().isEmpty()) {
                            continue;
                        }

                        System.out.println("[" + clientId + "] Mensaje recibido: " + clientMessage);  // ← TEXTO PLANO

                        String response = processor.process(clientMessage);

                        protocol.encode(response, outputStream);
                        System.out.println("[" + clientId + "] Respuesta enviada: " + response);

                    } catch (SocketTimeoutException e) {
                        if (!clientSocket.isClosed()) {
                            System.out.println("[" + clientId + "] Timeout - esperando...");
                        }
                        continue;

                    } catch (EOFException e) {
                        System.out.println("[" + clientId + "] Cliente cerró conexión");
                        break;

                    } catch (SocketException e) {
                        if (!clientSocket.isClosed()) {
                            System.err.println("[" + clientId + "] Error de socket: " + e.getMessage());
                        }
                        break;

                    } catch (IOException e) {
                        if (!clientSocket.isClosed()) {
                            System.err.println("[" + clientId + "] Error de E/S: " + e.getMessage());
                        }
                        break;
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("[" + clientId + "] Error: " + e.getMessage());
        } finally {
            try { clientSocket.close(); } catch (IOException ignored) {}
            System.out.println("[" + clientId + "] Conexión cerrada");
        }
    }

    public void stop() {
        running = false;
        try { clientSocket.close(); } catch (IOException ignored) {}
    }
}