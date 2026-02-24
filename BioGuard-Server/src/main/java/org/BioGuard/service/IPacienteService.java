package org.BioGuard.service;

import org.BioGuard.model.Paciente;
import org.BioGuard.exception.PacienteDuplicadoException;
import org.BioGuard.exception.MuestraNoEncontradaException;

import java.util.List;
import java.util.Optional;

public interface IPacienteService {

    /**
     * Registra un nuevo paciente en el archivo CSV.
     *
     * @param paciente Paciente a registrar
     * @return El paciente registrado
     * @throws PacienteDuplicadoException Si ya existe un paciente con el mismo documento
     */
    Paciente registrarPaciente(Paciente paciente) throws PacienteDuplicadoException;

    /**
     * Busca un paciente por su documento.
     *
     * @param documento Documento del paciente
     * @return Optional con el paciente si existe
     */
    Optional<Paciente> buscarPorDocumento(String documento);

    /**
     * Lista todos los pacientes registrados.
     *
     * @return Lista de pacientes
     */
    List<Paciente> listarTodos();

    /**
     * Actualiza datos de un paciente existente.
     *
     * @param paciente Paciente con datos actualizados
     * @return Paciente actualizado
     * @throws MuestraNoEncontradaException Si el paciente no existe
     */
    Paciente actualizarPaciente(Paciente paciente) throws MuestraNoEncontradaException;

    /**
     * Elimina un paciente del archivo.
     *
     * @param documento Documento del paciente
     * @return true si se eliminó
     */
    boolean eliminarPaciente(String documento);
}