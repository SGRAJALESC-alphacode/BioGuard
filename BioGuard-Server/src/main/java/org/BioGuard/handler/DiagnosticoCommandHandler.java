package org.BioGuard.handler;

import org.BioGuard.model.Diagnostico;
import org.BioGuard.service.IPacienteService;
import org.BioGuard.service.diagnostico.IDiagnosticoService;
import org.BioGuard.service.reporte.MutacionReporter;
import org.BioGuard.exception.DiagnosticoException;

import java.util.List;
import java.util.Optional;

/**
 * Manejador de comandos relacionados con diagnósticos.
 *
 * <p>Responsabilidad Única: Procesar los comandos de diagnóstico
 * recibidos del cliente y delegar en los servicios correspondientes.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class DiagnosticoCommandHandler {

    private final IDiagnosticoService diagnosticoService;
    private final IPacienteService pacienteService;

    /**
     * Constructor del manejador de diagnósticos.
     *
     * @param diagnosticoService Servicio de diagnósticos
     * @param pacienteService Servicio de pacientes (para validaciones)
     */
    public DiagnosticoCommandHandler(IDiagnosticoService diagnosticoService, IPacienteService pacienteService) {
        this.diagnosticoService = diagnosticoService;
        this.pacienteService = pacienteService;
    }

    /**
     * Procesa el envío de una muestra de ADN.
     *
     * <p>Formato esperado: ENVIAR_MUESTRA:documento|secuencia</p>
     *
     * @param datos Datos del comando (documento|secuencia)
     * @return Resultado del procesamiento
     */
    public String handleEnviarMuestra(String datos) {
        try {
            String[] partes = datos.split("\\|", 2);
            if (partes.length < 2) {
                return "ERROR: Formato inválido. Se esperaba: documento|secuencia";
            }

            String documento = partes[0].trim();
            String secuencia = partes[1].trim();

            // Validar que el paciente existe
            if (!pacienteService.buscarPorDocumento(documento).isPresent()) {
                return "ERROR: Paciente no encontrado: " + documento;
            }

            // Validar secuencia (solo ATCG)
            if (!secuencia.matches("^[ATCG]+$")) {
                return "ERROR: La secuencia solo puede contener A, T, C, G";
            }

            // Validar longitud
            if (secuencia.length() > 10000) {
                return "ERROR: Secuencia demasiado larga (máx 10000 caracteres)";
            }

            Diagnostico diagnostico = diagnosticoService.procesarMuestra(documento, secuencia);

            return "DIAGNOSTICO_COMPLETADO:" + diagnostico.getId() +
                    "|Virus detectados: " + diagnostico.getVirusDetectados().size();

        } catch (DiagnosticoException e) {
            return "ERROR: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Procesa la consulta de diagnósticos de un paciente.
     *
     * <p>Formato esperado: CONSULTAR_DIAGNOSTICOS:documento</p>
     *
     * @param documento Documento del paciente
     * @return Lista de diagnósticos del paciente
     */
    public String handleConsultarDiagnosticos(String documento) {
        try {
            String doc = documento.trim();

            List<Diagnostico> diagnosticos = diagnosticoService.buscarPorPaciente(doc);

            if (diagnosticos.isEmpty()) {
                return "No hay diagnósticos para el paciente " + doc;
            }

            StringBuilder sb = new StringBuilder("DIAGNOSTICOS:");
            for (Diagnostico d : diagnosticos) {
                sb.append("\n").append(d.getId()).append(",")
                        .append(d.getFecha()).append(",")
                        .append(d.getVirusDetectados().size()).append(" virus");
            }
            return sb.toString();

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Procesa la consulta detallada de un diagnóstico específico.
     *
     * <p>Formato esperado: VER_DIAGNOSTICO:id_diagnostico</p>
     *
     * @param id ID del diagnóstico
     * @return Detalle completo del diagnóstico
     */
    public String handleVerDiagnostico(String id) {
        try {
            String idDiagnostico = id.trim();

            Optional<Diagnostico> opt = diagnosticoService.buscarPorId(idDiagnostico);
            if (!opt.isPresent()) {
                return "ERROR: Diagnóstico no encontrado: " + idDiagnostico;
            }

            Diagnostico d = opt.get();
            StringBuilder sb = new StringBuilder();
            sb.append("DIAGNOSTICO:").append(d.getId());
            sb.append("\nPaciente: ").append(d.getDocumentoPaciente());
            sb.append("\nFecha: ").append(d.getFecha());
            sb.append("\nVirus detectados: ").append(d.getVirusDetectados().size());

            if (!d.getVirusDetectados().isEmpty()) {
                sb.append("\n\nHallazgos:");
                for (Diagnostico.HallazgoVirus h : d.getVirusDetectados()) {
                    sb.append("\n  ").append(h.getNombreVirus())
                            .append(": posición ").append(h.getPosicionInicio())
                            .append("-").append(h.getPosicionFin());
                }
            }

            return sb.toString();

        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    /**
     * Procesa la generación de reporte de mutaciones.
     *
     * <p>Formato esperado: REPORTE_MUTACIONES:documento|idMuestra</p>
     * <p>El idMuestra es opcional. Si no se proporciona, se usa la muestra más reciente.</p>
     *
     * @param parametros Parámetros del comando (documento|idMuestra)
     * @return Resultado de la generación del reporte
     */
    public String handleReporteMutaciones(String parametros) {
        try {
            // Formato: documento|idMuestra (idMuestra opcional)
            String[] partes = parametros.split("\\|", 2);
            String documento = partes[0].trim();
            String idMuestra = partes.length > 1 ? partes[1].trim() : null;

            // Validar que el paciente existe
            if (!pacienteService.buscarPorDocumento(documento).isPresent()) {
                return "ERROR: Paciente no encontrado: " + documento;
            }

            MutacionReporter reporter = new MutacionReporter(diagnosticoService);
            return reporter.generarReporteComoString(documento, idMuestra);

        } catch (Exception e) {
            return "ERROR generando reporte de mutaciones: " + e.getMessage();
        }
    }
}