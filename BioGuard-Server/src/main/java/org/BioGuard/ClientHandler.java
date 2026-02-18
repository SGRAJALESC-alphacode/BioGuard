package org.BioGuard;

import org.BioGuard.exception.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Manejador de peticiones de clientes (SIN SSL)
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final PacienteService pacienteService;
    private final VirusService virusService;
    private final DiagnosticoService diagnosticoService;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.pacienteService = new PacienteService();
        this.virusService = new VirusService();
        this.diagnosticoService = new DiagnosticoService();
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request = in.readLine();
            if (request == null) return;

            System.out.println("Comando recibido: " + request);
            String[] parts = request.split("\\|");
            String command = parts[0];

            String response = procesarComando(command, parts);
            out.println(response);

        } catch (IOException e) {
            System.err.println("Error I/O cliente: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    private String procesarComando(String command, String[] parts) {
        try {
            switch (command) {
                case "REGISTRAR_PACIENTE":
                    return registrarPaciente(parts);
                case "CONSULTAR_PACIENTE":
                    return consultarPaciente(parts);
                case "CARGAR_VIRUS":
                    return cargarVirus(parts);
                case "DIAGNOSTICAR":
                    return diagnosticarMuestra(parts);
                case "REPORTE_ALTO_RIESGO":
                    return generarReporteAltoRiesgo();
                case "REPORTE_MUTACIONES":
                    return generarReporteMutaciones(parts);
                default:
                    return "ERROR: Comando desconocido";
            }
        } catch (PacienteDuplicadoException e) {
            return "ERROR_DUPLICADO: " + e.getMessage();
        } catch (FormatoFastaInvalidoException e) {
            return "ERROR_FASTA: " + e.getMessage();
        } catch (MuestraNoEncontradaException e) {
            return "ERROR_MUESTRA: " + e.getMessage();
        } catch (DiagnosticoException e) {
            return "ERROR_DIAGNOSTICO: " + e.getMessage();
        } catch (IOException e) {
            return "ERROR_IO: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR_INTERNO: " + e.getMessage();
        }
    }

    private String registrarPaciente(String[] parts) throws PacienteDuplicadoException, IOException {
        if (parts.length < 9) return "ERROR: Faltan datos";

        Paciente p = new Paciente();
        p.setDocumento(parts[1]);
        p.setNombre(parts[2]);
        p.setApellido(parts[3]);
        p.setEdad(Integer.parseInt(parts[4]));
        p.setCorreo(parts[5]);
        p.setGenero(parts[6]);
        p.setCiudad(parts[7]);
        p.setPais(parts[8]);

        pacienteService.registrarPaciente(p);
        return "OK: Paciente " + p.getDocumento() + " registrado";
    }

    private String consultarPaciente(String[] parts) throws IOException {
        if (parts.length < 2) return "ERROR: Documento requerido";

        Paciente p = pacienteService.consultarPaciente(parts[1]);
        if (p == null) return "ERROR: Paciente no encontrado";

        return String.join("|",
                p.getDocumento(), p.getNombre(), p.getApellido(),
                String.valueOf(p.getEdad()), p.getCorreo(),
                p.getGenero(), p.getCiudad(), p.getPais()
        );
    }

    private String cargarVirus(String[] parts) throws FormatoFastaInvalidoException, IOException {
        if (parts.length < 4) return "ERROR: Faltan datos";

        virusService.guardarVirus(parts[1], parts[2], parts[3]);
        return "OK: Virus " + parts[1] + " guardado";
    }

    private String diagnosticarMuestra(String[] parts) throws MuestraNoEncontradaException, DiagnosticoException, IOException {
        if (parts.length < 4) return "ERROR: Faltan datos";

        List<Diagnostico> resultados = diagnosticoService.diagnosticarMuestra(
                parts[1], parts[2], parts[3]
        );

        if (resultados.isEmpty()) return "RESULTADO: No se detectaron virus";

        StringBuilder sb = new StringBuilder("RESULTADO:");
        for (Diagnostico d : resultados) {
            sb.append("|").append(d.toString());
        }
        return sb.toString();
    }

    private String generarReporteAltoRiesgo() throws IOException {
        String ruta = diagnosticoService.generarReporteAltoRiesgo();
        return "OK: Reporte en " + ruta;
    }

    private String generarReporteMutaciones(String[] parts) throws MuestraNoEncontradaException, IOException {
        if (parts.length < 2) return "ERROR: Documento requerido";

        String ruta = diagnosticoService.generarReporteMutaciones(parts[1]);
        return "OK: Reporte en " + ruta;
    }
}