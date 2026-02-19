package org.BioGuard.network.server;

import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.network.protocol.IMessageProtocol;

import java.io.*;
import java.net.Socket;

/**
 * Manejador de clientes individuales para el servidor TCP.
 *
 * <p>Esta clase implementa {@link Runnable} para permitir su ejecución en hilos
 * separados. Su responsabilidad exclusiva es gestionar la comunicación con un
 * único cliente durante todo el ciclo de vida de la conexión.</p>
 *
 * <p>Flujo de operación:</p>
 * <ol>
 *   <li>Recibir el socket del cliente aceptado por {@link TCPServer}</li>
 *   <li>Leer el mensaje usando el {@link IMessageProtocol}</li>
 *   <li>Procesar el mensaje con {@link IMessageProcessor}</li>
 *   <li>Enviar la respuesta usando el mismo protocolo</li>
 *   <li>Cerrar la conexión gracefulmente</li>
 * </ol>
 *
 * <p>Características:</p>
 * <ul>
 *   <li><b>Independencia de protocolo:</b> No conoce el formato de los mensajes</li>
 *   <li><b>Aislamiento de errores:</b> Un error con un cliente no afecta a otros</li>
 *   <li><b>Cierre garantizado:</b> Los recursos se cierran incluso ante errores</li>
 * </ul>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 * @see TCPServer
 * @see IMessageProtocol
 * @see IMessageProcessor
 */
public class ClientHandler implements Runnable {

    /** Socket de conexión con el cliente */
    private final Socket clientSocket;

    /** Protocolo para codificar/decodificar mensajes */
    private final IMessageProtocol protocol;

    /** Procesador de mensajes (lógica de negocio) */
    private final IMessageProcessor processor;

    /**
     * Constructor del manejador de cliente.
     *
     * @param clientSocket Socket conectado al cliente (no null)
     * @param protocol Protocolo para comunicación (no null)
     * @param processor Procesador de mensajes (no null)
     * @throws IllegalArgumentException Si algún parámetro es null
     */
    public ClientHandler(Socket clientSocket, IMessageProtocol protocol, IMessageProcessor processor) {
        if (clientSocket == null) {
            throw new IllegalArgumentException("El socket del cliente no puede ser null");
        }
        if (protocol == null) {
            throw new IllegalArgumentException("El protocolo no puede ser null");
        }
        if (processor == null) {
            throw new IllegalArgumentException("El procesador no puede ser null");
        }

        this.clientSocket = clientSocket;
        this.protocol = protocol;
        this.processor = processor;
    }

    /**
     * {@inheritDoc}
     *
     * <p>Ejecuta el ciclo completo de atención al cliente:</p>
     * <ul>
     *   <li>Obtiene la dirección del cliente para logging</li>
     *   <li>Usa try-with-resources para garantizar el cierre automático</li>
     *   <li>Decodifica el mensaje entrante</li>
     *   <li>Procesa el mensaje</li>
     *   <li>Codifica y envía la respuesta</li>
     *   <li>Maneja cualquier excepción sin propagarla</li>
     * </ul>
     */
    @Override
    public void run() {
        String clientAddress = clientSocket.getInetAddress().getHostAddress();
        int clientPort = clientSocket.getPort();
        String clientId = clientAddress + ":" + clientPort;

        System.out.println("[Cliente " + clientId + "] Conectado");

        // try-with-resources asegura que todo se cierra automáticamente
        try (clientSocket;
             InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {

            // 1. Leer mensaje del cliente usando el protocolo
            String clientMessage = protocol.decode(inputStream);
            System.out.println("[Cliente " + clientId + "] Mensaje recibido: " + clientMessage);

            // 2. Procesar mensaje (lógica de negocio)
            String response = processor.process(clientMessage);
            System.out.println("[Cliente " + clientId + "] Respuesta generada: " + response);

            // 3. Enviar respuesta usando el protocolo
            protocol.encode(response, outputStream);
            System.out.println("[Cliente " + clientId + "] Respuesta enviada");

        } catch (IOException e) {
            System.err.println("[Cliente " + clientId + "] Error de comunicación: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[Cliente " + clientId + "] Error inesperado: " + e.getMessage());
        } finally {
            System.out.println("[Cliente " + clientId + "] Conexión cerrada\n");
        }
    }

    /**
     * Obtiene la dirección del cliente asociado.
     *
     * @return Dirección IP del cliente
     */
    public String getClientAddress() {
        return clientSocket.getInetAddress().getHostAddress();
    }

    /**
     * Obtiene el puerto del cliente asociado.
     *
     * @return Puerto del cliente
     */
    public int getClientPort() {
        return clientSocket.getPort();
    }
}