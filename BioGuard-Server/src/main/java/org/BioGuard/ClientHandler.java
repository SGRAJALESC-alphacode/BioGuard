package org.BioGuard;

/*
 * // Objetivo
 *    Gestionar una conexión de cliente en el servidor: leer solicitudes en formato
 *    COMANDO|PAYLOAD, despachar a los servicios correspondientes y devolver la
 *    respuesta por el socket SSL.
 *
 * // Atributos principales
 *    socket             : SSLSocket que representa la conexión cliente-servidor.
 *    gson               : Gson para serializar/deserializar JSON.
 *    pacienteService    : Servicio para operaciones sobre pacientes (registro, consulta).
 *    virusService       : Servicio para operaciones sobre virus (guardar, cargar).
 *    diagnosticoService : Servicio para diagnóstico de muestras y generación de reportes.
 *
 * // Comportamiento / métodos
 *    run()              : Ciclo de vida del handler; lee request, parsea comando y payload,
 *                         delega al método procesarComando() y escribe la respuesta.
 *    procesarComando()  : Dispatchea comandos como REGISTRAR_PACIENTE, CONSULTAR_PACIENTE,
 *                         CARGAR_VIRUS, DIAGNOSTICAR, REPORTE_ALTO_RIESGO, REPORTE_MUTACIONES.
 */

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.BioGuard.exception.*;
import javax.net.ssl.SSLSocket;
import java.io.*;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private final SSLSocket socket;
    private final Gson gson = new Gson();
    private final PacienteService pacienteService = new PacienteService();
    private final VirusService virusService = new VirusService();
    private final DiagnosticoService diagnosticoService = new DiagnosticoService();

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

            String response = procesarComando(command, payload);
            out.writeUTF(response);

        } catch (IOException e) {
            System.err.println("Error I/O cliente: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
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
        } catch (PacienteDuplicadoException | FormatoFastaInvalidoException | MuestraNoEncontradaException | DiagnosticoException e) {
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
        }
    }
}