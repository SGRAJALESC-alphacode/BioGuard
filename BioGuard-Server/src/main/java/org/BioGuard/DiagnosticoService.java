package org.BioGuard;

import org.BioGuard.exception.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Servicio para diagnóstico de muestras y generación de reportes.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class DiagnosticoService {

    private static final String MUESTRAS_FOLDER = "data/muestras/";
    private static final String REPORTES_FOLDER = "data/reportes/";

    private final VirusService virusService;
    private final PacienteService pacienteService;

    public DiagnosticoService() {
        this.virusService = new VirusService();
        this.pacienteService = new PacienteService();

        // Crear directorios si no existen
        new File(MUESTRAS_FOLDER).mkdirs();
        new File(REPORTES_FOLDER).mkdirs();

        System.out.println("[INFO] DiagnosticoService inicializado");
    }

    /**
     * Realiza diagnóstico de una muestra de ADN.
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

        System.out.println("[DEBUG] Diagnosticando muestra - Documento: " + documento);
        System.out.println("[DEBUG] Fecha: " + fechaMuestra);
        System.out.println("[DEBUG] Secuencia: " + secuencia);

        // Validar secuencia
        if (secuencia == null || secuencia.trim().isEmpty()) {
            throw new DiagnosticoException("La secuencia de ADN está vacía");
        }

        if (!secuencia.matches("^[ATCG]+$")) {
            throw new DiagnosticoException("La secuencia contiene caracteres inválidos. Solo se permiten A,T,C,G");
        }

        // Guardar la muestra
        String carpetaPaciente = MUESTRAS_FOLDER + documento + "/";
        Files.createDirectories(Paths.get(carpetaPaciente));

        String nombreArchivo = fechaMuestra.replaceAll("[^0-9]", "_") + ".fasta";
        Path rutaMuestra = Paths.get(carpetaPaciente + nombreArchivo);

        String header = ">" + documento + "|" + fechaMuestra;
        Files.writeString(rutaMuestra, header + "\n" + secuencia + "\n");
        System.out.println("[DEBUG] Muestra guardada en: " + rutaMuestra);

        // Cargar virus registrados
        List<Virus> virusRegistrados = virusService.cargarTodosLosVirus();
        System.out.println("[DEBUG] Virus cargados: " + virusRegistrados.size());

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
                System.out.println("[DEBUG] Virus detectado: " + virus.getNombre() +
                        " en posición " + index);
                index++;
            }
        }

        // Guardar resultados
        guardarResultadosDiagnostico(documento, fechaMuestra, diagnosticos);

        return diagnosticos;
    }

    /**
     * Guarda los resultados del diagnóstico en un archivo CSV.
     */
    private void guardarResultadosDiagnostico(String documento, String fechaMuestra, List<Diagnostico> diagnosticos)
            throws IOException {

        String carpetaPaciente = MUESTRAS_FOLDER + documento + "/";
        Files.createDirectories(Paths.get(carpetaPaciente));

        String nombreArchivo = "diagnostico_" + fechaMuestra.replaceAll("[^0-9]", "_") + ".csv";
        Path rutaResultado = Paths.get(carpetaPaciente + nombreArchivo);

        StringBuilder contenido = new StringBuilder();
        contenido.append("virus,nivel_infecciosidad,posicion_inicio,posicion_fin\n");

        for (Diagnostico d : diagnosticos) {
            contenido.append(d.toCsvString()).append("\n");
        }

        Files.writeString(rutaResultado, contenido.toString());
        System.out.println("[DEBUG] Resultados guardados en: " + rutaResultado);
    }

    /**
     * Genera reporte de pacientes de alto riesgo.
     * Formato: documento,cantidad_virus_detectados,cantidad_altamente,lista_normal,lista_altamente
     */
    public String generarReporteAltoRiesgo() throws IOException {
        System.out.println("[DEBUG] ===== GENERANDO REPORTE ALTO RIESGO =====");

        String timestamp = String.valueOf(System.currentTimeMillis());
        String nombreReporte = "alto_riesgo_" + timestamp + ".csv";
        Path rutaReporte = Paths.get(REPORTES_FOLDER + nombreReporte);

        List<Paciente> pacientes = pacienteService.listarTodos();
        System.out.println("[DEBUG] Total pacientes: " + pacientes.size());

        if (pacientes.isEmpty()) {
            Files.writeString(rutaReporte, "No hay pacientes registrados");
            return "No hay pacientes registrados. Reporte vacío.";
        }

        List<Virus> virusList = virusService.cargarTodosLosVirus();
        System.out.println("[DEBUG] Total virus: " + virusList.size());

        if (virusList.isEmpty()) {
            Files.writeString(rutaReporte, "No hay virus registrados");
            return "No hay virus registrados. No se puede generar reporte.";
        }

        // Mapa de virus por nombre
        Map<String, Virus> mapaVirus = new HashMap<>();
        for (Virus v : virusList) {
            mapaVirus.put(v.getNombre(), v);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("documento,cantidad_virus_detectados,cantidad_altamente,lista_normal,lista_altamente\n");

        int pacientesAltoRiesgo = 0;

        for (Paciente p : pacientes) {
            System.out.println("[DEBUG] Procesando paciente: " + p.getDocumento());

            // Obtener el último diagnóstico del paciente
            List<Diagnostico> diagnosticos = obtenerUltimoDiagnostico(p.getDocumento());

            if (diagnosticos.isEmpty()) {
                System.out.println("[DEBUG] Paciente sin diagnósticos: " + p.getDocumento());
                continue;
            }

            System.out.println("[DEBUG] Diagnósticos encontrados: " + diagnosticos.size());

            List<String> virusNormal = new ArrayList<>();
            List<String> virusAltamente = new ArrayList<>();

            for (Diagnostico d : diagnosticos) {
                Virus v = mapaVirus.get(d.getNombreVirus());
                if (v != null) {
                    if ("Altamente Infeccioso".equals(v.getNivelInfecciosidad())) {
                        if (!virusAltamente.contains(d.getNombreVirus())) {
                            virusAltamente.add(d.getNombreVirus());
                            System.out.println("[DEBUG] Virus altamente detectado: " + d.getNombreVirus());
                        }
                    } else {
                        if (!virusNormal.contains(d.getNombreVirus())) {
                            virusNormal.add(d.getNombreVirus());
                        }
                    }
                }
            }

            // Filtrar pacientes con más de 3 virus altamente infecciosos
            if (virusAltamente.size() > 3) {
                pacientesAltoRiesgo++;
                sb.append(p.getDocumento()).append(",")
                        .append(diagnosticos.size()).append(",")
                        .append(virusAltamente.size()).append(",")
                        .append(virusNormal.toString()).append(",")
                        .append(virusAltamente.toString()).append("\n");

                System.out.println("[DEBUG] PACIENTE ALTO RIESGO: " + p.getDocumento() +
                        " - Altamente: " + virusAltamente.size());
            }
        }

        if (pacientesAltoRiesgo == 0) {
            String mensaje = "No se encontraron pacientes con más de 3 virus altamente infecciosos";
            Files.writeString(rutaReporte, mensaje);
            System.out.println("[INFO] " + mensaje);
            return mensaje + ". Reporte vacío.";
        }

        Files.writeString(rutaReporte, sb.toString());
        System.out.println("[INFO] Reporte alto riesgo generado: " + rutaReporte.toAbsolutePath());
        System.out.println("[INFO] Pacientes en reporte: " + pacientesAltoRiesgo);

        return "OK: Reporte generado en " + rutaReporte.toString() +
                " con " + pacientesAltoRiesgo + " pacientes de alto riesgo";
    }

    /**
     * Genera reporte de mutaciones para un paciente.
     */
    public String generarReporteMutaciones(String documento)
            throws MuestraNoEncontradaException, IOException {

        System.out.println("[DEBUG] ===== GENERANDO REPORTE MUTACIONES =====");
        System.out.println("[DEBUG] Documento: " + documento);

        File carpeta = new File(MUESTRAS_FOLDER + documento + "/");
        System.out.println("[DEBUG] Buscando en carpeta: " + carpeta.getAbsolutePath());

        if (!carpeta.exists()) {
            throw new MuestraNoEncontradaException(documento);
        }

        File[] muestras = carpeta.listFiles((dir, name) -> name.endsWith(".fasta"));
        if (muestras == null || muestras.length < 2) {
            return "No hay suficientes muestras para detectar mutaciones. Se necesitan al menos 2. Actual: " +
                    (muestras != null ? muestras.length : 0);
        }

        System.out.println("[DEBUG] Total muestras encontradas: " + muestras.length);

        // Ordenar por fecha (más reciente primero)
        Arrays.sort(muestras, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        String timestamp = String.valueOf(System.currentTimeMillis());
        String nombreReporte = "mutaciones_" + documento + "_" + timestamp + ".csv";
        Path rutaReporte = Paths.get(REPORTES_FOLDER + nombreReporte);

        // Leer secuencia actual (la más reciente)
        String secuenciaActual = leerSecuenciaDeFasta(muestras[0]);
        System.out.println("[DEBUG] Muestra actual: " + muestras[0].getName());
        System.out.println("[DEBUG] Longitud secuencia actual: " + secuenciaActual.length());

        StringBuilder sb = new StringBuilder();
        sb.append("muestra_anterior,fecha_anterior,posicion_inicio,posicion_fin,tipo_cambio\n");

        int totalMutaciones = 0;

        // Comparar con cada muestra anterior
        for (int i = 1; i < muestras.length; i++) {
            String secuenciaAnterior = leerSecuenciaDeFasta(muestras[i]);
            System.out.println("[DEBUG] Comparando con: " + muestras[i].getName());
            System.out.println("[DEBUG] Longitud secuencia anterior: " + secuenciaAnterior.length());

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

                    totalMutaciones++;
                }
            }
        }

        if (totalMutaciones == 0) {
            Files.writeString(rutaReporte, "No se detectaron mutaciones entre las muestras del paciente " + documento);
            return "No se detectaron mutaciones. Reporte generado en: " + rutaReporte.toString();
        }

        Files.writeString(rutaReporte, sb.toString());
        System.out.println("[DEBUG] Total mutaciones detectadas: " + totalMutaciones);
        System.out.println("[INFO] Reporte mutaciones generado: " + rutaReporte.toAbsolutePath());

        return "OK: Reporte de mutaciones generado en " + rutaReporte.toString() +
                " con " + totalMutaciones + " mutaciones detectadas";
    }

    /**
     * Obtiene el último diagnóstico de un paciente.
     */
    public List<Diagnostico> obtenerUltimoDiagnostico(String documento) throws IOException {
        File carpeta = new File(MUESTRAS_FOLDER + documento + "/");
        if (!carpeta.exists()) {
            return Collections.emptyList();
        }

        File[] diagFiles = carpeta.listFiles((dir, name) ->
                name.startsWith("diagnostico_") && name.endsWith(".csv"));

        if (diagFiles == null || diagFiles.length == 0) {
            return Collections.emptyList();
        }

        Arrays.sort(diagFiles, (a, b) -> Long.compare(b.lastModified(), a.lastModified()));

        return leerDiagnosticosDeCSV(diagFiles[0]);
    }

    /**
     * Lee diagnósticos desde un archivo CSV.
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

    /**
     * Lee la secuencia de un archivo FASTA.
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
}