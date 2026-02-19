package org.BioGuard.network.server;

import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.exception.ConfigurationException;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementación concreta de un servidor TCP para el sistema BioGuard.
 *
 * <p>Esta clase sigue el principio de responsabilidad única: su única función
 * es gestionar el ciclo de vida del servidor y la aceptación de conexiones.
 * El procesamiento de mensajes se delega a {@link IMessageProcessor} y el
 * formato de comunicación a {@link IMessageProtocol}.</p>
 *
 * <p>Características principales:</p>
 * <ul>
 *   <li><b>Concurrencia:</b> Utiliza un pool de hilos para manejar múltiples
 *       clientes simultáneamente sin bloquear el hilo principal</li>
 *   <li><b>Graceful shutdown:</b> Al detenerse, espera a que los clientes activos
 *       terminen su procesamiento</li>
 *   <li><b>Manejo de errores:</b> Errores en clientes individuales no afectan
 *       la operación del servidor</li>
 *   <li><b>Protocolo independiente:</b> El formato de los mensajes es completamente
 *       configurable mediante {@link IMessageProtocol}</li>
 * </ul>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 * IMessageProtocol protocol = new LengthPrefixedProtocol();
 * IMessageProcessor processor = new MessageHandler();
 * TCPServer server = new TCPServer(8080, protocol, processor);
 *
 * // Iniciar en un hilo separado
 * new Thread(() -> {
 *     try {
 *         server.start();
 *     } catch (IOException e) {
 *         e.printStackTrace();
 *     }
 * }).start();
 *
 * // Para detener
 * server.stop();
 * </pre>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 * @see ITCPServer
 * @see ClientHandler
 */
public class TCPServer implements ITCPServer {

    /** Puerto en el que el servidor escuchará conexiones */
    private final int port;

    /** Protocolo para serialización/deserialización de mensajes */
    private final IMessageProtocol protocol;

    /** Procesador de mensajes (lógica de negocio) */
    private final IMessageProcessor processor;

    /** Pool de hilos para manejar clientes concurrentemente */
    private final ExecutorService threadPool;

    /** Socket del servidor para aceptar conexiones */
    private ServerSocket serverSocket;

    /** Bandera atómica para controlar el estado del servidor */
    private final AtomicBoolean running = new AtomicBoolean(false);

    /**
     * Constructor del servidor TCP.
     *
     * <p>Inicializa el servidor con los parámetros necesarios para su operación.
     * El pool de hilos se crea con política "cached", que crea hilos según
     * demanda y reutiliza los existentes.</p>
     *
     * @param port Puerto donde escuchar conexiones (1-65535)
     * @param protocol Protocolo para comunicación (no null)
     * @param processor Procesador de mensajes (no null)
     * @throws ConfigurationException Si algún parámetro es inválido
     */
    public TCPServer(int port, IMessageProtocol protocol, IMessageProcessor processor) {
        if (port <= 0 || port > 65535) {
            throw new ConfigurationException("Puerto inválido: " + port + ". Debe estar entre 1 y 65535");
        }
        if (protocol == null) {
            throw new ConfigurationException("El protocolo no puede ser null");
        }
        if (processor == null) {
            throw new ConfigurationException("El procesador no puede ser null");
        }

        this.port = port;
        this.protocol = protocol;
        this.processor = processor;
        this.threadPool = Executors.newCachedThreadPool();
    }

    /**
     * {@inheritDoc}
     *
     * <p>La implementación crea un {@link ServerSocket} en el puerto especificado
     * y entra en un bucle infinito aceptando conexiones. Cada conexión entrante
     * se delega al pool de hilos para su procesamiento, permitiendo que el servidor
     * continúe aceptando nuevas conexiones sin bloqueo.</p>
     */
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
                System.out.println("Nueva conexión desde: " + clientSocket.getInetAddress().getHostAddress());

                // Delegar el manejo del cliente al pool de hilos
                ClientHandler clientHandler = new ClientHandler(clientSocket, protocol, processor);
                threadPool.submit(clientHandler);

            } catch (IOException e) {
                if (running.get()) {
                    System.err.println("Error aceptando conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>La implementación realiza un apagado ordenado:</p>
     * <ol>
     *   <li>Marca el servidor como no disponible para nuevas conexiones</li>
     *   <li>Cierra el ServerSocket para liberar el puerto</li>
     *   <li>Detiene el pool de hilos y espera hasta 30 segundos a que los
     *       clientes activos terminen</li>
     *   <li>Forza la terminación si hay clientes que no responden</li>
     * </ol>
     */
    @Override
    public void stop() {
        System.out.println("\nDeteniendo servidor BioGuard...");
        running.set(false);

        // 1. Cerrar ServerSocket para dejar de aceptar conexiones
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Socket del servidor cerrado");
            }
        } catch (IOException e) {
            System.err.println("Error cerrando server socket: " + e.getMessage());
        }

        // 2. Apagar pool de hilos ordenadamente
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

        System.out.println("Servidor BioGuard detenido");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRunning() {
        return running.get();
    }

    /**
     * Obtiene el puerto en el que el servidor está escuchando.
     *
     * @return Puerto de escucha
     */
    public int getPort() {
        return port;
    }
}