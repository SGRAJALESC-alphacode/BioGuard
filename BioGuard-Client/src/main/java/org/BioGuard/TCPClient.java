package org.BioGuard;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.Properties;

public class TCPClient {

    private final String serverAddress;
    private final int serverPort;

    public TCPClient(String address, int port, Properties config) {
        this.serverAddress = address;
        this.serverPort = port;

        // Configurar truststore
        String certPath = config.getProperty("SSL_CERTIFICATE_ROUTE");
        String certPass = config.getProperty("SSL_PASSWORD");

        if (certPath != null && !certPath.isEmpty()) {
            File certFile = buscarTruststore(certPath);

            if (certFile != null && certFile.exists()) {
                System.setProperty("javax.net.ssl.trustStore", certFile.getAbsolutePath());
                System.setProperty("javax.net.ssl.trustStorePassword", certPass);
                System.setProperty("javax.net.ssl.trustStoreType", "JKS");
                System.out.println("[INFO] TrustStore configurado: " + certFile.getAbsolutePath());
            } else {
                System.err.println("[ERROR] No se encontró truststore. Buscado en:");
                System.err.println("        - " + certPath);
                System.err.println("        - src/main/resources/certs/truststore.jks");
                System.err.println("        - certs/truststore.jks");
                System.err.println("        - " + new File("BioGuard-Client/src/main/resources/certs/truststore.jks").getAbsolutePath());
            }
        }
    }

    /**
     * Busca el truststore en múltiples ubicaciones
     */
    private File buscarTruststore(String ruta) {
        System.out.println("[DEBUG] Buscando truststore...");

        // 1. Ruta exacta del properties
        File file = new File(ruta);
        if (file.exists()) {
            System.out.println("[DEBUG] Truststore encontrado en: " + file.getAbsolutePath());
            return file;
        }

        // 2. Buscar en resources del cliente
        try {
            java.net.URL resourceUrl = getClass().getClassLoader().getResource("certs/truststore.jks");
            if (resourceUrl != null) {
                file = new File(resourceUrl.getPath());
                if (file.exists()) {
                    System.out.println("[DEBUG] Truststore encontrado en resources: " + file.getAbsolutePath());
                    return file;
                }
            }
        } catch (Exception e) {
            // Ignorar
        }

        // 3. Buscar en src/main/resources/certs
        file = new File("src/main/resources/certs/truststore.jks");
        if (file.exists()) {
            System.out.println("[DEBUG] Truststore encontrado en: " + file.getAbsolutePath());
            return file;
        }

        // 4. Buscar en certs/ (raíz del cliente)
        file = new File("certs/truststore.jks");
        if (file.exists()) {
            System.out.println("[DEBUG] Truststore encontrado en: " + file.getAbsolutePath());
            return file;
        }

        // 5. Buscar en BioGuard-Client/src/main/resources/certs
        String userDir = System.getProperty("user.dir");
        file = new File(userDir, "BioGuard-Client/src/main/resources/certs/truststore.jks");
        if (file.exists()) {
            System.out.println("[DEBUG] Truststore encontrado en: " + file.getAbsolutePath());
            return file;
        }

        // 6. Buscar en la carpeta del proyecto
        file = new File("BioGuard-Client/certs/truststore.jks");
        if (file.exists()) {
            System.out.println("[DEBUG] Truststore encontrado en: " + file.getAbsolutePath());
            return file;
        }

        return null;
    }

    public String sendRequest(String command, String payload) {
        SSLSocket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            System.out.println("[DEBUG] Conectando a " + serverAddress + ":" + serverPort);

            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(serverAddress, serverPort);
            socket.setSoTimeout(10000);

            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            String request = command + "|" + payload;
            System.out.println("[DEBUG] Enviando: " + request);
            out.println(request);

            String response = in.readLine();
            System.out.println("[DEBUG] Respuesta: " + response);

            return response != null ? response : "ERROR: Respuesta vacía";

        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            return "ERROR DE CONEXIÓN: " + e.getMessage();
        } finally {
            try { if (out != null) out.close(); } catch (Exception e) {}
            try { if (in != null) in.close(); } catch (Exception e) {}
            try { if (socket != null) socket.close(); } catch (Exception e) {}
        }
    }
}