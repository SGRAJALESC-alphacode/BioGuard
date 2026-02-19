package org.BioGuard.handler;

import org.BioGuard.service.IPacienteService;
import org.BioGuard.service.IDiagnosticoService;
import org.BioGuard.service.IVirusService;

/**
 * Implementación del procesador de mensajes del servidor.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MessageHandler implements IMessageProcessor {

    private final IPacienteService pacienteService;
    private final IDiagnosticoService diagnosticoService;
    private final IVirusService virusService;

    /**
     * Constructor que recibe todos los servicios necesarios.
     *
     * @param pacienteService Servicio de pacientes
     * @param diagnosticoService Servicio de diagnósticos
     * @param virusService Servicio de virus
     */
    public MessageHandler(IPacienteService pacienteService,
                          IDiagnosticoService diagnosticoService,
                          IVirusService virusService) {
        this.pacienteService = pacienteService;
        this.diagnosticoService = diagnosticoService;
        this.virusService = virusService;
    }

    @Override
    public String process(String message) {
        // TODO: Implementar lógica de procesamiento
        return "Mensaje recibido: " + message;
    }
}