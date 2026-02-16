package org.BioGuard;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PatientHandler {

    /*
     *  // Objetivo //
     *     Definir las rutas de carpetas utilizadas por el sistema para almacenar archivos clave.
     *  // Constantes //
     *     ADN_FOLDER      : Carpeta donde se almacenan los archivos FASTA de ADN de los pacientes.
     *     DISEASES_FOLDER : Carpeta donde se guardan los archivos relacionados con enfermedades.
     *     RESULTS_FOLDER  : Carpeta donde se almacenan los resultados generados para los pacientes.
     *  // Entradas //
     *     Ninguna, son constantes de ruta utilizadas internamente.
     *  // Salidas //
     *     Ninguna, pero permiten que otras clases y métodos accedan a las rutas de manera consistente.
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
     *     1. Construye la ruta del archivo FASTA del paciente y lo lee.
     *     2. Lista todos los archivos FASTA de enfermedades en la carpeta correspondiente.
     *     3. Para cada archivo de enfermedad:
     *        a) Lee la secuencia de ADN de la enfermedad.
     *        b) Compara si la secuencia del paciente contiene la secuencia de la enfermedad.
     *        c) Si coincide, agrega el nombre de la enfermedad a la lista de detectadas.
     *     4. Guarda los resultados del paciente en un archivo en la carpeta de resultados.
     *     5. Maneja excepciones de entrada/salida mostrando un mensaje en consola.
     *  // Salidas //
     *     Retorna una lista de nombres de enfermedades detectadas en el paciente.
     */

    public static List<String> processPatient(Patient patient) {
        List<String> detectedDiseases = new ArrayList<>();

        try {
            // Leer archivo FASTA del paciente
            String patientFile = ADN_FOLDER + "patient" + patient.getPatient_id() + ".fasta";
            String patientDNA = readFastaFile(patientFile);

            // Leer todos los archivos de enfermedades
            File diseasesDir = new File(DISEASES_FOLDER);
            File[] diseaseFiles = diseasesDir.listFiles((dir, name) -> name.endsWith(".fasta"));

            if (diseaseFiles != null) {
                for (File diseaseFile : diseaseFiles) {
                    String diseaseDNA = readFastaFile(diseaseFile.getPath());
                    String diseaseName = diseaseFile.getName().replace(".fasta", "");

                    // Buscar si el ADN del paciente contiene la secuencia de la enfermedad
                    if (patientDNA.contains(diseaseDNA)) {
                        detectedDiseases.add(diseaseName);
                    }
                }
            }

            // Guardar los resultados del análisis de enfermedades de un paciente y manejar posibles errores
            savePatientResult(patient, detectedDiseases);

        } catch (IOException e) {
            System.out.println("Error procesando paciente: " + e.getMessage());
        }

        return detectedDiseases;
    }

    /*
     *  // Objetivo //
     *     Leer un archivo FASTA y devolver la secuencia de ADN ignorando las líneas de encabezado.
     *  // Entradas //
     *     path : Ruta del archivo FASTA a leer.
     *  // Proceso //
     *     1. Abre el archivo con BufferedReader.
     *     2. Recorre cada línea del archivo.
     *     3. Ignora las líneas que comienzan con ">" (encabezados FASTA).
     *     4. Agrega al StringBuilder las líneas de secuencia, eliminando espacios en blanco.
     *  // Salidas //
     *     Retorna un String que contiene la secuencia completa de ADN del archivo FASTA.
     *  // Excepciones //
     *     Lanza IOException si ocurre un error al leer el archivo.
     */
    private static String readFastaFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")) {
                    sb.append(line.trim());
                }
            }
        }
        return sb.toString();
    }

    /*
     *     Guardar los resultados del análisis de ADN de un paciente en un archivo de texto.
     *  // Proceso //
     *     1. Verifica si la carpeta de resultados existe; si no, la crea.
     *     2. Construye la ruta del archivo de resultados con el formato "patient_<ID>_results.txt".
     *     3. Abre un BufferedWriter para escribir en el archivo.
     *     4. Escribe la información básica del paciente y las enfermedades detectadas.
     *     5. Si no se detectaron enfermedades, indica "Ninguna".
     *  // Salidas //
     *     Ninguna, pero genera un archivo de texto con los resultados del paciente.
     *  // Excepciones //
     *     Lanza IOException si ocurre un error al crear o escribir el archivo.
     */
    private static void savePatientResult(Patient patient, List<String> diseases) throws IOException {
        File dir = new File(RESULTS_FOLDER);
        if (!dir.exists()) dir.mkdirs();

        String resultFile = RESULTS_FOLDER + "patient_" + patient.getPatient_id() + "_results.txt";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile))) {
            bw.write("Paciente ID: " + patient.getPatient_id() + "\n");
            bw.write("Nombre: " + patient.getFull_name() + "\n");
            bw.write("Documento: " + patient.getDocument_id() + "\n");
            bw.write("Edad: " + patient.getAge() + "\n");
            bw.write("Archivo FASTA: " + patient.getClinical_notes() + "\n");
            if (diseases.isEmpty()) {
                bw.write("Enfermedades detectadas: Ninguna\n");
            } else {
                bw.write("Enfermedades detectadas: " + String.join(", ", diseases) + "\n");
            }
        }
    }
}