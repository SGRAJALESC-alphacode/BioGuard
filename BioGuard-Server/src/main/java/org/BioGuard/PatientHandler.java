package org.BioGuard;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import org.BioGuard.exceptions.GeneticAnalysisException;
import org.BioGuard.exceptions.FastaFileException;
import org.BioGuard.exceptions.ResultsSaveException;

/*
 *  // Objetivo //
 *     Procesar información genética de pacientes, realizar análisis de detección de enfermedades
 *     y guardar los resultados en archivos de texto.
 *  // Atributos //
 *     ADN_FOLDER      : Carpeta donde se almacenan los archivos FASTA de ADN de los pacientes.
 *     DISEASES_FOLDER : Carpeta donde se guardan los archivos relacionados con enfermedades.
 *     RESULTS_FOLDER  : Carpeta donde se almacenan los resultados generados para los pacientes.
 */
public class PatientHandler {

    /*
     *  // Objetivo //
     *     Definir las rutas de carpetas utilizadas por el sistema para almacenar archivos clave.
     *  // Constantes //
     *     ADN_FOLDER      : Carpeta donde se almacenan los archivos FASTA de ADN de los pacientes.
     *     DISEASES_FOLDER : Carpeta donde se guardan los archivos relacionados con enfermedades.
     *     RESULTS_FOLDER  : Carpeta donde se almacenan los resultados generados para los pacientes.
     */
    private static final String ADN_FOLDER = "data/adn/";
    private static final String DISEASES_FOLDER = "data/diseases/";
    private static final String RESULTS_FOLDER = "data/patients_results/";


    /*
     *  // Objetivo //
     *     Analizar el ADN de un paciente para detectar enfermedades comparando su secuencia con archivos FASTA de enfermedades conocidas.
     *  // Entradas //
     *     patient : Objeto Patient que contiene la información del paciente a procesar.
     *  // Proceso //
     *     1. Valida que el paciente y su ID no sean nulos.
     *     2. Construye la ruta del archivo FASTA del paciente y lo lee usando readFastaFile.
     *     3. Lista todos los archivos FASTA de enfermedades en la carpeta correspondiente.
     *     4. Para cada archivo de enfermedad:
     *        a) Lee la secuencia de ADN de la enfermedad.
     *        b) Compara si la secuencia del paciente contiene la secuencia de la enfermedad.
     *        c) Si coincide, agrega el nombre de la enfermedad a la lista de detectadas.
     *     5. Guarda los resultados del paciente en un archivo en la carpeta de resultados.
     *     6. Maneja excepciones lanzando GeneticAnalysisException con contexto del error.
     *  // Salidas //
     *     Retorna una List<String> con los nombres de enfermedades detectadas en el paciente.
     *  // Excepciones //
     *     Lanza GeneticAnalysisException si hay error en el análisis genético.
     */
    public static List<String> processPatient(Patient patient) throws GeneticAnalysisException {
        // Validación de entrada
        if (patient == null || patient.getPatient_id() == null || patient.getPatient_id().isEmpty()) {
            throw new GeneticAnalysisException("unknown", "Datos del paciente inválidos o nulos");
        }

        List<String> detectedDiseases = new ArrayList<>();
        String patientId = patient.getPatient_id();

        try {
            // Leer archivo FASTA del paciente
            String patientFile = ADN_FOLDER + "patient" + patientId + ".fasta";
            String patientDNA = readFastaFile(patientFile);

            if (patientDNA == null || patientDNA.isEmpty()) {
                throw new GeneticAnalysisException(patientId, "La secuencia genética del paciente está vacía");
            }

            // Leer todos los archivos de enfermedades
            File diseasesDir = new File(DISEASES_FOLDER);
            if (!diseasesDir.exists()) {
                throw new GeneticAnalysisException(patientId, "Carpeta de enfermedades no encontrada: " + DISEASES_FOLDER);
            }

            File[] diseaseFiles = diseasesDir.listFiles((dir, name) -> name.endsWith(".fasta"));

            if (diseaseFiles != null && diseaseFiles.length > 0) {
                for (File diseaseFile : diseaseFiles) {
                    String diseaseDNA = readFastaFile(diseaseFile.getPath());
                    String diseaseName = diseaseFile.getName().replace(".fasta", "");

                    // Buscar si el ADN del paciente contiene la secuencia de la enfermedad
                    if (diseaseDNA != null && !diseaseDNA.isEmpty() && patientDNA.contains(diseaseDNA)) {
                        detectedDiseases.add(diseaseName);
                    }
                }
            }

            // Guardar los resultados del análisis
            savePatientResult(patient, detectedDiseases);

        } catch (FastaFileException e) {
            throw new GeneticAnalysisException(patientId, "Error al leer archivo FASTA: " + e.getMessage(), e);
        } catch (ResultsSaveException e) {
            throw new GeneticAnalysisException(patientId, "Error al guardar resultados: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new GeneticAnalysisException(patientId, "Error en la operación de análisis: " + e.getMessage(), e);
        }

        return detectedDiseases;
    }

    /*
     *  // Objetivo //
     *     Leer un archivo FASTA y devolver la secuencia de ADN ignorando las líneas de encabezado.
     *  // Entradas //
     *     path : Ruta del archivo FASTA a leer.
     *  // Proceso //
     *     1. Valida que la ruta no sea nula ni vacía.
     *     2. Verifica que el archivo existe.
     *     3. Abre el archivo con BufferedReader.
     *     4. Recorre cada línea del archivo.
     *     5. Ignora las líneas que comienzan con ">" (encabezados FASTA).
     *     6. Agrega al StringBuilder las líneas de secuencia, eliminando espacios en blanco.
     *  // Salidas //
     *     Retorna un String que contiene la secuencia completa de ADN del archivo FASTA.
     *  // Excepciones //
     *     Lanza FastaFileException si el archivo no existe o hay error al leer.
     */
    private static String readFastaFile(String path) throws FastaFileException {
        // Validación
        if (path == null || path.isEmpty()) {
            throw new FastaFileException(path, "Ruta del archivo no puede ser nula ni vacía");
        }

        File file = new File(path);
        if (!file.exists()) {
            throw new FastaFileException(path, "Archivo no encontrado");
        }

        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")) {
                    sb.append(line.trim());
                }
            }
        } catch (IOException e) {
            throw new FastaFileException(path, "Error de I/O al leer archivo", e);
        }
        return sb.toString();
    }

    /*
     *  // Objetivo //
     *     Guardar los resultados del análisis de ADN de un paciente en un archivo de texto.
     *  // Entradas //
     *     patient  : Objeto Patient con información del paciente.
     *     diseases : List<String> con los nombres de enfermedades detectadas.
     *  // Proceso //
     *     1. Valida que el paciente y la lista no sean nulos.
     *     2. Verifica si la carpeta de resultados existe; si no, la crea.
     *     3. Construye la ruta del archivo de resultados con el formato "patient_<ID>_results.txt".
     *     4. Abre un BufferedWriter para escribir en el archivo.
     *     5. Escribe la información básica del paciente y las enfermedades detectadas.
     *     6. Si no se detectaron enfermedades, indica "Ninguna".
     *  // Salidas //
     *     Ninguna, pero genera un archivo de texto con los resultados del paciente.
     *  // Excepciones //
     *     Lanza ResultsSaveException si hay error al crear o escribir el archivo.
     */
    private static void savePatientResult(Patient patient, List<String> diseases) throws ResultsSaveException {
        // Validación
        if (patient == null || patient.getPatient_id() == null || patient.getPatient_id().isEmpty()) {
            throw new ResultsSaveException("unknown", "Datos del paciente inválidos");
        }

        try {
            File dir = new File(RESULTS_FOLDER);
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    throw new ResultsSaveException(patient.getPatient_id(), "No se pudo crear la carpeta de resultados");
                }
            }

            String resultFile = RESULTS_FOLDER + "patient_" + patient.getPatient_id() + "_results.txt";
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile))) {
                bw.write("Paciente ID: " + patient.getPatient_id() + "\n");
                bw.write("Nombre: " + patient.getFull_name() + "\n");
                bw.write("Documento: " + patient.getDocument_id() + "\n");
                bw.write("Edad: " + patient.getAge() + "\n");
                bw.write("Archivo FASTA: " + patient.getClinical_notes() + "\n");
                if (diseases == null || diseases.isEmpty()) {
                    bw.write("Enfermedades detectadas: Ninguna\n");
                } else {
                    bw.write("Enfermedades detectadas: " + String.join(", ", diseases) + "\n");
                }
            }
        } catch (IOException e) {
            throw new ResultsSaveException(patient.getPatient_id(), "Error de I/O al guardar resultados", e);
        }
    }
}