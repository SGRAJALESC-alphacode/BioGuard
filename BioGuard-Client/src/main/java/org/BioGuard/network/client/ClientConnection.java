package org.BioGuard.network.client;

import java.io.*;
import java.net.Socket;

public class ClientConnection {

    private final Socket socket;
    private final OutputStream out;
    private final InputStream in;

    public ClientConnection(Socket socket) throws IOException {
        this.socket = socket;
        this.out = socket.getOutputStream();
        this.in = socket.getInputStream();
    }

    public OutputStream getOutputStream() { return out; }
    public InputStream getInputStream() { return in; }

    public void close() {
        try {
            if (socket != null) socket.close();
        } catch (IOException e) {
            // Ignorar
        }
    }
}