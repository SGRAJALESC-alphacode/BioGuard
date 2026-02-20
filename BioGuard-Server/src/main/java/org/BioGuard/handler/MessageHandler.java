package org.BioGuard.handler;

import org.BioGuard.service.*;
import org.BioGuard.service.diagnostico.IDiagnosticoService;

public class MessageHandler implements IMessageProcessor {

    private final CommandParser parser;

    public MessageHandler(IPacienteService ps, IDiagnosticoService ds, IVirusService vs) {
        ResponseFormatter fmt = new ResponseFormatter();

        PacienteCommandHandler ph = new PacienteCommandHandler(ps, fmt);
        VirusCommandHandler vh = new VirusCommandHandler(vs, fmt);
        DiagnosticoCommandHandler dh = new DiagnosticoCommandHandler(ds, ps, fmt);

        this.parser = new CommandParser();

        // Registrar comandos
        parser.register("PACIENTE:", ph::handleRegistroSimple);
        parser.register("REGISTRAR_PACIENTE:", ph::handleRegistroCompleto);
        parser.register("CONSULTAR_PACIENTE:", ph::handleConsulta);
        parser.register("LISTAR_PACIENTES", ph::handleListar);

        parser.register("VIRUS:", vh::handleRegistroSimple);
        parser.register("REGISTRAR_VIRUS:", vh::handleRegistroCompleto);
        parser.register("CONSULTAR_VIRUS:", vh::handleConsulta);
        parser.register("LISTAR_VIRUS", vh::handleListar);

        parser.register("ENVIAR_MUESTRA:", dh::handleEnviarMuestra);
        parser.register("CONSULTAR_DIAGNOSTICOS:", dh::handleConsultarDiagnosticos);
        parser.register("VER_DIAGNOSTICO:", dh::handleVerDiagnostico);
    }

    @Override
    public String process(String message) {
        return parser.parse(message);
    }
}