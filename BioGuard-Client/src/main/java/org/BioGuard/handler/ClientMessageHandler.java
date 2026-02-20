package org.BioGuard.handler;

import org.BioGuard.model.Patient;
import org.BioGuard.business.processor.FastaLineProcessor;

public class ClientMessageHandler {

    private final FastaLineProcessor processor;

    public ClientMessageHandler(FastaLineProcessor processor) {
        if (processor == null) {
            throw new IllegalArgumentException("El processor no puede ser null");
        }
        this.processor = processor;
    }

    public String prepararMensaje(Patient patient) {
        return processor.procesar(patient);
    }

    public void procesarRespuesta(String respuesta) {
        if (respuesta == null || respuesta.trim().isEmpty()) {
            System.out.println("Respuesta vacía del servidor");
            return;
        }
        System.out.println(">" + respuesta);
    }
}