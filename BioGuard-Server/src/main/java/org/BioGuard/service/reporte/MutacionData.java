package org.BioGuard.service.reporte;

import org.BioGuard.model.Muestra;
import org.BioGuard.util.SecuenciaComparator;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que procesa los datos de mutación para una muestra.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class MutacionData {

    private final Muestra muestraActual;
    private final List<Muestra> muestrasAnteriores;
    private final SecuenciaComparator comparador;

    public MutacionData(Muestra muestraActual, List<Muestra> muestrasAnteriores) {
        this.muestraActual = muestraActual;
        this.muestrasAnteriores = muestrasAnteriores;
        this.comparador = new SecuenciaComparator();
    }

    /**
     * Genera el reporte de mutaciones comparando con todas las muestras anteriores.
     */
    public String generarReporte() {
        StringBuilder reporte = new StringBuilder();

        reporte.append("REPORTE DE MUTACIONES\n");
        reporte.append("=====================\n");
        reporte.append("Paciente: ").append(muestraActual.getDocumentoPaciente()).append("\n");
        reporte.append("Muestra actual: ").append(muestraActual.getId()).append("\n");
        reporte.append("Fecha: ").append(muestraActual.getFecha()).append("\n\n");

        if (muestrasAnteriores.isEmpty()) {
            reporte.append("No hay muestras anteriores para comparar.\n");
            return reporte.toString();
        }

        for (Muestra anterior : muestrasAnteriores) {
            reporte.append("Comparación con muestra: ").append(anterior.getId()).append("\n");
            reporte.append("Fecha muestra anterior: ").append(anterior.getFecha()).append("\n");

            double similitud = comparador.calcularSimilitud(
                    muestraActual.getSecuencia(),
                    anterior.getSecuencia()
            );

            reporte.append(String.format("Similitud: %.2f%%\n", similitud));

            List<SecuenciaComparator.Diferencia> diferencias = comparador.comparar(
                    muestraActual.getSecuencia(),
                    anterior.getSecuencia()
            );

            if (diferencias.isEmpty()) {
                reporte.append("No se detectaron mutaciones.\n");
            } else {
                reporte.append("Mutaciones detectadas:\n");
                for (SecuenciaComparator.Diferencia diff : diferencias) {
                    reporte.append("  Posición ").append(diff.getPosicionInicio())
                            .append("-").append(diff.getPosicionFin()).append(": ")
                            .append(diff.getSecuenciaOriginal()).append(" → ")
                            .append(diff.getSecuenciaNueva()).append("\n");
                }
            }
            reporte.append("\n");
        }

        return reporte.toString();
    }

    /**
     * Genera un reporte en formato CSV con las mutaciones.
     */
    public String generarCSV() {
        StringBuilder csv = new StringBuilder();
        csv.append("muestra_actual,fecha_actual,muestra_anterior,fecha_anterior,similitud,mutaciones\n");

        for (Muestra anterior : muestrasAnteriores) {
            double similitud = comparador.calcularSimilitud(
                    muestraActual.getSecuencia(),
                    anterior.getSecuencia()
            );

            List<SecuenciaComparator.Diferencia> diferencias = comparador.comparar(
                    muestraActual.getSecuencia(),
                    anterior.getSecuencia()
            );

            String mutacionesStr = diferencias.isEmpty() ? "ninguna" :
                    String.join(";", diferencias.stream()
                            .map(d -> d.getPosicionInicio() + "-" + d.getPosicionFin())
                            .toList());

            csv.append(String.format("%s,%s,%s,%s,%.2f,%s\n",
                    muestraActual.getId(),
                    muestraActual.getFecha(),
                    anterior.getId(),
                    anterior.getFecha(),
                    similitud,
                    mutacionesStr
            ));
        }

        return csv.toString();
    }
}