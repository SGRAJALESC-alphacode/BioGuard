package org.BioGuard;

import org.BioGuard.exception.*;
import java.io.*;
import java.net.Socket;
import java.util.List;

/**
 * Manejador de peticiones de clientes en el servidor BioGuard.
 * Cada instancia corre en un hilo independiente y gestiona la comunicación
 * con un cliente específico a través de un socket.
 *
 * // Objetivo
 *    Procesar las solicitudes de los clientes, interpretar los comandos
 *    recibidos y delegar la lógica de negocio a los servicios
 *    correspondientes (PacienteService, VirusService, DiagnosticoService).
 *
 * // Atributos
 *    socket              : Socket TCP para la comunicación con el cliente
 *    pacienteService     : Servicio para operaciones sobre pacientes (registro, consulta)
 *    virusService        : Servicio para operaciones sobre virus (carga, consulta)
 *    diagnosticoService  : Servicio para diagnóstico de muestras y generación de reportes
 *
 * // Comportamiento / métodos
 *    run()               : Método principal del hilo, lee la solicitud del cliente,
 *                          procesa el comando y envía la respuesta
 *    procesarComando()   : Interpreta el comando recibido y lo dirige al método adecuado
 *    registrarPaciente() : Procesa el registro de un nuevo paciente
 *    consultarPaciente() : Busca y retorna información de un paciente
 *    cargarVirus()       : Almacena un nuevo virus en el sistema
 *    diagnosticarMuestra(): Realiza análisis de una muestra de ADN
 *    generarReporteAltoRiesgo(): Genera reporte de pacientes de alto riesgo
 *    generarReporteMutaciones(): Genera reporte de mutaciones para un paciente
 */
public class ClientHandler implements Runnable {

    private final Socket socket;
    private final PacienteService pacienteService;
    private final VirusService virusService;
    private final DiagnosticoService diagnosticoService;

    /**
     * Constructor del manejador de cliente.
     * Inicializa el socket y los servicios necesarios para atender al cliente.
     *
     * // Objetivo
     *    Establecer la conexión con el cliente y preparar los servicios
     *    que se utilizarán durante la atención de sus solicitudes.
     *
     * @param socket Socket del cliente conectado
     */
    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.pacienteService = new PacienteService();
        this.virusService = new VirusService();
        this.diagnosticoService = new DiagnosticoService();
    }

    /**
     * Método principal del hilo que atiende al cliente.
     * Lee la solicitud entrante, la procesa y envía la respuesta.
     *
     * // Objetivo
     *    Gestionar el ciclo de vida de la comunicación con el cliente:
     *    - Leer la línea de solicitud (formato: COMANDO|param1|param2...)
     *    - Procesar el comando mediante procesarComando()
     *    - Enviar la respuesta al cliente
     *    - Cerrar la conexión al finalizar
     *
     * // Flujo de trabajo
     *    1. Abrir flujos de entrada/salida del socket
     *    2. Leer la línea de solicitud
     *    3. Dividir la línea en partes (separador "|")
     *    4. Identificar el comando y sus parámetros
     *    5. Ejecutar la lógica correspondiente
     *    6. Enviar respuesta al cliente
     *    7. Cerrar recursos y conexión
     */
    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            String request = in.readLine();
            if (request == null) return;

            System.out.println("Comando recibido: " + request);
            String[] parts = request.split("\\|");
            String command = parts[0];

            String response = procesarComando(command, parts);
            out.println(response);

        } catch (IOException e) {
            System.err.println("Error I/O cliente: " + e.getMessage());
        } finally {
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    /**
     * Procesa el comando recibido y lo dirige al método específico.
     *
     * // Objetivo
     *    Actuar como un enrutador de comandos, identificando la acción
     *    solicitada por el cliente y delegando en el método correspondiente.
     *    También maneja las excepciones específicas para retornar mensajes
     *    de error claros al cliente.
     *
     * // Comandos soportados
     *    REGISTRAR_PACIENTE   : Registra un nuevo paciente en el sistema
     *    CONSULTAR_PACIENTE   : Consulta información de un paciente
     *    CARGAR_VIRUS         : Almacena un virus desde archivo FASTA
     *    DIAGNOSTICAR         : Analiza una muestra de ADN
     *    REPORTE_ALTO_RIESGO  : Genera reporte de pacientes de alto riesgo
     *    REPORTE_MUTACIONES   : Genera reporte de mutaciones de un paciente
     *
     * @param command Comando a ejecutar (primer elemento del array)
     * @param parts   Array completo con comando y parámetros
     * @return Respuesta formateada para el cliente
     */
    private String procesarComando(String command, String[] parts) {
        try {
            switch (command) {
                case "REGISTRAR_PACIENTE":
                    return registrarPaciente(parts);
                case "CONSULTAR_PACIENTE":
                    return consultarPaciente(parts);
                case "CARGAR_VIRUS":
                    return cargarVirus(parts);
                case "DIAGNOSTICAR":
                    return diagnosticarMuestra(parts);
                case "REPORTE_ALTO_RIESGO":
                    return generarReporteAltoRiesgo();
                case "REPORTE_MUTACIONES":
                    return generarReporteMutaciones(parts);
                default:
                    return "ERROR: Comando desconocido";
            }
        } catch (PacienteDuplicadoException e) {
            return "ERROR_DUPLICADO: " + e.getMessage();
        } catch (FormatoFastaInvalidoException e) {
            return "ERROR_FASTA: " + e.getMessage();
        } catch (MuestraNoEncontradaException e) {
            return "ERROR_MUESTRA: " + e.getMessage();
        } catch (DiagnosticoException e) {
            return "ERROR_DIAGNOSTICO: " + e.getMessage();
        } catch (IOException e) {
            return "ERROR_IO: " + e.getMessage();
        } catch (Exception e) {
            return "ERROR_INTERNO: " + e.getMessage();
        }
    }

    /**
     * Procesa el registro de un nuevo paciente.
     *
     * // Formato esperado
     *    REGISTRAR_PACIENTE|documento|nombre|apellido|edad|correo|genero|ciudad|pais
     *
     * // Validaciones
     *    - Verificar que todos los campos estén presentes
     *    - Validar documento duplicado (lanza PacienteDuplicadoException)
     *    - Validar formato de datos (edad numérica, correo con @)
     *
     * @param parts Array con los datos del paciente
     * @return Mensaje de confirmación o error
     * @throws PacienteDuplicadoException Si el documento ya existe
     * @throws IOException Si hay error de E/S
     */
    private String registrarPaciente(String[] parts) throws PacienteDuplicadoException, IOException {
        if (parts.length < 9) return "ERROR: Faltan datos";

        Paciente p = new Paciente();
        p.setDocumento(parts[1]);
        p.setNombre(parts[2]);
        p.setApellido(parts[3]);
        p.setEdad(Integer.parseInt(parts[4]));
        p.setCorreo(parts[5]);
        p.setGenero(parts[6]);
        p.setCiudad(parts[7]);
        p.setPais(parts[8]);

        pacienteService.registrarPaciente(p);
        return "OK: Paciente " + p.getDocumento() + " registrado";
    }

    /**
     * Consulta información de un paciente por su documento.
     *
     * // Formato esperado
     *    CONSULTAR_PACIENTE|documento
     *
     * // Respuesta
     *    Si existe: documento|nombre|apellido|edad|correo|genero|ciudad|pais
     *    Si no existe: ERROR: Paciente no encontrado
     *
     * @param parts Array con el documento a consultar
     * @return Datos del paciente separados por "|" o mensaje de error
     * @throws IOException Si hay error de E/S
     */
    private String consultarPaciente(String[] parts) throws IOException {
        if (parts.length < 2) return "ERROR: Documento requerido";

        Paciente p = pacienteService.consultarPaciente(parts[1]);
        if (p == null) return "ERROR: Paciente no encontrado";

        return String.join("|",
                p.getDocumento(), p.getNombre(), p.getApellido(),
                String.valueOf(p.getEdad()), p.getCorreo(),
                p.getGenero(), p.getCiudad(), p.getPais()
        );
    }

    /**
     * Almacena un nuevo virus en el sistema.
     *
     * // Formato esperado
     *    CARGAR_VIRUS|nombre|nivel|secuencia
     *
     * // Validaciones
     *    - Nombre no vacío
     *    - Nivel debe ser "Poco Infeccioso", "Normal" o "Altamente Infeccioso"
     *    - Secuencia debe contener solo A, T, C, G
     *
     * @param parts Array con nombre, nivel y secuencia del virus
     * @return Mensaje de confirmación
     * @throws FormatoFastaInvalidoException Si los datos no cumplen el formato
     * @throws IOException Si hay error de E/S
     */
    private String cargarVirus(String[] parts) throws FormatoFastaInvalidoException, IOException {
        if (parts.length < 4) return "ERROR: Faltan datos";

        virusService.guardarVirus(parts[1], parts[2], parts[3]);
        return "OK: Virus " + parts[1] + " guardado";
    }

    /**
     * Realiza el diagnóstico de una muestra de ADN.
     *
     * // Formato esperado
     *    DIAGNOSTICAR|documento|fecha|secuencia
     *
     * // Proceso
     *    1. Validar que la secuencia solo contenga A,T,C,G
     *    2. Guardar la muestra en data/muestras/[documento]/
     *    3. Comparar con virus registrados
     *    4. Generar archivo CSV con resultados
     *
     * @param parts Array con documento, fecha y secuencia
     * @return Resultado del diagnóstico
     * @throws MuestraNoEncontradaException Si hay error con la muestra
     * @throws DiagnosticoException Si hay error en el análisis
     * @throws IOException Si hay error de E/S
     */
    private String diagnosticarMuestra(String[] parts) throws MuestraNoEncontradaException, DiagnosticoException, IOException {
        if (parts.length < 4) return "ERROR: Faltan datos";

        List<Diagnostico> resultados = diagnosticoService.diagnosticarMuestra(
                parts[1], parts[2], parts[3]
        );

        if (resultados.isEmpty()) return "RESULTADO: No se detectaron virus";

        StringBuilder sb = new StringBuilder("RESULTADO:");
        for (Diagnostico d : resultados) {
            sb.append("|").append(d.toString());
        }
        return sb.toString();
    }

    /**
     * Genera reporte de pacientes de alto riesgo.
     *
     * // Criterio
     *    Pacientes con más de 3 virus de nivel "Altamente Infeccioso"
     *
     * // Formato de salida
     *    Archivo CSV en data/reportes/alto_riesgo_[timestamp].csv
     *    Columnas: documento,cantidad_virus,cantidad_altamente,lista_normal,lista_altamente
     *
     * @return Ruta del archivo generado
     * @throws IOException Si hay error de E/S
     */
    private String generarReporteAltoRiesgo() throws IOException {
        String ruta = diagnosticoService.generarReporteAltoRiesgo();
        return "OK: Reporte en " + ruta;
    }

    /**
     * Genera reporte de mutaciones para un paciente.
     *
     * // Formato esperado
     *    REPORTE_MUTACIONES|documento
     *
     * // Proceso
     *    Compara la muestra más reciente con todas las anteriores
     *    Detecta posiciones donde hay cambios en la secuencia
     *
     * // Formato de salida
     *    Archivo CSV en data/reportes/mutaciones_[documento]_[timestamp].csv
     *    Columnas: muestra_anterior,inicio,fin,tipo
     *
     * @param parts Array con el documento del paciente
     * @return Ruta del archivo generado
     * @throws MuestraNoEncontradaException Si no hay suficientes muestras
     * @throws IOException Si hay error de E/S
     */
    private String generarReporteMutaciones(String[] parts) throws MuestraNoEncontradaException, IOException {
        if (parts.length < 2) return "ERROR: Documento requerido";

        String ruta = diagnosticoService.generarReporteMutaciones(parts[1]);
        return "OK: Reporte en " + ruta;
    }
}