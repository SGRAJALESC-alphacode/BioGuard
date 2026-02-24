package org.BioGuard.service.diagnostico;

import org.BioGuard.model.Muestra;
import org.BioGuard.exception.FileReadException;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Repositorio para gestionar la persistencia de muestras.
 *
 * <p>Responsabilidad Única: Gestionar el almacenamiento y recuperación
 * de muestras de ADN en el sistema de archivos y en memoria.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MuestraRepository {

    private final Map<String, Muestra> muestras = new ConcurrentHashMap<>();
    private final MuestraParser parser;
    private static final String MUESTRAS_DIR = "data/muestras/";

    /**
     * Constructor del repositorio de muestras.
     * Inicializa el parser y carga las muestras existentes.
     */
    public MuestraRepository() {
        this.parser = new MuestraParser();
        crearDirectorioMuestras();
        cargarMuestrasDesdeArchivos();
    }

    /**
     * Crea el directorio de muestras si no existe.
     */
    private void crearDirectorioMuestras() {
        try {
            Files.createDirectories(Paths.get(MUESTRAS_DIR));
        } catch (IOException e) {
            System.err.println("[MuestraRepository] Error creando directorio: " + e.getMessage());
        }
    }

    /**
     * Carga todas las muestras desde los archivos FASTA en el sistema de archivos.
     * Recorre recursivamente las carpetas de pacientes y parsea cada archivo .fasta.
     */
    private void cargarMuestrasDesdeArchivos() {
        Path muestrasDir = Paths.get(MUESTRAS_DIR);
        if (!Files.exists(muestrasDir)) {
            return;
        }

        System.out.println("[MuestraRepository] Cargando muestras desde archivos...");
        int contador = 0;
        int errores = 0;

        try (Stream<Path> pacientesDirs = Files.list(muestrasDir)) {
            List<Path> carpetasPacientes = pacientesDirs.filter(Files::isDirectory).toList();

            for (Path pacienteDir : carpetasPacientes) {
                String documento = pacienteDir.getFileName().toString();

                // Ignorar carpetas que son placeholders
                if (documento.equals("documento") || documento.equals("placeholder")) {
                    continue;
                }

                try (DirectoryStream<Path> archivos = Files.newDirectoryStream(pacienteDir, "*.fasta")) {
                    for (Path archivo : archivos) {
                        try {
                            Muestra muestra = parser.parsear(archivo);

                            // Verificar que el documento coincida con la carpeta
                            if (!documento.equals(muestra.getDocumentoPaciente())) {
                                System.err.println("  Advertencia: Documento en archivo (" +
                                        muestra.getDocumentoPaciente() +
                                        ") no coincide con carpeta (" + documento + ")");
                            }

                            muestras.put(muestra.getId(), muestra);
                            contador++;
                            System.out.println("  Cargada: " + muestra.getId());

                        } catch (FileReadException e) {
                            errores++;
                            System.err.println("  Error de formato en " + archivo.getFileName() + ": " + e.getMessage());
                        } catch (IOException e) {
                            errores++;
                            System.err.println("  Error de E/S en " + archivo.getFileName() + ": " + e.getMessage());
                        }
                    }
                }
            }

            System.out.println("[MuestraRepository] " + contador + " muestras cargadas, " + errores + " errores");

        } catch (IOException e) {
            System.err.println("[MuestraRepository] Error cargando muestras: " + e.getMessage());
        }
    }

    /**
     * Guarda una nueva muestra en el sistema de archivos y en memoria.
     *
     * @param muestra Muestra a guardar
     * @return La muestra guardada con su ruta de archivo actualizada
     * @throws IOException Si hay error de escritura
     */
    public Muestra guardar(Muestra muestra) throws IOException {
        // Crear carpeta del paciente
        Path pacienteDir = Paths.get(MUESTRAS_DIR, muestra.getDocumentoPaciente());
        Files.createDirectories(pacienteDir);

        // Generar nombre de archivo
        String nombreArchivo = muestra.getNombreArchivo();
        Path archivoPath = pacienteDir.resolve(nombreArchivo);

        // Evitar sobrescribir archivos existentes
        int contador = 1;
        while (Files.exists(archivoPath)) {
            String nombreSinExt = nombreArchivo.replace(".fasta", "");
            nombreArchivo = nombreSinExt + "_" + contador + ".fasta";
            archivoPath = pacienteDir.resolve(nombreArchivo);
            contador++;
        }

        // Crear contenido en formato FASTA
        String contenido = ">" + muestra.getDocumentoPaciente() + "|" +
                muestra.getFecha().format(DateTimeFormatter.ISO_DATE_TIME) + "\n" +
                muestra.getSecuencia() + "\n";

        // Escribir archivo
        Files.writeString(archivoPath, contenido);
        muestra.setArchivoPath(archivoPath.toString());

        // Guardar en memoria
        muestras.put(muestra.getId(), muestra);
        System.out.println("[MuestraRepository] Muestra guardada: " + archivoPath);

        return muestra;
    }

    /**
     * Busca una muestra por su ID.
     *
     * @param id ID de la muestra
     * @return Optional con la muestra si existe
     */
    public Optional<Muestra> buscarPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            return Optional.empty();
        }
        return Optional.ofNullable(muestras.get(id));
    }

    /**
     * Busca muestras por documento de paciente.
     *
     * @param documento Documento del paciente
     * @return Lista de muestras ordenadas por fecha (más reciente primero)
     */
    public List<Muestra> buscarPorPaciente(String documento) {
        if (documento == null || documento.trim().isEmpty()) {
            return Collections.emptyList();
        }

        return muestras.values().stream()
                .filter(m -> documento.equals(m.getDocumentoPaciente()))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene la muestra más reciente de un paciente.
     *
     * @param documento Documento del paciente
     * @return Optional con la muestra más reciente
     */
    public Optional<Muestra> obtenerMuestraReciente(String documento) {
        return buscarPorPaciente(documento).stream().findFirst();
    }

    /**
     * Obtiene todas las muestras anteriores a una fecha específica.
     *
     * @param documento Documento del paciente
     * @param fecha Fecha límite
     * @return Lista de muestras anteriores a la fecha
     */
    public List<Muestra> obtenerMuestrasAnterioresA(String documento, LocalDateTime fecha) {
        return buscarPorPaciente(documento).stream()
                .filter(m -> m.getFecha().isBefore(fecha))
                .collect(Collectors.toList());
    }

    /**
     * Verifica si existe una muestra con el ID dado.
     *
     * @param id ID de la muestra
     * @return true si existe
     */
    public boolean existe(String id) {
        return muestras.containsKey(id);
    }

    /**
     * Obtiene todas las muestras del sistema.
     *
     * @return Lista de todas las muestras
     */
    public List<Muestra> listarTodas() {
        return new ArrayList<>(muestras.values());
    }

    /**
     * Elimina una muestra por su ID.
     *
     * @param id ID de la muestra
     * @return true si se eliminó correctamente
     */
    public boolean eliminar(String id) {
        Muestra muestra = muestras.remove(id);
        if (muestra != null && muestra.getArchivoPath() != null) {
            try {
                Files.deleteIfExists(Paths.get(muestra.getArchivoPath()));
                System.out.println("[MuestraRepository] Muestra eliminada: " + id);
                return true;
            } catch (IOException e) {
                System.err.println("[MuestraRepository] Error eliminando archivo: " + e.getMessage());
                // La muestra se eliminó de memoria pero no del disco
                return true;
            }
        }
        return false;
    }

    /**
     * Obtiene el número total de muestras.
     *
     * @return Cantidad de muestras
     */
    public int contar() {
        return muestras.size();
    }
}