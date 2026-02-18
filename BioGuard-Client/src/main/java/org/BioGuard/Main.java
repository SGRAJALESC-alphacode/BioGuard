package org.BioGuard;

import java.io.IOException;
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
            java.io.File localConfig = new java.io.File("config.properties");
            if (localConfig.exists()) {
                try (java.io.FileInputStream fis = new java.io.FileInputStream(localConfig)) {
                    config.load(fis);
                    System.out.println("[INFO] Configuración cargada desde: config.properties");
                }
            } else {
                try (java.io.InputStream is = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
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
            config.setProperty("SERVER_PORT", "2021");
        }

        String addr = config.getProperty("SERVER_ADDRESS", "127.0.0.1");
        int port = Integer.parseInt(config.getProperty("SERVER_PORT", "2021"));
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
            String[] virusData = leerArchivoVirus(ruta);
            if (virusData == null) {
                System.out.println("ERROR: Formato FASTA inválido");
                return;
            }

            String nombre = virusData[0];
            String nivel = virusData[1];
            String secuencia = virusData[2];

            // Validar secuencia
            if (!secuencia.matches("^[ATCG]+$")) {
                System.out.println("ERROR: La secuencia contiene caracteres inválidos");
                return;
            }

            String payload = nombre + "|" + nivel + "|" + secuencia;
            System.out.println("[DEBUG] Enviando: CARGAR_VIRUS|" + nombre + "|" + nivel + "|" + secuencia);

            String respuesta = client.sendRequest("CARGAR_VIRUS", payload);
            System.out.println("\nRESPUESTA: " + respuesta);

        } catch (IOException e) {
            System.out.println("ERROR: No se pudo leer el archivo - " + e.getMessage());
        }
    }

    /**
     * MÉTODO CORREGIDO - Diagnóstico de muestra
     * Ahora envía SOLO headerCompleto|secuencia, sin duplicar el documento
     */
    private static void diagnosticarMuestra() {
        System.out.println("\n--- DIAGNÓSTICO DE MUESTRA ---");
        System.out.print("Documento del paciente: ");
        String documentoIngresado = scanner.nextLine();

        System.out.print("Ruta del archivo FASTA de la muestra: ");
        String rutaMuestra = scanner.nextLine();

        try {
            String[] muestraData = leerArchivoMuestraConDepuracion(rutaMuestra);

            if (muestraData == null) {
                System.out.println("ERROR: No se pudo leer el archivo correctamente");
                return;
            }

            String headerCompleto = muestraData[0]; // "documento|fecha"
            String secuencia = muestraData[1];

            // Validar que el documento del header coincide con el ingresado
            String[] partesHeader = headerCompleto.split("\\|");
            if (partesHeader.length < 2) {
                System.out.println("ERROR: Formato de header inválido. Debe ser: documento|fecha");
                return;
            }

            String documentoHeader = partesHeader[0];
            String fecha = partesHeader[1];

            if (!documentoHeader.equals(documentoIngresado)) {
                System.out.println("ERROR: El documento del archivo (" + documentoHeader +
                        ") no coincide con el ingresado (" + documentoIngresado + ")");
                return;
            }

            // Validar secuencia
            if (!secuencia.matches("^[ATCG]+$")) {
                System.out.println("ERROR: La secuencia contiene caracteres inválidos");
                return;
            }

            // CORREGIDO: Enviar SOLO headerCompleto + secuencia (sin duplicar documento)
            // El servidor espera: DIAGNOSTICAR|documento|fecha|secuencia
            String payload = documentoHeader + "|" + fecha + "|" + secuencia;

            System.out.println("[DEBUG] Enviando: DIAGNOSTICAR|" + payload);

            String respuesta = client.sendRequest("DIAGNOSTICAR", payload);
            System.out.println("\n=== RESPUESTA ===");
            System.out.println(respuesta);

        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
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

    /**
     * Lee un archivo FASTA de virus.
     * Formato esperado: >nombre_virus|nivel\n secuencia
     */
    private static String[] leerArchivoVirus(String ruta) throws IOException {
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

    /**
     * Lee un archivo FASTA de muestra con depuración.
     * Formato esperado: >documento|fecha\n secuencia
     */
    private static String[] leerArchivoMuestraConDepuracion(String ruta) throws IOException {
        List<String> lineas = Files.readAllLines(Paths.get(ruta));
        if (lineas.isEmpty()) {
            System.out.println("ERROR: Archivo vacío");
            return null;
        }

        // Mostrar información de depuración
        System.out.println("\n[DEBUG] ===== INFORMACIÓN DEL ARCHIVO =====");
        System.out.println("[DEBUG] Ruta: " + ruta);
        System.out.println("[DEBUG] Total líneas: " + lineas.size());

        for (int i = 0; i < Math.min(3, lineas.size()); i++) {
            String linea = lineas.get(i);
            System.out.println("[DEBUG] Línea " + i + ": '" + linea + "'");
            System.out.println("[DEBUG] Longitud: " + linea.length());
        }

        // Procesar primera línea
        String primeraLinea = lineas.get(0);
        String header = primeraLinea.trim();

        if (!header.startsWith(">")) {
            System.out.println("ERROR: La primera línea debe comenzar con '>'");
            System.out.println("Contenido: '" + primeraLinea + "'");
            return null;
        }

        // Extraer header (todo después del '>')
        String headerCompleto = header.substring(1).trim();
        System.out.println("[DEBUG] Header extraído: '" + headerCompleto + "'");

        // Procesar secuencia
        StringBuilder secuencia = new StringBuilder();
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (!linea.isEmpty() && !linea.startsWith(">")) {
                secuencia.append(linea);
            }
        }

        String secuenciaStr = secuencia.toString();
        System.out.println("[DEBUG] Secuencia encontrada: '" + secuenciaStr + "'");
        System.out.println("[DEBUG] Longitud secuencia: " + secuenciaStr.length());
        System.out.println("[DEBUG] ===== FIN DEPURACIÓN =====\n");

        if (secuenciaStr.isEmpty()) {
            System.out.println("ERROR: No se encontró secuencia en el archivo");
            return null;
        }

        return new String[]{headerCompleto, secuenciaStr};
    }
}