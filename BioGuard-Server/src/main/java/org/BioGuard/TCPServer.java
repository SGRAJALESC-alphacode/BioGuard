package org.BioGuard;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;

public class TCPServer {

    private final int serverPort;

    public TCPServer(int port) {
        this.serverPort = port;
    }

    public void start() {
        try {
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(serverPort);
            serverSocket.setReuseAddress(true);

            System.out.println("==========================================");
            System.out.println("Servidor BioGuard iniciado (MODO SSL)");
            System.out.println("Puerto: " + serverPort);
            System.out.println("==========================================");

            while (true) {
                javax.net.ssl.SSLSocket clientSocket = (javax.net.ssl.SSLSocket) serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }
}