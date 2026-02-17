package org.BioGuard;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
<<<<<<< HEAD
<<<<<<< Updated upstream
import java.util.List;
import org.BioGuard.exceptions.GeneticAnalysisException;
import org.BioGuard.exceptions.ServerCommunicationException;
=======
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
>>>>>>> Stashed changes
=======
>>>>>>> main

public class TCPServer {
    private int serverPort;
    private ExecutorService threadPool;

    public TCPServer(int serverPort) {
        this.serverPort = serverPort;
        this.threadPool = Executors.newCachedThreadPool();
    }

    public void start() {
        try {
            // REQUERIMIENTO: Uso obligatorio de SSL [cite: 28]
            SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslSocketFactory.createServerSocket(serverPort);
<<<<<<< HEAD
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
=======
            System.out.println("[SERVIDOR] Escuchando en puerto " + serverPort + " con SSL...");

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                // REQUERIMIENTO: Concurrencia mediante Threads nativos [cite: 29]
                new Thread(new ClientHandler(clientSocket)).start();
>>>>>>> main
            }

        } catch (IOException e) {
            System.err.println("[ERROR SERVIDOR] " + e.getMessage());
        }
    }
}