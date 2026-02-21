package org.BioGuard.ui;

import org.BioGuard.controller.ClienteController;
import org.BioGuard.utils.Validador;

import java.io.IOException;
import java.util.Scanner;

/**
 * Menú para opciones relacionadas con muestras y diagnósticos.
 *
 * <p>Responsabilidad Única: Gestionar el envío de muestras y la
 * consulta de diagnósticos.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MenuMuestra {

    private final ClienteController controller;
    private final Scanner scanner;

    /**
     * Constructor del menú de muestras.
     *
     * @param controller Controlador de comunicación con el servidor
     * @param scanner Scanner para entrada de datos
     */
    public MenuMuestra(ClienteController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    /**
     * Envía una muestra de ADN al servidor.
     *
     * @throws IOException Si hay error de comunicación
     */
    public void enviarMuestra() throws IOException {
        System.out.println("\n--- ENVIAR MUESTRA DE ADN ---");

        System.out.print("Documento del paciente: ");
        String doc = scanner.nextLine().trim();

        if (!Validador.validarDocumento(doc)) {
            throw new IllegalArgumentException("El documento no puede estar vacio");
        }

        System.out.print("Secuencia de ADN (solo ATCG): ");
        String sec = scanner.nextLine().trim().toUpperCase();

        if (!Validador.validarSecuenciaADN(sec)) {
            throw new IllegalArgumentException("La secuencia solo puede contener A, T, C, G");
        }

        System.out.print("Enviando muestra al servidor... ");
        String comando = "ENVIAR_MUESTRA:" + doc + "|" + sec;
        String respuesta = controller.enviarComando(comando);

        System.out.println("Completado.\n");
        System.out.println("Respuesta: " + respuesta);
    }

    /**
     * Consulta los diagnósticos de un paciente.
     *
     * @throws IOException Si hay error de comunicación
     */
    public void consultarDiagnosticos() throws IOException {
        System.out.println("\n--- CONSULTAR DIAGNOSTICOS ---");

        System.out.print("Documento del paciente: ");
        String doc = scanner.nextLine().trim();

        if (!Validador.validarDocumento(doc)) {
            throw new IllegalArgumentException("El documento no puede estar vacio");
        }

        System.out.print("Consultando diagnosticos... ");
        String comando = "CONSULTAR_DIAGNOSTICOS:" + doc;
        String respuesta = controller.enviarComando(comando);

        System.out.println("Completado.\n");
        System.out.println("RESULTADOS:");

        if (respuesta.startsWith("DIAGNOSTICOS:")) {
            String[] lineas = respuesta.substring(13).split("\n");
            if (lineas.length == 0 || (lineas.length == 1 && lineas[0].isEmpty())) {
                System.out.println("  No hay diagnosticos para este paciente.");
            } else {
                System.out.println("  ID del diagnostico | Fecha | Virus detectados");
                System.out.println("  " + "-".repeat(50));
                for (String linea : lineas) {
                    if (linea.trim().isEmpty()) continue;
                    String[] partes = linea.split(",");
                    System.out.printf("  %-25s | %s | %s virus%n",
                            partes[0], partes[1].substring(0, 16), partes[2]);
                }
            }
        } else {
            System.out.println("  " + respuesta);
        }
    }

    /**
     * Muestra el detalle de un diagnóstico específico.
     *
     * @throws IOException Si hay error de comunicación
     */
    public void verDiagnostico() throws IOException {
        System.out.println("\n--- VER DETALLE DE DIAGNOSTICO ---");

        System.out.print("ID del diagnostico: ");
        String id = scanner.nextLine().trim();

        if (id.isEmpty()) {
            throw new IllegalArgumentException("El ID no puede estar vacio");
        }

        System.out.print("Consultando detalle... ");
        String comando = "VER_DIAGNOSTICO:" + id;
        String respuesta = controller.enviarComando(comando);

        System.out.println("Completado.\n");
        System.out.println("DETALLE:");

        if (respuesta.startsWith("ERROR:")) {
            System.out.println("  " + respuesta);
        } else {
            String[] lineas = respuesta.split("\n");
            for (String linea : lineas) {
                System.out.println("  " + linea);
            }
        }
    }
}