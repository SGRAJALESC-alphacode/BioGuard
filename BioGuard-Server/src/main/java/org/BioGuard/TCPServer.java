package org.BioGuard;

import com.google.gson.Gson;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
<<<<<<< Updated upstream
import java.util.List;
import org.BioGuard.exceptions.GeneticAnalysisException;
import org.BioGuard.exceptions.ServerCommunicationException;
=======
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
>>>>>>> Stashed changes


/*
 *  // Objetivo //
 *     Implementar un servidor TCP seguro (SSL/TLS) capaz de recibir objetos Patient en formato JSON,
 *     procesarlos para detectar enfermedades y enviar una respuesta al cliente.
 *  // Atributos //
 *     serverPort : Puerto en el que el servidor escuchará conexiones entrantes.
 *  // Constructor //
 *     TCPServer(int serverPort) : Inicializa el servidor con el puerto especificado.
 *  // Métodos //
 *     start() :
 *         Objetivo: Escuchar conexiones entrantes de clientes, procesar sus pacientes y enviar respuestas.
 *         Proceso:
 *            1. Crea un SSLServerSocket en el puerto definido.
 *            2. Escucha conexiones entrantes en un bucle infinito.
 *            3. Para cada cliente:
 *               a) Crea flujos de entrada y salida de datos.
 *               b) Lee un mensaje en formato UTF (JSON de Patient).
 *               c) Convierte el JSON a un objeto Patient usando Gson.
 *               d) Procesa el paciente llamando a PatientHandler.processPatient.
 *               e) Envía la respuesta al cliente con las enfermedades detectadas.
 *               f) Maneja excepciones de parsing o de conexión y cierra el socket del cliente.
 *         Salidas: Muestra en consola información de los pacientes recibidos, resultados del procesamiento
 *                  y mensajes de error si ocurren problemas.
 *  // Excepciones //
 *     Captura IOException al crear el socket o al aceptar conexiones, mostrando un mensaje en consola.
 */
public class TCPServer {
    private int serverPort;
    private ExecutorService threadPool;

    /*
     *  // Objetivo //
     *     Inicializar el servidor TCP con el puerto en el que escuchará conexiones entrantes.
     *  // Entradas //
     *     serverPort : Puerto en el que se ejecutará el servidor (generalmente 2020).
     *  // Proceso //
     *     Asigna el puerto especificado al atributo de clase.
     *  // Salidas //
     *     Ninguna, pero prepara el servidor para ser iniciado.
     */
    public TCPServer(int serverPort) {
        this.serverPort = serverPort;
        this.threadPool = Executors.newCachedThreadPool();
    }

    /*
     *  // Objetivo //
     *     Escuchar conexiones entrantes de clientes de forma segura y procesar sus datos.
     *  // Entradas //
     *     Ninguna, se ejecuta indefinidamente esperando conexiones entrantes.
     *  // Proceso //
     *     1. Crea un SSLServerSocket en el puerto configurado.
     *     2. Entra en un bucle infinito esperando conexiones de clientes.
     *     3. Para cada cliente que se conecta:
     *        a) Acepta la conexión SSL.
     *        b) Crea flujos de entrada (DataInputStream) y salida (DataOutputStream).
     *        c) Lee el mensaje JSON enviado por el cliente.
     *        d) Convierte el JSON a un objeto Patient usando Gson.
     *        e) Procesa el paciente llamando a PatientHandler.processPatient().
     *        f) Muestra en consola las enfermedades detectadas.
     *        g) Envía al cliente una respuesta con los resultados del análisis.
     *        h) Maneja excepciones de conversión JSON mostrando un mensaje de error.
     *        i) Cierra el socket del cliente.
     *  // Salidas //
     *     Mensajes en consola indicando: conexión establecida, paciente procesado,
     *     enfermedades detectadas y respuesta enviada al cliente.
     *  // Excepciones //
     *     Captura IOException y muestra un mensaje de error si hay problemas con el socket o conexión.
     */
    public void start() {
        try {
            SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslSocketFactory.createServerSocket(serverPort);
<<<<<<< Updated upstream
            System.out.println("Server started on port: " + serverPort);

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                String clientInfo = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
                System.out.println("Cliente conectado: " + clientInfo);

                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                try {
                    String message = dis.readUTF();
                    System.out.println("Received raw: " + message);

                    // Convertir de JSON a Patient
                    Gson gson = new Gson();
                    try {
                        Patient patient = gson.fromJson(message, Patient.class);
                        if (patient == null || patient.getPatient_id() == null || patient.getPatient_id().isEmpty()) {
                            throw new ServerCommunicationException(clientInfo, "Datos de paciente inválidos o nulos en JSON");
                        }

                        System.out.println("Procesando paciente: " + patient.getFull_name());

                        try {
                            List<String> diseases = PatientHandler.processPatient(patient);
                            System.out.println("Enfermedades detectadas: " + (diseases.isEmpty() ? "Ninguna" : String.join(", ", diseases)));
                            out.writeUTF("Paciente " + patient.getFull_name() + " procesado. Enfermedades detectadas: " +
                                    (diseases.isEmpty() ? "Ninguna" : String.join(", ", diseases)));
                        } catch (GeneticAnalysisException e) {
                            System.out.println("Error en análisis genético: " + e.getMessage());
                            out.writeUTF("Error en análisis genético del paciente: " + e.getMessage());
                        }
                    } catch (ServerCommunicationException e) {
                        System.out.println("Error de comunicación: " + e.getMessage());
                        out.writeUTF("Error en los datos del paciente: " + e.getMessage());
                    } catch (Exception e) {
                        System.out.println("Error al parsear JSON: " + e.getMessage());
                        out.writeUTF("Error al procesar el paciente: datos JSON inválidos");
                    }

                    clientSocket.close();
                } catch (IOException e) {
                    System.out.println("Error en I/O con cliente: " + e.getMessage());
                }
=======

            System.out.println("========================================");
            System.out.println("[SERVIDOR BIOGUARD]");
            System.out.println("[ESTADO] ACTIVO");
            System.out.println("[PUERTO] " + serverPort + " (SSL/TLS)");
            System.out.println("[HILOS] Pool de hilos activo");
            System.out.println("========================================");

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                System.out.println("[CONEXIÓN] Cliente conectado: " + clientSocket.getInetAddress());

                // Usar pool de hilos para mejor concurrencia
                threadPool.execute(new ClientHandler(clientSocket));
>>>>>>> Stashed changes
            }

        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
    }
