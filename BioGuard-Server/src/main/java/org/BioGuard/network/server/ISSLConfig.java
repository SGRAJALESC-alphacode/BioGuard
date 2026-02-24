package org.BioGuard.network.server;

public interface ISSLConfig extends ITCPConfig {
    String getKeyStorePath();
    String getKeyStorePassword();
}