package org.BioGuard.network.client;

/**
 * Configuración para el cliente TCP.
 *
 * <p>Encapsula todos los parámetros de configuración necesarios para
 * establecer una conexión con el servidor.</p>
 *
 * <p>Responsabilidad Única: Almacenar y validar la configuración de red.</p>
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

        validarConfiguracion();
    }

    /**
     * Valida que la configuración sea correcta.
     *
     * @throws IllegalArgumentException Si algún parámetro es inválido
     */
    private void validarConfiguracion() {
        if (serverHost == null || serverHost.trim().isEmpty()) {
            throw new IllegalArgumentException("El host del servidor no puede ser null o vacío");
        }

        if (serverPort <= 0 || serverPort > 65535) {
            throw new IllegalArgumentException("Puerto inválido: " + serverPort + ". Debe estar entre 1 y 65535");
        }

        if (connectionTimeoutMs < 0) {
            throw new IllegalArgumentException("Timeout de conexión no puede ser negativo: " + connectionTimeoutMs);
        }

        if (readTimeoutMs < 0) {
            throw new IllegalArgumentException("Timeout de lectura no puede ser negativo: " + readTimeoutMs);
        }
    }

    // Getters
    public String getServerHost() { return serverHost; }
    public int getServerPort() { return serverPort; }
    public int getConnectionTimeoutMs() { return connectionTimeoutMs; }
    public int getReadTimeoutMs() { return readTimeoutMs; }
    public boolean useSSL() { return useSSL; }

    @Override
    public String toString() {
        return String.format("ClientConfig{host='%s', port=%d, timeout=%dms, ssl=%s}",
                serverHost, serverPort, connectionTimeoutMs, useSSL);
    }

    /**
     * Builder para crear configuraciones de forma legible y flexible.
     */
    public static class Builder {
        private String serverHost = "localhost";
        private int serverPort = 8080;
        private int connectionTimeoutMs = 5000;
        private int readTimeoutMs = 30000;
        private boolean useSSL = false;

        public Builder withHost(String host) {
            this.serverHost = host;
            return this;
        }

        public Builder withPort(int port) {
            this.serverPort = port;
            return this;
        }

        public Builder withConnectionTimeoutMs(int timeoutMs) {
            this.connectionTimeoutMs = timeoutMs;
            return this;
        }

        public Builder withReadTimeoutMs(int timeoutMs) {
            this.readTimeoutMs = timeoutMs;
            return this;
        }

        public Builder withSSL(boolean useSSL) {
            this.useSSL = useSSL;
            return this;
        }

        public ClientConfig build() {
            return new ClientConfig(this);
        }
    }
}