package org.BioGuard.handler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CommandParser {

    private final Map<String, Function<String, String>> handlers = new HashMap<>();

    public void register(String prefix, Function<String, String> handler) {
        handlers.put(prefix, handler);
    }

    public String parse(String message) {
        if (message == null || message.trim().isEmpty()) {
            return "ERROR: Mensaje vacío";
        }

        for (Map.Entry<String, Function<String, String>> entry : handlers.entrySet()) {
            if (message.startsWith(entry.getKey())) {
                String data = message.substring(entry.getKey().length());
                return entry.getValue().apply(data);
            }
        }

        return "ERROR: Comando no reconocido";
    }
}