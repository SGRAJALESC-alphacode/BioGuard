package org.BioGuard.handler;

import org.BioGuard.model.Paciente;
import org.BioGuard.service.IPacienteService;
import org.BioGuard.exception.PacienteDuplicadoException;

import java.util.Optional;

/**
 * Manejador de comandos relacionados con pacientes.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class PacienteCommandHandler {

    private final IPacienteService pacienteService;

    public PacienteCommandHandler(IPacienteService pacienteService) {
        this.pacienteService = pacienteService;
    }

    public String handleRegistroSimple(String datos) {
        try {
            String[] partes = datos.split("\\|");
            if (partes.length < 5) {
                return "ERROR: Formato inválido. Se esperaba: id|nombre|edad|genero|telefono";
            }

            Paciente paciente = new Paciente();
            paciente.setDocumento(partes[0].trim());

            String[] nombreParts = partes[1].trim().split(" ", 2);
            paciente.setNombre(nombreParts[0]);
            paciente.setApellido(nombreParts.length > 1 ? nombreParts[1] : "No especificado");

            paciente.setEdad(Integer.parseInt(partes[2].trim()));
            paciente.setGenero(partes[3].trim());
            paciente.setCorreo(partes[4].trim() + "@ejemplo.com");
            paciente.setCiudad("No especificada");
            paciente.setPais("No especificado");

            Paciente registrado = pacienteService.registrarPaciente(paciente);
            return "PACIENTE_REGISTRADO:" + registrado.getDocumento();

        } catch (PacienteDuplicadoException e) {
            return "ERROR: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "ERROR: Edad debe ser un número";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String handleRegistroCompleto(String datos) {
        try {
            String[] partes = datos.split(",");
            if (partes.length < 8) {
                return "ERROR: Se requieren 8 campos: documento,nombre,apellido,edad,correo,genero,ciudad,pais";
            }

            Paciente paciente = new Paciente(
                    partes[0].trim(), partes[1].trim(), partes[2].trim(),
                    Integer.parseInt(partes[3].trim()),
                    partes[4].trim(), partes[5].trim(), partes[6].trim(), partes[7].trim()
            );

            Paciente registrado = pacienteService.registrarPaciente(paciente);
            return "PACIENTE_REGISTRADO:" + registrado.getDocumento();

        } catch (PacienteDuplicadoException e) {
            return "ERROR: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "ERROR: Edad debe ser un número";
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    public String handleConsulta(String documento) {
        Optional<Paciente> opt = pacienteService.buscarPorDocumento(documento.trim());
        if (opt.isPresent()) {
            Paciente p = opt.get();
            return String.format("PACIENTE:%s,%s,%s,%d,%s,%s,%s,%s",
                    p.getDocumento(), p.getNombre(), p.getApellido(), p.getEdad(),
                    p.getCorreo(), p.getGenero(), p.getCiudad(), p.getPais());
        } else {
            return "ERROR: Paciente no encontrado";
        }
    }

    public String handleListar(String ignorado) {
        var pacientes = pacienteService.listarTodos();
        if (pacientes.isEmpty()) {
            return "No hay pacientes registrados";
        }

        StringBuilder sb = new StringBuilder("PACIENTES:");
        for (Paciente p : pacientes) {
            sb.append(String.format("\n%s,%s,%s,%d,%s",
                    p.getDocumento(), p.getNombre(), p.getApellido(), p.getEdad(), p.getCorreo()));
        }
        return sb.toString();
    }
}