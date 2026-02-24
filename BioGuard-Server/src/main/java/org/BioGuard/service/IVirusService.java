package org.BioGuard.service;

import org.BioGuard.model.Virus;
import org.BioGuard.exception.VirusNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IVirusService {
    Virus registrarVirus(Virus virus);
    Optional<Virus> buscarPorId(String id);
    List<Virus> buscarPorNombre(String nombre, boolean exacta);
    List<Virus> buscarPorNivelPeligrosidad(int nivel);
    Virus actualizarVirus(Virus virus) throws VirusNotFoundException;
    boolean eliminarVirus(String id);
    List<Virus> listarTodos();
}