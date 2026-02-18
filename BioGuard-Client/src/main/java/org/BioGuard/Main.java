package org.BioGuard;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static final Properties config = new Properties();
    private static final Scanner scanner = new Scanner(System.in);
    private static TCPClient client;

    public static void main(String[] args) {
        // Cargar configuración
        try {
            // Intentar cargar desde archivo en el directorio actual
            java.io.File localConfig = new java.io.File("config.properties");
            if (localConfig.exists()) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(localConfig)) {
                    config.load(fis);
                    System.out.println("[INFO] Configuración cargada desde: config.properties");
                }
            } else {
                // Intentar desde resources
                try (InputStream is = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
                    if (is == null) {
                        throw new IOException("Archivo config.properties no encontrado");
                    }
                    config.load(is);
                    System.out.println("[INFO] Configuración cargada desde: resources");
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] No se pudo cargar configuración: " + e.getMessage());
            System.err.println("[INFO] Usando valores por defecto");
            config.setProperty("SERVER_ADDRESS", "127.0.0.1");
            config.setProperty("SERVER_PORT", "2020");
        }

        String addr = config.getProperty("SERVER_ADDRESS", "127.0.0.1");
        int port = Integer.parseInt(config.getProperty("SERVER_PORT", "2020"));
        client = new TCPClient(addr, port, config);

        System.out.println("=== BIOGUARD CLIENTE ===");
        System.out.println("Servidor: " + addr + ":" + port);

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
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Registrar Paciente");
        System.out.println("2. Consultar Paciente");
        System.out.println("3. Cargar Virus");
        System.out.println("4. Diagnosticar Muestra");
        System.out.println("5. Reporte Alto Riesgo");
        System.out.println("6. Reporte Mutaciones");
        System.out.println("7. Salir");
        System.out.print("Selección: ");
    }

    private static void procesarOpcion(int op) {
        switch (op) {
            case 1: registrarPaciente(); break;
            case 2: consultarPaciente(); break;
            case 3: cargarVirus(); break;
            case 4: diagnosticarMuestra(); break;
            case 5: generarReporteAltoRiesgo(); break;
            case 6: generarReporteMutaciones(); break;
            case 7: System.out.println("¡Hasta luego!"); break;
            default: System.out.println("Opción no válida");
        }
    }

    private static void registrarPaciente() {
        System.out.println("\n--- REGISTRO DE PACIENTE ---");

        System.out.print("Documento: ");
        String documento = scanner.nextLine();

        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Apellido: ");
        String apellido = scanner.nextLine();

        System.out.print("Edad: ");
        String edad = scanner.nextLine();

        System.out.print("Correo: ");
        String correo = scanner.nextLine();

        System.out.print("Género: ");
        String genero = scanner.nextLine();

        System.out.print("Ciudad: ");
        String ciudad = scanner.nextLine();

        System.out.print("País: ");
        String pais = scanner.nextLine();

        String payload = documento + "|" + nombre + "|" + apellido + "|" + edad + "|" +
                correo + "|" + genero + "|" + ciudad + "|" + pais;

        String respuesta = client.sendRequest("REGISTRAR_PACIENTE", payload);
        System.out.println("\nRESPUESTA: " + respuesta);
    }

    private static void consultarPaciente() {
        System.out.println("\n--- CONSULTAR PACIENTE ---");
        System.out.print("Documento: ");
        String documento = scanner.nextLine();

        String respuesta = client.sendRequest("CONSULTAR_PACIENTE", documento);
        System.out.println("\nRESPUESTA: " + respuesta);
    }

    private static void cargarVirus() {
        System.out.println("\n--- CARGAR VIRUS ---");
        System.out.print("Ruta del archivo FASTA: ");
        String ruta = scanner.nextLine();

        try {
            String[] virusData = leerFastaVirus(ruta);
            if (virusData == null) {
                System.out.println("ERROR: Formato FASTA inválido");
                return;
            }

            String payload = virusData[0] + "|" + virusData[1] + "|" + virusData[2];
            String respuesta = client.sendRequest("CARGAR_VIRUS", payload);
            System.out.println("\nRESPUESTA: " + respuesta);

        } catch (IOException e) {
            System.out.println("ERROR: No se pudo leer el archivo - " + e.getMessage());
        }
    }

    private static void diagnosticarMuestra() {
        System.out.println("\n--- DIAGNÓSTICO DE MUESTRA ---");
        System.out.print("Documento del paciente: ");
        String documento = scanner.nextLine();

        System.out.print("Ruta del archivo FASTA: ");
        String ruta = scanner.nextLine();

        try {
            String[] muestraData = leerFastaMuestra(ruta);
            if (muestraData == null) {
                System.out.println("ERROR: Formato FASTA inválido");
                return;
            }

            String payload = documento + "|" + muestraData[0] + "|" + muestraData[1];
            String respuesta = client.sendRequest("DIAGNOSTICAR", payload);
            System.out.println("\nRESPUESTA: " + respuesta);

        } catch (IOException e) {
            System.out.println("ERROR: No se pudo leer el archivo - " + e.getMessage());
        }
    }

    private static void generarReporteAltoRiesgo() {
        System.out.println("\n--- REPORTE ALTO RIESGO ---");
        String respuesta = client.sendRequest("REPORTE_ALTO_RIESGO", "");
        System.out.println("\nRESPUESTA: " + respuesta);
    }

    private static void generarReporteMutaciones() {
        System.out.println("\n--- REPORTE MUTACIONES ---");
        System.out.print("Documento del paciente: ");
        String documento = scanner.nextLine();

        String respuesta = client.sendRequest("REPORTE_MUTACIONES", documento);
        System.out.println("\nRESPUESTA: " + respuesta);
    }

    private static String[] leerFastaVirus(String ruta) throws IOException {
        List<String> lineas = Files.readAllLines(Paths.get(ruta));
        if (lineas.isEmpty()) return null;

        String header = lineas.get(0).trim();
        if (!header.startsWith(">")) return null;

        String headerSinMayor = header.substring(1);
        String[] partes = headerSinMayor.split("\\|");
        String nombre = partes[0];
        String nivel = partes.length > 1 ? partes[1] : "Normal";

        StringBuilder secuencia = new StringBuilder();
        for (int i = 1; i < lineas.size(); i++) {
            secuencia.append(lineas.get(i).trim());
        }

        return new String[]{nombre, nivel, secuencia.toString()};
    }

    private static String[] leerFastaMuestra(String ruta) throws IOException {
        List<String> lineas = Files.readAllLines(Paths.get(ruta));
        if (lineas.isEmpty()) return null;

        String header = lineas.get(0).trim();
        if (!header.startsWith(">")) return null;

        String fechaCompleta = header.substring(1); // documento|fecha

        StringBuilder secuencia = new StringBuilder();
        for (int i = 1; i < lineas.size(); i++) {
            secuencia.append(lineas.get(i).trim());
        }

        // Validar secuencia
        String secStr = secuencia.toString();
        if (!secStr.matches("^[ATCG]+$")) {
            throw new IOException("La secuencia contiene caracteres inválidos. Solo se permiten A,T,C,G");
        }

        return new String[]{fechaCompleta, secStr};
    }
}