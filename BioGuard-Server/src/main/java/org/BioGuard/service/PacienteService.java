package org.BioGuard.service;

import org.BioGuard.model.Paciente;
import org.BioGuard.exception.PacienteDuplicadoException;
import org.BioGuard.exception.MuestraNoEncontradaException;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Servicio de pacientes con persistencia en archivo CSV.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class PacienteService implements IPacienteService {

    private static final String CSV_FILE = "data/pacientes.csv";
    private static final String CSV_HEADER = "documento,nombre,apellido,edad,correo,genero,ciudad,pais";

    private final Map<String, Paciente> pacientes = new LinkedHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public PacienteService() {
        cargarDesdeCSV();
    }

    @Override
    public Paciente registrarPaciente(Paciente paciente) throws PacienteDuplicadoException {
        lock.writeLock().lock();
        try {
            if (pacientes.containsKey(paciente.getDocumento())) {
                throw new PacienteDuplicadoException(
                        "Ya existe paciente con documento: " + paciente.getDocumento()
                );
            }

            validarPaciente(paciente);
            pacientes.put(paciente.getDocumento(), paciente);
            guardarEnCSV();

            return paciente;

        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public Optional<Paciente> buscarPorDocumento(String documento) {
        lock.readLock().lock();
        try {
            return Optional.ofNullable(pacientes.get(documento));
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public List<Paciente> listarTodos() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(pacientes.values());
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public Paciente actualizarPaciente(Paciente paciente) throws MuestraNoEncontradaException {
        lock.writeLock().lock();
        try {
            if (!pacientes.containsKey(paciente.getDocumento())) {
                throw new MuestraNoEncontradaException(
                        "Paciente no encontrado: " + paciente.getDocumento()
                );
            }

            validarPaciente(paciente);
            pacientes.put(paciente.getDocumento(), paciente);
            guardarEnCSV();

            return paciente;

        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean eliminarPaciente(String documento) {
        lock.writeLock().lock();
        try {
            if (pacientes.remove(documento) != null) {
                guardarEnCSV();
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void validarPaciente(Paciente p) {
        if (p.getDocumento() == null || p.getDocumento().trim().isEmpty())
            throw new IllegalArgumentException("Documento obligatorio");
        if (p.getNombre() == null || p.getNombre().trim().isEmpty())
            throw new IllegalArgumentException("Nombre obligatorio");
        if (p.getApellido() == null || p.getApellido().trim().isEmpty())
            throw new IllegalArgumentException("Apellido obligatorio");
        if (p.getEdad() < 0 || p.getEdad() > 150)
            throw new IllegalArgumentException("Edad inválida");
        if (p.getCorreo() == null || !p.getCorreo().contains("@"))
            throw new IllegalArgumentException("Correo inválido");
        if (p.getGenero() == null || p.getGenero().trim().isEmpty())
            throw new IllegalArgumentException("Género obligatorio");
        if (p.getCiudad() == null || p.getCiudad().trim().isEmpty())
            throw new IllegalArgumentException("Ciudad obligatoria");
        if (p.getPais() == null || p.getPais().trim().isEmpty())
            throw new IllegalArgumentException("País obligatorio");
    }

    private void cargarDesdeCSV() {
        Path path = Paths.get(CSV_FILE);
        if (!Files.exists(path)) return;

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String linea = reader.readLine(); // Saltar cabecera
            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] campos = linea.split(",");
                if (campos.length < 8) continue;

                Paciente p = new Paciente(
                        campos[0].trim(), // documento
                        campos[1].trim(), // nombre
                        campos[2].trim(), // apellido
                        Integer.parseInt(campos[3].trim()), // edad
                        campos[4].trim(), // correo
                        campos[5].trim(), // genero
                        campos[6].trim(), // ciudad
                        campos[7].trim()  // pais
                );
                pacientes.put(p.getDocumento(), p);
            }
        } catch (IOException e) {
            System.err.println("Error cargando pacientes: " + e.getMessage());
        }
    }

    private void guardarEnCSV() {
        try {
            Files.createDirectories(Paths.get("data"));

            try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(CSV_FILE))) {
                writer.write(CSV_HEADER);
                writer.newLine();

                for (Paciente p : pacientes.values()) {
                    writer.write(String.format("%s,%s,%s,%d,%s,%s,%s,%s",
                            p.getDocumento(),
                            p.getNombre(),
                            p.getApellido(),
                            p.getEdad(),
                            p.getCorreo(),
                            p.getGenero(),
                            p.getCiudad(),
                            p.getPais()
                    ));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error guardando pacientes: " + e.getMessage());
        }
    }
}