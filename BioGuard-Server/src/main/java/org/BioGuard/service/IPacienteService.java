package org.BioGuard.service;

import org.BioGuard.model.Paciente;
import org.BioGuard.exception.PacienteDuplicadoException;
import org.BioGuard.exception.MuestraNoEncontradaException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de negocio relacionadas con pacientes.
 *
 * <p>Define el contrato para la gestión de pacientes en el sistema BioGuard.
 * Las implementaciones concretas deben encargarse de la persistencia,
 * validación y lógica de negocio asociada a los pacientes.</p>
 *
 * <p>Principios aplicados:</p>
 * <ul>
 *   <li><b>Responsabilidad Única:</b> Solo opera con la entidad Paciente</li>
 *   <li><b>Inversión de Dependencias:</b> Las capas superiores dependen de esta abstracción</li>
 *   <li><b>Segregación de Interfaces:</b> Métodos cohesivos y específicos</li>
 * </ul>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 * @see Paciente
 * @see PacienteService
 */
public interface IPacienteService {

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * @param paciente Paciente a registrar (no null)
     * @return El paciente registrado con su ID generado
     * @throws PacienteDuplicadoException Si ya existe un paciente con el mismo identificador
     * @throws IllegalArgumentException Si el paciente es null o tiene datos inválidos
     */
    Paciente registrarPaciente(Paciente paciente) throws PacienteDuplicadoException;

    /**
     * Busca un paciente por su identificador único.
     *
     * @param id ID del paciente a buscar
     * @return Optional con el paciente si existe, vacío si no
     */
    Optional<Paciente> buscarPorId(String id);

    /**
     * Busca pacientes por su nombre (búsqueda parcial, case-insensitive).
     *
     * @param nombre Nombre o parte del nombre a buscar
     * @return Lista de pacientes que coinciden (puede estar vacía)
     */
    List<Paciente> buscarPorNombre(String nombre);

    /**
     * Actualiza los datos de un paciente existente.
     *
     * @param paciente Paciente con los datos actualizados
     * @return El paciente actualizado
     * @throws MuestraNoEncontradaException Si no existe el paciente
     */
    Paciente actualizarPaciente(Paciente paciente) throws MuestraNoEncontradaException;

    /**
     * Elimina un paciente del sistema.
     *
     * @param id ID del paciente a eliminar
     * @return true si se eliminó, false si no existía
     */
    boolean eliminarPaciente(String id);

    /**
     * Lista todos los pacientes registrados.
     *
     * @return Lista completa de pacientes (puede estar vacía)
     */
    List<Paciente> listarTodos();

    /**
     * Cuenta el total de pacientes registrados.
     *
     * @return Número total de pacientes
     */
    long contarPacientes();
}