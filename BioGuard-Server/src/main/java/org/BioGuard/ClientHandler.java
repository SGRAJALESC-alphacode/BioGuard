package org.BioGuard;

import com.google.gson.Gson;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.List;

public class ClientHandler implements Runnable {
    private SSLSocket socket;
    private Gson gson = new Gson();

    public ClientHandler(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            // Formato de comunicación: COMANDO|JSON
            String request = in.readUTF();
            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";

            String response;
            switch (command) {
                case "CREATE" -> {
                    Patient p = gson.fromJson(payload, Patient.class);
                    PatientCRUD.createPatient(p); // Persistencia en Servidor [cite: 31, 32]
                    response = "OK: Paciente " + p.getPatient_id() + " registrado.";
                }
                case "READ" -> {
                    Patient found = PatientCRUD.readPatient(payload);
                    response = (found != null) ? gson.toJson(found) : "ERROR: Paciente no encontrado.";
                }
                case "ANALYZE" -> {
                    Patient toAnalyze = gson.fromJson(payload, Patient.class);
                    List<String> results = PatientHandler.processPatient(toAnalyze); // Análisis de ADN [cite: 17]
                    response = "ANÁLISIS: " + (results.isEmpty() ? "No se detectaron virus." : String.join(", ", results));
                }
                default -> response = "ERROR: Comando desconocido.";
            }
            out.writeUTF(response);

        } catch (IOException e) {
            System.err.println("[INFO] Cliente desconectado.");
        } finally {
            try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }
}