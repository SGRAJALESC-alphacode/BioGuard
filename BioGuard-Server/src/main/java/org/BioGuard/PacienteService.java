package org.BioGuard;

import org.BioGuard.exception.PacienteDuplicadoException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Servicio para gestión de pacientes.
 * Persistencia exclusiva en archivo CSV.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class PacienteService {

    private static final String PACIENTES_CSV = "data/pacientes/pacientes.csv";

    /**
     * Registra un nuevo paciente.
     *
     * @param paciente El paciente a registrar
     * @throws PacienteDuplicadoException Si el documento ya existe
     * @throws IOException Si hay error de escritura
     */
    public void registrarPaciente(Paciente paciente) throws PacienteDuplicadoException, IOException {
        if (consultarPaciente(paciente.getDocumento()) != null) {
            throw new PacienteDuplicadoException(paciente.getDocumento());
        }

        validarPaciente(paciente);

        File csvFile = new File(PACIENTES_CSV);
        csvFile.getParentFile().mkdirs();

        if (!csvFile.exists()) {
            Files.writeString(csvFile.toPath(), "documento,nombre,apellido,edad,correo,genero,ciudad,pais\n");
        }

        String linea = String.format("%s,%s,%s,%d,%s,%s,%s,%s\n",
                paciente.getDocumento(),
                paciente.getNombre(),
                paciente.getApellido(),
                paciente.getEdad(),
                paciente.getCorreo(),
                paciente.getGenero(),
                paciente.getCiudad(),
                paciente.getPais()
        );

        Files.writeString(csvFile.toPath(), linea, StandardOpenOption.APPEND);
    }

    /**
     * Consulta un paciente por documento.
     *
     * @param documento Documento a buscar
     * @return Paciente encontrado o null
     * @throws IOException Si hay error de lectura
     */
    public Paciente consultarPaciente(String documento) throws IOException {
        File csvFile = new File(PACIENTES_CSV);
        if (!csvFile.exists()) return null;

        List<String> lineas = Files.readAllLines(csvFile.toPath());

        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length >= 8 && partes[0].equals(documento)) {
                return crearPaciente(partes);
            }
        }
        return null;
    }

    /**
     * Lista todos los pacientes.
     *
     * @return Lista de pacientes
     * @throws IOException Si hay error de lectura
     */
    public List<Paciente> listarTodos() throws IOException {
        List<Paciente> pacientes = new ArrayList<>();
        File csvFile = new File(PACIENTES_CSV);

        if (!csvFile.exists()) return pacientes;

        List<String> lineas = Files.readAllLines(csvFile.toPath());

        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length >= 8) {
                pacientes.add(crearPaciente(partes));
            }
        }
        return pacientes;
    }

    /**
     * Valida los datos del paciente.
     */
    private void validarPaciente(Paciente p) {
        if (p.getDocumento() == null || p.getDocumento().trim().isEmpty())
            throw new IllegalArgumentException("Documento obligatorio");
        if (p.getNombre() == null || p.getNombre().trim().isEmpty())
            throw new IllegalArgumentException("Nombre obligatorio");
        if (p.getApellido() == null || p.getApellido().trim().isEmpty())
            throw new IllegalArgumentException("Apellido obligatorio");
        if (p.getEdad() <= 0 || p.getEdad() > 150)
            throw new IllegalArgumentException("Edad inválida");
        if (p.getCorreo() == null || !p.getCorreo().contains("@"))
            throw new IllegalArgumentException("Correo inválido");
    }

    /**
     * Crea un paciente desde array de strings.
     */
    private Paciente crearPaciente(String[] partes) {
        Paciente p = new Paciente();
        p.setDocumento(partes[0]);
        p.setNombre(partes[1]);
        p.setApellido(partes[2]);
        p.setEdad(Integer.parseInt(partes[3]));
        p.setCorreo(partes[4]);
        p.setGenero(partes[5]);
        p.setCiudad(partes[6]);
        p.setPais(partes[7]);
        return p;
    }

    /**
     * Verifica si existe un paciente.
     */
    public boolean existePaciente(String documento) throws IOException {
        return consultarPaciente(documento) != null;
    }
}