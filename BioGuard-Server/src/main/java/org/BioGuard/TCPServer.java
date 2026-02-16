package org.BioGuard;

import com.google.gson.Gson;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;


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
 *         1. Crea un SSLServerSocket en el puerto definido.
 *         2. Escucha conexiones entrantes en un bucle infinito.
 *         3. Para cada cliente:
 *            a) Crea flujos de entrada y salida de datos.
 *            b) Lee un mensaje en formato UTF (JSON de Patient).
 *            c) Convierte el JSON a un objeto Patient usando Gson.
 *            d) Procesa el paciente llamando a PatientHandler.processPatient.
 *            e) Envía la respuesta al cliente con las enfermedades detectadas.
 *            f) Maneja excepciones de parsing o de conexión y cierra el socket del cliente.
 *  // Salidas //
 *     Muestra en consola información de los pacientes recibidos, resultados del procesamiento
 *     y mensajes de error si ocurren problemas.
 *  // Excepciones //
 *     Captura IOException al crear el socket o al aceptar conexiones, mostrando un mensaje en consola.
 */

public class TCPServer {
    private int serverPort;

    public TCPServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() {
        try {
            SSLServerSocketFactory sslSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslSocketFactory.createServerSocket(serverPort);
            System.out.println("Server started on port: " + serverPort);

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

                String message = dis.readUTF();
                System.out.println("Received raw: " + message);

                // Convertir de JSON a Patient
                Gson gson = new Gson();
                try {
                    Patient patient = gson.fromJson(message, Patient.class);
                    System.out.println("Procesando paciente: " + patient.getFull_name());
                    List<String> diseases = PatientHandler.processPatient(patient);

                    System.out.println("Enfermedades detectadas: " + (diseases.isEmpty() ? "Ninguna" : String.join(", ", diseases)));
                    out.writeUTF("Paciente " + patient.getFull_name() + " procesado. Enfermedades detectadas: " +
                            (diseases.isEmpty() ? "Ninguna" : String.join(", ", diseases)));
                } catch (Exception e) {
                    System.out.println("Error al parsear JSON: " + e.getMessage());
                    out.writeUTF("Error al procesar el paciente.");
                }

                clientSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }
}