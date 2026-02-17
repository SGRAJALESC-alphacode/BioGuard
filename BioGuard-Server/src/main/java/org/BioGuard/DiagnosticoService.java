package org.BioGuard;

import org.BioGuard.exception.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

public class DiagnosticoService {

    private static final String MUESTRAS_FOLDER = "data/muestras/";
    private static final String REPORTES_FOLDER = "data/reportes/";

    private VirusService virusService;
    private PacienteService pacienteService;

    public DiagnosticoService() {
        this.virusService = new VirusService();
        this.pacienteService = new PacienteService();
        new File(MUESTRAS_FOLDER).mkdirs();
        new File(REPORTES_FOLDER).mkdirs();
    }

    /**
     * Realiza el diagnóstico de una muestra de ADN.
     *
     * @param documento Documento del paciente
     * @param fechaMuestra Fecha de la muestra
     * @param secuencia Secuencia de ADN
     * @return Lista de diagnósticos encontrados
     * @throws DiagnosticoException Si hay error en el diagnóstico
     * @throws IOException Si hay error de E/S
     */
    public List<Diagnostico> diagnosticarMuestra(String documento, String fechaMuestra, String secuencia)
            throws DiagnosticoException, IOException {

        // Validar secuencia
        if (secuencia == null || secuencia.trim().isEmpty()) {
            throw new DiagnosticoException("La secuencia de ADN está vacía");
        }

        if (!secuencia.matches("^[ATCG]+$")) {
            throw new DiagnosticoException("La secuencia contiene caracteres inválidos. Solo se permiten A, T, C, G");
        }

        // Guardar la muestra
        String carpetaPaciente = MUESTRAS_FOLDER + documento + "/";
        Files.createDirectories(Paths.get(carpetaPaciente));

        String nombreArchivo = fechaMuestra.replaceAll("[^0-9T]", "_") + ".fasta";
        Path rutaMuestra = Paths.get(carpetaPaciente + nombreArchivo);

        String header = ">" + documento + "|" + fechaMuestra;
        Files.writeString(rutaMuestra, header + "\n" + secuencia + "\n");

        // Cargar virus registrados
        List<Virus> virusRegistrados = virusService.cargarTodosLosVirus();
        List<Diagnostico> diagnosticos = new ArrayList<>();

        // Buscar coincidencias
        for (Virus virus : virusRegistrados) {
            String secuenciaVirus = virus.getSecuencia();
            if (secuenciaVirus == null || secuenciaVirus.isEmpty()) {
                continue;
            }

            int index = 0;
            while ((index = secuencia.indexOf(secuenciaVirus, index)) != -1) {
                diagnosticos.add(new Diagnostico(
                        virus.getNombre(),
                        virus.getNivelInfecciosidad(),
                        index,
                        index + secuenciaVirus.length() - 1
                ));
                index++;
            }
        }

        // Guardar resultados
        guardarResultadosDiagnostico(documento, fechaMuestra, diagnosticos);

        return diagnosticos;
    }

    /**
     * Guarda los resultados del diagnóstico en un archivo CSV.
     *
     * @param documento Documento del paciente
     * @param fechaMuestra Fecha de la muestra
     * @param diagnosticos Lista de diagnósticos
     * @throws IOException Si hay error de escritura
     */
    private void guardarResultadosDiagnostico(String documento, String fechaMuestra, List<Diagnostico> diagnosticos)
            throws IOException {

        String carpetaPaciente = MUESTRAS_FOLDER + documento + "/";
        Files.createDirectories(Paths.get(carpetaPaciente));

        String nombreArchivo = "diagnostico_" + fechaMuestra.replaceAll("[^0-9T]", "_") + ".csv";
        Path rutaResultado = Paths.get(carpetaPaciente + nombreArchivo);

        StringBuilder contenido = new StringBuilder();
        contenido.append("virus,nivel_infecciosidad,posicion_inicio,posicion_fin\n");

        for (Diagnostico d : diagnosticos) {
            contenido.append(d.toCsvString()).append("\n");
        }

        Files.writeString(rutaResultado, contenido.toString());
    }

    /**
     * Genera reporte de pacientes de alto riesgo.
     * Formato: documento, cantidad_virus_detectados, cantidad_virus_altamente_infecciosos,
     *          lista_virus_normal, lista_virus_altamente_infecciosos
     *
     * @return Ruta del archivo generado
     * @throws IOException Si hay error de E/S
     */
    public String generarReporteAltoRiesgo() throws IOException {
        String timestamp = String.valueOf(System.currentTimeMillis());
        Path rutaReporte = Paths.get(REPORTES_FOLDER + "alto_riesgo_" + timestamp + ".csv");

        List<Paciente> pacientes = pacienteService.listarTodos();
        List<Virus> virusList = virusService.cargarTodosLosVirus();

        // Mapa de virus por nombre para acceso rápido
        Map<String, Virus> mapaVirus = new HashMap<>();
        for (Virus v : virusList) {
            mapaVirus.put(v.getNombre(), v);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("documento,cantidad_virus_detectados,cantidad_virus_altamente_infecciosos,lista_virus_normal,lista_virus_altamente_infecciosos\n");

        for (Paciente p : pacientes) {
            // Obtener el último diagnóstico del paciente
            File carpeta = new File(MUESTRAS_FOLDER + p.getDocumento() + "/");
            if (!carpeta.exists()) continue;

            File[] diagFiles = carpeta.listFiles((dir, name) -> name.startsWith("diagnostico_") && name.endsWith(".csv"));
            if (diagFiles == null || diagFiles.length == 0) continue;

            // Tomar el más reciente
            Arrays.sort(diagFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

            List<String> lineas = Files.readAllLines(diagFiles[0].toPath());

            List<String> virusNormal = new ArrayList<>();
            List<String> virusAltamente = new ArrayList<>();

            // Saltar cabecera (i=1)
            for (int i = 1; i < lineas.size(); i++) {
                String linea = lineas.get(i).trim();
                if (linea.isEmpty()) continue;

                String[] partes = linea.split(",");
                if (partes.length >= 4) {
                    String nombreVirus = partes[0];
                    Virus v = mapaVirus.get(nombreVirus);

                    if (v != null && "Altamente Infeccioso".equals(v.getNivelInfecciosidad())) {
                        if (!virusAltamente.contains(nombreVirus)) {
                            virusAltamente.add(nombreVirus);
                        }
                    } else {
                        if (!virusNormal.contains(nombreVirus)) {
                            virusNormal.add(nombreVirus);
                        }
                    }
                }
            }

            // Filtrar solo pacientes con más de 3 virus altamente infecciosos
            if (virusAltamente.size() > 3) {
                sb.append(p.getDocumento()).append(",")
                        .append(lineas.size() - 1).append(",")
                        .append(virusAltamente.size()).append(",")
                        .append(virusNormal).append(",")
                        .append(virusAltamente).append("\n");
            }
        }

        Files.writeString(rutaReporte, sb.toString());
        return rutaReporte.toString();
    }

    /**
     * Genera reporte de mutaciones para un paciente.
     * Compara la muestra actual con todas las anteriores.
     *
     * @param documento Documento del paciente
     * @return Ruta del archivo generado
     * @throws MuestraNoEncontradaException Si no hay suficientes muestras
     * @throws IOException Si hay error de E/S
     */
    public String generarReporteMutaciones(String documento) throws MuestraNoEncontradaException, IOException {
        File carpeta = new File(MUESTRAS_FOLDER + documento + "/");
        if (!carpeta.exists()) {
            throw new MuestraNoEncontradaException(documento);
        }

        File[] muestras = carpeta.listFiles((dir, name) -> name.endsWith(".fasta"));
        if (muestras == null || muestras.length < 2) {
            throw new MuestraNoEncontradaException("Se necesitan al menos 2 muestras para detectar mutaciones");
        }

        // Ordenar por fecha (más reciente primero)
        Arrays.sort(muestras, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        String timestamp = String.valueOf(System.currentTimeMillis());
        Path rutaReporte = Paths.get(REPORTES_FOLDER + "mutaciones_" + documento + "_" + timestamp + ".csv");

        // Leer secuencia actual (la más reciente)
        String secuenciaActual = leerSecuenciaDeFasta(muestras[0]);

        StringBuilder sb = new StringBuilder();
        sb.append("muestra_anterior,fecha_anterior,posicion_inicio,posicion_fin,tipo_cambio\n");

        // Comparar con cada muestra anterior
        for (int i = 1; i < muestras.length; i++) {
            String secuenciaAnterior = leerSecuenciaDeFasta(muestras[i]);

            int minLen = Math.min(secuenciaAnterior.length(), secuenciaActual.length());

            for (int j = 0; j < minLen; j++) {
                if (secuenciaAnterior.charAt(j) != secuenciaActual.charAt(j)) {
                    int inicio = j;

                    // Encontrar el final de la diferencia
                    while (j < minLen && secuenciaAnterior.charAt(j) != secuenciaActual.charAt(j)) {
                        j++;
                    }

                    String tipo;
                    if (secuenciaAnterior.length() != secuenciaActual.length()) {
                        tipo = "INDEL";
                    } else {
                        tipo = "SUSTITUCION";
                    }

                    sb.append(muestras[i].getName()).append(",")
                            .append(new Date(muestras[i].lastModified()).toString()).append(",")
                            .append(inicio).append(",")
                            .append(j - 1).append(",")
                            .append(tipo).append("\n");
                }
            }
        }

        Files.writeString(rutaReporte, sb.toString());
        return rutaReporte.toString();
    }

    /**
     * Lee la secuencia de un archivo FASTA (ignorando headers).
     *
     * @param archivo El archivo a leer
     * @return La secuencia de ADN
     * @throws IOException Si hay error de lectura
     */
    private String leerSecuenciaDeFasta(File archivo) throws IOException {
        List<String> lineas = Files.readAllLines(archivo.toPath());
        StringBuilder secuencia = new StringBuilder();

        for (String linea : lineas) {
            if (!linea.startsWith(">")) {
                secuencia.append(linea.trim());
            }
        }

        return secuencia.toString();
    }

    /**
     * Obtiene el último diagnóstico de un paciente.
     *
     * @param documento Documento del paciente
     * @return Lista de diagnósticos o lista vacía
     * @throws IOException Si hay error de lectura
     */
    public List<Diagnostico> obtenerUltimoDiagnostico(String documento) throws IOException {
        File carpeta = new File(MUESTRAS_FOLDER + documento + "/");
        if (!carpeta.exists()) {
            return Collections.emptyList();
        }

        File[] diagFiles = carpeta.listFiles((dir, name) -> name.startsWith("diagnostico_") && name.endsWith(".csv"));
        if (diagFiles == null || diagFiles.length == 0) {
            return Collections.emptyList();
        }

        Arrays.sort(diagFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        return leerDiagnosticosDeCSV(diagFiles[0]);
    }

    /**
     * Lee diagnósticos desde un archivo CSV.
     *
     * @param archivo El archivo a leer
     * @return Lista de diagnósticos
     * @throws IOException Si hay error de lectura
     */
    private List<Diagnostico> leerDiagnosticosDeCSV(File archivo) throws IOException {
        List<Diagnostico> diagnosticos = new ArrayList<>();
        List<String> lineas = Files.readAllLines(archivo.toPath());

        // Saltar cabecera
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i).trim();
            if (linea.isEmpty()) continue;

            String[] partes = linea.split(",");
            if (partes.length >= 4) {
                diagnosticos.add(new Diagnostico(
                        partes[0], // virus
                        partes[1], // nivel
                        Integer.parseInt(partes[2]), // inicio
                        Integer.parseInt(partes[3])  // fin
                ));
            }
        }

        return diagnosticos;
    }
}