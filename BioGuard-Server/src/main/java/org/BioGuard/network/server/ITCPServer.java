package org.BioGuard.network.server;

import java.io.IOException;

/**
 * Interfaz que define el contrato para los servidores TCP del sistema BioGuard.
 *
 * <p>Esta interfaz establece las operaciones fundamentales que cualquier servidor
 * TCP debe implementar, siguiendo el principio de inversión de dependencias.
 * Las implementaciones concretas pueden variar (con o sin SSL, con diferentes
 * políticas de concurrencia, etc.) pero todas deben cumplir con este contrato.</p>
 *
 * <p>El ciclo de vida típico de un servidor es:
 * <ol>
 *   <li>Crear una instancia con su configuración específica</li>
 *   <li>Invocar {@link #start()} para iniciar la escucha de conexiones</li>
 *   <li>Invocar {@link #stop()} para detener el servidor gracefulmente</li>
 * </ol>
 * </p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 * @see TCPServer
 */
public interface ITCPServer {

    /**
     * Inicia el servidor en el puerto configurado y comienza a aceptar conexiones.
     *
     * <p>Este método es bloqueante y debe ejecutarse en un hilo separado si se
     * necesita que el programa principal continúe con otras tareas. El servidor
     * permanecerá activo hasta que se invoque {@link #stop()}.</p>
     *
     * <p>Durante la ejecución, el servidor:</p>
     * <ul>
     *   <li>Acepta conexiones entrantes de clientes</li>
     *   <li>Delega cada conexión a un manejador especializado</li>
     *   <li>Maneja errores de forma controlada sin detener el servidor</li>
     * </ul>
     *
     * @throws IOException Si ocurre un error crítico al iniciar el servidor
     *         (puerto en uso, permisos, etc.)
     * @throws IllegalStateException Si el servidor ya está en ejecución
     */
    void start() throws IOException;

    /**
     * Detiene el servidor de forma ordenada (graceful shutdown).
     *
     * <p>Este método:</p>
     * <ul>
     *   <li>Deja de aceptar nuevas conexiones</li>
     *   <li>Espera a que las conexiones activas terminen su procesamiento</li>
     *   <li>Libera todos los recursos (puertos, hilos, etc.)</li>
     * </ul>
     *
     * <p>Es seguro invocar este método múltiples veces; las llamadas subsiguientes
     * no tendrán efecto.</p>
     */
    void stop();

    /**
     * Verifica si el servidor está actualmente en ejecución.
     *
     * @return {@code true} si el servidor está activo y aceptando conexiones,
     *         {@code false} en caso contrario
     */
    boolean isRunning();
}