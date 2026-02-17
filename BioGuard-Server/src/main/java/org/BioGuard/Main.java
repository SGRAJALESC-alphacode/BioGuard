package org.BioGuard;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        Properties p = new Properties();
        try (InputStream is = Main.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                throw new FileNotFoundException("No se encontró config.properties en resources.");
            }
            p.load(is);

            // Obtener la URL del certificado dentro de la carpeta resources/certs
            String certName = p.getProperty("SSL_CERTIFICATE_ROUTE");
            URL certUrl = Main.class.getClassLoader().getResource(certName);

            if (certUrl == null) {
                throw new FileNotFoundException("No se encontró el archivo del certificado en: " + certName);
            }

            String certPath = certUrl.getPath();
            String certPass = p.getProperty("SSL_PASSWORD");

            // Configuración obligatoria de certificados SSL [cite: 28]
            System.setProperty("javax.net.ssl.keyStore", certPath);
            System.setProperty("javax.net.ssl.keyStorePassword", certPass);
            System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
            System.setProperty("javax.net.ssl.trustStore", certPath);
            System.setProperty("javax.net.ssl.trustStorePassword", certPass);
            System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");

            System.out.println("[INFO] Certificado cargado desde: " + certPath);

            TCPServer server = new TCPServer(Integer.parseInt(p.getProperty("SERVER_PORT", "2020")));
            server.start();

        } catch (Exception e) {
            System.err.println("[ERROR CRÍTICO] " + e.getMessage());
            e.printStackTrace(); // Esto te dirá exactamente qué algoritmo o archivo falla
        }
    }
}