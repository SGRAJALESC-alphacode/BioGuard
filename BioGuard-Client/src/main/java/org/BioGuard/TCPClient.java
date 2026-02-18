package org.BioGuard;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.util.Properties;

/**
 * Cliente TCP del sistema BioGuard con soporte SSL/TLS.
 * Esta clase maneja la comunicación del cliente con el servidor,
 * estableciendo conexiones seguras y enviando solicitudes.
 *
 * // Objetivo
 *    Proporcionar una interfaz de comunicación segura entre el
 *    cliente y el servidor BioGuard, manejando la configuración
 *    SSL, el envío de comandos y la recepción de respuestas.
 *
 * // Características
 *    - Comunicación segura mediante SSL/TLS
 *    - Configuración de truststore para validación del servidor
 *    - Búsqueda multi-ruta del archivo truststore
 *    - Timeout configurable en las conexiones
 *    - Manejo de errores de conexión
 *
 * // Atributos
 *    serverAddress : Dirección IP del servidor
 *    serverPort    : Puerto del servidor
 *
 * // Flujo de comunicación
 *    1. Configurar truststore con certificado del servidor
 *    2. Establecer conexión SSL con el servidor
 *    3. Enviar comando en formato COMANDO|payload
 *    4. Recibir respuesta del servidor
 *    5. Cerrar conexión
 *
 * // Formato de comunicación
 *    Solicitud:  COMANDO|param1|param2|...
 *    Respuesta:  OK: mensaje
 *                ERROR: mensaje
 *                RESULTADO: datos
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class TCPClient {

    private final String serverAddress;
    private final int serverPort;

    /**
     * Constructor del cliente TCP.
     *
     * // Objetivo
     *    Inicializar el cliente con la dirección y puerto del servidor,
     *    y configurar el truststore para la conexión SSL.
     *
     * // Proceso de configuración SSL
     *    1. Obtener ruta del truststore desde configuración
     *    2. Buscar el archivo en múltiples ubicaciones
     *    3. Configurar propiedades del sistema:
     *       - javax.net.ssl.trustStore
     *       - javax.net.ssl.trustStorePassword
     *       - javax.net.ssl.trustStoreType
     *
     * // Búsqueda del truststore
     *    El truststore se busca en:
     *    - Ruta exacta especificada en configuración
     *    - resources/certs/truststore.jks
     *    - src/main/resources/certs/truststore.jks
     *    - certs/truststore.jks (raíz del cliente)
     *    - BioGuard-Client/src/main/resources/certs/truststore.jks
     *    - BioGuard-Client/certs/truststore.jks
     *
     * @param address Dirección del servidor
     * @param port Puerto del servidor
     * @param config Configuración con datos SSL
     */
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
     * Busca el truststore en múltiples ubicaciones.
     *
     * // Objetivo
     *    Localizar el archivo truststore en diferentes ubicaciones
     *    comunes para adaptarse a distintos entornos de ejecución.
     *
     * // Estrategia de búsqueda
     *    1. Ruta exacta del properties
     *    2. En resources del cliente (classpath)
     *    3. src/main/resources/certs/truststore.jks
     *    4. certs/truststore.jks (raíz del cliente)
     *    5. Ruta absoluta desde user.dir
     *    6. En carpeta BioGuard-Client/certs
     *
     * @param ruta Ruta original del truststore
     * @return Archivo del truststore o null si no existe
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
            // Ignorar errores de recursos
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

    /**
     * Envía una solicitud al servidor y espera la respuesta.
     *
     * // Objetivo
     *    Establecer una conexión SSL con el servidor, enviar un comando
     *    con sus parámetros y recibir la respuesta procesada.
     *
     * // Proceso
     *    1. Crear socket SSL y conectar al servidor
     *    2. Configurar timeout de 10 segundos
     *    3. Preparar flujos de entrada/salida con codificación UTF-8
     *    4. Enviar solicitud en formato COMANDO|payload
     *    5. Leer respuesta del servidor (una línea)
     *    6. Retornar respuesta o mensaje de error
     *
     * // Manejo de errores
     *    - Timeout: Si el servidor no responde en 10 segundos
     *    - IOError: Problemas de red o conexión
     *    - SSLException: Errores de certificados
     *
     * @param command Comando a ejecutar
     * @param payload Parámetros del comando
     * @return Respuesta del servidor o mensaje de error
     */
    public String sendRequest(String command, String payload) {
        SSLSocket socket = null;
        BufferedReader in = null;
        PrintWriter out = null;

        try {
            System.out.println("[DEBUG] Conectando a " + serverAddress + ":" + serverPort);

            // Crear socket SSL
            SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            socket = (SSLSocket) factory.createSocket(serverAddress, serverPort);

            // Configurar timeout
            socket.setSoTimeout(10000);

            // Flujos de comunicación con UTF-8
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));

            // Enviar solicitud
            String request = command + "|" + payload;
            System.out.println("[DEBUG] Enviando: " + request);
            out.println(request);

            // Recibir respuesta
            String response = in.readLine();
            System.out.println("[DEBUG] Respuesta: " + response);

            return response != null ? response : "ERROR: Respuesta vacía";

        } catch (java.net.SocketTimeoutException e) {
            return "ERROR: Timeout - El servidor no respondió";
        } catch (javax.net.ssl.SSLHandshakeException e) {
            return "ERROR SSL: Problema de certificados - " + e.getMessage();
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            return "ERROR DE CONEXIÓN: " + e.getMessage();
        } finally {
            // Cerrar recursos en orden inverso
            try { if (out != null) out.close(); } catch (Exception e) {}
            try { if (in != null) in.close(); } catch (Exception e) {}
            try { if (socket != null) socket.close(); } catch (Exception e) {}
        }
    }
}