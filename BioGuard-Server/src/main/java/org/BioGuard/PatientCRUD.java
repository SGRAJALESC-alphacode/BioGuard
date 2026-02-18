package org.BioGuard;

/*
 * // Objetivo
 *    Proveer utilidades de persistencia por paciente (almacenar/leer JSON en
 *    `data/patients/patient_<document_id>.json`).
 *
 * // Responsabilidades
 *    - serializar/deserializar objetos `Paciente` a/desde JSON
 *    - ofrecer métodos create/read para operaciones rápidas
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;

public class PatientCRUD {
    // Requerimiento: Persistencia en archivos CSV y JSON [cite: 4, 32]
    private static final String DATA_FOLDER = "data/patients/";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public static void createPatient(Paciente paciente) {
        try {
            File folder = new File(DATA_FOLDER);
            if (!folder.exists()) folder.mkdirs();

            // Validación: No se permiten documentos duplicados
            File file = new File(DATA_FOLDER + "patient_" + paciente.getDocumento() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(paciente, writer);
            }
            System.out.println("[CRUD] Paciente " + paciente.getDocumento() + " guardado exitosamente.");
        } catch (IOException e) {
            System.err.println("[ERROR CRUD] " + e.getMessage());
        }
    }

    public static Paciente readPatient(String patientId) {
        File file = new File(DATA_FOLDER + "patient_" + patientId + ".json");
        if (!file.exists()) return null;

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, Paciente.class);
        } catch (IOException e) {
            return null;
        }
    }
}