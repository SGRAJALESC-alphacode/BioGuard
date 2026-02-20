package org.BioGuard.service.diagnostico;

import org.BioGuard.model.*;
import org.BioGuard.service.IVirusService;
import org.BioGuard.exception.DiagnosticoException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

/**
 * Servicio de diagnósticos - Orquestador.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class DiagnosticoService implements IDiagnosticoService {

    private final DiagnosticoRepository repository;
    private final MuestraProcessor processor;
    private final DiagnosticoCSVGenerator csvGenerator;

    public DiagnosticoService(IVirusService virusService) {
        this.repository = new DiagnosticoRepository();
        this.processor = new MuestraProcessor(virusService);
        this.csvGenerator = new DiagnosticoCSVGenerator();
        // Cargar diagnósticos existentes al iniciar
        this.repository.cargarDiagnosticosDesdeArchivos();
    }

    @Override
    public Diagnostico procesarMuestra(String documento, String secuencia)
            throws DiagnosticoException {

        // Validar secuencia
        processor.validarSecuencia(secuencia);

        // Crear y guardar muestra
        Muestra muestra = new Muestra(documento, secuencia);
        try {
            repository.guardarMuestra(muestra);
        } catch (IOException e) {
            throw new DiagnosticoException("Error guardando muestra: " + e.getMessage());
        }

        // Detectar virus
        List<Diagnostico.HallazgoVirus> hallazgos = processor.detectarVirus(secuencia);

        // Crear diagnóstico
        String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String id = documento + "_" + fechaStr;

        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setId(id);
        diagnostico.setDocumentoPaciente(documento);
        diagnostico.setFecha(LocalDateTime.now());

        for (Diagnostico.HallazgoVirus h : hallazgos) {
            diagnostico.agregarHallazgo(h);
        }

        // Guardar en memoria
        repository.guardarDiagnostico(diagnostico);

        // Generar CSV
        try {
            String csvPath = csvGenerator.generarCSV(diagnostico);
            System.out.println("[Service] CSV generado: " + csvPath);
        } catch (IOException e) {
            System.err.println("Error generando CSV: " + e.getMessage());
        }

        return diagnostico;
    }

    @Override
    public Optional<Diagnostico> buscarPorId(String id) {
        return repository.buscarPorId(id);
    }

    @Override
    public List<Diagnostico> buscarPorPaciente(String documento) {
        return repository.buscarPorPaciente(documento);
    }

    @Override
    public Muestra guardarMuestra(Muestra muestra) throws IOException {
        return repository.guardarMuestra(muestra);
    }

    @Override
    public String generarCSV(Diagnostico diagnostico) throws IOException {
        return csvGenerator.generarCSV(diagnostico);
    }
}