package org.BioGuard.network.client;

public class ClientConfig {

    private final String serverHost;
    private final int serverPort;
    private final int connectionTimeoutMs;
    private final int readTimeoutMs;
    private final boolean useSSL;  // ← NUEVO

    private ClientConfig(Builder builder) {
        this.serverHost = builder.serverHost;
        this.serverPort = builder.serverPort;
        this.connectionTimeoutMs = builder.connectionTimeoutMs;
        this.readTimeoutMs = builder.readTimeoutMs;
        this.useSSL = builder.useSSL;  // ← NUEVO
    }

    public String getServerHost() { return serverHost; }
    public int getServerPort() { return serverPort; }
    public int getConnectionTimeoutMs() { return connectionTimeoutMs; }
    public int getReadTimeoutMs() { return readTimeoutMs; }
    public boolean useSSL() { return useSSL; }  // ← NUEVO

    public static class Builder {
        private String serverHost = "localhost";
        private int serverPort = 8443;  // ← CAMBIADO a 8443 por defecto
        private int connectionTimeoutMs = 5000;
        private int readTimeoutMs = 30000;
        private boolean useSSL = true;  // ← NUEVO (true por defecto)

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

        public Builder withSSL(boolean useSSL) {  // ← NUEVO
            this.useSSL = useSSL;
            return this;
        }

        public ClientConfig build() {
            validate();
            return new ClientConfig(this);
        }

        private void validate() {
            if (serverHost == null || serverHost.trim().isEmpty()) {
                throw new IllegalStateException("Host inválido");
            }
            if (serverPort <= 0 || serverPort > 65535) {
                throw new IllegalStateException("Puerto inválido: " + serverPort);
            }
        }
    }
}