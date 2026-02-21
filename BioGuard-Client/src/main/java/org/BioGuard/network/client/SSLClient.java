package org.BioGuard.network.client;

import org.BioGuard.network.protocol.IMessageProtocol;

import javax.net.ssl.*;
import java.io.*;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

public class SSLClient implements ITCPClient {

    private final ClientConfig config;
    private final IMessageProtocol protocol;
    private SSLSocket socket;
    private OutputStream out;
    private InputStream in;
    private volatile boolean connected = false;

    public SSLClient(ClientConfig config, IMessageProtocol protocol) {
        this.config = config;
        this.protocol = protocol;
    }

    private TrustManager[] createTrustAllManager() {
        return new TrustManager[] {
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };
    }

    private SSLSocketFactory createSSLFactory() throws Exception {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, createTrustAllManager(), new SecureRandom());
        return sslContext.getSocketFactory();
    }

    @Override
    public void connect() throws IOException {
        if (connected) return;

        try {
            SSLSocketFactory factory = createSSLFactory();
            socket = (SSLSocket) factory.createSocket(config.getServerHost(), config.getServerPort());
            socket.setSoTimeout(config.getReadTimeoutMs());
            socket.setEnabledProtocols(new String[] {"TLSv1.2", "TLSv1.3"});

            socket.startHandshake();

            out = socket.getOutputStream();
            in = socket.getInputStream();
            connected = true;

        } catch (Exception e) {
            disconnect();
            throw new IOException("Error conectando SSL: " + e.getMessage(), e);
        }
    }

    @Override
    public String sendMessage(String message) throws IOException {
        if (!connected || socket == null || socket.isClosed()) {
            throw new IllegalStateException("Cliente no está conectado");
        }

        try {
            protocol.encode(message, out);
            String response = protocol.decode(in);
            return response;
        } catch (IOException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public void disconnect() {
        connected = false;
        try { if (out != null) out.close(); } catch (IOException ignored) {}
        try { if (in != null) in.close(); } catch (IOException ignored) {}
        try { if (socket != null) socket.close(); } catch (IOException ignored) {}
        out = null; in = null; socket = null;
    }

    @Override
    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
}