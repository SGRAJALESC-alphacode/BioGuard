package org.BioGuard;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manejador de procesos para análisis de pacientes y detección de virus.
 * Esta clase se encarga de procesar las muestras de ADN de los pacientes,
 * compararlas con el catálogo viral y generar resultados de diagnóstico.
 *
 * // Objetivo
 *    Realizar el análisis de las muestras de ADN de los pacientes,
 *    detectando la presencia de virus conocidos y generando reportes
 *    de resultados en formato CSV.
 *
 * // Atributos
 *    ADN_FOLDER          : Carpeta donde se organizan las muestras por paciente
 *    DISEASES_FOLDER     : Carpeta que contiene los archivos FASTA de virus
 *    RESULTS_FOLDER      : Carpeta donde se guardan los resultados de diagnóstico
 *
 * // Estructura de directorios
 *    data/
 *    ├── adn/                 - Muestras organizadas por ID de paciente
 *    │   └── [documento]/
 *    ├── diseases/            - Catálogo viral en archivos .fasta
 *    │   ├── virus1.fasta
 *    │   └── virus2.fasta
 *    └── patients_results/    - Resultados de diagnóstico en CSV
 *        └── patient_[documento]_diag.csv
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class PatientHandler {

    private static final String ADN_FOLDER = "data/adn/";
    private static final String DISEASES_FOLDER = "data/diseases/";
    private static final String RESULTS_FOLDER = "data/patients_results/";

    /**
     * Procesa un paciente para detectar virus en su muestra de ADN.
     *
     * // Objetivo
     *    Analizar la muestra de ADN asociada a un paciente, compararla
     *    con todos los virus registrados y generar un reporte de resultados.
     *
     * // Proceso
     *    1. Crear carpeta específica para el paciente en ADN_FOLDER
     *    2. Leer la muestra del paciente desde data/muestras/paciente_[documento].fasta
     *    3. Cargar todos los virus registrados en DISEASES_FOLDER
     *    4. Comparar la secuencia de cada virus con la muestra del paciente
     *    5. Registrar los virus detectados
     *    6. Guardar resultados en RESULTS_FOLDER/patient_[documento]_diag.csv
     *
     * // Formato de muestra esperado
     *    Archivo: data/muestras/paciente_[documento].fasta
     *    Contenido:
     *    >[documento]|[fecha]
     *    ATCGATCGATCG
     *
     * // Formato de virus
     *    Archivo: data/diseases/[nombre].fasta
     *    Contenido:
     *    >[nombre]|[nivel]
     *    ATCGATCG
     *
     * @param paciente Paciente a procesar (debe tener documento válido)
     * @return Lista de nombres de virus detectados en la muestra
     */
    public static List<String> processPatient(Paciente paciente) {
        List<String> detectedViruses = new ArrayList<>();
        try {
            // REQUERIMIENTO: Organizar muestras en carpetas por ID de paciente
            String patientSpecificDir = ADN_FOLDER + paciente.getDocumento() + "/";
            File dir = new File(patientSpecificDir);
            if (!dir.exists()) dir.mkdirs();

            // Intentar leer la muestra asociada al paciente en data/muestras/paciente_<documento>.fasta
            String samplePath = "data/muestras/paciente_" + paciente.getDocumento() + ".fasta";
            String patientDNA = readFastaFile(samplePath);

            System.out.println("[DEBUG] Muestra leída de: " + samplePath);
            System.out.println("[DEBUG] Longitud de secuencia: " + patientDNA.length());

            // Comparar con catálogo viral (archivos FASTA)
            File diseasesDir = new File(DISEASES_FOLDER);
            File[] virusFiles = diseasesDir.listFiles((d, name) -> name.endsWith(".fasta"));

            if (virusFiles != null) {
                System.out.println("[DEBUG] Virus encontrados: " + virusFiles.length);

                for (File virusFile : virusFiles) {
                    String virusDNA = readFastaFile(virusFile.getPath());
                    String virusName = virusFile.getName().replace(".fasta", "");

                    if (!virusDNA.isEmpty() && patientDNA.contains(virusDNA)) {
                        detectedViruses.add(virusName);
                        System.out.println("[DEBUG] Virus detectado: " + virusName);
                    }
                }
            } else {
                System.out.println("[DEBUG] No se encontraron archivos de virus en " + DISEASES_FOLDER);
            }

            savePatientResult(paciente, detectedViruses);

        } catch (IOException e) {
            System.err.println("[ERROR ANALISIS] " + e.getMessage());
        }

        return detectedViruses;
    }

    /**
     * Lee un archivo FASTA y extrae la secuencia de ADN.
     *
     * // Objetivo
     *    Extraer la secuencia de nucleótidos de un archivo FASTA,
     *    ignorando las líneas de encabezado (que comienzan con '>').
     *
     * // Formato de archivo esperado
     *    >cabecera (se ignora)
     *    ATCGATCGATCG  (línea(s) con la secuencia)
     *
     * // Comportamiento
     *    - Ignora líneas que comienzan con '>'
     *    - Concatena todas las demás líneas eliminando espacios
     *    - Retorna la secuencia como un String continuo
     *
     * @param path Ruta del archivo FASTA a leer
     * @return Secuencia de ADN como String (vacía si el archivo no tiene secuencia)
     * @throws IOException Si hay error de lectura del archivo
     */
    private static String readFastaFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();

        File file = new File(path);
        if (!file.exists()) {
            System.err.println("[WARN] Archivo no encontrado: " + path);
            return "";
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")) {
                    sb.append(line.trim());
                }
            }
        }

        return sb.toString();
    }

    /**
     * Guarda los resultados del análisis en un archivo CSV.
     *
     * // Objetivo
     *    Persistir los resultados de la detección de virus en un
     *    archivo CSV estructurado para su posterior consulta.
     *
     * // Formato de salida
     *    Archivo: data/patients_results/patient_[documento]_diag.csv
     *    Contenido:
     *    virus,resultado
     *    Virus1,Detectado
     *    Virus2,Detectado
     *    NINGUNO,No detectado  (si no hay virus)
     *
     * // Proceso
     *    1. Crear directorio RESULTS_FOLDER si no existe
     *    2. Crear archivo con nombre basado en documento del paciente
     *    3. Escribir cabecera "virus,resultado"
     *    4. Escribir una línea por cada virus detectado
     *    5. Si no hay virus, escribir "NINGUNO,No detectado"
     *
     * @param paciente Paciente asociado al resultado
     * @param diseases Lista de virus detectados
     * @throws IOException Si hay error de escritura
     */
    private static void savePatientResult(Paciente paciente, List<String> diseases) throws IOException {
        File resDir = new File(RESULTS_FOLDER);
        if (!resDir.exists()) resDir.mkdirs();

        String resultFile = RESULTS_FOLDER + "patient_" + paciente.getDocumento() + "_diag.csv";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile))) {
            // REQUERIMIENTO: Formato CSV para resultados
            bw.write("virus,resultado\n");

            if (diseases.isEmpty()) {
                bw.write("NINGUNO,No detectado\n");
                System.out.println("[INFO] No se detectaron virus para paciente: " + paciente.getDocumento());
            } else {
                for (String d : diseases) {
                    bw.write(d + ",Detectado\n");
                }
                System.out.println("[INFO] Resultados guardados para paciente: " + paciente.getDocumento() +
                        " - Virus detectados: " + diseases.size());
            }
        }

        System.out.println("[DEBUG] Archivo generado: " + resultFile);
    }
}