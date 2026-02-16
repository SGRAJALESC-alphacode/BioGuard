package org.BioGuard;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/*
 *  // Objetivo //
 *     Representar un paciente en el sistema con toda su información personal y clínica.
 *  // Atributos //
 *     patient_id       : Identificador único del paciente.
 *     full_name        : Nombre completo del paciente.
 *     document_id      : Documento de identidad del paciente.
 *     contact_email    : Correo electrónico de contacto.
 *     registration_date: Fecha de registro del paciente en el sistema.
 *     age              : Edad del paciente.
 *     sex              : Sexo del paciente (M/F).
 *     clinical_notes   : Ruta del archivo FASTA asociado al paciente.
 *     checksum_fasta   : Checksum del archivo FASTA para validación.
 *     file_size_bytes  : Tamaño del archivo FASTA en bytes.
 *     is_active        : Indica si el paciente está activo (soft delete).
 */
public class Patient {
    private String patient_id;
    private String full_name;
    private String document_id;
    private String contact_email;
    private Date registration_date;
    private Integer age;
    private String sex;
    private String clinical_notes;
    private String checksum_fasta;
    private long file_size_bytes;
    private boolean is_active = true;

    /*
     *     Objetivo:
     *     Inicializar un nuevo objeto Patient con toda la información personal y clínica.
     *
     *     Proceso:
     *     Asigna los valores recibidos a los atributos correspondientes del objeto Patient.
     *
     *     Salidas:
     *     Nuevo objeto Patient inicializado con los datos proporcionados.
     */

    // Constructor
    public Patient(String patient_id, String full_name, String document_id, String contact_email,
                   Date registration_date, Integer age, String sex, String clinical_notes,
                   String checksum_fasta, long file_size_bytes) {
        this.patient_id = patient_id;
        this.full_name = full_name;
        this.document_id = document_id;
        this.contact_email = contact_email;
        this.registration_date = registration_date;
        this.age = age;
        this.sex = sex;
        this.clinical_notes = clinical_notes;
        this.checksum_fasta = checksum_fasta;
        this.file_size_bytes = file_size_bytes;
    }

    /*
     *  // Objetivo //
     *     Proveer métodos de acceso (getters) y modificación (setters) para los atributos del paciente.
     *  // Atributos Accedidos //
     *     patient_id       : Identificador único del paciente.
     *     full_name        : Nombre completo del paciente.
     *     document_id      : Documento de identidad del paciente.
     *     contact_email    : Correo electrónico de contacto.
     *     registration_date: Fecha de registro del paciente en el sistema.
     *     age              : Edad del paciente.
     *     sex              : Sexo del paciente (M/F).
     *     clinical_notes   : Ruta del archivo FASTA asociado al paciente.
     *     checksum_fasta   : Checksum del archivo FASTA para validación.
     *     file_size_bytes  : Tamaño del archivo FASTA en bytes.
     *     is_active        : Indica si el paciente está activo (soft delete).
     */
    public String getPatient_id() {
        return patient_id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDocument_id() {
        return document_id;
    }

    public void setDocument_id(String document_id) {
        this.document_id = document_id;
    }

    public String getContact_email() {
        return contact_email;
    }

    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }

    public Date getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(Date registration_date) {
        this.registration_date = registration_date;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getClinical_notes() {
        return clinical_notes;
    }

    public void setClinical_notes(String clinical_notes) {
        this.clinical_notes = clinical_notes;
    }

    public String getChecksum_fasta() {
        return checksum_fasta;
    }

    public void setChecksum_fasta(String checksum_fasta) {
        this.checksum_fasta = checksum_fasta;
    }

    public long getFile_size_bytes() {
        return file_size_bytes;
    }

    public void setFile_size_bytes(long file_size_bytes) {
        this.file_size_bytes = file_size_bytes;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    /*
     *  // Objetivo //
     *     Convertir los atributos del paciente en un mapa clave-valor para su manipulación o serialización.
     *  // Entradas //
     *     Ninguna, utiliza los valores actuales de los atributos del paciente.
     *  // Proceso //
     *     Crea un HashMap y agrega cada atributo del paciente con su correspondiente clave.
     *  // Salidas //
     *     Retorna un Map<String, Object> con todos los atributos del paciente.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> data = new HashMap<>();
        data.put("patient_id", patient_id);
        data.put("full_name", full_name);
        data.put("document_id", document_id);
        data.put("contact_email", contact_email);
        data.put("registration_date", registration_date);
        data.put("age", age);
        data.put("sex", sex);
        data.put("clinical_notes", clinical_notes);
        data.put("checksum_fasta", checksum_fasta);
        data.put("file_size_bytes", file_size_bytes);
        data.put("is_active", is_active);
        return data;
    }
}
