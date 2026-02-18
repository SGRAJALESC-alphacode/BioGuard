package org.BioGuard;

/*
 * // Objetivo
 *    Cliente de consola interactivo que permite al usuario final realizar
 *    operaciones contra el servidor BioGuard (registro de pacientes, consulta,
 *    carga de virus, diagnóstico y generación de reportes).
 *
 * // Comportamiento
 *    - Cargar `config.properties` para obtener dirección/puerto y SSL
 *    - Presentar un menú por consola
 *    - Serializar payloads con Gson y enviar en formato COMANDO|payload
 *    - Mostrar la respuesta del servidor (JSON o texto)
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static final Properties config = new Properties();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static TCPClient client;

    static void main(String[] args) {
        // Referencia mínima a args para evitar advertencia de 'parámetro no usado'
        int _argsLen = args.length;

        // Cargar config.properties del cliente
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) throw new IOException("Archivo config.properties no encontrado.");
            config.load(is);
        } catch (IOException e) {
            System.err.println("[ERROR] " + e.getMessage());
            return;
        }

        String addr = config.getProperty("SERVER_ADDRESS", "127.0.0.1");
        int port = Integer.parseInt(config.getProperty("SERVER_PORT", "2020"));
        client = new TCPClient(addr, port, config);

        int opcion;
        do {
            mostrarMenu();
            try {
                opcion = Integer.parseInt(scanner.nextLine());
                procesarOpcion(opcion);
            } catch (NumberFormatException e) {
                System.err.println("Por favor ingrese un número válido");
                opcion = 0;
            }
        } while (opcion != 7);
    }

    private static void mostrarMenu() {
        System.out.println("\n=== BIOGUARD CLIENTE (SSL) ===");
        System.out.println("1. Registrar Paciente");
        System.out.println("2. Consultar Paciente");
        System.out.println("3. Cargar Virus (archivo FASTA)");
        System.out.println("4. Diagnosticar Muestra (análisis de ADN)");
        System.out.println("5. Generar Reporte de Pacientes de Alto Riesgo");
        System.out.println("6. Generar Reporte de Mutaciones");
        System.out.println("7. Salir");
        System.out.print("Selección: ");
    }

    private static void procesarOpcion(int op) {
        switch (op) {
            case 1 -> registrarPaciente();
            case 2 -> consultarPaciente();
            case 3 -> cargarVirus();
            case 4 -> diagnosticarMuestra();
            case 5 -> generarReporteAltoRiesgo();
            case 6 -> generarReporteMutaciones();
            case 7 -> System.out.println("¡Hasta luego!");
            default -> System.out.println("Opción no válida");
        }
    }

    private static void registrarPaciente() {
        System.out.println("\n--- REGISTRO DE PACIENTE ---");

        Patient p = new Patient();

        System.out.print("Documento de identidad: ");
        p.setDocumento(scanner.nextLine());

        System.out.print("Nombre: ");
        p.setNombre(scanner.nextLine());

        System.out.print("Apellido: ");
        p.setApellido(scanner.nextLine());

        System.out.print("Edad: ");
        try {
            p.setEdad(Integer.parseInt(scanner.nextLine()));
        } catch (NumberFormatException e) {
            System.out.println("Edad inválida, usando 0 por defecto");
            p.setEdad(0);
        }

        System.out.print("Correo electrónico: ");
        p.setCorreo(scanner.nextLine());

        System.out.print("Género (M/F/Otro): ");
        p.setGenero(scanner.nextLine());

        System.out.print("Ciudad: ");
        p.setCiudad(scanner.nextLine());

        System.out.print("País: ");
        p.setPais(scanner.nextLine());

        String respuesta = client.sendRequest("REGISTRAR_PACIENTE", gson.toJson(p));
        System.out.println("\n=== RESPUESTA ===");
        System.out.println(respuesta);
    }

    private static void consultarPaciente() {
        System.out.println("\n--- CONSULTAR PACIENTE ---");
        System.out.print("Documento del paciente: ");
        String documento = scanner.nextLine();

        String respuesta = client.sendRequest("CONSULTAR_PACIENTE", documento);
        System.out.println("\n=== RESPUESTA ===");

        try {
            Object json = gson.fromJson(respuesta, Object.class);
            System.out.println(gson.toJson(json));
        } catch (Exception e) {
            System.out.println(respuesta);
        }
    }

    private static void cargarVirus() {
        System.out.println("\n--- CARGAR VIRUS DESDE FASTA ---");
        System.out.print("Ruta del archivo FASTA del virus: ");
        String rutaArchivo = scanner.nextLine();

        try {
            Map<String, String> fastaData = FastaReader.leerFasta(rutaArchivo);
            Map<String, String> headerData = FastaReader.parsearHeaderVirus(fastaData.get("header"));

            Map<String, String> virusData = new java.util.HashMap<>();
            virusData.put("nombre", headerData.get("nombre"));
            virusData.put("nivel", headerData.get("nivel"));
            virusData.put("secuencia", fastaData.get("secuencia"));
            virusData.put("ruta_original", rutaArchivo);

            String respuesta = client.sendRequest("CARGAR_VIRUS", gson.toJson(virusData));
            System.out.println("\n=== RESPUESTA ===");
            System.out.println(respuesta);

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    private static void diagnosticarMuestra() {
        System.out.println("\n--- DIAGNÓSTICO DE MUESTRA ---");
        System.out.print("Documento del paciente: ");
        String documento = scanner.nextLine();

        System.out.print("Ruta del archivo FASTA de la muestra: ");
        String rutaMuestra = scanner.nextLine();

        try {
            Map<String, String> muestraData = FastaReader.leerFasta(rutaMuestra);
            Map<String, String> headerData = FastaReader.parsearHeaderMuestra(muestraData.get("header"));

            Map<String, String> diagnosticoRequest = new java.util.HashMap<>();
            diagnosticoRequest.put("documento", documento);
            diagnosticoRequest.put("fecha_muestra", headerData.get("fecha"));
            diagnosticoRequest.put("secuencia", muestraData.get("secuencia"));
            diagnosticoRequest.put("ruta_original", rutaMuestra);

            String respuesta = client.sendRequest("DIAGNOSTICAR", gson.toJson(diagnosticoRequest));
            System.out.println("\n=== RESULTADO DEL DIAGNÓSTICO ===");
            System.out.println(respuesta);

        } catch (IOException e) {
            System.err.println("Error al leer la muestra: " + e.getMessage());
        }
    }

    private static void generarReporteAltoRiesgo() {
        System.out.println("\n--- GENERAR REPORTE DE PACIENTES DE ALTO RIESGO ---");
        String respuesta = client.sendRequest("REPORTE_ALTO_RIESGO", "");
        System.out.println("\n=== REPORTE GENERADO ===");
        System.out.println(respuesta);
    }

    private static void generarReporteMutaciones() {
        System.out.println("\n--- GENERAR REPORTE DE MUTACIONES ---");
        System.out.print("Documento del paciente: ");
        String documento = scanner.nextLine();

        String respuesta = client.sendRequest("REPORTE_MUTACIONES", documento);
        System.out.println("\n=== REPORTE DE MUTACIONES ===");
        System.out.println(respuesta);
    }
}