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

            String request = in.readUTF();
            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";

            String response;
            switch (command) {
                case "CREATE":
                    Patient p = gson.fromJson(payload, Patient.class);
                    // Ahora PatientCRUD debe existir en el mismo paquete del servidor
                    PatientCRUD.createPatient(p);
                    response = "ÉXITO: Paciente " + p.getPatient_id() + " registrado en servidor.";
                    break;
                case "READ":
                    Patient found = PatientCRUD.readPatient(payload);
                    response = (found != null) ? gson.toJson(found) : "ERROR: Paciente no encontrado.";
                    break;
                case "ANALYZE":
                    Patient toAnalyze = gson.fromJson(payload, Patient.class);
                    // Lógica de análisis de ADN [cite: 17]
                    List<String> viruses = PatientHandler.processPatient(toAnalyze);
                    response = viruses.isEmpty() ? "RESULTADO: Ningún virus detectado."
                            : "RESULTADO: Virus detectados: " + String.join(", ", viruses);
                    break;
                default:
                    response = "ERROR: Comando desconocido.";
            }
            out.writeUTF(response);

        } catch (IOException e) {
            System.err.println("[ERROR CLIENTE] " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException e) { e.printStackTrace(); }
        }
    }
}