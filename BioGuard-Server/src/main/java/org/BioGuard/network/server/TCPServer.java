package org.BioGuard.network.server;

import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.network.protocol.IMessageProtocol;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Servidor TCP mejorado con pool de hilos.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class TCPServer implements ITCPServer {

    private final int port;
    private final IMessageProtocol protocol;
    private final IMessageProcessor processor;
    private final ExecutorService threadPool;
    private ServerSocket serverSocket;
    private final AtomicBoolean running = new AtomicBoolean(false);

    public TCPServer(int port, IMessageProtocol protocol, IMessageProcessor processor) {
        if (port <= 0 || port > 65535) {
            throw new IllegalArgumentException("Puerto inválido: " + port);
        }
        if (protocol == null) {
            throw new IllegalArgumentException("El protocolo no puede ser null");
        }
        if (processor == null) {
            throw new IllegalArgumentException("El procesador no puede ser null");
        }

        this.port = port;
        this.protocol = protocol;
        this.processor = processor;
        this.threadPool = Executors.newCachedThreadPool();
    }

    @Override
    public void start() throws IOException {
        if (running.get()) {
            throw new IllegalStateException("El servidor ya está en ejecución");
        }

        serverSocket = new ServerSocket(port);
        running.set(true);

        System.out.println("=== Servidor BioGuard iniciado ===");
        System.out.println("Puerto: " + port);
        System.out.println("Protocolo: " + protocol.getClass().getSimpleName());
        System.out.println("Procesador: " + processor.getClass().getSimpleName());
        System.out.println("Esperando conexiones...\n");

        while (running.get()) {
            try {
                Socket clientSocket = serverSocket.accept();
                System.out.println(" Nueva conexión desde: " + clientSocket.getInetAddress().getHostAddress());

                ClientHandler clientHandler = new ClientHandler(clientSocket, protocol, processor);
                threadPool.submit(clientHandler);

            } catch (IOException e) {
                if (running.get()) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void stop() {
        System.out.println("\n⏹ Deteniendo servidor...");
        running.set(false);

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Socket del servidor cerrado");
            }
        } catch (IOException e) {
            System.err.println("Error cerrando server socket: " + e.getMessage());
        }

        threadPool.shutdown();
        try {
            if (!threadPool.awaitTermination(30, TimeUnit.SECONDS)) {
                threadPool.shutdownNow();
                System.out.println("Pool de hilos forzado a terminar");
            } else {
                System.out.println("Pool de hilos terminado correctamente");
            }
        } catch (InterruptedException e) {
            threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Servidor detenido");
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }
}