package org.BioGuard;

import org.BioGuard.exception.FormatoFastaInvalidoException;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Servicio para gestión de virus en el sistema BioGuard.
 * Proporciona operaciones para guardar, cargar y buscar virus,
 * con persistencia exclusiva en archivos con formato FASTA.
 *
 * // Objetivo
 *    Administrar el catálogo viral del sistema, permitiendo:
 *    - Almacenar nuevos virus con validación de datos
 *    - Cargar todos los virus registrados
 *    - Buscar virus específicos por nombre
 *    - Persistencia en formato FASTA estándar
 *
 * // Atributos
 *    VIRUS_FOLDER : Carpeta donde se almacenan los archivos FASTA de virus
 *
 * // Estructura de archivos
 *    data/
 *    └── virus/
 *        ├── virus1.fasta
 *        ├── virus2.fasta
 *        └── ...
 *
 * // Formato FASTA esperado
 *    Archivo: [nombre_sanitizado].fasta
 *    Contenido:
 *    >nombre_virus|nivel_infecciosidad
 *    ATCGATCGATCG
 *    (la secuencia puede estar en múltiples líneas)
 *
 * // Validaciones
 *    - Nombre: No vacío
 *    - Nivel: "Poco Infeccioso", "Normal" o "Altamente Infeccioso"
 *    - Secuencia: Solo caracteres A, T, C, G
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class VirusService {

    private static final String VIRUS_FOLDER = "data/virus/";

    /**
     * Constructor del servicio de virus.
     *
     * // Objetivo
     *    Inicializar el servicio asegurando que el directorio
     *    de almacenamiento de virus existe.
     *
     * // Proceso
     *    Crear la carpeta VIRUS_FOLDER si no existe.
     */
    public VirusService() {
        new File(VIRUS_FOLDER).mkdirs();
    }

    /**
     * Guarda un nuevo virus en el sistema.
     *
     * // Objetivo
     *    Almacenar un virus en formato FASTA después de validar
     *    que todos sus datos cumplen con las reglas de negocio.
     *
     * // Proceso
     *    1. Validar nombre (no vacío)
     *    2. Validar nivel (debe ser uno de los tres permitidos)
     *    3. Validar secuencia (no vacía y solo ATCG)
     *    4. Sanitizar nombre para nombre de archivo
     *    5. Crear archivo .fasta con header y secuencia
     *    6. Escribir en disco
     *
     * // Sanitización de nombre
     *    Los caracteres no alfanuméricos se reemplazan por '_'
     *    para evitar problemas con nombres de archivo.
     *
     * @param nombre Nombre del virus
     * @param nivel Nivel de infecciosidad
     * @param secuencia Secuencia de ADN
     * @throws FormatoFastaInvalidoException Si los datos son inválidos
     * @throws IOException Si hay error de escritura
     */
    public void guardarVirus(String nombre, String nivel, String secuencia)
            throws FormatoFastaInvalidoException, IOException {

        // Validaciones de entrada
        if (nombre == null || nombre.trim().isEmpty())
            throw new FormatoFastaInvalidoException("El nombre del virus es obligatorio");

        if (nivel == null || (!nivel.equals("Poco Infeccioso") &&
                !nivel.equals("Normal") && !nivel.equals("Altamente Infeccioso")))
            throw new FormatoFastaInvalidoException("Nivel de infecciosidad inválido");

        if (secuencia == null || secuencia.trim().isEmpty())
            throw new FormatoFastaInvalidoException("La secuencia es obligatoria");

        if (!secuencia.matches("^[ATCG]+$"))
            throw new FormatoFastaInvalidoException("Caracteres inválidos en secuencia");

        // Sanitizar nombre para nombre de archivo
        String nombreArchivo = nombre.replaceAll("[^a-zA-Z0-9]", "_") + ".fasta";
        Path ruta = Paths.get(VIRUS_FOLDER + nombreArchivo);

        // Crear contenido en formato FASTA
        String header = ">" + nombre + "|" + nivel;
        String contenido = header + "\n" + secuencia + "\n";

        // Escribir archivo
        Files.writeString(ruta, contenido);
    }

    /**
     * Carga todos los virus registrados en el sistema.
     *
     * // Objetivo
     *    Obtener una lista completa de todos los virus almacenados
     *    en la carpeta VIRUS_FOLDER, parseando cada archivo FASTA.
     *
     * // Proceso
     *    1. Verificar que la carpeta existe
     *    2. Listar todos los archivos .fasta
     *    3. Por cada archivo, parsear su contenido a objeto Virus
     *    4. Agregar a la lista de resultados
     *    5. Ignorar archivos con formato inválido
     *
     * // Manejo de errores
     *    Si un archivo no tiene el formato FASTA correcto,
     *    se ignora y se continua con el siguiente.
     *
     * @return Lista de virus (puede estar vacía)
     * @throws IOException Si hay error de lectura del directorio
     */
    public List<Virus> cargarTodosLosVirus() throws IOException {
        List<Virus> virusList = new ArrayList<>();
        File folder = new File(VIRUS_FOLDER);

        if (!folder.exists()) return virusList;

        File[] virusFiles = folder.listFiles((dir, name) -> name.endsWith(".fasta"));
        if (virusFiles == null) return virusList;

        for (File file : virusFiles) {
            Virus virus = cargarVirusDeArchivo(file);
            if (virus != null) virusList.add(virus);
        }
        return virusList;
    }

    /**
     * Carga un virus desde un archivo FASTA específico.
     *
     * // Objetivo
     *    Parsear un archivo FASTA individual y convertirlo en un
     *    objeto Virus con todos sus atributos.
     *
     * // Formato esperado
     *    Línea 1: >nombre|nivel
     *    Línea 2: secuencia (puede continuar en líneas siguientes)
     *
     * // Proceso de parseo
     *    1. Leer todo el contenido del archivo
     *    2. Dividir por líneas
     *    3. Validar que hay al menos 2 líneas
     *    4. Validar que la primera línea comienza con '>'
     *    5. Extraer nombre y nivel del header
     *    6. Concatenar todas las líneas restantes como secuencia
     *
     * @param file Archivo FASTA a cargar
     * @return Virus parseado o null si el formato es inválido
     * @throws IOException Si hay error de lectura
     */
    private Virus cargarVirusDeArchivo(File file) throws IOException {
        String contenido = Files.readString(file.toPath()).trim();
        String[] lineas = contenido.split("\n");

        if (lineas.length < 2) return null;

        String header = lineas[0].trim();
        if (!header.startsWith(">")) return null;

        String headerSinMayor = header.substring(1);
        String[] partes = headerSinMayor.split("\\|");

        String nombre = partes[0];
        String nivel = partes.length > 1 ? partes[1] : "Normal";

        StringBuilder secuencia = new StringBuilder();
        for (int i = 1; i < lineas.length; i++) {
            secuencia.append(lineas[i].trim());
        }

        return new Virus(nombre, nivel, secuencia.toString());
    }

    /**
     * Busca un virus por su nombre (ignorando mayúsculas/minúsculas).
     *
     * // Objetivo
     *    Localizar un virus específico dentro del catálogo utilizando
     *    su nombre como criterio de búsqueda.
     *
     * // Proceso
     *    1. Cargar todos los virus
     *    2. Iterar sobre la lista
     *    3. Comparar nombres con equalsIgnoreCase
     *    4. Retornar el primer virus que coincida
     *
     * // Uso típico
     *    Virus v = virusService.buscarVirusPorNombre("gripe");
     *    if (v != null) {
     *        // Procesar virus encontrado
     *    }
     *
     * @param nombre Nombre del virus a buscar
     * @return Virus encontrado o null si no existe
     * @throws IOException Si hay error de lectura
     */
    public Virus buscarVirusPorNombre(String nombre) throws IOException {
        for (Virus v : cargarTodosLosVirus()) {
            if (v.getNombre().equalsIgnoreCase(nombre)) return v;
        }
        return null;
    }
}