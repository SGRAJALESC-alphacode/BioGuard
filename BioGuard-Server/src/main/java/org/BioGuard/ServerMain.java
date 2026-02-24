package org.BioGuard;

import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;
import org.BioGuard.network.server.ITCPServer;
import org.BioGuard.network.server.TCPServer;
import org.BioGuard.network.server.SSLTCPServer;
import org.BioGuard.network.server.ISSLConfig;
import org.BioGuard.network.server.SSLConfig;
import org.BioGuard.handler.IMessageProcessor;
import org.BioGuard.handler.MessageHandler;
import org.BioGuard.service.*;
import org.BioGuard.service.diagnostico.DiagnosticoService;
import org.BioGuard.service.diagnostico.IDiagnosticoService;

public class ServerMain {

    private static final int PUERTO_NORMAL = 8080;
    private static final int PUERTO_SSL = 8443;
    private static final String KEYSTORE_PATH = "certs/keystore.p12";
    private static final String KEYSTORE_PASSWORD = "changeit";
    private static final boolean USAR_SSL = true;

    public static void main(String[] args) {
        System.out.println("╔═════════════════════════╗");
        System.out.println("║     SERVIDOR BioGuard   ║");
        System.out.println("╚═════════════════════════╝");

        try {
            System.out.println("\n Inicializando servicios...");

            // 1. Crear servicios en orden correcto
            IPacienteService pacienteService = new PacienteService();
            IVirusService virusService = new VirusService();                    // ← PRIMERO
            IDiagnosticoService diagnosticoService = new DiagnosticoService(virusService); // ← AHORA FUNCIONA

            // 2. Crear procesador
            IMessageProcessor messageProcessor = new MessageHandler(
                    pacienteService,
                    diagnosticoService,
                    virusService
            );

            // 3. Crear protocolo
            IMessageProtocol protocol = new LengthPrefixedProtocol();

            // 4. Crear servidor
            ITCPServer server;
            if (USAR_SSL) {
                System.out.println("\nConfigurando servidor SSL...");
                ISSLConfig sslConfig = new SSLConfig(PUERTO_SSL, KEYSTORE_PATH, KEYSTORE_PASSWORD);
                server = new SSLTCPServer(sslConfig, messageProcessor);
                System.out.println("Servidor SSL configurado en puerto " + PUERTO_SSL);
            } else {
                System.out.println("\nConfigurando servidor TCP normal...");
                server = new TCPServer(PUERTO_NORMAL, protocol, messageProcessor);
                System.out.println(" TCP configurado en puerto " + PUERTO_NORMAL);
            }

            // 5. Iniciar servidor
            System.out.println("\nIniciando servidor...");
            server.start();

        } catch (Exception e) {
            System.err.println("\nError: " + e.getMessage());
            e.printStackTrace();
        }
    }
}