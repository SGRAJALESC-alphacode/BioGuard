package org.BioGuard;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import java.nio.file.*;
import java.security.*;

/*
 *  // Objetivo //
 *     Implementar un sistema de gestión de pacientes mediante un menú interactivo
 *     en consola, permitiendo crear, leer, actualizar, eliminar, listar pacientes
 *     y enviar información al servidor para análisis.
 *  // Entradas //
 *     Entrada del usuario por consola para seleccionar opciones del menú y proporcionar
 *     datos de pacientes.
 *     Archivo "config.properties" para cargar configuración inicial del sistema.
 *  // Proceso //
 *     1. Se carga el archivo de configuración "config.properties".
 *     2. Se inicializa un Scanner para leer entradas de usuario desde la consola.
 *     3. Se muestra un menú de opciones repetidamente hasta que el usuario seleccione salir:
 *        - Opción 1: Llama a 'crearPaciente()' para registrar un nuevo paciente.
 *        - Opción 2: Llama a 'leerPaciente()' para mostrar información de un paciente.
 *        - Opción 3: Llama a 'actualizarPaciente()' para modificar los datos de un paciente.
 *        - Opción 4: Llama a 'eliminarPaciente()' para realizar un borrado lógico (soft delete).
 *        - Opción 5: Llama a 'listarPacientesActivos()' para mostrar los pacientes activos.
 *        - Opción 6: Llama a 'enviarPacienteServidor()' para enviar datos a un servidor.
 *        - Opción 7: Finaliza la ejecución del programa.
 *     4. Se valida que la opción ingresada sea correcta; si no, se muestra un mensaje de error.
 *  // Salidas //
 *     No retorna valores, pero produce:
 *        - Impresiones en consola del menú y resultados de las acciones.
 *        - Actualización de la información de pacientes según las operaciones seleccionadas.
 *        - Mensaje de salida al terminar el programa o al producirse un error de configuración.
 */

public class Main {
    private static Properties config = new Properties();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            config.load(Main.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            System.out.println("Error cargando configuración: " + e.getMessage());
            return;
        }

        int opcion;
        do {
            System.out.println("=== MENÚ PACIENTES ===");
            System.out.println("1. Crear paciente");
            System.out.println("2. Leer paciente");
            System.out.println("3. Actualizar paciente");
            System.out.println("4. Eliminar paciente (soft delete)");
            System.out.println("5. Listar pacientes activos");
            System.out.println("6. Enviar paciente al servidor para análisis");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");
            opcion = Integer.parseInt(scanner.nextLine());

            switch (opcion) {
                case 1 -> crearPaciente();
                case 2 -> leerPaciente();
                case 3 -> actualizarPaciente();
                case 4 -> eliminarPaciente();
                case 5 -> listarPacientesActivos();
                case 6 -> enviarPacienteServidor();
                case 7 -> System.out.println("Saliendo del sistema...");
                default -> System.out.println("Opción inválida.");
            }
        } while (opcion != 7);
    }

    /*
     *  // Objetivo //
     *     Pedir al usuario la ruta de un archivo FASTA y verificar que exista.
     *  // Entradas //
     *     Scanner sc: para leer la entrada del usuario.
     *  // Proceso //
     *     Solicita el nombre del archivo y revisa dos posibles rutas hasta encontrar el archivo.
     *  // Salidas //
     *     Devuelve la ruta válida del archivo FASTA.
     */
    private static String pedirRutaFasta(Scanner sc) {
        String ruta;
        File archivo;

        do {
            System.out.print("Ingrese el nombre del archivo FASTA (ej: patient004.fasta): ");
            String nombreArchivo = sc.nextLine().trim();

            // Probar dos posibles rutas según desde dónde se ejecute
            String ruta1 = "data/adn/" + nombreArchivo;     // si ejecuta desde raíz del proyecto
            String ruta2 = "../data/adn/" + nombreArchivo;  // si ejecuta desde módulo client

            if (new File(ruta1).exists()) {
                ruta = ruta1;
            } else if (new File(ruta2).exists()) {
                ruta = ruta2;
            } else {
                System.out.println("Error: el archivo no existe en 'data/adn'. Intente de nuevo.");
                ruta = null;
            }

        } while (ruta == null);

        return ruta;
    }

    /*
     *  Objetivo:
     *     Crear un nuevo paciente solicitando sus datos al usuario y guardarlo en el sistema.
     *  Entradas:
     *     Entrada del usuario por consola: ID, nombre, documento, email, edad, sexo, ruta y checksum del archivo FASTA.
     *  Proceso:
     *     1. Solicita los datos del paciente uno por uno.
     *     2. Obtiene la ruta del archivo FASTA usando 'pedirRutaFasta'.
     *     3. Calcula el tamaño del archivo en bytes.
     *     4. Crea un objeto 'Patient' con toda la información.
     *     5. Llama a 'PatientCRUD.createPatient' para guardar el paciente.
     *  Salidas:
     *     Ninguna, pero el paciente queda registrado en el sistema.
     */

    private static void crearPaciente() {
        System.out.println("\n--- Crear nuevo paciente ---");

        System.out.print("ID: ");
        String id = scanner.nextLine();

        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine();

        System.out.print("Documento: ");
        String doc = scanner.nextLine();

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Edad: ");
        int edad = Integer.parseInt(scanner.nextLine());

        System.out.print("Sexo (M/F): ");
        String sexo = scanner.nextLine();

        System.out.print("Ruta archivo FASTA: ");
        String fasta = pedirRutaFasta(scanner);

        System.out.print("Checksum archivo: ");
        String checksum = scanner.nextLine();

        System.out.print("Tamaño archivo en bytes: ");
        File file = new File(fasta);
        long fileSize = file.length();
        System.out.println("Tamaño del archivo: " + fileSize + " bytes");

        Patient patient = new Patient(
                id, nombre, doc, email,
                new Date(), edad, sexo,
                fasta, checksum, fileSize
        );

        PatientCRUD.createPatient(patient);
    }

    /*Consultar y mostrar la información de un paciente registrado en el sistema.*/

    private static void leerPaciente() {
        System.out.println("\n--- Leer paciente ---");
        System.out.print("Ingrese el ID del paciente: ");
        String id = scanner.nextLine();
        PatientCRUD.readPatient(id);
    }


    /*
     *     Actualizar los datos de un paciente existente en el sistema.
     *     ID del paciente desde consola y nuevos valores opcionales para nombre, email, edad y ruta del archivo FASTA.
     * */
    private static void actualizarPaciente() {
        System.out.println("\n--- Actualizar paciente ---");
        System.out.print("Ingrese el ID del paciente: ");
        String id = scanner.nextLine();

        Patient patient = PatientCRUD.readPatient(id);
        if (patient == null) return;

        System.out.print("Nuevo nombre completo (" + patient.getFull_name() + "): ");
        String nombre = scanner.nextLine();
        if (!nombre.isEmpty()) patient.setFull_name(nombre);

        System.out.print("Nuevo email (" + patient.getContact_email() + "): ");
        String email = scanner.nextLine();
        if (!email.isEmpty()) patient.setContact_email(email);

        System.out.print("Nueva edad (" + patient.getAge() + "): ");
        String edadStr = scanner.nextLine();
        if (!edadStr.isEmpty()) patient.setAge(Integer.parseInt(edadStr));

        System.out.print("Nueva ruta archivo FASTA (" + patient.getClinical_notes() + "): ");
        String fasta = pedirRutaFasta(scanner);
        if (!fasta.isEmpty()) patient.setClinical_notes(fasta);

        PatientCRUD.updatePatient(patient);
    }

    /* Eliminar un paciente del sistema (soft delete).*/
    private static void eliminarPaciente() {
        System.out.println("\n--- Eliminar paciente (soft delete) ---");
        System.out.print("Ingrese el ID del paciente: ");
        String id = scanner.nextLine();
        PatientCRUD.deletePatient(id);
    }
    /* Mostrar todos los pacientes activos registrados en el sistema..*/
    private static void listarPacientesActivos() {
        System.out.println("\n--- Pacientes activos ---");
        var pacientes = PatientCRUD.getAllActivePatients();
        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes activos.");
        } else {
            for (Patient p : pacientes) {
                System.out.println("- " + p.getPatient_id() + ": " + p.getFull_name() +
                        " (" + p.getAge() + " años, archivo: " + p.getClinical_notes() + ")");
            }
        }
    }
    /*
     *  // Objetivo //
     *     Enviar los datos de un paciente al servidor para análisis.
     *  // Entradas //
     *     ID del paciente desde consola.
     *  // Proceso //
     *     1. Solicita el ID del paciente y recupera su información.
     *     2. Convierte el objeto Patient a formato JSON usando Gson.
     *     3. Obtiene la dirección y puerto del servidor desde la configuración.
     *     4. Crea un cliente TCP y envía el JSON al servidor.
     *  // Salidas //
     *     Ninguna, pero el paciente se envía al servidor para su procesamiento.
     */

    private static void enviarPacienteServidor() {
        System.out.print("Ingrese el ID del paciente a enviar: ");
        String patientId = scanner.nextLine();

        Patient patient = PatientCRUD.readPatient(patientId);
        if (patient == null) {
            System.out.println("Paciente no encontrado.");
            return;
        }

        Gson gson = new Gson();
        String json = gson.toJson(patient);

        String serverAddress = config.getProperty("SERVER_ADDRESS", "127.0.0.1");
        int serverPort = Integer.parseInt(config.getProperty("SERVER_PORT", "2020"));

        TCPClient client = new TCPClient(serverAddress, serverPort, config);
        client.sendMessage(json);
    }
}