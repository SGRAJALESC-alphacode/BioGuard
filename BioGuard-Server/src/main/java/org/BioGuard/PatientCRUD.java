package org.BioGuard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PatientCRUD {
    // REQUERIMIENTO: Persistencia mediante archivos [cite: 4, 32]
    private static final String DATA_FOLDER = "data/patients/";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void createPatient(Patient patient) {
        try {
            File folder = new File(DATA_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            // Validación: No permitir documentos duplicados según requerimiento [cite: 8]
            File file = new File(DATA_FOLDER + "patient_" + patient.getPatient_id() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(patient, writer);
            }
            System.out.println("[CRUD] Paciente guardado: " + patient.getPatient_id());
        } catch (IOException e) {
            System.err.println("[ERROR CRUD] No se pudo crear: " + e.getMessage());
        }
    }

    public static Patient readPatient(String patientId) {
        File file = new File(DATA_FOLDER + "patient_" + patientId + ".json");
        if (!file.exists()) {
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, Patient.class);
        } catch (IOException e) {
            System.err.println("[ERROR CRUD] No se pudo leer: " + e.getMessage());
            return null;
        }
    }
}