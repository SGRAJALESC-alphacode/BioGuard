package org.BioGuard.handler;

import org.BioGuard.service.IPacienteService;
import org.BioGuard.service.IVirusService;
import org.BioGuard.service.diagnostico.IDiagnosticoService;
import org.BioGuard.service.diagnostico.DiagnosticoService;
import org.BioGuard.service.reporte.AltoRiesgoReporter;

/**
 * Procesador de mensajes del servidor que delega en handlers especializados.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MessageHandler implements IMessageProcessor {

    private final CommandParser parser;
    private final IPacienteService pacienteService;
    private final IDiagnosticoService diagnosticoService;
    private final IVirusService virusService;

    public MessageHandler(IPacienteService pacienteService,
                          IDiagnosticoService diagnosticoService,
                          IVirusService virusService) {
        this.pacienteService = pacienteService;
        this.diagnosticoService = diagnosticoService;
        this.virusService = virusService;
        this.parser = new CommandParser();

        inicializarComandos();
    }

    private void inicializarComandos() {
        PacienteCommandHandler pacienteHandler = new PacienteCommandHandler(pacienteService);
        VirusCommandHandler virusHandler = new VirusCommandHandler(virusService);
        DiagnosticoCommandHandler diagnosticoHandler = new DiagnosticoCommandHandler(diagnosticoService, pacienteService);

        // Pacientes - Nombres CORREGIDOS
        parser.registrarComando("PACIENTE:", pacienteHandler::handleRegistroSimple);
        parser.registrarComando("REGISTRAR_PACIENTE:", pacienteHandler::handleRegistroCompleto);  // ← CORREGIDO
        parser.registrarComando("CONSULTAR_PACIENTE:", pacienteHandler::handleConsulta);
        parser.registrarComando("LISTAR_PACIENTES", pacienteHandler::handleListar);

        // Virus - Nombres CORREGIDOS
        parser.registrarComando("VIRUS:", virusHandler::handleRegistroSimple);
        parser.registrarComando("REGISTRAR_VIRUS:", virusHandler::handleRegistroCompleto);        // ← CORREGIDO
        parser.registrarComando("CONSULTAR_VIRUS:", virusHandler::handleConsulta);
        parser.registrarComando("LISTAR_VIRUS", virusHandler::handleListar);

        // Diagnósticos - Nombres CORREGIDOS
        parser.registrarComando("ENVIAR_MUESTRA:", diagnosticoHandler::handleEnviarMuestra);
        parser.registrarComando("CONSULTAR_DIAGNOSTICOS:", diagnosticoHandler::handleConsultarDiagnosticos); // ← CORREGIDO
        parser.registrarComando("VER_DIAGNOSTICO:", diagnosticoHandler::handleVerDiagnostico);

        // Reportes
        parser.registrarComando("REPORTE_ALTO_RIESGO", this::handleReporteAltoRiesgo);

        //Reporte de mutacion
        parser.registrarComando("REPORTE_MUTACIONES:", diagnosticoHandler::handleReporteMutaciones);
    }

    private String handleReporteAltoRiesgo(String parametros) {
        try {
            if (!(diagnosticoService instanceof DiagnosticoService)) {
                return "ERROR: Servicio de diagnóstico no compatible";
            }

            AltoRiesgoReporter reporter = new AltoRiesgoReporter(
                    (DiagnosticoService) diagnosticoService,
                    virusService
            );

            return reporter.generarReporteComoString();

        } catch (Exception e) {
            return "ERROR generando reporte: " + e.getMessage();
        }
    }

    @Override
    public String process(String message) {
        return parser.ejecutarComando(message);
    }
}