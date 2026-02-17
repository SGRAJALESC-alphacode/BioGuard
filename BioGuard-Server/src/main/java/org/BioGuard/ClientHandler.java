package org.BioGuard;

import com.google.gson.Gson;
<<<<<<< HEAD
import com.google.gson.reflect.TypeToken;
import org.BioGuard.exception.*;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
=======
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.util.List;
>>>>>>> main

public class ClientHandler implements Runnable {
    private SSLSocket socket;
    private Gson gson = new Gson();
<<<<<<< HEAD
    private PacienteService pacienteService = new PacienteService();
    private VirusService virusService = new VirusService();
    private DiagnosticoService diagnosticoService = new DiagnosticoService();
=======
>>>>>>> main

    public ClientHandler(SSLSocket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(socket.getInputStream());
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

<<<<<<< HEAD
=======
            // Formato de comunicación: COMANDO|JSON
>>>>>>> main
            String request = in.readUTF();
            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";

<<<<<<< HEAD
            String response = procesarComando(command, payload);
            out.writeUTF(response);

        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    private String procesarComando(String command, String payload) {
        try {
            return switch (command) {
                case "REGISTRAR_PACIENTE" -> registrarPaciente(payload);
                case "CONSULTAR_PACIENTE" -> consultarPaciente(payload);
                case "CARGAR_VIRUS" -> cargarVirus(payload);
                case "DIAGNOSTICAR" -> diagnosticarMuestra(payload);
                case "REPORTE_ALTO_RIESGO" -> generarReporteAltoRiesgo();
                case "REPORTE_MUTACIONES" -> generarReporteMutaciones(payload);
                default -> "ERROR: Comando desconocido";
            };
        } catch (PacienteDuplicadoException e) {
            return "ERROR: " + e.getMessage();
        } catch (FormatoFastaInvalidoException e) {
            return "ERROR: " + e.getMessage();
        } catch (MuestraNoEncontradaException e) {
            return "ERROR: " + e.getMessage();
        } catch (DiagnosticoException e) {
            return "ERROR: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: Error interno";
        }
    }

    private String registrarPaciente(String payload) throws PacienteDuplicadoException, IOException {
        Paciente p = gson.fromJson(payload, Paciente.class);
        pacienteService.registrarPaciente(p);
        return "OK: Paciente registrado";
    }

    private String consultarPaciente(String payload) {
        try {
            Paciente p = pacienteService.consultarPaciente(payload);
            return p != null ? gson.toJson(p) : "ERROR: No encontrado";
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String cargarVirus(String payload) throws FormatoFastaInvalidoException, IOException {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> data = gson.fromJson(payload, type);
        virusService.guardarVirus(data);
        return "OK: Virus guardado";
    }

    private String diagnosticarMuestra(String payload) throws MuestraNoEncontradaException, DiagnosticoException, IOException {
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> data = gson.fromJson(payload, type);

        List<Diagnostico> resultados = diagnosticoService.diagnosticarMuestra(
                data.get("documento"),
                data.get("fecha_muestra"),
                data.get("secuencia")
        );

        return resultados.isEmpty() ? "No se detectaron virus" : "Virus detectados: " + resultados.size();
    }

    private String generarReporteAltoRiesgo() {
        try {
            return "OK: Reporte en " + diagnosticoService.generarReporteAltoRiesgo();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String generarReporteMutaciones(String payload) {
        try {
            return "OK: Reporte en " + diagnosticoService.generarReporteMutaciones(payload);
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
=======
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
>>>>>>> main
        }
    }
}