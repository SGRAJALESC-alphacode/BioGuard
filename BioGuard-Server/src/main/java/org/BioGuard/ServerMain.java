package org.BioGuard;

import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;
import org.BioGuard.network.server.ITCPServer;
import org.BioGuard.network.server.TCPServer;
import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.handler.MessageHandler;
import org.BioGuard.service.*;

public class ServerMain {

    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════╗");
        System.out.println("║     SERVIDOR BioGuard v1.0        ║");
        System.out.println("╚════════════════════════════════════╝");

        try {
            // Inicializar servicios
            System.out.println("\n Inicializando servicios...");
            IPacienteService pacienteService = new PacienteService();
            IDiagnosticoService diagnosticoService = new DiagnosticoService();
            IVirusService virusService = new VirusService();

            // Crear procesador
            IMessageProcessor messageProcessor = new MessageHandler(
                    pacienteService,
                    diagnosticoService,
                    virusService
            );

            // Crear protocolo
            IMessageProtocol protocol = new LengthPrefixedProtocol();

            // Crear servidor
            ITCPServer server = new TCPServer(PUERTO, protocol, messageProcessor);

            // Iniciar servidor
            System.out.println("\nIniciando servidor en puerto " + PUERTO + "...");
            server.start();

        } catch (Exception e) {
            System.err.println("\nError: " + e.getMessage());
            e.printStackTrace();
        }
    }
}