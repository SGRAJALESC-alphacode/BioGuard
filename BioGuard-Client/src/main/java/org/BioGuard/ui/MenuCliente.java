package org.BioGuard.ui;

import org.BioGuard.controller.ClienteController;

import java.util.Scanner;

/**
 * Menú interactivo para el cliente BioGuard.
 *
 * <p>Esta clase proporciona una interfaz de usuario por consola que permite
 * al usuario interactuar con el sistema BioGuard. Se conecta al servidor
 * a través de {@link ClienteController} y envía comandos para realizar
 * diferentes operaciones.</p>
 *
 * <p>Las opciones disponibles son:</p>
 * <ul>
 *   <li>Enviar muestra de ADN para diagnóstico</li>
 *   <li>Consultar diagnósticos de un paciente</li>
 *   <li>Ver detalle de un diagnóstico específico</li>
 *   <li>Registrar un nuevo paciente</li>
 *   <li>Listar todos los pacientes registrados</li>
 *   <li>Salir del programa</li>
 * </ul>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 * @see ClienteController
 */
public class MenuCliente {

    private final ClienteController controller;
    private final Scanner scanner;

    /**
     * Constructor del menú cliente.
     *
     * <p>Inicializa el controlador y el scanner para entrada de datos.</p>
     */
    public MenuCliente() {
        this.controller = new ClienteController();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Inicia el menú interactivo.
     *
     * <p>Este método muestra el menú principal y procesa las opciones
     * seleccionadas por el usuario en un bucle continuo hasta que
     * se elige la opción de salir.</p>
     */
    public void iniciar() {
        System.out.println("╔═════════════════════════╗");
        System.out.println("║     CLIENTE BioGuard    ║");
        System.out.println("╚═════════════════════════╝");

        if (!controller.conectar()) {
            System.out.println("No se pudo conectar al servidor. Saliendo...");
            return;
        }

        while (true) {
            mostrarMenu();
            System.out.print("Opción: ");
            String opcion = scanner.nextLine().trim();

            switch (opcion) {
                case "1":
                    enviarMuestra();
                    break;
                case "2":
                    consultarDiagnosticos();
                    break;
                case "3":
                    verDiagnostico();
                    break;
                case "4":
                    registrarPaciente();
                    break;
                case "5":
                    listarPacientes();
                    break;
                case "6":
                    System.out.println("Saliendo...");
                    controller.desconectar();
                    return;
                default:
                    System.out.println(" Opción inválida");
            }
        }
    }

    /**
     * Muestra el menú principal con las opciones disponibles.
     */
    private void mostrarMenu() {
        System.out.println("\n╔════════════════════════════════════╗");
        System.out.println("║           MENÚ PRINCIPAL           ║");
        System.out.println("╠════════════════════════════════════╣");
        System.out.println("║ 1. Enviar muestra                  ║");
        System.out.println("║ 2. Consultar diagnósticos          ║");
        System.out.println("║ 3. Ver detalle diagnóstico         ║");
        System.out.println("║ 4. Registrar paciente              ║");
        System.out.println("║ 5. Listar pacientes                ║");
        System.out.println("║ 6. Salir                           ║");
        System.out.println("╚════════════════════════════════════╝");
    }

    /**
     * Envía una muestra de ADN al servidor para su análisis.
     *
     * <p>Solicita al usuario el documento del paciente y la secuencia de ADN.
     * La secuencia es validada para asegurar que solo contenga los caracteres
     * A, T, C, G. Si la validación es exitosa, envía el comando al servidor
     * y muestra la respuesta.</p>
     */
    private void enviarMuestra() {
        System.out.println("\n--- ENVIAR MUESTRA ---");
        System.out.print("Documento del paciente: ");
        String doc = scanner.nextLine().trim();

        System.out.print("Secuencia de ADN (solo ATCG): ");
        String sec = scanner.nextLine().trim().toUpperCase();

        if (!sec.matches("^[ATCG]+$")) {
            System.out.println(" Error: La secuencia solo puede contener A, T, C, G");
            return;
        }

        String comando = "ENVIAR_MUESTRA:" + doc + "|" + sec;
        String respuesta = controller.enviarComando(comando);
        System.out.println("> " + respuesta);
    }

    /**
     * Consulta los diagnósticos de un paciente específico.
     *
     * <p>Solicita al usuario el documento del paciente y envía el comando
     * al servidor. Muestra la lista de diagnósticos encontrados con sus IDs
     * para facilitar la consulta detallada posterior.</p>
     */
    private void consultarDiagnosticos() {
        System.out.println("\n--- CONSULTAR DIAGNÓSTICOS ---");
        System.out.print("Documento del paciente: ");
        String doc = scanner.nextLine().trim();

        String comando = "CONSULTAR_DIAGNOSTICOS:" + doc;
        String respuesta = controller.enviarComando(comando);

        System.out.println(" Diagnósticos encontrados:");
        if (respuesta.startsWith("DIAGNOSTICOS:")) {
            String[] lineas = respuesta.substring(13).split("\n");
            for (String linea : lineas) {
                if (linea.trim().isEmpty()) continue;
                String[] partes = linea.split(",");
                System.out.println("  ID: " + partes[0]); // Solo muestra el ID
            }
        } else {
            System.out.println(respuesta);
        }
    }

    /**
     * Muestra el detalle completo de un diagnóstico específico.
     *
     * <p>Solicita al usuario el ID completo del diagnóstico y envía el comando
     * al servidor. Muestra toda la información del diagnóstico incluyendo
     * los virus detectados y sus posiciones.</p>
     */
    private void verDiagnostico() {
        System.out.println("\n--- VER DETALLE DE DIAGNÓSTICO ---");
        System.out.print("ID del diagnóstico: ");
        String id = scanner.nextLine().trim();

        String comando = "VER_DIAGNOSTICO:" + id;
        String respuesta = controller.enviarComando(comando);
        System.out.println("> " + respuesta);
    }

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * <p>Solicita al usuario todos los datos requeridos para el registro
     * de un paciente: documento, nombre, apellido, edad, correo, género,
     * ciudad y país. Envía los datos al servidor y muestra la respuesta.</p>
     */
    private void registrarPaciente() {
        System.out.println("\n--- REGISTRAR PACIENTE ---");
        System.out.print("Documento: ");
        String doc = scanner.nextLine().trim();
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Apellido: ");
        String apellido = scanner.nextLine().trim();
        System.out.print("Edad: ");
        String edad = scanner.nextLine().trim();
        System.out.print("Correo: ");
        String correo = scanner.nextLine().trim();
        System.out.print("Género: ");
        String genero = scanner.nextLine().trim();
        System.out.print("Ciudad: ");
        String ciudad = scanner.nextLine().trim();
        System.out.print("País: ");
        String pais = scanner.nextLine().trim();

        String comando = String.format("REGISTRAR_PACIENTE:%s,%s,%s,%s,%s,%s,%s,%s",
                doc, nombre, apellido, edad, correo, genero, ciudad, pais);

        String respuesta = controller.enviarComando(comando);
        System.out.println("> " + respuesta);
    }

    /**
     * Lista todos los pacientes registrados en el sistema.
     *
     * <p>Envía el comando LISTAR_PACIENTES al servidor y muestra
     * la lista completa de pacientes con su información básica.</p>
     */
    private void listarPacientes() {
        System.out.println("\n--- LISTAR PACIENTES ---");
        String comando = "LISTAR_PACIENTES";
        String respuesta = controller.enviarComando(comando);
        System.out.println(">" + respuesta);
    }
}