package org.BioGuard;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PatientHandler {
    private static final String ADN_FOLDER = "data/adn/";
    private static final String DISEASES_FOLDER = "data/diseases/";
    private static final String RESULTS_FOLDER = "data/patients_results/";

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

            // Comparar con catálogo viral (archivos FASTA)
            File diseasesDir = new File(DISEASES_FOLDER);
            File[] virusFiles = diseasesDir.listFiles((d, name) -> name.endsWith(".fasta"));

            if (virusFiles != null) {
                for (File virusFile : virusFiles) {
                    String virusDNA = readFastaFile(virusFile.getPath());
                    String virusName = virusFile.getName().replace(".fasta", "");

                    if (!virusDNA.isEmpty() && patientDNA.contains(virusDNA)) {
                        detectedViruses.add(virusName);
                    }
                }
            }
            savePatientResult(paciente, detectedViruses);
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

    private static void savePatientResult(Paciente paciente, List<String> diseases) throws IOException {
        File resDir = new File(RESULTS_FOLDER);
        if (!resDir.exists()) resDir.mkdirs();

        String resultFile = RESULTS_FOLDER + "patient_" + paciente.getDocumento() + "_diag.csv";
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(resultFile))) {
            // REQUERIMIENTO: Formato CSV para resultados
            bw.write("virus,resultado\n");
            if (diseases.isEmpty()) {
                bw.write("NINGUNO,No detectado\n");
            } else {
                for (String d : diseases) bw.write(d + ",Detectado\n");
            }
        }
    }
}