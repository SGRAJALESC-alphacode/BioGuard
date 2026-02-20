package org.BioGuard.handler;

import org.BioGuard.model.Paciente;
import org.BioGuard.service.IPacienteService;
import org.BioGuard.exception.PacienteDuplicadoException;
import java.util.Optional;

public class PacienteCommandHandler {

    private final IPacienteService service;
    private final ResponseFormatter fmt;

    public PacienteCommandHandler(IPacienteService service, ResponseFormatter fmt) {
        this.service = service;
        this.fmt = fmt;
    }

    public String handleRegistroSimple(String data) {
        try {
            String[] partes = data.split("\\|");
            if (partes.length < 5) {
                return fmt.error("Formato: id|nombre|edad|genero|telefono");
            }

            Paciente paciente = new Paciente();
            paciente.setDocumento(partes[0].trim());

            // Separar nombre y apellido si viene compuesto
            String nombreCompleto = partes[1].trim();
            String[] nombreParts = nombreCompleto.split(" ", 2);
            paciente.setNombre(nombreParts[0]);
            paciente.setApellido(nombreParts.length > 1 ? nombreParts[1] : "No especificado");

            paciente.setEdad(Integer.parseInt(partes[2].trim()));
            paciente.setGenero(partes[3].trim());
            paciente.setCorreo(partes[4].trim() + "@ejemplo.com");
            paciente.setCiudad("No especificada");
            paciente.setPais("No especificado");

            Paciente registrado = service.registrarPaciente(paciente);
            return fmt.pacienteRegistrado(registrado);

        } catch (PacienteDuplicadoException e) {
            return fmt.error(e.getMessage());
        } catch (NumberFormatException e) {
            return fmt.error("Edad debe ser número");
        } catch (Exception e) {
            return fmt.error(e.getMessage());
        }
    }

    public String handleRegistroCompleto(String data) {
        try {
            String[] partes = data.split(",");
            if (partes.length < 8) {
                return fmt.error("Requiere 8 campos separados por coma");
            }

            Paciente paciente = new Paciente(
                    partes[0].trim(),
                    partes[1].trim(),
                    partes[2].trim(),
                    Integer.parseInt(partes[3].trim()),
                    partes[4].trim(),
                    partes[5].trim(),
                    partes[6].trim(),
                    partes[7].trim()
            );

            Paciente registrado = service.registrarPaciente(paciente);
            return fmt.pacienteRegistrado(registrado);

        } catch (PacienteDuplicadoException e) {
            return fmt.error(e.getMessage());
        } catch (NumberFormatException e) {
            return fmt.error("Edad debe ser número");
        } catch (Exception e) {
            return fmt.error(e.getMessage());
        }
    }

    public String handleConsulta(String documento) {
        Optional<Paciente> opt = service.buscarPorDocumento(documento.trim());
        return opt.map(fmt::pacienteInfo)
                .orElse(fmt.error("Paciente no encontrado"));
    }

    public String handleListar(String ignorado) {
        return fmt.listaPacientes(service.listarTodos());
    }
}