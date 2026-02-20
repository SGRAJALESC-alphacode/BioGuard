package org.BioGuard.business.validation;

import org.BioGuard.model.Patient;

public class PatientDataValidator implements IValidator<Patient> {

    @Override
    public boolean esValido(Patient patient) {
        if (patient == null) return false;
        if (isBlank(patient.getId())) return false;
        if (isBlank(patient.getNombre())) return false;
        if (patient.getEdad() < 0 || patient.getEdad() > 150) return false;
        if (isBlank(patient.getGenero())) return false;
        return true;
    }

    @Override
    public String getMensajeError(Patient patient) {
        if (patient == null) return "El paciente es null";
        if (isBlank(patient.getId())) return "El ID del paciente es obligatorio";
        if (isBlank(patient.getNombre())) return "El nombre del paciente es obligatorio";
        if (patient.getEdad() < 0 || patient.getEdad() > 150)
            return "La edad debe estar entre 0 y 150 años";
        if (isBlank(patient.getGenero())) return "El género es obligatorio";
        return "Válido";
    }

    private boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }
}