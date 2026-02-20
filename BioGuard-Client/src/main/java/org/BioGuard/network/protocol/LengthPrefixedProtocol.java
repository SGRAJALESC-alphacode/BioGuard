package org.BioGuard.network.protocol;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * Implementación del protocolo de comunicación basado en prefijo de longitud.
 *
 * <p>Este protocolo es independiente del lenguaje y funciona con cualquier
 * sistema que pueda leer enteros de 32 bits en formato big-endian.</p>
 *
 * <h2>Formato del protocolo:</h2>
 * <pre>
 * +----------------+-------------------+
 * |  Longitud (4B) |  Datos (N bytes)  |
 * +----------------+-------------------+
 * </pre>
 *
 * <ul>
 *   <li><b>Longitud:</b> 4 bytes en big-endian indicando el tamaño del mensaje</li>
 *   <li><b>Datos:</b> El mensaje en codificación UTF-8</li>
 * </ul>
 *
 * <h2>Características:</h2>
 * <ul>
 *   <li>Resistente a fragmentación de red (usa readFully)</li>
 *   <li>Límite de 64KB por mensaje para evitar DoS</li>
 *   <li>Soporte completo para caracteres internacionales (UTF-8)</li>
 *   <li>Detección de conexiones cerradas abruptamente</li>
 * </ul>
 *
 * <h2>Ejemplo de uso:</h2>
 * <pre>
 * IMessageProtocol protocol = new LengthPrefixedProtocol();
 *
 * // Enviar
 * protocol.encode("Hola Mundo", outputStream);
 *
 * // Recibir
 * String mensaje = protocol.decode(inputStream);
 * </pre>
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 * @since 1.0
 * @see IMessageProtocol
 */
public class LengthPrefixedProtocol implements IMessageProtocol {

    /** Tamaño máximo permitido para un mensaje (64KB) */
    private static final int MAX_MESSAGE_SIZE = 65536;

    /** Tamaño del prefijo de longitud en bytes */
    private static final int LENGTH_PREFIX_SIZE = 4;

    @Override
    public String decode(InputStream inputStream) throws IOException {
        // Validación de parámetros
        if (inputStream == null) {
            throw new IllegalArgumentException("El InputStream no puede ser null");
        }

        DataInputStream dataInput = new DataInputStream(inputStream);

        try {
            // Paso 1: Leer la longitud del mensaje (4 bytes)
            int messageLength;
            try {
                messageLength = dataInput.readInt();
            } catch (EOFException e) {
                throw new IOException("Conexión cerrada antes de recibir la longitud del mensaje", e);
            }

            // Paso 2: Validar longitud (seguridad)
            if (messageLength <= 0) {
                throw new IOException("Longitud de mensaje inválida (menor o igual a 0): " + messageLength);
            }

            if (messageLength > MAX_MESSAGE_SIZE) {
                throw new IOException("Longitud de mensaje excede el máximo permitido: " +
                        messageLength + " > " + MAX_MESSAGE_SIZE);
            }

            // Paso 3: Leer exactamente los bytes del mensaje
            byte[] messageBytes = new byte[messageLength];
            try {
                dataInput.readFully(messageBytes);
            } catch (EOFException e) {
                throw new IOException("Conexión cerrada antes de recibir el mensaje completo. " +
                        "Se esperaban " + messageLength + " bytes", e);
            }

            // Paso 4: Convertir a String usando UTF-8
            return new String(messageBytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            // Relanzar la excepción con un mensaje más descriptivo
            throw new IOException("Error decodificando mensaje: " + e.getMessage(), e);
        }
    }

    @Override
    public void encode(String message, OutputStream outputStream) throws IOException {
        // Validación de parámetros
        if (message == null) {
            throw new IllegalArgumentException("El mensaje no puede ser null");
        }

        if (message.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede estar vacío");
        }

        if (outputStream == null) {
            throw new IllegalArgumentException("El OutputStream no puede ser null");
        }

        // Paso 1: Convertir mensaje a bytes (UTF-8)
        byte[] messageBytes;
        try {
            messageBytes = message.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IOException("Error codificando mensaje a UTF-8: " + e.getMessage(), e);
        }

        // Paso 2: Validar tamaño máximo
        if (messageBytes.length > MAX_MESSAGE_SIZE) {
            throw new IOException("Mensaje demasiado grande: " +
                    messageBytes.length + " bytes (máximo: " + MAX_MESSAGE_SIZE + ")");
        }

        // Paso 3: Escribir longitud y datos
        DataOutputStream dataOutput = new DataOutputStream(outputStream);

        try {
            // Escribir longitud (4 bytes)
            dataOutput.writeInt(messageBytes.length);

            // Escribir datos
            dataOutput.write(messageBytes);
            dataOutput.flush();

        } catch (IOException e) {
            throw new IOException("Error escribiendo mensaje: " + e.getMessage(), e);
        }
    }

    /**
     * Calcula el tamaño total que ocuparía un mensaje en bytes.
     *
     * <p>Útil para estimar buffers y validar límites.</p>
     *
     * @param message Mensaje a evaluar
     * @return Tamaño total en bytes (incluyendo cabecera)
     * @throws IllegalArgumentException Si el mensaje es null
     */
    public int calcularTamañoTotal(String message) {
        if (message == null) {
            throw new IllegalArgumentException("El mensaje no puede ser null");
        }

        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        return LENGTH_PREFIX_SIZE + messageBytes.length;
    }

    /**
     * Verifica si un mensaje excede el tamaño máximo permitido.
     *
     * @param message Mensaje a verificar
     * @return true si el mensaje es válido (no excede el máximo)
     * @throws IllegalArgumentException Si el mensaje es null
     */
    public boolean esTamañoValido(String message) {
        if (message == null) {
            throw new IllegalArgumentException("El mensaje no puede ser null");
        }

        byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
        return messageBytes.length <= MAX_MESSAGE_SIZE;
    }
}