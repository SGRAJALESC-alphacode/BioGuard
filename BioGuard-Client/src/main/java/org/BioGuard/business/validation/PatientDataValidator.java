package org.BioGuard.business.validation;

import org.BioGuard.model.Patient;

/**
 * Validador de datos de pacientes.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class PatientDataValidator implements IValidator<Patient> {

    @Override
    public boolean esValido(Patient patient) {
        if (patient == null) return false;
        if (patient.getNombre() == null || patient.getNombre().trim().isEmpty()) return false;
        if (patient.getEdad() < 0 || patient.getEdad() > 150) return false;
        return true;
    }

    @Override
    public String getMensajeError(Patient patient) {
        if (patient == null) return "Paciente es null";
        if (patient.getNombre() == null || patient.getNombre().trim().isEmpty())
            return "Nombre obligatorio";
        if (patient.getEdad() < 0 || patient.getEdad() > 150)
            return "Edad inválida: " + patient.getEdad();
        return "Válido";
    }
}