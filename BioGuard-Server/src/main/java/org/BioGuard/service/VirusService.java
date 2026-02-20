package org.BioGuard.service;

import org.BioGuard.model.Virus;
import org.BioGuard.exception.VirusNotFoundException;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class VirusService implements IVirusService {

    private final Map<String, Virus> virusMap = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public Virus registrarVirus(Virus virus) {
        if (virus == null) throw new IllegalArgumentException("Virus no puede ser null");

        if (virus.getId() == null) {
            virus.setId(String.valueOf(idGenerator.getAndIncrement()));
        }

        virusMap.put(virus.getId(), virus);
        return virus;
    }

    @Override
    public Optional<Virus> buscarPorId(String id) {
        return Optional.ofNullable(virusMap.get(id));
    }

    @Override
    public List<Virus> buscarPorNombre(String nombre, boolean exacta) {
        if (nombre == null) return Collections.emptyList();

        return virusMap.values().stream()
                .filter(v -> {
                    if (exacta) return nombre.equalsIgnoreCase(v.getNombre());
                    else return v.getNombre() != null && v.getNombre().toLowerCase().contains(nombre.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Virus> buscarPorNivelPeligrosidad(int nivel) {
        return virusMap.values().stream()
                .filter(v -> v.getNivelPeligrosidad() == nivel)
                .collect(Collectors.toList());
    }

    @Override
    public Virus actualizarVirus(Virus virus) throws VirusNotFoundException {
        if (virus == null || virus.getId() == null)
            throw new IllegalArgumentException("Virus o ID inválido");

        if (!virusMap.containsKey(virus.getId())) {
            throw new VirusNotFoundException("ID", virus.getId());
        }

        virusMap.put(virus.getId(), virus);
        return virus;
    }

    @Override
    public boolean eliminarVirus(String id) {
        return virusMap.remove(id) != null;
    }

    @Override
    public List<Virus> listarTodos() {
        return new ArrayList<>(virusMap.values());
    }
}