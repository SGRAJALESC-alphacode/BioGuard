package org.BioGuard;

/*
 * // Objetivo
 *    Servicio encargado de la lógica de negocio relacionada con pacientes:
 *    registro, consulta, listado y validaciones sobre `data/pacientes/pacientes.csv`.
 *
 * // Responsabilidades
 *    - validar y registrar nuevos pacientes (evitar duplicados)
 *    - consultar pacientes por documento
 *    - listar todos los pacientes
 *    - contar pacientes
 *
 * // Persistencia
 *    Usa un CSV (`data/pacientes/pacientes.csv`) como almacenamiento principal.
 */

import org.BioGuard.exception.PacienteDuplicadoException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PacienteService {

    private static final String PACIENTES_CSV = "data/pacientes/pacientes.csv";

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * @param paciente El paciente a registrar
     * @return true si el registro fue exitoso
     * @throws PacienteDuplicadoException Si ya existe un paciente con el mismo documento
     * @throws IOException Si hay error de escritura
     */
    public boolean registrarPaciente(Paciente paciente) throws PacienteDuplicadoException, IOException {
        // Validar que el documento no esté duplicado
        if (consultarPaciente(paciente.getDocumento()) != null) {
            throw new PacienteDuplicadoException(paciente.getDocumento());
        }

        // Validar datos básicos
        validarPaciente(paciente);

        // Crear directorio si no existe
        File csvFile = new File(PACIENTES_CSV);
        csvFile.getParentFile().mkdirs();

        // Crear archivo con cabecera si no existe
        if (!csvFile.exists()) {
            Files.writeString(csvFile.toPath(), "documento,nombre,apellido,edad,correo,genero,ciudad,pais\n");
        }

        // Agregar paciente al archivo
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

        return true;
    }

    /**
     * Consulta un paciente por su documento.
     *
     * @param documento El documento del paciente a buscar
     * @return El paciente encontrado o null si no existe
     * @throws IOException Si hay error de lectura
     */
    public Paciente consultarPaciente(String documento) throws IOException {
        File csvFile = new File(PACIENTES_CSV);

        if (!csvFile.exists()) {
            return null;
        }

        List<String> lineas = Files.readAllLines(csvFile.toPath());

        // Saltar cabecera (índice 0)
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length >= 8 && partes[0].equals(documento)) {
                return crearPacienteDesdeArray(partes);
            }
        }

        return null;
    }

    /**
     * Lista todos los pacientes registrados.
     *
     * @return Lista de pacientes
     * @throws IOException Si hay error de lectura
     */
    public List<Paciente> listarTodos() throws IOException {
        List<Paciente> pacientes = new ArrayList<>();
        File csvFile = new File(PACIENTES_CSV);

        if (!csvFile.exists()) {
            return pacientes;
        }

        List<String> lineas = Files.readAllLines(csvFile.toPath());

        // Saltar cabecera (índice 0)
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length >= 8) {
                pacientes.add(crearPacienteDesdeArray(partes));
            }
        }

        return pacientes;
    }

    /**
     * Valida que los datos del paciente sean correctos.
     *
     * @param paciente El paciente a validar
     * @throws IllegalArgumentException Si algún dato es inválido
     */
    private void validarPaciente(Paciente paciente) {
        if (paciente.getDocumento() == null || paciente.getDocumento().trim().isEmpty()) {
            throw new IllegalArgumentException("El documento es obligatorio");
        }
        if (paciente.getNombre() == null || paciente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        if (paciente.getApellido() == null || paciente.getApellido().trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido es obligatorio");
        }
        if (paciente.getEdad() <= 0 || paciente.getEdad() > 150) {
            throw new IllegalArgumentException("La edad debe estar entre 1 y 150 años");
        }
        if (paciente.getCorreo() == null || !paciente.getCorreo().contains("@")) {
            throw new IllegalArgumentException("El correo electrónico no es válido");
        }
    }

    /**
     * Crea un objeto Paciente desde un array de strings (línea CSV).
     *
     * @param partes Array con los datos del paciente
     * @return Objeto Paciente
     */
    private Paciente crearPacienteDesdeArray(String[] partes) {
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
     * Verifica si existe un paciente con el documento dado.
     *
     * @param documento El documento a verificar
     * @return true si existe, false si no
     * @throws IOException Si hay error de lectura
     */
    public boolean existePaciente(String documento) throws IOException {
        return consultarPaciente(documento) != null;
    }

    /**
     * Obtiene la cantidad total de pacientes registrados.
     *
     * @return Número de pacientes
     * @throws IOException Si hay error de lectura
     */
    public int contarPacientes() throws IOException {
        File csvFile = new File(PACIENTES_CSV);
        if (!csvFile.exists()) {
            return 0;
        }

        long count = Files.lines(csvFile.toPath())
                .skip(1) // Saltar cabecera
                .filter(linea -> !linea.trim().isEmpty())
                .count();

        return (int) count;
    }
}