package org.BioGuard;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;

/**
 * Servidor TCP del sistema BioGuard con soporte SSL/TLS.
 * Esta clase es responsable de aceptar conexiones entrantes de clientes
 * y delegar su atención a instancias de ClientHandler en hilos separados.
 *
 * // Objetivo
 *    Establecer un servidor seguro que escuche en un puerto específico,
 *    acepte conexiones SSL de clientes y cree un hilo por cada cliente
 *    para su atención concurrente.
 *
 * // Características
 *    - Comunicación segura mediante SSL/TLS
 *    - Concurrencia con hilos nativos de Java (sin pools)
 *    - Reutilización de direcciones (setReuseAddress)
 *    - Escucha continua hasta error crítico
 *
 * // Atributos
 *    serverPort : Puerto en el que el servidor escuchará conexiones
 *
 * // Flujo de trabajo
 *    1. Crear SSLServerSocketFactory por defecto
 *    2. Crear SSLServerSocket en el puerto especificado
 *    3. Habilitar reutilización de direcciones
 *    4. Entrar en bucle infinito aceptando conexiones
 *    5. Por cada cliente, crear un nuevo hilo con ClientHandler
 *    6. El hilo se encarga de toda la comunicación con ese cliente
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class TCPServer {

    private final int serverPort;

    /**
     * Constructor del servidor TCP.
     *
     * // Objetivo
     *    Inicializar el servidor con el puerto en el que escuchará
     *    las conexiones entrantes de clientes.
     *
     * @param port Número de puerto para escuchar conexiones
     */
    public TCPServer(int port) {
        this.serverPort = port;
    }

    /**
     * Inicia el servidor y comienza a aceptar conexiones.
     *
     * // Objetivo
     *    Poner en marcha el servidor, creando el socket SSL y entrando
     *    en un bucle infinito de aceptación de clientes. Cada cliente
     *    es atendido en un hilo separado para permitir concurrencia.
     *
     * // Proceso detallado
     *    1. Obtener la fábrica de sockets SSL por defecto
     *    2. Crear un SSLServerSocket en el puerto configurado
     *    3. Habilitar setReuseAddress(true) para permitir reutilización
     *    4. Mostrar información de inicio del servidor
     *    5. Entrar en bucle while(true):
     *        a. Aceptar nueva conexión de cliente (bloqueante)
     *        b. Mostrar dirección del cliente conectado
     *        c. Crear nuevo hilo con ClientHandler
     *        d. Iniciar el hilo (start)
     *    6. Capturar IOException en caso de error crítico
     *
     * // Concurrencia
     *    Se utiliza new Thread(...).start() para cada cliente,
     *    cumpliendo con el requerimiento de hilos nativos sin
     *    librerías externas ni pools de hilos.
     *
     * @throws IOException Si hay error al crear el socket o aceptar conexiones
     */
    public void start() {
        try {
            // Obtener la fábrica de sockets SSL por defecto
            SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();

            // Crear socket servidor SSL en el puerto especificado
            SSLServerSocket serverSocket = (SSLServerSocket) factory.createServerSocket(serverPort);

            // Permitir reutilización de direcciones (útil en reinicios rápidos)
            serverSocket.setReuseAddress(true);

            System.out.println("==========================================");
            System.out.println("Servidor BioGuard iniciado (MODO SSL)");
            System.out.println("Puerto: " + serverPort);
            System.out.println("==========================================");

            // Bucle infinito de aceptación de clientes
            while (true) {
                // Aceptar nueva conexión de cliente (bloqueante)
                javax.net.ssl.SSLSocket clientSocket = (javax.net.ssl.SSLSocket) serverSocket.accept();
                System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

                // Crear un nuevo hilo para atender al cliente y iniciarlo
                // REQUERIMIENTO: Concurrencia con hilos nativos de Java
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            // Error crítico del servidor (normalmente por puerto ocupado)
            System.err.println("Error en servidor: " + e.getMessage());
        }
    }
}