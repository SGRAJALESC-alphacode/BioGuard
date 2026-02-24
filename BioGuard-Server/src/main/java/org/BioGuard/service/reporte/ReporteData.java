package org.BioGuard.service.reporte;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que encapsula los datos de un paciente para el reporte de alto riesgo.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class ReporteData {

    private String documento;
    private int totalVirusDetectados;
    private int virusAltamenteInfecciosos;
    private List<String> virusNormales;
    private List<String> virusAltamenteInfecciososList;

    public ReporteData(String documento) {
        this.documento = documento;
        this.totalVirusDetectados = 0;
        this.virusAltamenteInfecciosos = 0;
        this.virusNormales = new ArrayList<>();
        this.virusAltamenteInfecciososList = new ArrayList<>();
    }

    public void agregarVirus(String nombreVirus, int nivel) {
        totalVirusDetectados++;
        if (nivel == 3) { // Altamente infeccioso
            virusAltamenteInfecciosos++;
            virusAltamenteInfecciososList.add(nombreVirus);
        } else {
            virusNormales.add(nombreVirus);
        }
    }

    public boolean esAltoRiesgo() {
        return virusAltamenteInfecciosos > 3;
    }

    // Getters
    public String getDocumento() { return documento; }
    public int getTotalVirusDetectados() { return totalVirusDetectados; }
    public int getVirusAltamenteInfecciosos() { return virusAltamenteInfecciosos; }
    public List<String> getVirusNormales() { return virusNormales; }
    public List<String> getVirusAltamenteInfecciososList() { return virusAltamenteInfecciososList; }

    @Override
    public String toString() {
        return String.format("%s,%d,%d,\"%s\",\"%s\"",
                documento,
                totalVirusDetectados,
                virusAltamenteInfecciosos,
                String.join(";", virusNormales),
                String.join(";", virusAltamenteInfecciososList)
        );
    }
}