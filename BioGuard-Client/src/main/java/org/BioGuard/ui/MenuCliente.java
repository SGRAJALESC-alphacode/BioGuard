package org.BioGuard.ui;

import org.BioGuard.controller.ClienteController;

import java.util.Scanner;

/**
 * Orquestador principal del menú cliente.
 *
 * <p>Responsabilidad Única: Mostrar el menú principal y delegar
 * en los submenús especializados según la opción seleccionada.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MenuCliente {

    private final ClienteController controller;
    private final Scanner scanner;
    private final MenuPaciente menuPaciente;
    private final MenuMuestra menuMuestra;
    private final MenuReporte menuReporte;
    private boolean ejecutando;

    /**
     * Constructor del menú principal.
     */
    public MenuCliente() {
        this.controller = new ClienteController();
        this.scanner = new Scanner(System.in);
        this.menuPaciente = new MenuPaciente(controller, scanner);
        this.menuMuestra = new MenuMuestra(controller, scanner);
        this.menuReporte = new MenuReporte(controller, scanner);
        this.ejecutando = true;
    }

    /**
     * Inicia el menú interactivo.
     */
    public void iniciar() {
        mostrarBanner();

        if (!controller.conectar()) {
            System.err.println("\nError: No se pudo conectar al servidor.");
            System.err.println("Verifique que el servidor esté ejecutándose en localhost:8443\n");
            return;
        }

        System.out.println("\nConexión establecida correctamente.\n");

        while (ejecutando) {
            mostrarMenu();
            procesarOpcion();
        }

        controller.desconectar();
        System.out.println("\nGracias por usar BioGuard. Hasta pronto.\n");
    }

    private void mostrarBanner() {
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║               BIOCUARD                        ║");
        System.out.println("║       Sistema de Vigilancia Genómica          ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }

    private void mostrarMenu() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║               MENU PRINCIPAL                  ║");
        System.out.println("╠═══════════════════════════════════════════════╣");
        System.out.println("║  [1] Enviar muestra de ADN                    ║");
        System.out.println("║  [2] Consultar diagnosticos                   ║");
        System.out.println("║  [3] Ver detalle de diagnostico               ║");
        System.out.println("║  [4] Registrar nuevo paciente                 ║");
        System.out.println("║  [5] Listar pacientes                         ║");
        System.out.println("║  [6] Reporte de alto riesgo                   ║");
        System.out.println("║  [7] Reporte de mutaciones                    ║");
        System.out.println("║  [8] Salir                                    ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
    }

    private void procesarOpcion() {
        System.out.print("\nIngrese su opcion [1-8]: ");
        String opcion = scanner.nextLine().trim();

        try {
            switch (opcion) {
                case "1":
                    menuMuestra.enviarMuestra();
                    break;
                case "2":
                    menuMuestra.consultarDiagnosticos();
                    break;
                case "3":
                    menuMuestra.verDiagnostico();
                    break;
                case "4":
                    menuPaciente.registrarPaciente();
                    break;
                case "5":
                    menuPaciente.listarPacientes();
                    break;
                case "6":
                    menuReporte.generarReporteAltoRiesgo();
                    break;
                case "7":
                    menuReporte.generarReporteMutaciones();
                    break;
                case "8":
                    confirmarSalida();
                    break;
                default:
                    System.out.println("Error: Opcion no valida.");
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private void confirmarSalida() {
        System.out.print("\n¿Esta seguro que desea salir? (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        if (confirmacion.equals("s") || confirmacion.equals("si")) {
            ejecutando = false;
        }
    }
}