package org.BioGuard;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 *  // Objetivo //
 *     Configurar el entorno SSL/TLS y arrancar un servidor TCP seguro para recibir conexiones
 *     de clientes y procesar pacientes de manera cifrada.
 *  // Atributos //
 *     No tiene atributos; es una clase principal que solo contiene el método main().
 *  // Proceso del main() //
 *     1. Carga el archivo de configuración "config.properties" que contiene rutas y contraseñas.
 *     2. Obtiene la ruta del certificado SSL y su contraseña de las propiedades.
 *     3. Valida que el certificado exista en el sistema de archivos.
 *     4. Configura las propiedades del sistema para keyStore y trustStore usando PKCS12.
 *     5. Crea una instancia del servidor TCP en el puerto 2020.
 *     6. Inicia el servidor que escuchará conexiones seguras de clientes indefinidamente.
 *  // Salidas //
 *     Mensaje de confirmación indicando que el servidor está activo y escuchando conexiones.
 */
public class Main {
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