package org.BioGuard;

import org.BioGuard.exception.PacienteDuplicadoException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Servicio para gestión de pacientes en el sistema BioGuard.
 * Proporciona operaciones CRUD sobre el archivo pacientes.csv
 * con validación de documentos duplicados y persistencia exclusiva en CSV.
 *
 * // Objetivo
 *    Gestionar el ciclo de vida de los pacientes en el sistema:
 *    registro, consulta, listado y validación de datos.
 *
 * // Atributos
 *    PACIENTES_CSV : Ruta del archivo CSV donde se almacenan los pacientes
 *
 * // Archivo CSV
 *    Formato: documento,nombre,apellido,edad,correo,genero,ciudad,pais
 *    Ubicación: data/pacientes/pacientes.csv
 *    Cabecera: Siempre presente en la primera línea
 */
public class PacienteService {

    private static final String PACIENTES_CSV = "data/pacientes/pacientes.csv";

    /**
     * Registra un nuevo paciente en el sistema.
     *
     * // Objetivo
     *    Almacenar un paciente en el archivo CSV validando que:
     *    - No exista un documento duplicado
     *    - Todos los campos requeridos estén presentes y sean válidos
     *
     * // Proceso
     *    1. Verificar que el documento no exista (consulta previa)
     *    2. Validar formato de cada campo
     *    3. Crear directorio data/pacientes/ si no existe
     *    4. Crear archivo con cabecera si no existe
     *    5. Agregar nueva línea al final del archivo
     *
     * // Validaciones
     *    - Documento: No vacío, único en el sistema
     *    - Nombre: No vacío
     *    - Apellido: No vacío
     *    - Edad: Entre 1 y 150 años
     *    - Correo: Debe contener '@'
     *    - Género: No vacío (M/F/Otro)
     *    - Ciudad: No vacía
     *    - País: No vacío
     *
     * @param paciente El paciente a registrar (con todos los campos)
     * @throws PacienteDuplicadoException Si el documento ya existe en el sistema
     * @throws IOException Si hay error de escritura en el archivo
     * @throws IllegalArgumentException Si algún campo no pasa las validaciones
     */
    public void registrarPaciente(Paciente paciente) throws PacienteDuplicadoException, IOException {
        if (consultarPaciente(paciente.getDocumento()) != null) {
            throw new PacienteDuplicadoException(paciente.getDocumento());
        }

        validarPaciente(paciente);

        File csvFile = new File(PACIENTES_CSV);
        csvFile.getParentFile().mkdirs();

        if (!csvFile.exists()) {
            Files.writeString(csvFile.toPath(), "documento,nombre,apellido,edad,correo,genero,ciudad,pais\n");
        }

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
    }

    /**
     * Consulta un paciente por su documento de identidad.
     *
     * // Objetivo
     *    Buscar un paciente específico en el archivo CSV utilizando
     *    su documento como clave de búsqueda única.
     *
     * // Proceso
     *    1. Verificar que el archivo exista
     *    2. Leer todas las líneas del archivo
     *    3. Saltar la cabecera (índice 0)
     *    4. Buscar coincidencia exacta en el primer campo (documento)
     *    5. Si se encuentra, construir objeto Paciente con los datos
     *
     * @param documento Documento del paciente a buscar
     * @return Paciente encontrado o null si no existe
     * @throws IOException Si hay error de lectura del archivo
     */
    public Paciente consultarPaciente(String documento) throws IOException {
        File csvFile = new File(PACIENTES_CSV);
        if (!csvFile.exists()) return null;

        List<String> lineas = Files.readAllLines(csvFile.toPath());

        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length >= 8 && partes[0].equals(documento)) {
                return crearPaciente(partes);
            }
        }
        return null;
    }

    /**
     * Lista todos los pacientes registrados en el sistema.
     *
     * // Objetivo
     *    Obtener una lista completa de todos los pacientes almacenados
     *    en el archivo CSV, excluyendo la cabecera y líneas vacías.
     *
     * // Proceso
     *    1. Verificar que el archivo exista
     *    2. Leer todas las líneas del archivo
     *    3. Saltar la cabecera (índice 0)
     *    4. Convertir cada línea válida en un objeto Paciente
     *    5. Agregar a la lista de resultados
     *
     * @return Lista de pacientes (puede estar vacía)
     * @throws IOException Si hay error de lectura del archivo
     */
    public List<Paciente> listarTodos() throws IOException {
        List<Paciente> pacientes = new ArrayList<>();
        File csvFile = new File(PACIENTES_CSV);

        if (!csvFile.exists()) return pacientes;

        List<String> lineas = Files.readAllLines(csvFile.toPath());

        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length >= 8) {
                pacientes.add(crearPaciente(partes));
            }
        }
        return pacientes;
    }

    /**
     * Valida los datos de un paciente antes de registrarlo.
     *
     * // Objetivo
     *    Verificar que todos los campos requeridos cumplan con
     *    las reglas de negocio establecidas:
     *    - Campos obligatorios no vacíos
     *    - Edad en rango válido (1-150)
     *    - Correo con formato básico (contiene @)
     *
     * @param p Paciente a validar
     * @throws IllegalArgumentException Si algún campo no es válido
     */
    private void validarPaciente(Paciente p) {
        if (p.getDocumento() == null || p.getDocumento().trim().isEmpty())
            throw new IllegalArgumentException("Documento obligatorio");
        if (p.getNombre() == null || p.getNombre().trim().isEmpty())
            throw new IllegalArgumentException("Nombre obligatorio");
        if (p.getApellido() == null || p.getApellido().trim().isEmpty())
            throw new IllegalArgumentException("Apellido obligatorio");
        if (p.getEdad() <= 0 || p.getEdad() > 150)
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

    /**
     * Crea un objeto Paciente a partir de un array de strings.
     *
     * // Objetivo
     *    Convertir una línea del archivo CSV en un objeto Paciente
     *    para facilitar su manipulación en el sistema.
     *
     * // Formato esperado del array
     *    partes[0] : documento
     *    partes[1] : nombre
     *    partes[2] : apellido
     *    partes[3] : edad (String que debe convertirse a int)
     *    partes[4] : correo
     *    partes[5] : genero
     *    partes[6] : ciudad
     *    partes[7] : pais
     *
     * @param partes Array con los datos del paciente
     * @return Objeto Paciente con los datos cargados
     */
    private Paciente crearPaciente(String[] partes) {
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
     * Verifica si existe un paciente con el documento especificado.
     *
     * // Objetivo
     *    Método de utilidad para validar existencia antes de
     *    operaciones que requieran un paciente existente.
     *
     * @param documento Documento a verificar
     * @return true si el paciente existe, false en caso contrario
     * @throws IOException Si hay error de lectura del archivo
     */
    public boolean existePaciente(String documento) throws IOException {
        return consultarPaciente(documento) != null;
    }
}