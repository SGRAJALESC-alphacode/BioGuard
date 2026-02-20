package org.BioGuard.network.server;

import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.security.KeyStore;

/**
 * Servidor SSL que maneja múltiples clientes concurrentemente.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class SSLTCPServer implements ITCPServer {

    private final ISSLConfig sslConfig;
    private final IMessageProcessor processor;
    private final IMessageProtocol protocol;
    private volatile boolean running = false;
    private ServerSocket serverSocket;

    /**
     * Constructor del servidor SSL.
     *
     * @param sslConfig Configuración SSL (keystore, puerto, contraseña)
     * @param processor Procesador de mensajes
     */
    public SSLTCPServer(ISSLConfig sslConfig, IMessageProcessor processor) {
        this.sslConfig = sslConfig;
        this.processor = processor;
        this.protocol = new LengthPrefixedProtocol();
    }

    /**
     * Crea la fábrica de sockets SSL usando el keystore configurado.
     */
    private SSLServerSocketFactory createSSLFactory() throws Exception {
        char[] password = sslConfig.getKeyStorePassword().toCharArray();
        KeyStore ks = KeyStore.getInstance("PKCS12");

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(sslConfig.getKeyStorePath())) {
            if (is == null) {
                throw new IOException("No se encontro el keystore: " + sslConfig.getKeyStorePath());
            }
            ks.load(is, password);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, password);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kmf.getKeyManagers(), null, null);

            return sslContext.getServerSocketFactory();
        }
    }

    @Override
    public void start() throws IOException {
        try {
            SSLServerSocketFactory factory = createSSLFactory();
            serverSocket = factory.createServerSocket(sslConfig.getPort());
            running = true;

            System.out.println("[Server] Escuchando SSL en puerto: " + sslConfig.getPort());

            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    // Cada cliente en su propio hilo
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (IOException e) {
                    if (running) {
                        System.err.println("[Server] Error aceptando conexión: " + e.getMessage());
                    }
                }
            }

        } catch (Exception e) {
            throw new IOException("Error iniciando servidor SSL: " + e.getMessage(), e);
        }
    }

    /**
     * Maneja la comunicación con un cliente específico.
     */
    private void handleClient(Socket clientSocket) {
        String clientAddress = clientSocket.getInetAddress().getHostAddress();
        int clientPort = clientSocket.getPort();
        String clientId = clientAddress + ":" + clientPort;

        try {
            // Configurar timeout para no bloquear indefinidamente
            clientSocket.setSoTimeout(60000); // 60 segundos

            InputStream in = new BufferedInputStream(clientSocket.getInputStream());
            OutputStream out = new BufferedOutputStream(clientSocket.getOutputStream());

            System.out.println("[Server] Cliente conectado: " + clientId);

            // Bucle para recibir múltiples mensajes del mismo cliente
            while (!clientSocket.isClosed() && running) {
                try {
                    // Leer mensaje del cliente usando el protocolo
                    String clientMessage = protocol.decode(in);

                    if (clientMessage == null || clientMessage.trim().isEmpty()) {
                        continue; // Mensaje vacío, seguir esperando
                    }

                    System.out.println("[Server] Mensaje recibido de " + clientId + ": " + clientMessage);

                    // Procesar mensaje (lógica de negocio)
                    String response = processor.process(clientMessage);

                    // Enviar respuesta usando el protocolo
                    protocol.encode(response, out);

                    System.out.println("[Server] Respuesta enviada a " + clientId + ": " + response);

                } catch (SocketTimeoutException e) {
                    // Timeout sin datos - el cliente sigue conectado pero inactivo
                    if (!clientSocket.isClosed()) {
                        System.out.println("[Server] Timeout - cliente " + clientId + " inactivo, esperando...");
                    }
                    continue;

                } catch (EOFException e) {
                    // Cliente cerró la conexión normalmente
                    System.out.println("[Server] Cliente " + clientId + " cerró conexión");
                    break;

                } catch (SocketException e) {
                    // Error de socket (conexión rota)
                    if (!clientSocket.isClosed()) {
                        System.err.println("[Server] Error de socket con " + clientId + ": " + e.getMessage());
                    }
                    break;

                } catch (IOException e) {
                    // Otros errores de E/S
                    if (!clientSocket.isClosed()) {
                        System.err.println("[Server] Error de E/S con " + clientId + ": " + e.getMessage());
                    }
                    break;
                }
            }

            // Cerrar recursos
            try {
                in.close();
                out.close();
                clientSocket.close();
            } catch (IOException ignored) {}

        } catch (IOException e) {
            System.err.println("[Server] Error configurando socket para " + clientId + ": " + e.getMessage());
        }

        System.out.println("[Server] Conexión con " + clientId + " finalizada");
    }

    @Override
    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.err.println("[Server] Error cerrando servidor: " + e.getMessage());
        }
        System.out.println("[Server] Servidor SSL detenido");
    }

    @Override
    public boolean isRunning() {
        return running;
    }
}