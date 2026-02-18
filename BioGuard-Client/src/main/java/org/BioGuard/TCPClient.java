package org.BioGuard;

/*
 * // Objetivo
 *    Proveer una capa cliente para conectarse al `BioGuard-Server` mediante
 *    TLS/SSL, enviar peticiones en formato `COMANDO|PAYLOAD` y leer respuestas.
 *
 * // Atributos
 *    serverAddress     : Dirección del servidor (String)
 *    serverPort        : Puerto del servidor (int)
 *    clientSocket      : Socket SSL utilizado para la conexión (Socket)
 *    dataInputStream   : Stream de lectura para respuestas (DataInputStream)
 *    dataOutputStream  : Stream de escritura para requests (DataOutputStream)
 *
 * // Comportamiento
 *    connect()         : Configura SSL a partir de recursos y abre la conexión
 *    sendRequest()     : Envía `COMANDO|payload` y devuelve la respuesta del servidor
 *    closeConnection() : Cierra streams y socket
 */

import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

public class TCPClient {
    private String serverAddress;
    private int serverPort;
    private Socket clientSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    public TCPClient(String serverAddress, int serverPort, Properties config) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        String certResource = config.getProperty("SSL_CERTIFICATE_ROUTE");
        String ksPassword = config.getProperty("SSL_PASSWORD");

        // BUSCAR EL RECURSO DINÁMICAMENTE
        URL certUrl = getClass().getClassLoader().getResource(certResource);

        if (certUrl == null) {
            throw new IllegalArgumentException("No se encontró el certificado en los recursos: " + certResource);
        }

        // Convertir URL a ruta absoluta de archivo
        String ksRoute = certUrl.getPath();

        System.setProperty("javax.net.ssl.keyStore", ksRoute);
        System.setProperty("javax.net.ssl.keyStorePassword", ksPassword);
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");
        System.setProperty("javax.net.ssl.trustStore", ksRoute);
        System.setProperty("javax.net.ssl.trustStorePassword", ksPassword);
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
    }

    public void connect() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        // El casting a SSLSocket no es estrictamente necesario aquí si usas la factory Default
        this.clientSocket = sslSocketFactory.createSocket(serverAddress, serverPort);
        this.dataInputStream = new DataInputStream(this.clientSocket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.clientSocket.getOutputStream());
    }

    public String sendRequest(String command, String payload) {
        try {
            this.connect();
            this.dataOutputStream.writeUTF(command + "|" + payload);
            return this.dataInputStream.readUTF();
        } catch (IOException e) {
            return "ERROR DE CONEXIÓN: " + e.getMessage();
        } finally {
            this.closeConnection();
        }
    }

    public void closeConnection() {
        try {
            if (this.dataInputStream != null) this.dataInputStream.close();
            if (this.dataOutputStream != null) this.dataOutputStream.close();
            if (this.clientSocket != null) this.clientSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}