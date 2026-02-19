package org.BioGuard.service;

import org.BioGuard.model.Paciente;
import org.BioGuard.exception.PacienteDuplicadoException;
import org.BioGuard.exception.MuestraNoEncontradaException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Implementación en memoria del servicio de pacientes.
 *
 * <p>Esta clase proporciona una implementación simple usando un mapa concurrente
 * para almacenar los pacientes. Es ideal para pruebas y desarrollo, pero debe
 * reemplazarse por una versión con base de datos en producción.</p>
 *
 * <p>Características:</p>
 * <ul>
 *   <li><b>Thread-safe:</b> Usa ConcurrentHashMap para operaciones concurrentes</li>
 *   <li><b>IDs auto-generados:</b> Asigna IDs secuenciales únicos</li>
 *   <li><b>Búsquedas eficientes:</b> Indexa por ID y nombre</li>
 *   <li><b>Validaciones:</b> Verifica datos obligatorios antes de guardar</li>
 * </ul>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 * @see IPacienteService
 * @see Paciente
 */
public class PacienteService implements IPacienteService {

    /** Almacenamiento concurrente de pacientes (ID -> Paciente) */
    private final Map<String, Paciente> pacientes = new ConcurrentHashMap<>();

    /** Índice secundario para búsqueda por nombre (nombre -> Lista de IDs) */
    private final Map<String, Set<String>> indicePorNombre = new ConcurrentHashMap<>();

    /** Generador de IDs secuenciales */
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Paciente registrarPaciente(Paciente paciente) throws PacienteDuplicadoException {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser null");
        }

        validarPaciente(paciente);

        // Generar ID único si no tiene
        if (paciente.getId() == null || paciente.getId().trim().isEmpty()) {
            String nuevoId = String.valueOf(idGenerator.getAndIncrement());
            paciente = new Paciente(
                    nuevoId,
                    paciente.getNombre(),
                    paciente.getEdad(),
                    paciente.getGenero(),
                    paciente.getTelefono(),
                    paciente.getDireccion()
            );
        } else {
            // Verificar que no exista ya un paciente con ese ID
            if (pacientes.containsKey(paciente.getId())) {
                throw new PacienteDuplicadoException("Ya existe un paciente con ID: " + paciente.getId());
            }
        }

        // Guardar paciente
        pacientes.put(paciente.getId(), paciente);
        indexarPorNombre(paciente);

        return paciente;
    }

    @Override
    public Optional<Paciente> buscarPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(pacientes.get(id));
    }

    @Override
    public List<Paciente> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return Collections.emptyList();
        }

        String nombreLower = nombre.toLowerCase().trim();
        return indicePorNombre.entrySet().stream()
                .filter(entry -> entry.getKey().contains(nombreLower))
                .flatMap(entry -> entry.getValue().stream())
                .map(pacientes::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public Paciente actualizarPaciente(Paciente paciente) throws MuestraNoEncontradaException {
        if (paciente == null) {
            throw new IllegalArgumentException("El paciente no puede ser null");
        }

        if (paciente.getId() == null || !pacientes.containsKey(paciente.getId())) {
            throw new MuestraNoEncontradaException("No existe paciente con ID: " + paciente.getId());
        }

        validarPaciente(paciente);

        // Remover del índice anterior
        Paciente viejo = pacientes.get(paciente.getId());
        if (viejo != null) {
            desindexarPorNombre(viejo);
        }

        // Actualizar y re-indexar
        pacientes.put(paciente.getId(), paciente);
        indexarPorNombre(paciente);

        return paciente;
    }

    @Override
    public boolean eliminarPaciente(String id) {
        if (id == null || id.trim().isEmpty()) {
            return false;
        }

        Paciente eliminado = pacientes.remove(id);
        if (eliminado != null) {
            desindexarPorNombre(eliminado);
            return true;
        }
        return false;
    }

    @Override
    public List<Paciente> listarTodos() {
        return new ArrayList<>(pacientes.values());
    }

    @Override
    public long contarPacientes() {
        return pacientes.size();
    }

    /**
     * Valida que los datos del paciente sean correctos.
     *
     * @param paciente Paciente a validar
     * @throws IllegalArgumentException Si algún dato es inválido
     */
    private void validarPaciente(Paciente paciente) {
        if (paciente.getNombre() == null || paciente.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del paciente es obligatorio");
        }
        if (paciente.getEdad() < 0 || paciente.getEdad() > 150) {
            throw new IllegalArgumentException("Edad inválida: " + paciente.getEdad());
        }
        // Más validaciones según reglas de negocio
    }

    /**
     * Agrega el paciente al índice de búsqueda por nombre.
     *
     * @param paciente Paciente a indexar
     */
    private void indexarPorNombre(Paciente paciente) {
        String nombreLower = paciente.getNombre().toLowerCase();
        indicePorNombre.computeIfAbsent(nombreLower, k -> ConcurrentHashMap.newKeySet())
                .add(paciente.getId());
    }

    /**
     * Remueve el paciente del índice de búsqueda por nombre.
     *
     * @param paciente Paciente a desindexar
     */
    private void desindexarPorNombre(Paciente paciente) {
        String nombreLower = paciente.getNombre().toLowerCase();
        Set<String> ids = indicePorNombre.get(nombreLower);
        if (ids != null) {
            ids.remove(paciente.getId());
            if (ids.isEmpty()) {
                indicePorNombre.remove(nombreLower);
            }
        }
    }
}