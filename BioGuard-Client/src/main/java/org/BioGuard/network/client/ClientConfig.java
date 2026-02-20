package org.BioGuard.network.client;

/**
 * Configuración para el cliente TCP del sistema BioGuard.
 *
 * <p>Esta clase encapsula todos los parámetros de configuración necesarios
 * para establecer una conexión con el servidor. Utiliza el patrón Builder
 * para facilitar la creación de instancias con diferentes configuraciones.</p>
 *
 * <h2>Parámetros configurables:</h2>
 * <ul>
 *   <li><b>Host:</b> Dirección del servidor (IP o nombre)</li>
 *   <li><b>Puerto:</b> Puerto de conexión (1-65535)</li>
 *   <li><b>Timeout de conexión:</b> Tiempo máximo para establecer conexión</li>
 *   <li><b>Timeout de lectura:</b> Tiempo máximo para recibir respuesta</li>
 *   <li><b>SSL:</b> Activar/desactivar conexión segura</li>
 * </ul>
 *
 * <h2>Ejemplo de uso:</h2>
 * <pre>
 * ClientConfig config = new ClientConfig.Builder()
 *     .withHost("localhost")
 *     .withPort(8080)
 *     .withReadTimeoutMs(30000)
 *     .build();
 * </pre>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 */
public class ClientConfig {

    private final String serverHost;
    private final int serverPort;
    private final int connectionTimeoutMs;
    private final int readTimeoutMs;
    private final boolean useSSL;

    private ClientConfig(Builder builder) {
        this.serverHost = builder.serverHost;
        this.serverPort = builder.serverPort;
        this.connectionTimeoutMs = builder.connectionTimeoutMs;
        this.readTimeoutMs = builder.readTimeoutMs;
        this.useSSL = builder.useSSL;
    }

    /**
     * Obtiene el host del servidor.
     *
     * @return Dirección IP o nombre del host
     */
    public String getServerHost() { return serverHost; }

    /**
     * Obtiene el puerto del servidor.
     *
     * @return Puerto de conexión
     */
    public int getServerPort() { return serverPort; }

    /**
     * Obtiene el timeout de conexión en milisegundos.
     *
     * @return Timeout de conexión
     */
    public int getConnectionTimeoutMs() { return connectionTimeoutMs; }

    /**
     * Obtiene el timeout de lectura en milisegundos.
     *
     * @return Timeout de lectura
     */
    public int getReadTimeoutMs() { return readTimeoutMs; }

    /**
     * Indica si se debe usar SSL/TLS.
     *
     * @return true para conexión segura, false para conexión normal
     */
    public boolean useSSL() { return useSSL; }

    @Override
    public String toString() {
        return String.format("ClientConfig{host='%s', port=%d, connectTimeout=%dms, readTimeout=%dms, ssl=%s}",
                serverHost, serverPort, connectionTimeoutMs, readTimeoutMs, useSSL);
    }

    /**
     * Builder para crear instancias de ClientConfig.
     *
     * <p>Este builder permite construir configuraciones de forma legible y flexible,
     * con valores por defecto sensatos para todos los parámetros.</p>
     */
    public static class Builder {
        private String serverHost = "localhost";
        private int serverPort = 8080;
        private int connectionTimeoutMs = 5000;
        private int readTimeoutMs = 30000;
        private boolean useSSL = false;

        /**
         * Establece el host del servidor.
         *
         * @param host Dirección IP o nombre del host
         * @return Este builder para encadenar llamadas
         */
        public Builder withHost(String host) {
            this.serverHost = host;
            return this;
        }

        /**
         * Establece el puerto del servidor.
         *
         * @param port Puerto de conexión (1-65535)
         * @return Este builder para encadenar llamadas
         * @throws IllegalArgumentException Si el puerto está fuera de rango
         */
        public Builder withPort(int port) {
            if (port <= 0 || port > 65535) {
                throw new IllegalArgumentException("El puerto debe estar entre 1 y 65535");
            }
            this.serverPort = port;
            return this;
        }

        /**
         * Establece el timeout de conexión en milisegundos.
         *
         * @param timeoutMs Timeout en milisegundos (no negativo)
         * @return Este builder para encadenar llamadas
         * @throws IllegalArgumentException Si el timeout es negativo
         */
        public Builder withConnectionTimeoutMs(int timeoutMs) {
            if (timeoutMs < 0) {
                throw new IllegalArgumentException("El timeout no puede ser negativo");
            }
            this.connectionTimeoutMs = timeoutMs;
            return this;
        }

        /**
         * Establece el timeout de lectura en milisegundos.
         *
         * @param timeoutMs Timeout en milisegundos (no negativo)
         * @return Este builder para encadenar llamadas
         * @throws IllegalArgumentException Si el timeout es negativo
         */
        public Builder withReadTimeoutMs(int timeoutMs) {
            if (timeoutMs < 0) {
                throw new IllegalArgumentException("El timeout no puede ser negativo");
            }
            this.readTimeoutMs = timeoutMs;
            return this;
        }

        /**
         * Activa o desactiva el uso de SSL/TLS.
         *
         * @param useSSL true para conexión segura, false para conexión normal
         * @return Este builder para encadenar llamadas
         */
        public Builder withSSL(boolean useSSL) {
            this.useSSL = useSSL;
            return this;
        }

        /**
         * Construye la configuración con los parámetros establecidos.
         *
         * @return Nueva instancia de ClientConfig
         * @throws IllegalStateException Si la configuración es inválida
         */
        public ClientConfig build() {
            validate();
            return new ClientConfig(this);
        }

        private void validate() {
            if (serverHost == null || serverHost.trim().isEmpty()) {
                throw new IllegalStateException("El host del servidor no puede ser null o vacío");
            }
        }
    }
}