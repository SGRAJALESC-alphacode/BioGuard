package org.BioGuard;

import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;
import org.BioGuard.network.server.ITCPServer;
import org.BioGuard.network.server.TCPServer;
import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.handler.MessageHandler;
import org.BioGuard.service.*;
import org.BioGuard.exception.ConfigurationException;

/**
 * Clase principal para iniciar el servidor BioGuard.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class ServerMain {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        try {
            // 1. Crear servicios
            System.out.println("Inicializando servicios...");
            IPacienteService pacienteService = new PacienteService();
            IDiagnosticoService diagnosticoService = new DiagnosticoService();
            IVirusService virusService = new VirusService();

            // 2. Crear procesador de mensajes
            IMessageProcessor messageProcessor = new MessageHandler(
                    pacienteService,
                    diagnosticoService,
                    virusService
            );

            // 3. Crear protocolo
            IMessageProtocol protocol = new LengthPrefixedProtocol();

            // 4. Crear e iniciar servidor
            ITCPServer server = new TCPServer(PUERTO, protocol, messageProcessor);

            // 5. Iniciar servidor (bloqueante)
            server.start();

        } catch (ConfigurationException e) {
            System.err.println("Error de configuración: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}