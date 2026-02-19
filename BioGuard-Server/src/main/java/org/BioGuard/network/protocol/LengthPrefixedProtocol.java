package org.BioGuard.network.protocol;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementación del protocolo de comunicación basado en prefijo de longitud.
 *
 * <p>Formato del protocolo:
 * <ul>
 *   <li>4 bytes (int) que indican la longitud del mensaje en bytes</li>
 *   <li>N bytes con el contenido del mensaje en UTF-8</li>
 * </ul>
 * </p>
 *
 * <p>Esta implementación es resistente a problemas de fragmentación de red
 * y funciona correctamente con cualquier lenguaje que pueda leer enteros
 * de 32 bits en formato big-endian.</p>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 */
public class LengthPrefixedProtocol implements IMessageProtocol {

    /** Tamaño máximo permitido para un mensaje (64KB) */
    private static final int MAX_MESSAGE_SIZE = 65536;

    @Override
    public String decode(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            throw new IllegalArgumentException("El InputStream no puede ser null");
        }

        DataInputStream dataInput = new DataInputStream(inputStream);

        try {
            // 1. Leer la longitud del mensaje (4 bytes)
            int messageLength = dataInput.readInt();

            // 2. Validar longitud
            if (messageLength <= 0 || messageLength > MAX_MESSAGE_SIZE) {
                throw new IOException("Longitud de mensaje inválida: " + messageLength);
            }

            // 3. Leer los bytes del mensaje
            byte[] messageBytes = new byte[messageLength];
            dataInput.readFully(messageBytes);

            // 4. Convertir a String
            return new String(messageBytes, StandardCharsets.UTF_8);

        } catch (EOFException e) {
            throw new IOException("Conexión cerrada antes de recibir el mensaje completo", e);
        }
    }

    @Override
    public void encode(String message, OutputStream outputStream) throws IOException {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede ser null o vacío");
        }

        if (outputStream == null) {
            throw new IllegalArgumentException("El OutputStream no puede ser null");
        }

        // 1. Convertir mensaje a bytes
        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        // 2. Validar tamaño
        if (messageBytes.length > MAX_MESSAGE_SIZE) {
            throw new IOException("Mensaje demasiado grande: " + messageBytes.length + " bytes");
        }

        // 3. Escribir longitud + datos
        DataOutputStream dataOutput = new DataOutputStream(outputStream);
        dataOutput.writeInt(messageBytes.length);
        dataOutput.write(messageBytes);
        dataOutput.flush();
    }
}