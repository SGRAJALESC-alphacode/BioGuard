package org.BioGuard;

/*
 * // Objetivo
 *    Escuchar conexiones entrantes TLS/SSL en un puerto configurado y delegar
 *    cada conexión a un `ClientHandler` para procesar comandos. Gestiona un
 *    pool de hilos para concurrencia.
 *
 * // Atributos
 *    serverPort : Puerto en el que escucha el servidor (int)
 *    threadPool : ExecutorService para manejar concurrencia
 *
 * // Comportamiento
 *    start() : Inicializa el SSLServerSocket, imprime info de estado y entra
 *              en un loop de aceptación; por cada cliente crea un ClientHandler
 *              y lo envía al pool de hilos.
 */

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TCPServer {
    private final int serverPort;
    private final ExecutorService threadPool;

    public TCPServer(int serverPort) {
        this.serverPort = serverPort;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        try {
            // REQUERIMIENTO: Uso obligatorio de SSL
            SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            try (SSLServerSocket serverSocket = (SSLServerSocket) sslSocketFactory.createServerSocket(serverPort)) {

                System.out.println("========================================");
                System.out.println("[SERVIDOR BIOGUARD]");
                System.out.println("[ESTADO] ACTIVO");
                System.out.println("[PUERTO] " + serverPort + " (SSL/TLS)");
                System.out.println("[HILOS] Pool de hilos activo");
                System.out.println("========================================");

                while (true) {
                    SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                    System.out.println("[CONEXIÓN] Cliente conectado: " + clientSocket.getInetAddress());

                    // Usar pool de hilos para mejor concurrencia
                    threadPool.execute(new ClientHandler(clientSocket));
                }
            }

        } catch (IOException e) {
            System.err.println("[ERROR SERVIDOR] " + e.getMessage());
        }
    }
}