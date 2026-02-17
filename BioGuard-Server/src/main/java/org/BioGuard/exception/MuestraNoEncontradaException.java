package org.BioGuard.exception;

/**
 * Excepción lanzada cuando no se encuentra una muestra de ADN solicitada.
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class MuestraNoEncontradaException extends Exception {

    private static final long serialVersionUID = 1L;
    private final String documentoPaciente;
    private final String fechaMuestra;
    private final String rutaBuscada;

    /**
     * Constructor para paciente sin muestras.
     *
     * @param documento El documento del paciente
     */
    public MuestraNoEncontradaException(String documento) {
        super(String.format("No se encontraron muestras para el paciente con documento: %s", documento));
        this.documentoPaciente = documento;
        this.fechaMuestra = null;
        this.rutaBuscada = null;
    }

    /**
     * Constructor para muestra específica no encontrada.
     *
     * @param documento Documento del paciente
     * @param fecha Fecha de la muestra buscada
     */
    public MuestraNoEncontradaException(String documento, String fecha) {
        super(String.format("No se encontró la muestra del paciente %s con fecha: %s", documento, fecha));
        this.documentoPaciente = documento;
        this.fechaMuestra = fecha;
        this.rutaBuscada = null;
    }

    /**
     * Constructor para ruta específica no encontrada.
     *
     * @param ruta La ruta del archivo que no existe
     */
    public MuestraNoEncontradaException(String ruta, boolean esRuta) {
        super(String.format("No se encontró el archivo de muestra en la ruta: %s", ruta));
        this.documentoPaciente = null;
        this.fechaMuestra = null;
        this.rutaBuscada = ruta;
    }

    /**
     * Constructor con causa raíz.
     *
     * @param documento Documento del paciente
     * @param causa Causa original del error
     */
    public MuestraNoEncontradaException(String documento, Throwable causa) {
        super(String.format("Error al acceder a las muestras del paciente: %s", documento), causa);
        this.documentoPaciente = documento;
        this.fechaMuestra = null;
        this.rutaBuscada = null;
    }

    public String getDocumentoPaciente() {
        return documentoPaciente;
    }

    public String getFechaMuestra() {
        return fechaMuestra;
    }

    public String getRutaBuscada() {
        return rutaBuscada;
    }
}