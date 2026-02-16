package org.BioGuard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    /** Main class to start the TCP server.
     * Configures SSL config.config.properties and initiates the server on the specified port.
     */

    /*
     *
     *     Configurar el entorno SSL/TLS y arrancar un servidor TCP para recibir conexiones seguras.
     */
    public static void main(String[] args) {
        Properties p = new Properties();

        try {
            p.load(Main.class.getClassLoader().getResourceAsStream("config.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        String certificateRoute = p.getProperty("SSL_CERTIFICATE_ROUTE");
        String certificatePassword = p.getProperty("SSL_PASSWORD");

        System.out.println("Certificado: " + new File(certificateRoute).getAbsolutePath());
        System.out.println("Existe? " + new File(certificateRoute).exists());

        System.setProperty("javax.net.ssl.keyStore",certificateRoute);
        System.setProperty("javax.net.ssl.keyStorePassword",certificatePassword);
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        System.setProperty("javax.net.ssl.trustStore", certificateRoute);
        System.setProperty("javax.net.ssl.trustStorePassword", certificatePassword);
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
        TCPServer server = new TCPServer(2020);
        server.start();
    }
}