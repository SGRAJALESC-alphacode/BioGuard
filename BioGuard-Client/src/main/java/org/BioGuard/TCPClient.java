package org.BioGuard;

import javax.net.ssl.SSLSocketFactory;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Properties;

/*
 *  // Objetivo //
 *     Representar un cliente TCP capaz de conectarse a un servidor de forma segura (SSL/TLS)
 *     y enviar o recibir mensajes.
 *  // Atributos //
 *     serverAddress     : Dirección IP o nombre del servidor al que se conectará el cliente.
 *     serverPort        : Puerto del servidor.
 *     clientSocket      : Socket TCP que establece la conexión con el servidor.
 *     dataInputStream   : Flujo de entrada para recibir datos del servidor.
 *     dataOutputStream  : Flujo de salida para enviar datos al servidor.
 *  // Entradas //
 *     Se establecen mediante el constructor y los métodos de conexión/envío.
 *  // Salidas //
 *     Permite enviar y recibir datos a través de la conexión TCP segura.
 */

public class TCPClient {
    private String serverAddress;
    private int serverPort;
    private Socket clientSocket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;

    /*
     *  // Objetivo //
     *     Inicializar un cliente TCP seguro (SSL/TLS) para conectarse a un servidor.
     *  // Entradas //
     *     serverAddress : Dirección IP o nombre del servidor.
     *     serverPort    : Puerto del servidor.
     *     config        : Objeto Properties que debe contener SSL_CERTIFICATE_ROUTE y SSL_PASSWORD.
     *  // Proceso //
     *     1. Asigna la dirección y puerto del servidor.
     *     2. Obtiene la ruta y contraseña del certificado SSL desde la configuración.
     *     3. Configura las propiedades del sistema para keyStore y trustStore usando PKCS12.
     *     4. Si falta la ruta o contraseña del certificado, lanza IllegalArgumentException.
     *  // Salidas //
     *     Ninguna, pero prepara el cliente para establecer conexiones seguras con el servidor.
     */

    public TCPClient(String serverAddress, int serverPort, Properties config) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;

        String ksRoute = config.getProperty("SSL_CERTIFICATE_ROUTE");
        String ksPassword = config.getProperty("SSL_PASSWORD");

        if (ksRoute == null || ksPassword == null) {
            throw new IllegalArgumentException("Config file must define SSL_CERTIFICATE_ROUTE and SSL_PASSWORD");
        }

        System.setProperty("javax.net.ssl.keyStore", ksRoute);
        System.setProperty("javax.net.ssl.keyStorePassword", ksPassword);
        System.setProperty("javax.net.ssl.keyStoreType", "PKCS12");

        System.setProperty("javax.net.ssl.trustStore", ksRoute);
        System.setProperty("javax.net.ssl.trustStorePassword", ksPassword);
        System.setProperty("javax.net.ssl.trustStoreType", "PKCS12");
    }

    /*Establecer una conexión segura SSL/TLS con el servidor TCP.*/
    public void connect() throws IOException {
        SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        this.clientSocket = sslSocketFactory.createSocket(serverAddress, serverPort);
        System.out.println("Connected to server: " + this.serverAddress + ":" + this.serverPort);

        this.dataInputStream = new DataInputStream(this.clientSocket.getInputStream());
        this.dataOutputStream = new DataOutputStream(this.clientSocket.getOutputStream());
    }

    /*
     *  // Objetivo //
     *     Enviar un mensaje al servidor TCP de forma segura y recibir la respuesta.
     */
    public void sendMessage(String message) {
        try {
            this.connect();
            System.out.println("Sending: " + message);
            this.dataOutputStream.writeUTF(message);

            String response = this.dataInputStream.readUTF();
            System.out.println("Response: " + response);
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            this.closeConnection();
        }
    }

    /*
     *  // Objetivo //
     *     Cerrar de manera segura la conexión TCP y los flujos asociados con el servidor.
     */
    public void closeConnection() {
        try {
            if (this.dataInputStream != null) this.dataInputStream.close();
            if (this.dataOutputStream != null) this.dataOutputStream.close();
            if (this.clientSocket != null) this.clientSocket.close();
        } catch (IOException e) {
            System.out.println("Error closing connection: " + e.getMessage());
        }
    }
}
