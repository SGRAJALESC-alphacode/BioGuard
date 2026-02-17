package org.BioGuard;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;

public class TCPServer {
    private int serverPort;

    public TCPServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() {
        try {
            // REQUERIMIENTO: Uso obligatorio de SSL [cite: 28]
            SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslSocketFactory.createServerSocket(serverPort);
            System.out.println("[SERVIDOR] Escuchando en puerto " + serverPort + " con SSL...");

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                // REQUERIMIENTO: Concurrencia mediante Threads nativos [cite: 29]
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("[ERROR SERVIDOR] " + e.getMessage());
        }
    }
}