package org.BioGuard.ui;

import org.BioGuard.controller.ClienteController;
import org.BioGuard.utils.Validador;

import java.io.IOException;
import java.util.Scanner;

/**
 * Menú para opciones relacionadas con reportes.
 *
 * <p>Responsabilidad Única: Gestionar la generación de reportes
 * de alto riesgo y mutaciones.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MenuReporte {

    private final ClienteController controller;
    private final Scanner scanner;

    /**
     * Constructor del menú de reportes.
     *
     * @param controller Controlador de comunicación con el servidor
     * @param scanner Scanner para entrada de datos
     */
    public MenuReporte(ClienteController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    /**
     * Genera el reporte de pacientes de alto riesgo.
     *
     * @throws IOException Si hay error de comunicación
     */
    public void generarReporteAltoRiesgo() throws IOException {
        System.out.println("\n--- GENERAR REPORTE DE ALTO RIESGO ---");

        System.out.print("Generando reporte... ");
        String comando = "REPORTE_ALTO_RIESGO";
        String respuesta = controller.enviarComando(comando);

        System.out.println("Completado.\n");
        System.out.println("Resultado: " + respuesta);
    }

    /**
     * Genera el reporte de mutaciones para un paciente.
     *
     * @throws IOException Si hay error de comunicación
     */
    public void generarReporteMutaciones() throws IOException {
        System.out.println("\n--- GENERAR REPORTE DE MUTACIONES ---");

        System.out.print("Documento del paciente: ");
        String doc = scanner.nextLine().trim();

        if (!Validador.validarDocumento(doc)) {
            throw new IllegalArgumentException("El documento no puede estar vacio");
        }

        System.out.print("ID de la muestra (opcional, Enter para usar la mas reciente): ");
        String idMuestra = scanner.nextLine().trim();

        System.out.print("Generando reporte... ");

        String comando;
        if (idMuestra.isEmpty()) {
            comando = "REPORTE_MUTACIONES:" + doc;
        } else {
            comando = "REPORTE_MUTACIONES:" + doc + "|" + idMuestra;
        }

        String respuesta = controller.enviarComando(comando);
        System.out.println("Completado.\n");
        System.out.println("Resultado: " + respuesta);
    }
}