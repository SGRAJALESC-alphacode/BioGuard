package org.BioGuard.business.validation;

public interface IValidator<T> {
    boolean esValido(T objeto);
    String getMensajeError(T objeto);
}