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
 * <p>Responsabilidad Única: Coordinar las operaciones de diagnóstico
 * delegando en los repositorios y procesadores especializados.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class DiagnosticoService implements IDiagnosticoService {

    private final DiagnosticoRepository diagnosticoRepository;
    private final MuestraRepository muestraRepository;
    private final MuestraProcessor muestraProcessor;
    private final DiagnosticoCSVGenerator csvGenerator;
    private final IVirusService virusService;

    public DiagnosticoService(IVirusService virusService) {
        this.virusService = virusService;
        this.diagnosticoRepository = new DiagnosticoRepository();
        this.muestraRepository = new MuestraRepository();
        this.muestraProcessor = new MuestraProcessor(virusService);
        this.csvGenerator = new DiagnosticoCSVGenerator();

        // Cargar diagnósticos existentes
        diagnosticoRepository.cargarDiagnosticosDesdeArchivos();
    }

    @Override
    public Diagnostico procesarMuestra(String documento, String secuencia)
            throws DiagnosticoException {

        // 1. Validar
        muestraProcessor.validarSecuencia(secuencia);

        // 2. Crear y guardar muestra
        Muestra muestra = new Muestra(documento, secuencia);
        try {
            muestraRepository.guardar(muestra);
        } catch (IOException e) {
            throw new DiagnosticoException("Error guardando muestra: " + e.getMessage());
        }

        // 3. Detectar virus
        List<Diagnostico.HallazgoVirus> hallazgos =
                muestraProcessor.detectarVirus(secuencia);

        // 4. Crear diagnóstico
        String fechaStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String id = documento + "_" + fechaStr;

        Diagnostico diagnostico = new Diagnostico(documento, id);
        diagnostico.setFecha(LocalDateTime.now());
        hallazgos.forEach(diagnostico::agregarHallazgo);

        // 5. Guardar diagnóstico
        diagnosticoRepository.guardar(diagnostico);

        // 6. Generar CSV (opcional, no detiene el flujo)
        try {
            csvGenerator.generarCSV(diagnostico);
        } catch (IOException e) {
            System.err.println("Error generando CSV: " + e.getMessage());
        }

        return diagnostico;
    }

    @Override
    public Optional<Diagnostico> buscarPorId(String id) {
        return diagnosticoRepository.buscarPorId(id);
    }

    @Override
    public List<Diagnostico> buscarPorPaciente(String documento) {
        return diagnosticoRepository.buscarPorPaciente(documento);
    }

    @Override
    public List<Diagnostico> listarTodos() {
        return diagnosticoRepository.listarTodos();
    }

    @Override
    public Muestra guardarMuestra(Muestra muestra) throws IOException {
        return muestraRepository.guardar(muestra);
    }

    @Override
    public String generarCSV(Diagnostico diagnostico) throws IOException {
        return csvGenerator.generarCSV(diagnostico);
    }

    @Override
    public List<Muestra> obtenerMuestrasDePaciente(String documento) {
        return muestraRepository.buscarPorPaciente(documento);
    }

    @Override
    public Optional<Muestra> obtenerMuestraPorId(String id) {
        return muestraRepository.buscarPorId(id);
    }
}