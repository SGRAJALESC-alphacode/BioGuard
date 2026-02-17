package org.BioGuard;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PatientHandler {
    private static final String ADN_FOLDER = "data/adn/";
    private static final String DISEASES_FOLDER = "data/diseases/";
    private static final String RESULTS_FOLDER = "data/patients_results/";

    public static List<String> processPatient(Patient patient) {
        List<String> detectedViruses = new ArrayList<>();
        try {
            // REQUERIMIENTO: Organizar muestras en carpetas por ID de paciente
            String patientSpecificDir = ADN_FOLDER + patient.getPatient_id() + "/";
            File dir = new File(patientSpecificDir);
            if (!dir.exists()) dir.mkdirs();

            // Asumiendo que la secuencia viene en clinical_notes o se lee del archivo
            String patientDNA = readFastaFile(patient.getClinical_notes());

            // Comparar con catálogo viral (archivos FASTA) [cite: 10, 17]
            File diseasesDir = new File(DISEASES_FOLDER);
            File[] virusFiles = diseasesDir.listFiles((d, name) -> name.endsWith(".fasta"));

            if (virusFiles != null) {
                for (File virusFile : virusFiles) {
                    String virusDNA = readFastaFile(virusFile.getPath());
                    String virusName = virusFile.getName().replace(".fasta", "");

                    if (patientDNA.contains(virusDNA)) {
                        detectedViruses.add(virusName);
                    }
                }
            }
            savePatientResult(patient, detectedViruses);
        } catch (IOException e) {
            System.err.println("[ERROR ANALISIS] " + e.getMessage());
        }
        return detectedViruses;
    }

    private static String readFastaFile(String path) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith(">")) sb.append(line.trim());
            }
        }
        return sb.toString();
    }

    private static void savePatientResult(Patient patient, List<String> diseases) throws IOException {
        File resDir = new File(RESULTS_FOLDER);
        if (!resDir.exists()) resDir.mkdirs();

        String resultFile = RESULTS_FOLDER + "patient_" + patient.getPatient_id() + "_diag.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile))) {
            // REQUERIMIENTO: Formato CSV para resultados [cite: 18]
            bw.write("virus,resultado\n");
            if (diseases.isEmpty()) {
                bw.write("NINGUNO,No detectado\n");
            } else {
                for (String d : diseases) bw.write(d + ",Detectado\n");
            }
        }
    }
}