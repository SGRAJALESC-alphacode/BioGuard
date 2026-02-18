package org.BioGuard;

/**
 * Representa un virus en el sistema BioGuard.
 * Esta clase encapsula toda la información relevante de un virus
 * para su uso en diagnósticos y reportes.
 *
 * // Objetivo
 *    Modelar un virus con su nombre, nivel de infecciosidad y
 *    secuencia genética, proporcionando métodos para acceder y
 *    manipular esta información.
 *
 * // Atributos
 *    nombre                : Identificador único del virus (String)
 *    nivelInfecciosidad    : Nivel de peligrosidad del virus (String)
 *                            Valores permitidos: "Poco Infeccioso", "Normal", "Altamente Infeccioso"
 *    secuencia             : Cadena de ADN del virus (solo caracteres A,T,C,G)
 *
 * // Niveles de infecciosidad
 *    - Poco Infeccioso     : Virus de bajo riesgo
 *    - Normal              : Virus de riesgo moderado
 *    - Altamente Infeccioso : Virus de alto riesgo (requiere monitoreo especial)
 *
 * // Validaciones (externas)
 *    - La secuencia debe contener solo los caracteres A, T, C, G
 *    - El nivel debe ser uno de los tres valores permitidos
 *    - El nombre no puede estar vacío
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class Virus {

    private String nombre;
    private String nivelInfecciosidad; // "Poco Infeccioso", "Normal", "Altamente Infeccioso"
    private String secuencia;

    /**
     * Constructor vacío requerido para serialización/deserialización.
     *
     * // Objetivo
     *    Permitir la creación de instancias sin parámetros iniciales,
     *    útil para frameworks de serialización o para construir objetos
     *    paso a paso mediante setters.
     */
    public Virus() {}

    /**
     * Constructor completo para crear un virus con todos sus datos.
     *
     * // Objetivo
     *    Inicializar un virus con todos sus atributos en una sola operación,
     *    garantizando que el objeto quede completamente configurado.
     *
     * @param nombre Nombre del virus
     * @param nivelInfecciosidad Nivel de infecciosidad (valores permitidos)
     * @param secuencia Secuencia genética (solo ATCG)
     */
    public Virus(String nombre, String nivelInfecciosidad, String secuencia) {
        this.nombre = nombre;
        this.nivelInfecciosidad = nivelInfecciosidad;
        this.secuencia = secuencia;
    }

    /**
     * Obtiene el nombre del virus.
     *
     * // Objetivo
     *    Acceder al identificador único del virus para mostrarlo
     *    en interfaces, diagnósticos o reportes.
     *
     * @return Nombre del virus
     */
    public String getNombre() { return nombre; }

    /**
     * Establece el nombre del virus.
     *
     * // Objetivo
     *    Asignar o modificar el nombre del virus, útil durante
     *    la creación o actualización de registros virales.
     *
     * @param nombre Nuevo nombre del virus
     */
    public void setNombre(String nombre) { this.nombre = nombre; }

    /**
     * Obtiene el nivel de infecciosidad del virus.
     *
     * // Objetivo
     *    Conocer el nivel de riesgo del virus para clasificarlo
     *    en reportes y diagnósticos.
     *
     * @return Nivel de infecciosidad (String)
     */
    public String getNivelInfecciosidad() { return nivelInfecciosidad; }

    /**
     * Establece el nivel de infecciosidad del virus.
     *
     * // Objetivo
     *    Asignar el nivel de riesgo al virus, validando externamente
     *    que sea uno de los valores permitidos.
     *
     * @param nivelInfecciosidad Nuevo nivel ("Poco Infeccioso", "Normal", "Altamente Infeccioso")
     */
    public void setNivelInfecciosidad(String nivelInfecciosidad) { this.nivelInfecciosidad = nivelInfecciosidad; }

    /**
     * Obtiene la secuencia genética del virus.
     *
     * // Objetivo
     *    Acceder a la cadena de ADN del virus para realizar
     *    comparaciones y diagnósticos con muestras de pacientes.
     *
     * @return Secuencia de ADN (String)
     */
    public String getSecuencia() { return secuencia; }

    /**
     * Establece la secuencia genética del virus.
     *
     * // Objetivo
     *    Asignar la secuencia de ADN al virus, validando externamente
     *    que contenga solo los caracteres A, T, C, G.
     *
     * @param secuencia Nueva secuencia de ADN
     */
    public void setSecuencia(String secuencia) { this.secuencia = secuencia; }

    /**
     * Verifica si el virus es altamente infeccioso.
     *
     * // Objetivo
     *    Determinar rápidamente si un virus pertenece a la categoría
     *    de alto riesgo, útil para filtrar en reportes como el de
     *    pacientes de alto riesgo (más de 3 virus altamente infecciosos).
     *
     * // Uso típico
     *    if (virus.esAltamenteInfeccioso()) {
     *        // Clasificar como virus de alto riesgo
     *    }
     *
     * @return true si el nivel es "Altamente Infeccioso", false en caso contrario
     */
    public boolean esAltamenteInfeccioso() {
        return "Altamente Infeccioso".equalsIgnoreCase(nivelInfecciosidad);
    }

    /**
     * Representación en String del virus para depuración.
     *
     * // Objetivo
     *    Proporcionar una representación textual del virus útil
     *    para logs, depuración y mensajes de error.
     *
     * // Formato
     *    Virus{nombre='X', nivel='Y', secuencia.length=Z}
     *
     * @return String con información resumida del virus
     */
    @Override
    public String toString() {
        return "Virus{" +
                "nombre='" + nombre + '\'' +
                ", nivel='" + nivelInfecciosidad + '\'' +
                ", secuencia.length=" + (secuencia != null ? secuencia.length() : 0) +
                '}';
    }
}