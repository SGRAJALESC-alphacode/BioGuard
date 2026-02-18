package org.BioGuard;

/*
 * // Objetivo
 *    Leer y parsear archivos FASTA (tanto virus como muestras) y extraer
 *    los metadatos del header y la secuencia concatenada.
 *
 * // Funciones principales
 *    leerFasta(ruta): Lee un archivo FASTA y devuelve un mapa con "header" y "secuencia".
 *    parsearHeaderVirus(header): Parsea headers con formato nombre|nivel.
 *    parsearHeaderMuestra(header): Parsea headers con formato documento|fecha.
 */

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class FastaReader {

    /**
     * Lee un archivo FASTA y extrae el header y la secuencia
     * @param rutaArchivo Ruta al archivo .fasta
     * @return Mapa con "header" y "secuencia"
     * @throws IOException Si hay error de lectura o formato inválido
     */
    public static Map<String, String> leerFasta(String rutaArchivo) throws IOException {
        Path path = Paths.get(rutaArchivo);
        if (!Files.exists(path)) {
            throw new IOException("El archivo no existe: " + rutaArchivo);
        }

        String contenido = Files.readString(path).trim();
        String[] lineas = contenido.split("\n");

        if (lineas.length < 2) {
            throw new IOException("Formato FASTA inválido: debe tener al menos 2 líneas");
        }

        String header = lineas[0].trim();
        if (!header.startsWith(">")) {
            throw new IOException("Formato FASTA inválido: la primera línea debe comenzar con '>'");
        }

        // La secuencia puede estar en múltiples líneas, las concatenamos
        StringBuilder secuencia = new StringBuilder();
        for (int i = 1; i < lineas.length; i++) {
            secuencia.append(lineas[i].trim());
        }

        // Validar que solo contenga A,T,C,G
        if (!secuencia.toString().matches("^[ATCG]+$")) {
            throw new IOException("Secuencia inválida: solo se permiten caracteres A,T,C,G");
        }

        Map<String, String> resultado = new HashMap<>();
        resultado.put("header", header.substring(1)); // Quitamos el '>'
        resultado.put("secuencia", secuencia.toString());

        return resultado;
    }

    /**
     * Parsea el header de un virus con formato: nombre_virus|nivel_infecciosidad
     * @param header El header sin el '>'
     * @return Mapa con "nombre" y "nivel"
     */
    public static Map<String, String> parsearHeaderVirus(String header) {
        Map<String, String> resultado = new HashMap<>();

        if (header.contains("|")) {
            String[] partes = header.split("\\|");
            resultado.put("nombre", partes[0]);
            resultado.put("nivel", partes[1]);
        } else {
            resultado.put("nombre", header);
            resultado.put("nivel", "Normal"); // Valor por defecto
        }

        return resultado;
    }

    /**
     * Parsea el header de una muestra con formato: documento|fecha
     * @param header El header sin el '>'
     * @return Mapa con "documento" y "fecha"
     */
    public static Map<String, String> parsearHeaderMuestra(String header) {
        Map<String, String> resultado = new HashMap<>();

        if (header.contains("|")) {
            String[] partes = header.split("\\|");
            resultado.put("documento", partes[0]);
            resultado.put("fecha", partes[1]);
        } else {
            resultado.put("documento", header);
            resultado.put("fecha", String.valueOf(System.currentTimeMillis()));
        }

        return resultado;
    }
}