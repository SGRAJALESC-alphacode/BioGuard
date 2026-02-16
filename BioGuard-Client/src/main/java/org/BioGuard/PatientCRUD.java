package org.BioGuard;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import org.BioGuard.exceptions.PatientNotFoundException;
import org.BioGuard.exceptions.PatientFileException;
import org.BioGuard.exceptions.InvalidPatientDataException;

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
     *     1. Valida que el paciente y su ID no sean nulos.
     *     2. Verifica si la carpeta de datos existe; si no, la crea.
     *     3. Crea un archivo JSON con el nombre "patient_<ID>.json".
     *     4. Serializa el objeto Patient usando Gson y lo escribe en el archivo.
     *     5. Muestra en consola la ruta donde se guardó el archivo.
     *  // Salidas //
     *     Ninguna, pero el paciente queda registrado en un archivo JSON.
     *  // Excepciones //
     *     Lanza InvalidPatientDataException si el paciente es nulo o su ID es inválido.
     *     Lanza PatientFileException si hay error al crear o escribir el archivo.
     */
    public static void createPatient(Patient patient) throws InvalidPatientDataException, PatientFileException {
        // Validación de datos
        if (patient == null) {
            throw new InvalidPatientDataException("patient", "El objeto paciente no puede ser nulo");
        }
        if (patient.getPatient_id() == null || patient.getPatient_id().isEmpty()) {
            throw new InvalidPatientDataException("patient_id", "El ID del paciente no puede estar vacío");
        }

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
            throw new PatientFileException(
                DATA_FOLDER + "patient_" + patient.getPatient_id() + ".json",
                "crear",
                e
            );
        }
    }

    /*
     *  // Objetivo:
     *     Leer la información de un paciente desde un archivo JSON y retornar sus datos.
     *  // Entradas:
     *     patientId : ID del paciente a buscar.
     *  // Proceso:
     *     1. Valida que el ID del paciente no sea nulo ni vacío.
     *     2. Construye la ruta del archivo JSON correspondiente al paciente.
     *     3. Verifica si el archivo existe; si no, lanza PatientNotFoundException.
     *     4. Si existe, lee y deserializa el JSON a un objeto Patient usando Gson.
     *     5. Muestra en consola los datos del paciente.
     *  // Salidas:
     *     Retorna el objeto Patient si se encontró el archivo.
     *  // Excepciones:
     *     Lanza InvalidPatientDataException si el ID es nulo o vacío.
     *     Lanza PatientNotFoundException si el paciente no existe.
     *     Lanza PatientFileException si hay error de I/O al leer.
     */
    public static Patient readPatient(String patientId) throws InvalidPatientDataException, PatientNotFoundException, PatientFileException {
        // Validación
        if (patientId == null || patientId.isEmpty()) {
            throw new InvalidPatientDataException("patient_id", "El ID del paciente no puede estar vacío");
        }

        File file = new File(DATA_FOLDER + "patient_" + patientId + ".json");
        if (!file.exists()) {
            throw new PatientNotFoundException(patientId);
        }
        try (FileReader reader = new FileReader(file)) {
            Patient patient = gson.fromJson(reader, Patient.class);
            System.out.println("El paciente es " + patient.getFull_name() +
                    ", tiene " + patient.getAge() + " años, ingresó el " + patient.getRegistration_date() +
                    ", y su archivo FASTA está en: " + patient.getClinical_notes());
            return patient;
        } catch (IOException e) {
            throw new PatientFileException(file.getAbsolutePath(), "leer", e);
        }
    }

    /*
     *  // Objetivo //
     *     Actualizar la información de un paciente existente sobrescribiendo su archivo JSON.
     *  // Entradas //
     *     patient : Objeto Patient con los datos actualizados.
     *  // Proceso //
     *     1. Valida que el paciente y su ID no sean nulos.
     *     2. Construye la ruta del archivo JSON correspondiente al paciente.
     *     3. Verifica si el archivo existe; si no, lanza PatientNotFoundException.
     *     4. Si existe, sobrescribe el archivo JSON con los nuevos datos usando Gson.
     *     5. Muestra un mensaje confirmando la actualización.
     *  // Salidas //
     *     Ninguna, pero el archivo JSON del paciente se actualiza con la nueva información.
     *  // Excepciones //
     *     Lanza InvalidPatientDataException si el paciente es nulo o su ID es inválido.
     *     Lanza PatientNotFoundException si el paciente no existe.
     *     Lanza PatientFileException si hay error de I/O.
     */
    public static void updatePatient(Patient patient) throws InvalidPatientDataException, PatientNotFoundException, PatientFileException {
        // Validación
        if (patient == null || patient.getPatient_id() == null || patient.getPatient_id().isEmpty()) {
            throw new InvalidPatientDataException("patient", "Datos inválidos para actualizar paciente");
        }

        File file = new File(DATA_FOLDER + "patient_" + patient.getPatient_id() + ".json");
        if (!file.exists()) {
            throw new PatientNotFoundException(patient.getPatient_id(), "No se puede actualizar un paciente inexistente");
        }
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(patient, writer);
            System.out.println("Paciente actualizado: " + patient.getPatient_id());
        } catch (IOException e) {
            throw new PatientFileException(file.getAbsolutePath(), "actualizar", e);
        }
    }

    /*
     *  // Objetivo //
     *     Realizar un borrado lógico (soft delete) de un paciente marcándolo como inactivo.
     *  // Entradas //
     *     patientId : ID del paciente a eliminar.
     *  // Proceso //
     *     1. Valida que el ID no sea nulo ni vacío.
     *     2. Recupera el paciente usando readPatient.
     *     3. Si el paciente existe, cambia su atributo is_active a false.
     *     4. Llama a updatePatient para guardar el cambio en el archivo JSON.
     *     5. Muestra un mensaje confirmando que el paciente fue marcado como inactivo.
     *  // Salidas //
     *     Ninguna, pero el paciente queda registrado como inactivo en el sistema.
     *  // Excepciones //
     *     Lanza InvalidPatientDataException si el ID es inválido.
     *     Lanza PatientNotFoundException si el paciente no existe.
     *     Lanza PatientFileException si hay error de I/O.
     */
    public static void deletePatient(String patientId) throws InvalidPatientDataException, PatientNotFoundException, PatientFileException {
        // Validación
        if (patientId == null || patientId.isEmpty()) {
            throw new InvalidPatientDataException("patient_id", "El ID del paciente no puede estar vacío");
        }

        Patient patient = readPatient(patientId);

        patient.setIs_active(false);
        updatePatient(patient);
        System.out.println("Paciente con el ID " + patientId + " marcado como inactivo.");
    }

    /*
     *  // Objetivo //
     *     Obtener todos los pacientes que se encuentran activos en el sistema.
     *  // Entradas //
     *     Ninguna, utiliza la carpeta de datos definida en DATA_FOLDER.
     *  // Proceso //
     *     1. Verifica si la carpeta de datos existe y contiene archivos JSON.
     *     2. Itera sobre todos los archivos .json en la carpeta.
     *     3. Deserializa cada archivo JSON a un objeto Patient.
     *     4. Si el paciente está activo (is_active == true), lo agrega a la lista.
     *     5. Maneja excepciones de lectura de archivo lanzando PatientFileException.
     *  // Salidas //
     *     Retorna una List<Patient> con todos los pacientes activos del sistema.
     *  // Excepciones //
     *     Lanza PatientFileException si hay error al leer algún archivo.
     */
    public static List<Patient> getAllActivePatients() throws PatientFileException {
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
                    throw new PatientFileException(file.getAbsolutePath(), "leer", e);
                }
            }
        }

        return activePatients;
    }
}
