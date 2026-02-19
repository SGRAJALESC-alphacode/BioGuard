package org.BioGuard.service;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.model.Paciente;
import org.BioGuard.exception.DiagnosticoException;
import org.BioGuard.exception.MuestraNoEncontradaException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de diagnósticos.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public interface IDiagnosticoService {

    Diagnostico crearDiagnostico(Paciente paciente, String sintomas, String resultado)
            throws DiagnosticoException;

    Optional<Diagnostico> buscarPorId(String id);

    List<Diagnostico> buscarPorPaciente(String pacienteId)
            throws MuestraNoEncontradaException;

    List<Diagnostico> listarTodos();

    boolean eliminarDiagnostico(String id);
}