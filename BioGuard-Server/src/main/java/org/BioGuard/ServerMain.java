package org.BioGuard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Properties;

public class ServerMain {
    static void main(String[] args) {
        if (args != null && args.length > 0) System.out.println("[INFO] ServerMain recibió " + args.length + " argumentos");

        Properties config = new Properties();
        try (InputStream is = ServerMain.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new FileNotFoundException("No se encontró config.properties en resources.");
            }
            config.load(is);

            // Configurar SSL
            String certName = config.getProperty("SSL_CERTIFICATE_ROUTE");
            URL certUrl = ServerMain.class.getClassLoader().getResource(certName);
            if (certUrl == null) {
                throw new FileNotFoundException("No se encontró el certificado: " + certName);
            }

            String certPath = certUrl.getPath();
            String certPass = config.getProperty("SSL_PASSWORD");

            System.setProperty("javax.net.ssl.keyStore", certPath);
            System.setProperty("javax.net.ssl.keyStorePassword", certPass);
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.trustStore", certPath);
            System.setProperty("javax.net.ssl.trustStorePassword", certPass);
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

            System.out.println("[INFO] Certificado cargado desde: " + certPath);
            System.out.println("[INFO] Iniciando servidor BioGuard...");

            // Inicializar directorios necesarios
            inicializarDirectorios();

            // Iniciar servidor con pool de hilos para mejor concurrencia
            int port = Integer.parseInt(config.getProperty("SERVER_PORT", "2020"));
            TCPServer server = new TCPServer(port);
            server.start();

        } catch (Exception e) {
            System.err.println("[ERROR CRÍTICO] " + e.getMessage());
            if (e.getCause() != null) System.err.println("Causa: " + e.getCause());
        }
    }

    private static void inicializarDirectorios() {
        String[] directorios = {
                "data",
                "data/pacientes",
                "data/virus",
                "data/muestras",
                "data/reportes"
        };

        for (String dir : directorios) {
            File folder = new File(dir);
            if (!folder.exists()) {
                boolean ok = folder.mkdirs();
                if (ok) System.out.println("[INFO] Directorio creado: " + dir);
                else System.err.println("[WARN] No se pudo crear: " + dir);
            }
        }

        // Crear archivo pacientes.csv si no existe
        File csvFile = new File("data/pacientes/pacientes.csv");
        if (!csvFile.exists()) {
            try {
                boolean created = csvFile.createNewFile();
                if (created) {
                    // Escribir cabecera
                    Files.writeString(csvFile.toPath(), "documento,nombre,apellido,edad,correo,genero,ciudad,pais\n");
                    System.out.println("[INFO] Archivo pacientes.csv creado");
                } else {
                    System.err.println("[WARN] No se pudo crear pacientes.csv");
                }
            } catch (IOException e) {
                System.err.println("[ERROR] No se pudo crear pacientes.csv");
            }
        }
    }
}
