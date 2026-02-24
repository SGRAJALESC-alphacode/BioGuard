package org.BioGuard.network.server;

public class SSLConfig implements ISSLConfig {

    private final int port;
    private final String keyStorePath;
    private final String keyStorePassword;

    public SSLConfig(int port, String keyStorePath, String keyStorePassword) {
        this.port = port;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getKeyStorePath() {
        return keyStorePath;
    }

    @Override
    public String getKeyStorePassword() {
        return keyStorePassword;
    }
}