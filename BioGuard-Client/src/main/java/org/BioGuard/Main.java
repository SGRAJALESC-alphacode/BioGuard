package org.BioGuard;

import org.BioGuard.model.Patient;
import org.BioGuard.network.client.ClientConfig;
import org.BioGuard.network.client.TCPClient;
import org.BioGuard.network.protocol.IMessageProtocol;
import org.BioGuard.network.protocol.LengthPrefixedProtocol;
import org.BioGuard.io.reader.FastaReader;
import org.BioGuard.io.reader.FastaLineParser;
import org.BioGuard.business.validation.PatientDataValidator;
import org.BioGuard.business.validation.IValidator;
import org.BioGuard.business.processor.FastaLineProcessor;
import org.BioGuard.handler.ClientMessageHandler;
import org.BioGuard.exception.FileReadException;

import java.io.IOException;
import java.util.List;

public class Main {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;
    //private static final String ARCHIVO_FASTA = "data/muestra.fasta";
    private static final String ARCHIVO_FASTA = "C:\\Users\\jhona\\OneDrive\\Escritorio\\Backend-2026\\BioGuard\\BioGuard-Client\\data\\muestra.fasta";

    public static void main(String[] args) {
        System.out.println("CLIENTE BioGuard v1.0");
        System.out.println("=====================");

        System.out.println("Directorio actual: " + System.getProperty("user.dir"));

        try {
            // 1. Inicializar componentes
            System.out.println("\n Inicializando componentes...");

            FastaLineParser parser = new FastaLineParser();
            FastaReader reader = new FastaReader(parser);
            IValidator<Patient> validator = new PatientDataValidator();
            FastaLineProcessor lineProcessor = new FastaLineProcessor(validator);
            ClientMessageHandler messageHandler = new ClientMessageHandler(lineProcessor);

            ClientConfig config = new ClientConfig.Builder()
                    .withHost(SERVER_HOST)
                    .withPort(SERVER_PORT)
                    .withConnectionTimeoutMs(5000)
                    .withReadTimeoutMs(30000)
                    .build();

            IMessageProtocol protocol = new LengthPrefixedProtocol();
            TCPClient client = new TCPClient(config, protocol);

            // 2. Leer archivo FASTA
            System.out.println("\nLeyendo archivo: " + ARCHIVO_FASTA);
            List<Patient> pacientes = reader.leer(ARCHIVO_FASTA);
            System.out.println(pacientes.size() + " pacientes cargados");

            // 3. Conectar al servidor
            System.out.println("\nConectando a " + SERVER_HOST + ":" + SERVER_PORT + "...");
            client.connect();
            System.out.println("Conectado al servidor");

            // 4. Procesar y enviar cada paciente
            System.out.println("\nEnviando datos al servidor...");
            int exitosos = 0;
            int fallidos = 0;

            for (Patient patient : pacientes) {
                try {
                    String mensaje = messageHandler.prepararMensaje(patient);
                    System.out.print("   ▶ " + patient.getNombre() + "... ");

                    String respuesta = client.sendMessage(mensaje);
                    messageHandler.procesarRespuesta(respuesta);

                    exitosos++;
                    System.out.println("OK");

                } catch (IllegalArgumentException e) {
                    System.out.println("Error: " + e.getMessage());
                    fallidos++;
                } catch (IOException e) {
                    System.out.println("Error de red: " + e.getMessage());
                    fallidos++;
                }
            }

            // 5. Reporte final
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║           REPORTE FINAL           ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.printf("║ Total pacientes: %16d ║%n", pacientes.size());
            System.out.printf("║ Exitosos:        %16d ║%n", exitosos);
            System.out.printf("║ Fallidos:        %16d ║%n", fallidos);
            System.out.println("╚════════════════════════════════════╝");

            // 6. Desconectar
            client.disconnect();

        } catch (FileReadException e) {
            System.err.println("\nError leyendo archivo: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("\nError de conexión: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
}