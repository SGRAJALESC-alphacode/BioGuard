package org.BioGuard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

/*
 *  // Objetivo:
 *     Gestionar las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) de pacientes,
 *     almacenando la información en archivos JSON dentro de la carpeta de datos.
 *  // Atributos:
 *     DATA_FOLDER : Ruta absoluta de la carpeta donde se guardan los archivos de pacientes.
 *
 *     gson : Objeto Gson configurado para serializar y deserializar objetos Patient con formato legible.
 */

public class PatientCRUD {
    private static final String DATA_FOLDER =
            System.getProperty("user.dir") + "/data/patients/";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    /*
     *  // Objetivo //
     *     Crear un nuevo paciente y guardarlo como archivo JSON en la carpeta de datos.
     *  // Entradas //
     *     patient : Objeto Patient que contiene toda la información del paciente a guardar.
     *  // Proceso //
     *     1. Verifica si la carpeta de datos existe; si no, la crea.
     *     2. Crea un archivo JSON con el nombre "patient_<ID>.json".
     *     3. Serializa el objeto Patient usando Gson y lo escribe en el archivo.
     *     4. Muestra en consola la ruta donde se guardó el archivo.
     *  // Salidas //
     *     Ninguna, pero el paciente queda registrado en un archivo JSON.
     */
    public static void createPatient(Patient patient) {
        try {
            File folder = new File(DATA_FOLDER);
            if (!folder.exists()) {
                folder.mkdirs(); // Crea la carpeta si no existe
            }
            File file = new File(DATA_FOLDER + "patient_" + patient.getPatient_id() + ".json");
            try (FileWriter writer = new FileWriter(file)) {
                gson.toJson(patient, writer);
            }
            System.out.println("Paciente creado y guardado en: " + file.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *  // Objetivo:
     *     Leer la información de un paciente desde un archivo JSON y mostrar sus datos en consola.
     *  // Entradas:
     *     patientId : ID del paciente a buscar.
     *  // Proceso:
     *     1. Construye la ruta del archivo JSON correspondiente al paciente.
     *     2. Verifica si el archivo existe; si no, muestra un mensaje de error y retorna null.
     *     3. Si existe, lee y deserializa el JSON a un objeto Patient usando Gson.
     *     4. Muestra en consola los datos del paciente.
     *  // Salidas:
     *     Retorna el objeto Patient si se encontró el archivo; de lo contrario, retorna null.
     */
    public static Patient readPatient(String patientId) {
        File file = new File(DATA_FOLDER + "patient_" + patientId + ".json");
        if (!file.exists()) {
            System.out.println("No se encontró paciente con ID: " + patientId);
            return null;
        }
        try (FileReader reader = new FileReader(file)) {
            Patient patient = gson.fromJson(reader, Patient.class);
            System.out.println("El paciente es " + patient.getFull_name() +
                    ", tiene " + patient.getAge() + " años, ingresó el " + patient.getRegistration_date() +
                    ", y su archivo FASTA está en: " + patient.getClinical_notes());
            return patient;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /*
     *  // Objetivo //
     *     Actualizar la información de un paciente existente sobrescribiendo su archivo JSON.
     *  // Entradas //
     *     patient : Objeto Patient con los datos actualizados.
     *  // Proceso //
     *     1. Construye la ruta del archivo JSON correspondiente al paciente.
     *     2. Verifica si el archivo existe; si no, muestra un mensaje de error y finaliza.
     *     3. Si existe, sobrescribe el archivo JSON con los nuevos datos usando Gson.
     *     4. Muestra un mensaje confirmando la actualización.
     *  // Salidas //
     *     Ninguna, pero el archivo JSON del paciente se actualiza con la nueva información.
     */
    public static void updatePatient(Patient patient) {
        File file = new File(DATA_FOLDER + "patient_" + patient.getPatient_id() + ".json");
        if (!file.exists()) {
            System.out.println("No se puede actualizar, paciente no encontrado.");
            return;
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(patient, writer);
            System.out.println("Paciente actualizado: " + patient.getPatient_id());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *  // Objetivo //
     *     Realizar un borrado lógico (soft delete) de un paciente marcándolo como inactivo.
     *  // Entradas //
     *     patientId : ID del paciente a eliminar.
     *  // Proceso //
     *     1. Recupera el paciente usando readPatient.
     *     2. Si el paciente existe, cambia su atributo is_active a false.
     *     3. Llama a updatePatient para guardar el cambio en el archivo JSON.
     *     4. Muestra un mensaje confirmando que el paciente fue marcado como inactivo.
     *  // Salidas //
     *     Ninguna, pero el paciente queda registrado como inactivo en el sistema.
     */
    public static void deletePatient(String patientId) {
        Patient patient = readPatient(patientId);
        if (patient == null) return;

        patient.setIs_active(false);
        updatePatient(patient);
        System.out.println("Paciente con el ID " + patientId + " marcado como inactivo.");
    }

    /*
     *  // Objetivo:
     *     Obtener todos los pacientes que se encuentran activos en el sistema.
     */
    public static List<Patient> getAllActivePatients() {
        List<Patient> activePatients = new ArrayList<>();

        File folder = new File(DATA_FOLDER);
        if (!folder.exists() || folder.listFiles() == null) {
            System.out.println("No hay pacientes registrados aún.");
            return activePatients;
        }

        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                try (FileReader reader = new FileReader(file)) {
                    Patient patient = gson.fromJson(reader, Patient.class);
                    if (patient != null && patient.isIs_active()) {
                        activePatients.add(patient);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return activePatients;
    }
}
