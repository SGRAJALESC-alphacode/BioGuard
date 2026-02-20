package org.BioGuard.business.processor;

import org.BioGuard.model.Patient;
import org.BioGuard.business.validation.IValidator;

public class FastaLineProcessor {

    private final IValidator<Patient> validator;

    public FastaLineProcessor(IValidator<Patient> validator) {
        if (validator == null) {
            throw new IllegalArgumentException("El validador no puede ser null");
        }
        this.validator = validator;
    }

    public String procesar(Patient patient) {
        if (patient == null) {
            throw new IllegalArgumentException("El paciente no puede ser null");
        }

        if (!validator.esValido(patient)) {
            throw new IllegalArgumentException(
                    "Paciente inválido: " + validator.getMensajeError(patient)
            );
        }

        return String.format("PACIENTE:%s|%s|%d|%s|%s",
                patient.getId(),
                patient.getNombre(),
                patient.getEdad(),
                patient.getGenero(),
                patient.getTelefono() != null ? patient.getTelefono() : ""
        );
    }
}