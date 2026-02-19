package org.BioGuard.service;

import org.BioGuard.model.Virus;
import org.BioGuard.exception.VirusNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz que define las operaciones de negocio relacionadas con virus.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public interface IVirusService {

    /**
     * Registra un nuevo virus en el sistema.
     *
     * @param virus Virus a registrar
     * @return El virus registrado
     */
    Virus registrarVirus(Virus virus);

    /**
     * Busca un virus por su identificador único.
     *
     * @param id ID del virus
     * @return Optional con el virus si existe
     */
    Optional<Virus> buscarPorId(String id);

    /**
     * Busca virus por nombre.
     *
     * @param nombre Nombre a buscar
     * @param exacta true para búsqueda exacta, false para parcial
     * @return Lista de virus que coinciden
     */
    List<Virus> buscarPorNombre(String nombre, boolean exacta);

    /**
     * Busca virus por nivel de peligrosidad.
     *
     * @param nivel Nivel (1-5)
     * @return Lista de virus con ese nivel
     */
    List<Virus> buscarPorNivelPeligrosidad(int nivel);

    /**
     * Actualiza un virus existente.
     *
     * @param virus Virus con datos actualizados
     * @return Virus actualizado
     * @throws VirusNotFoundException Si no existe el virus
     */
    Virus actualizarVirus(Virus virus) throws VirusNotFoundException;

    /**
     * Elimina un virus del sistema.
     *
     * @param id ID del virus
     * @return true si se eliminó
     */
    boolean eliminarVirus(String id);

    /**
     * Lista todos los virus registrados.
     *
     * @return Lista completa de virus
     */
    List<Virus> listarTodos();
}