package org.BioGuard.network.protocol;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementación del protocolo de comunicación basado en prefijo de longitud.
 *
 * <p>Formato: [4 bytes de longitud][mensaje en UTF-8]</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class LengthPrefixedProtocol implements IMessageProtocol {

    private static final int MAX_MESSAGE_SIZE = 2 * 1024 * 1024; // 2MB
    private static final int LENGTH_PREFIX_SIZE = 4;

    @Override
    public String decode(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("InputStream no puede ser null");
        }

        DataInputStream dataInput = new DataInputStream(new BufferedInputStream(inputStream));

        try {
            // 1. Leer longitud (4 bytes)
            int messageLength = dataInput.readInt();

            // 2. Validar longitud
            if (messageLength <= 0) {
                throw new IOException("Longitud inválida: " + messageLength);
            }

            if (messageLength > MAX_MESSAGE_SIZE) {
                throw new IOException("Longitud excede máximo: " + messageLength + " > " + MAX_MESSAGE_SIZE);
            }

            // 3. Leer mensaje
            byte[] messageBytes = new byte[messageLength];
            dataInput.readFully(messageBytes);

            // 4. Convertir a String
            return new String(messageBytes, StandardCharsets.UTF_8);

        } catch (EOFException e) {
            throw new IOException("Conexión cerrada antes de recibir mensaje completo", e);
        }
    }

    @Override
    public void encode(String message, OutputStream outputStream) throws IOException {
        if (message == null) {
            throw new IllegalArgumentException("El mensaje no puede ser null");
        }

        if (outputStream == null) {
            throw new IllegalArgumentException("OutputStream no puede ser null");
        }

        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        if (messageBytes.length > MAX_MESSAGE_SIZE) {
            throw new IOException("Mensaje demasiado grande: " + messageBytes.length + " bytes");
        }

        DataOutputStream dataOutput = new DataOutputStream(new BufferedOutputStream(outputStream));
        dataOutput.writeInt(messageBytes.length);
        dataOutput.write(messageBytes);
        dataOutput.flush();
    }
}