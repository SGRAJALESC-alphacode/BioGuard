package org.BioGuard.service.diagnostico;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.model.Muestra;
import org.BioGuard.exception.DiagnosticoException;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface IDiagnosticoService {
    Diagnostico procesarMuestra(String documento, String secuencia) throws DiagnosticoException;
    Muestra guardarMuestra(Muestra muestra) throws IOException;
    Optional<Diagnostico> buscarPorId(String id);
    List<Diagnostico> buscarPorPaciente(String documento);
    String generarCSV(Diagnostico diagnostico) throws IOException;
    List<Diagnostico> listarTodos();

    // NUEVOS MÉTODOS PARA MUESTRAS
    List<Muestra> obtenerMuestrasDePaciente(String documento);
    Optional<Muestra> obtenerMuestraPorId(String id);
}