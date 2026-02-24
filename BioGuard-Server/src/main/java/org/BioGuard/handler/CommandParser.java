package org.BioGuard.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Parser de comandos que enruta mensajes a sus handlers correspondientes.
 *
 * @author Sergio Grajales
 * @author Jhonatan Tamayo
 * @version 1.0
 */
public class CommandParser {

    private final Map<String, Function<String, String>> comandos = new HashMap<>();

    /**
     * Registra un comando con su función asociada.
     *
     * @param prefijo Prefijo del comando
     * @param funcion Función que procesa el comando
     */
    public void registrarComando(String prefijo, Function<String, String> funcion) {
        comandos.put(prefijo, funcion);
    }

    /**
     * Ejecuta el comando correspondiente según el mensaje.
     *
     * @param mensaje Mensaje completo recibido del cliente
     * @return Resultado de la ejecución del comando
     */
    public String ejecutarComando(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            return "ERROR: Mensaje vacío";
        }

        for (Map.Entry<String, Function<String, String>> entry : comandos.entrySet()) {
            String prefijo = entry.getKey();
            if (mensaje.startsWith(prefijo)) {
                String parametros = mensaje.substring(prefijo.length());
                try {
                    return entry.getValue().apply(parametros);
                } catch (Exception e) {
                    return "ERROR: " + e.getMessage();
                }
            }
        }

        return "ERROR: Comando no reconocido";
    }
}