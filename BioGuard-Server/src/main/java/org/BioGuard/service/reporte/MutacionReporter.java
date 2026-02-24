package org.BioGuard.service.reporte;

import org.BioGuard.model.Muestra;
import org.BioGuard.service.diagnostico.IDiagnosticoService;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generador de reportes de mutaciones para pacientes.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MutacionReporter {

    private final IDiagnosticoService diagnosticoService;
    private static final String REPORTES_DIR = "C:/Users/jhona/OneDrive/Escritorio/Backend-2026/BioGuard/data/reportes/";

    public MutacionReporter(IDiagnosticoService diagnosticoService) {
        this.diagnosticoService = diagnosticoService;
        crearDirectorioReportes();
    }

    private void crearDirectorioReportes() {
        try {
            Files.createDirectories(Paths.get(REPORTES_DIR));
        } catch (IOException e) {
            System.err.println("Error creando directorio de reportes: " + e.getMessage());
        }
    }

    /**
     * Genera reporte de mutaciones para un paciente específico.
     */
    public String generarReporte(String documento, String idMuestraActual) throws IOException {
        // Obtener todas las muestras del paciente usando el servicio
        List<Muestra> muestras = diagnosticoService.obtenerMuestrasDePaciente(documento);

        if (muestras.isEmpty()) {
            throw new IOException("No hay muestras para el paciente " + documento);
        }

        // Ordenar por fecha descendente (más reciente primero)
        muestras.sort((a, b) -> b.getFecha().compareTo(a.getFecha()));

        // Seleccionar muestra actual
        Muestra muestraActual;
        List<Muestra> muestrasAnteriores;

        if (idMuestraActual != null && !idMuestraActual.isEmpty()) {
            Optional<Muestra> opt = diagnosticoService.obtenerMuestraPorId(idMuestraActual);
            if (!opt.isPresent()) {
                throw new IOException("Muestra no encontrada: " + idMuestraActual);
            }
            muestraActual = opt.get();

            muestrasAnteriores = muestras.stream()
                    .filter(m -> !m.getId().equals(idMuestraActual))
                    .collect(Collectors.toList());
        } else {
            muestraActual = muestras.get(0);
            muestrasAnteriores = muestras.size() > 1 ? muestras.subList(1, muestras.size()) : new ArrayList<>();
        }

        // Generar reporte
        MutacionData data = new MutacionData(muestraActual, muestrasAnteriores);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nombreArchivo = "mutaciones_" + documento + "_" + timestamp + ".txt";
        Path rutaArchivo = Paths.get(REPORTES_DIR, nombreArchivo);

        Files.writeString(rutaArchivo, data.generarReporte());

        return rutaArchivo.toString();
    }

    /**
     * Genera reporte como String para enviar al cliente.
     */
    public String generarReporteComoString(String documento, String idMuestra) {
        try {
            String ruta = generarReporte(documento, idMuestra);
            return "REPORTE_MUTACIONES: Archivo generado en " + ruta;
        } catch (IOException e) {
            return "ERROR: " + e.getMessage();
        }
    }
}