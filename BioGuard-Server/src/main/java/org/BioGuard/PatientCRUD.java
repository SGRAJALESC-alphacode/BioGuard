package org.BioGuard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class PatientCRUD {
    // Requerimiento: Persistencia en archivos CSV y JSON [cite: 4, 32]
    private static final String DATA_FOLDER = "data/patients/";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void createPatient(Patient patient) {
        try {
            File folder = new File(DATA_FOLDER);
            if (!folder.exists()) folder.mkdirs();

            // Validación: No se permiten documentos duplicados [cite: 8]
            File file = new File(DATA_FOLDER + "patient_" + patient.getPatient_id() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(patient, writer);
            }
            System.out.println("[CRUD] Paciente " + patient.getPatient_id() + " guardado exitosamente.");
        } catch (IOException e) {
            System.err.println("[ERROR CRUD] " + e.getMessage());
        }
    }

    public static Patient readPatient(String patientId) {
        File file = new File(DATA_FOLDER + "patient_" + patientId + ".json");
        if (!file.exists()) return null;

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, Patient.class);
        } catch (IOException e) {
            return null;
        }
    }
}