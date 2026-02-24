package org.BioGuard.ui;

import org.BioGuard.controller.ClienteController;
import org.BioGuard.utils.Validador;

import java.io.IOException;
import java.util.Scanner;

/**
 * Menú para opciones relacionadas con pacientes.
 *
 * <p>Responsabilidad Única: Gestionar las operaciones de registro
 * y consulta de pacientes.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MenuPaciente {

    private final ClienteController controller;
    private final Scanner scanner;

    /**
     * Constructor del menú de pacientes.
     *
     * @param controller Controlador de comunicación con el servidor
     * @param scanner Scanner para entrada de datos
     */
    public MenuPaciente(ClienteController controller, Scanner scanner) {
        this.controller = controller;
        this.scanner = scanner;
    }

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * @throws IOException Si hay error de comunicación
     */
    public void registrarPaciente() throws IOException {
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
        System.out.print("Genero: ");
        String genero = scanner.nextLine().trim();
        System.out.print("Ciudad: ");
        String ciudad = scanner.nextLine().trim();
        System.out.print("Pais: ");
        String pais = scanner.nextLine().trim();

        if (!Validador.validarEdad(edad)) {
            throw new IllegalArgumentException("La edad debe ser un numero entre 1 y 150");
        }

        System.out.print("Registrando paciente... ");
        String comando = String.format("REGISTRAR_PACIENTE:%s,%s,%s,%s,%s,%s,%s,%s",
                doc, nombre, apellido, edad, correo, genero, ciudad, pais);

        String respuesta = controller.enviarComando(comando);
        System.out.println("Completado.\n");

        if (respuesta.startsWith("PACIENTE_REGISTRADO:")) {
            System.out.println("Paciente registrado exitosamente con documento: " +
                    respuesta.substring(20));
        } else {
            System.out.println("Error: " + respuesta);
        }
    }

    /**
     * Lista todos los pacientes registrados.
     *
     * @throws IOException Si hay error de comunicación
     */
    public void listarPacientes() throws IOException {
        System.out.println("\n--- LISTAR PACIENTES ---");

        System.out.print("Consultando pacientes... ");
        String comando = "LISTAR_PACIENTES";
        String respuesta = controller.enviarComando(comando);

        System.out.println("Completado.\n");
        System.out.println("PACIENTES REGISTRADOS:");

        if (respuesta.startsWith("PACIENTES:")) {
            String[] lineas = respuesta.substring(10).split("\n");
            if (lineas.length == 0 || (lineas.length == 1 && lineas[0].isEmpty())) {
                System.out.println("  No hay pacientes registrados.");
            } else {
                for (String linea : lineas) {
                    if (linea.trim().isEmpty()) continue;
                    System.out.println("  " + linea);
                }
            }
        } else {
            System.out.println("  " + respuesta);
        }
    }
}