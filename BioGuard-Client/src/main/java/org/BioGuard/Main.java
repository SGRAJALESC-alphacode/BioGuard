package org.BioGuard;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class Main {
    private static Properties config = new Properties();
    private static final Scanner scanner = new Scanner(System.in);
    private static final Gson gson = new Gson();

    public static void main(String[] args) {
        // Cargar config.properties del cliente
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) throw new IOException("Archivo config.properties no encontrado.");
            config.load(is);
        } catch (IOException e) {
            System.err.println("[ERROR] " + e.getMessage());
            return;
        }

        int opcion;
        do {
            mostrarMenu();
            opcion = Integer.parseInt(scanner.nextLine());
            procesarOpcion(opcion);
        } while (opcion != 4);
    }

    private static void mostrarMenu() {
        System.out.println("\n=== BIOGUARD CLIENTE (SSL) ===");
        System.out.println("1. Registrar Paciente [Servidor]");
        System.out.println("2. Consultar Paciente [Servidor]");
        System.out.println("3. Análisis de ADN [Servidor]");
        System.out.println("4. Salir");
        System.out.print("Selección: ");
    }

    private static void procesarOpcion(int op) {
        String addr = config.getProperty("SERVER_ADDRESS", "127.0.0.1");
        int port = Integer.parseInt(config.getProperty("SERVER_PORT", "2020"));
        TCPClient client = new TCPClient(addr, port, config);

        switch (op) {
            case 1 -> {
                System.out.println("\n--- Nuevo Registro ---");
                System.out.print("ID: "); String id = scanner.nextLine();
                System.out.print("Nombre: "); String nombre = scanner.nextLine();
                // ... captura los demás campos según tu clase Patient ...
                Patient p = new Patient(id, nombre, "DOC123", "email@test.com", new Date(), 25, "M", "ruta/local.fasta", "abc", 1024);

                String res = client.sendRequest("CREATE", gson.toJson(p));
                System.out.println("Respuesta: " + res);
            }
            case 2 -> {
                System.out.print("ID a consultar: ");
                String id = scanner.nextLine();
                String res = client.sendRequest("READ", id);
                System.out.println("Datos: " + res);
            }
            case 3 -> {
                System.out.print("ID para análisis: ");
                String id = scanner.nextLine();
                // Primero pedimos los datos para tener el objeto Patient completo
                String pJson = client.sendRequest("READ", id);
                if (!pJson.startsWith("ERROR")) {
                    String res = client.sendRequest("ANALYZE", pJson);
                    System.out.println(res);
                } else {
                    System.out.println("Paciente no encontrado en el servidor.");
                }
            }
        }
    }
}