package org.BioGuard;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import java.nio.file.*;
import java.security.*;
import org.BioGuard.exceptions.PatientNotFoundException;
import org.BioGuard.exceptions.PatientFileException;
import org.BioGuard.exceptions.InvalidPatientDataException;

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
            System.out.println("\n=== MENÚ PACIENTES ===");
            System.out.println("1. Crear paciente");
            System.out.println("2. Leer paciente");
            System.out.println("3. Actualizar paciente");
            System.out.println("4. Eliminar paciente (soft delete)");
            System.out.println("5. Listar pacientes activos");
            System.out.println("6. Enviar paciente al servidor para análisis");
            System.out.println("7. Salir");
            System.out.print("Seleccione una opción: ");
            try {
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
            } catch (NumberFormatException e) {
                System.out.println("Error: Ingrese un número válido.");
                opcion = 0;
            } catch (InvalidPatientDataException e) {
                System.out.println("Error de validación: " + e.getMessage());
            } catch (PatientNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (PatientFileException e) {
                System.out.println("Error de archivo: " + e.getMessage());
            }
        } while (opcion != 7);
    }

    /*
     *  // Objetivo //
     *     Pedir al usuario la ruta de un archivo FASTA y verificar que exista.
     *  // Entradas //
     *     sc : Scanner para leer la entrada del usuario desde la consola.
     *  // Proceso //
     *     1. Solicita el nombre del archivo FASTA al usuario.
     *     2. Verifica dos posibles rutas de ejecución (desde raíz o desde módulo client).
     *     3. Comprueba si el archivo existe en alguna de las rutas.
     *     4. Si no existe, muestra error y pide reintentar.
     *     5. Si existe, retorna la ruta válida del archivo.
     *  // Salidas //
     *     Retorna la ruta válida del archivo FASTA encontrado.
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
     *  // Objetivo //
     *     Crear un nuevo paciente solicitando sus datos al usuario y guardarlo en el sistema.
     *  // Entradas //
     *     Entrada interactiva del usuario por consola: ID, nombre, documento, email, edad, sexo,
     *     ruta y checksum del archivo FASTA.
     *  // Proceso //
     *     1. Solicita uno por uno los datos personales del paciente.
     *     2. Valida que el ID y nombre no sean vacíos.
     *     3. Valida que la edad sea un número positivo válido.
     *     4. Obtiene la ruta del archivo FASTA usando pedirRutaFasta().
     *     5. Calcula automáticamente el tamaño del archivo en bytes.
     *     6. Asigna la fecha de registro actual usando new Date().
     *     7. Crea un nuevo objeto Patient con toda la información.
     *     8. Guarda el paciente en el sistema llamando a PatientCRUD.createPatient().
     *  // Salidas //
     *     Ninguna, pero el paciente queda registrado y persistido en un archivo JSON.
     *  // Excepciones //
     *     Lanza InvalidPatientDataException si hay datos inválidos.
     *     Lanza PatientFileException si hay error al crear el archivo.
     */
    private static void crearPaciente() throws InvalidPatientDataException, PatientFileException {
        System.out.println("\n--- Crear nuevo paciente ---");

        System.out.print("ID: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            throw new InvalidPatientDataException("patient_id", "El ID del paciente no puede estar vacío");
        }

        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine().trim();
        if (nombre.isEmpty()) {
            throw new InvalidPatientDataException("full_name", "El nombre del paciente no puede estar vacío");
        }

        System.out.print("Documento: ");
        String doc = scanner.nextLine().trim();
        if (doc.isEmpty()) {
            throw new InvalidPatientDataException("document_id", "El documento no puede estar vacío");
        }

        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            throw new InvalidPatientDataException("contact_email", "El email no puede estar vacío");
        }

        System.out.print("Edad: ");
        Integer edad;
        try {
            edad = Integer.parseInt(scanner.nextLine().trim());
            if (edad < 0 || edad > 150) {
                throw new InvalidPatientDataException("age", "La edad debe estar entre 0 y 150 años");
            }
        } catch (NumberFormatException e) {
            throw new InvalidPatientDataException("age", "La edad debe ser un número válido", e);
        }

        System.out.print("Sexo (M/F): ");
        String sexo = scanner.nextLine().trim();
        if (!sexo.equalsIgnoreCase("M") && !sexo.equalsIgnoreCase("F")) {
            throw new InvalidPatientDataException("sex", "El sexo debe ser M o F");
        }

        System.out.print("Ruta archivo FASTA: ");
        String fasta = pedirRutaFasta(scanner);

        System.out.print("Checksum archivo: ");
        String checksum = scanner.nextLine().trim();

        File file = new File(fasta);
        long fileSize = file.length();
        System.out.println("Tamaño del archivo: " + fileSize + " bytes");

        Patient patient = new Patient(
                id, nombre, doc, email,
                new Date(), edad, sexo,
                fasta, checksum, fileSize
        );

        PatientCRUD.createPatient(patient);
        System.out.println("✓ Paciente creado exitosamente.");
    }

    /*
     *  // Objetivo //
     *     Consultar y mostrar la información de un paciente registrado en el sistema.
     *  // Entradas //
     *     ID del paciente desde consola.
     *  // Proceso //
     *     1. Solicita al usuario el ID del paciente a consultar.
     *     2. Valida que el ID no sea vacío.
     *     3. Llama a PatientCRUD.readPatient() para recuperar los datos.
     *     4. Muestra la información del paciente en consola.
     *  // Salidas //
     *     Información del paciente mostrada en consola.
     *  // Excepciones //
     *     Lanza InvalidPatientDataException si el ID es vacío.
     *     Lanza PatientNotFoundException si el paciente no existe.
     *     Lanza PatientFileException si hay error de I/O.
     */
    private static void leerPaciente() throws InvalidPatientDataException, PatientNotFoundException, PatientFileException {
        System.out.println("\n--- Leer paciente ---");
        System.out.print("Ingrese el ID del paciente: ");
        String id = scanner.nextLine().trim();

        if (id.isEmpty()) {
            throw new InvalidPatientDataException("patient_id", "El ID del paciente no puede estar vacío");
        }

        Patient patient = PatientCRUD.readPatient(id);
        System.out.println("✓ Paciente encontrado y cargado exitosamente.");
    }

    /*
     *  // Objetivo //
     *     Actualizar los datos de un paciente existente en el sistema.
     *  // Entradas //
     *     ID del paciente desde consola y nuevos valores opcionales para nombre, email,
     *     edad y ruta del archivo FASTA.
     *  // Proceso //
     *     1. Solicita el ID del paciente a actualizar.
     *     2. Valida que el ID no sea vacío.
     *     3. Recupera el paciente existente usando PatientCRUD.readPatient().
     *     4. Permite actualizar opcionalmente: nombre, email, edad y ruta del archivo FASTA.
     *     5. Si el usuario deja un campo vacío, se mantiene el valor anterior.
     *     6. Valida datos como edad si se proporciona.
     *     7. Guarda los cambios llamando a PatientCRUD.updatePatient().
     *  // Salidas //
     *     Ninguna, pero el archivo JSON del paciente se actualiza con la nueva información.
     *  // Excepciones //
     *     Lanza InvalidPatientDataException si hay datos inválidos.
     *     Lanza PatientNotFoundException si el paciente no existe.
     *     Lanza PatientFileException si hay error de I/O.
     */
    private static void actualizarPaciente() throws InvalidPatientDataException, PatientNotFoundException, PatientFileException {
        System.out.println("\n--- Actualizar paciente ---");
        System.out.print("Ingrese el ID del paciente: ");
        String id = scanner.nextLine().trim();

        if (id.isEmpty()) {
            throw new InvalidPatientDataException("patient_id", "El ID del paciente no puede estar vacío");
        }

        Patient patient = PatientCRUD.readPatient(id);

        System.out.print("Nuevo nombre completo (" + patient.getFull_name() + "): ");
        String nombre = scanner.nextLine().trim();
        if (!nombre.isEmpty()) patient.setFull_name(nombre);

        System.out.print("Nuevo email (" + patient.getContact_email() + "): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) patient.setContact_email(email);

        System.out.print("Nueva edad (" + patient.getAge() + "): ");
        String edadStr = scanner.nextLine().trim();
        if (!edadStr.isEmpty()) {
            try {
                Integer edad = Integer.parseInt(edadStr);
                if (edad < 0 || edad > 150) {
                    throw new InvalidPatientDataException("age", "La edad debe estar entre 0 y 150 años");
                }
                patient.setAge(edad);
            } catch (NumberFormatException e) {
                throw new InvalidPatientDataException("age", "La edad debe ser un número válido", e);
            }
        }

        System.out.print("Nueva ruta archivo FASTA (" + patient.getClinical_notes() + "): ");
        String fasta = pedirRutaFasta(scanner);
        if (!fasta.isEmpty()) patient.setClinical_notes(fasta);

        PatientCRUD.updatePatient(patient);
        System.out.println("✓ Paciente actualizado exitosamente.");
    }

    /*
     *  // Objetivo //
     *     Eliminar un paciente del sistema realizando un borrado lógico (soft delete).
     *  // Entradas //
     *     ID del paciente desde consola.
     *  // Proceso //
     *     1. Solicita el ID del paciente a eliminar.
     *     2. Valida que el ID no sea vacío.
     *     3. Llama a PatientCRUD.deletePatient() que marca el paciente como inactivo.
     *     4. El paciente se marca como is_active = false sin eliminarlo físicamente.
     *  // Salidas //
     *     Ninguna, pero el paciente se marca como inactivo en el sistema.
     *  // Excepciones //
     *     Lanza InvalidPatientDataException si el ID es vacío.
     *     Lanza PatientNotFoundException si el paciente no existe.
     *     Lanza PatientFileException si hay error de I/O.
     */
    private static void eliminarPaciente() throws InvalidPatientDataException, PatientNotFoundException, PatientFileException {
        System.out.println("\n--- Eliminar paciente (soft delete) ---");
        System.out.print("Ingrese el ID del paciente: ");
        String id = scanner.nextLine().trim();

        if (id.isEmpty()) {
            throw new InvalidPatientDataException("patient_id", "El ID del paciente no puede estar vacío");
        }

        PatientCRUD.deletePatient(id);
        System.out.println("✓ Paciente marcado como inactivo exitosamente.");
    }

    /*
     *  // Objetivo //
     *     Mostrar todos los pacientes activos registrados en el sistema.
     *  // Entradas //
     *     Ninguna, utiliza la información persistida en archivos JSON.
     *  // Proceso //
     *     1. Llama a PatientCRUD.getAllActivePatients() para obtener la lista de pacientes activos.
     *     2. Si no hay pacientes activos, muestra un mensaje informativo.
     *     3. Si hay pacientes, itera sobre la lista y muestra información resumida de cada uno.
     *  // Salidas //
     *     Lista de pacientes activos mostrada en consola.
     *  // Excepciones //
     *     Lanza PatientFileException si hay error al leer los archivos.
     */
    private static void listarPacientesActivos() throws PatientFileException {
        System.out.println("\n--- Pacientes activos ---");
        var pacientes = PatientCRUD.getAllActivePatients();
        if (pacientes.isEmpty()) {
            System.out.println("No hay pacientes activos.");
        } else {
            System.out.println("✓ Total de pacientes activos: " + pacientes.size());
            for (Patient p : pacientes) {
                System.out.println("  - " + p.getPatient_id() + ": " + p.getFull_name() +
                        " (" + p.getAge() + " años, archivo: " + p.getClinical_notes() + ")");
            }
        }
    }
    /*
     *  // Objetivo //
     *     Enviar los datos de un paciente al servidor para análisis genético.
     *  // Entradas //
     *     ID del paciente desde consola.
     *  // Proceso //
     *     1. Solicita el ID del paciente y valida que no sea vacío.
     *     2. Recupera su información usando PatientCRUD.readPatient().
     *     3. Convierte el objeto Patient a formato JSON usando Gson.
     *     4. Obtiene la dirección y puerto del servidor desde la configuración.
     *     5. Crea un cliente TCP seguro (SSL/TLS) y envía el JSON al servidor.
     *     6. Maneja excepciones de conexión y procesamiento.
     *  // Salidas //
     *     Ninguna, pero el paciente se envía al servidor para su procesamiento.
     *  // Excepciones //
     *     Lanza InvalidPatientDataException si el ID es vacío.
     *     Lanza PatientNotFoundException si el paciente no existe.
     *     Lanza PatientFileException si hay error de I/O.
     */
    private static void enviarPacienteServidor() throws InvalidPatientDataException, PatientNotFoundException, PatientFileException {
        System.out.println("\n--- Enviar paciente al servidor ---");
        System.out.print("Ingrese el ID del paciente a enviar: ");
        String patientId = scanner.nextLine().trim();

        if (patientId.isEmpty()) {
            throw new InvalidPatientDataException("patient_id", "El ID del paciente no puede estar vacío");
        }

        Patient patient = PatientCRUD.readPatient(patientId);

        Gson gson = new Gson();
        String json = gson.toJson(patient);

        String serverAddress = config.getProperty("SERVER_ADDRESS", "127.0.0.1");
        int serverPort = Integer.parseInt(config.getProperty("SERVER_PORT", "2020"));

        try {
            System.out.println("Conectando al servidor: " + serverAddress + ":" + serverPort);
            TCPClient client = new TCPClient(serverAddress, serverPort, config);
            client.sendMessage(json);
            System.out.println("✓ Paciente enviado al servidor exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error de configuración SSL: " + e.getMessage());
            throw new PatientFileException("config.properties", "leer configuración SSL", e);
        } catch (Exception e) {
            System.out.println("Error al enviar paciente al servidor: " + e.getMessage());
        }
    }
}