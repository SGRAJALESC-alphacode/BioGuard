package org.BioGuard;

import org.BioGuard.exception.ConfiguracionException;
import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Punto de entrada del servidor BioGuard.
 * Esta clase es responsable de inicializar y configurar todos los componentes
 * del servidor antes de ponerlo en marcha.
 *
 * // Objetivo
 *    Inicializar el servidor BioGuard, cargando la configuración necesaria,
 *    estableciendo la seguridad SSL, creando la estructura de directorios
 *    y finalmente arrancando el servidor TCP para atender clientes.
 *
 * // Proceso de inicialización
 *    1. Cargar configuración desde archivo properties (búsqueda multi-ruta)
 *    2. Configurar SSL con el certificado del servidor
 *    3. Crear estructura de directorios necesaria (data/, logs/, etc.)
 *    4. Iniciar el servidor TCP en el puerto configurado
 *
 * // Archivos de configuración
 *    - config.properties: Archivo principal de configuración
 *    - Ubicaciones de búsqueda: resources/, raíz, src/main/resources/
 *
 * // Atributos
 *    CONFIG_FILE : Nombre del archivo de configuración
 *    DEFAULT_PORT: Puerto por defecto si no se especifica en configuración
 *
 * @author BioGuard Team
 * @version 1.0
 */
public class ServerMain {

    private static final String CONFIG_FILE = "config.properties";
    private static final int DEFAULT_PORT = 2021;

    /**
     * Método principal que inicia el servidor BioGuard.
     *
     * // Objetivo
     *    Orquestar el proceso completo de inicialización del servidor:
     *    - Mostrar información de depuración
     *    - Cargar configuración
     *    - Configurar SSL
     *    - Crear directorios
     *    - Iniciar servidor TCP
     *
     * // Flujo de ejecución
     *    1. Imprimir banner de inicio
     *    2. Mostrar información de ClassLoader y directorio actual
     *    3. Cargar configuración (cargarConfiguracion)
     *    4. Configurar SSL (configurarSSL)
     *    5. Crear estructura de directorios (crearEstructuraDirectorios)
     *    6. Iniciar servidor TCP en el puerto indicado
     *    7. Capturar y manejar excepciones críticas
     *
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("    BIOGUARD - SISTEMA DE VIGILANCIA     ");
        System.out.println("==========================================");

        try {
            // Mostrar información de depuración
            System.out.println("[DEBUG] ClassLoader: " + ServerMain.class.getClassLoader().getResource(""));
            System.out.println("[DEBUG] user.dir: " + System.getProperty("user.dir"));

            // 1. Cargar configuración
            Properties config = cargarConfiguracion();

            // 2. Configurar SSL
            configurarSSL(config);

            // 3. Crear estructura de directorios
            crearEstructuraDirectorios();

            // 4. Iniciar servidor
            int port = Integer.parseInt(config.getProperty("SERVER_PORT", String.valueOf(DEFAULT_PORT)));
            System.out.println("\n[INFO] Iniciando servidor en puerto " + port + "...");

            TCPServer server = new TCPServer(port);
            server.start();

        } catch (ConfiguracionException e) {
            System.err.println("[ERROR] " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Carga la configuración desde archivo properties.
     *
     * // Objetivo
     *    Localizar y cargar el archivo de configuración del sistema,
     *    buscando en múltiples ubicaciones para adaptarse a diferentes
     *    entornos de ejecución (desarrollo, producción, IDE, línea de comandos).
     *
     * // Estrategia de búsqueda
     *    1. Buscar en resources (classpath) - para ejecución desde JAR/IDE
     *    2. Buscar en la raíz del proyecto - para ejecución directa
     *    3. Buscar en src/main/resources - para estructura Maven
     *    4. Si no encuentra, crear archivo por defecto
     *
     * // Validaciones
     *    - Verificar que el archivo exista y sea legible
     *    - Cargar las propiedades
     *    - Mostrar valores cargados (sin contraseña)
     *
     * @return Properties con la configuración cargada
     * @throws ConfiguracionException Si hay error crítico en la carga
     */
    private static Properties cargarConfiguracion() throws ConfiguracionException {
        Properties config = new Properties();

        // 1. Buscar en resources
        try (InputStream is = ServerMain.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (is != null) {
                config.load(is);
                System.out.println("[INFO] Configuración cargada desde: resources/" + CONFIG_FILE);
                mostrarConfiguracion(config);
                return config;
            }
        } catch (IOException e) {
            System.err.println("[WARN] Error leyendo resources: " + e.getMessage());
        }

        // 2. Buscar en la raíz del proyecto
        File raizFile = new File(CONFIG_FILE);
        if (raizFile.exists()) {
            try (FileInputStream fis = new FileInputStream(raizFile)) {
                config.load(fis);
                System.out.println("[INFO] Configuración cargada desde raíz: " + raizFile.getAbsolutePath());
                mostrarConfiguracion(config);
                return config;
            } catch (IOException e) {
                System.err.println("[WARN] Error leyendo raíz: " + e.getMessage());
            }
        }

        // 3. Buscar en src/main/resources
        File resourcesFile = new File("src/main/resources/" + CONFIG_FILE);
        if (resourcesFile.exists()) {
            try (FileInputStream fis = new FileInputStream(resourcesFile)) {
                config.load(fis);
                System.out.println("[INFO] Configuración cargada desde: " + resourcesFile.getAbsolutePath());
                mostrarConfiguracion(config);
                return config;
            } catch (IOException e) {
                System.err.println("[WARN] Error leyendo src/main/resources: " + e.getMessage());
            }
        }

        // 4. Si no encuentra, crear por defecto
        System.out.println("[INFO] No se encontró archivo de configuración. Creando valores por defecto...");
        return crearConfiguracionPorDefecto();
    }

    /**
     * Muestra la configuración cargada (sin contraseña)
     *
     * // Objetivo
     *    Mostrar al usuario los valores de configuración cargados,
     *    omitiendo información sensible como contraseñas.
     *
     * @param config Propiedades cargadas
     */
    private static void mostrarConfiguracion(Properties config) {
        System.out.println("[INFO] Puerto: " + config.getProperty("SERVER_PORT"));
        System.out.println("[INFO] Certificado: " + config.getProperty("SSL_CERTIFICATE_ROUTE"));
    }

    /**
     * Crea archivo de configuración por defecto.
     *
     * // Objetivo
     *    Generar un archivo de configuración básico con valores predeterminados
     *    cuando no se encuentra ningún archivo existente.
     *
     * // Valores por defecto
     *    SERVER_PORT=2021
     *    SSL_CERTIFICATE_ROUTE=src/main/resources/certs/mi_clave.p12
     *    SSL_PASSWORD=12345678
     *    SSL_KEYSTORE_TYPE=PKCS12
     *
     * @return Properties con configuración por defecto
     * @throws ConfiguracionException Si no se puede crear el archivo
     */
    private static Properties crearConfiguracionPorDefecto() throws ConfiguracionException {
        Properties config = new Properties();

        config.setProperty("SERVER_PORT", String.valueOf(DEFAULT_PORT));
        config.setProperty("SSL_CERTIFICATE_ROUTE", "src/main/resources/certs/mi_clave.p12");
        config.setProperty("SSL_PASSWORD", "12345678");
        config.setProperty("SSL_KEYSTORE_TYPE", "PKCS12");

        // Guardar en la raíz
        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            config.store(fos, "Configuración por defecto de BioGuard");
            System.out.println("[INFO] Archivo creado en raíz: " + new File(CONFIG_FILE).getAbsolutePath());
        } catch (IOException e) {
            throw new ConfiguracionException("No se pudo crear config.properties: " + e.getMessage());
        }

        return config;
    }

    /**
     * Configura SSL buscando el certificado en múltiples ubicaciones.
     *
     * // Objetivo
     *    Establecer las propiedades del sistema necesarias para que
     *    el servidor pueda utilizar SSL/TLS en las comunicaciones.
     *
     * // Proceso
     *    1. Obtener ruta del certificado desde configuración
     *    2. Buscar el certificado en múltiples ubicaciones
     *    3. Configurar propiedades del sistema:
     *       - javax.net.ssl.keyStore
     *       - javax.net.ssl.keyStorePassword
     *       - javax.net.ssl.keyStoreType
     *
     * @param config Configuración con datos SSL
     * @throws ConfiguracionException Si no se encuentra el certificado
     */
    private static void configurarSSL(Properties config) throws ConfiguracionException {
        String certPath = config.getProperty("SSL_CERTIFICATE_ROUTE");
        String certPass = config.getProperty("SSL_PASSWORD");
        String ksType = config.getProperty("SSL_KEYSTORE_TYPE", "PKCS12");

        System.out.println("[DEBUG] Buscando certificado: " + certPath);

        File certFile = buscarCertificado(certPath);

        if (certFile == null || !certFile.exists()) {
            StringBuilder error = new StringBuilder("Certificado no encontrado.\nRutas buscadas:\n");

            // Mostrar todas las rutas que se buscaron
            String[] rutasBuscadas = {
                    certPath,
                    "src/main/resources/certs/mi_clave.p12",
                    "certs/mi_clave.p12",
                    "resources/certs/mi_clave.p12",
                    "../certs/mi_clave.p12",
                    System.getProperty("user.dir") + "/src/main/resources/certs/mi_clave.p12"
            };

            for (String ruta : rutasBuscadas) {
                error.append("  - ").append(ruta).append("\n");
            }

            throw new ConfiguracionException(error.toString());
        }

        System.setProperty("javax.net.ssl.keyStore", certFile.getAbsolutePath());
        System.setProperty("javax.net.ssl.keyStorePassword", certPass);
        System.setProperty("javax.net.ssl.keyStoreType", ksType);

        System.out.println("[INFO] SSL configurado correctamente");
        System.out.println("[INFO] Certificado: " + certFile.getAbsolutePath());
    }

    /**
     * Busca el certificado en múltiples ubicaciones.
     *
     * // Objetivo
     *    Localizar el archivo de certificado SSL en diferentes
     *    ubicaciones comunes para adaptarse a distintos entornos.
     *
     * // Rutas de búsqueda
     *    1. Ruta exacta especificada en configuración
     *    2. En resources (classpath)
     *    3. src/main/resources/certs/mi_clave.p12
     *    4. certs/mi_clave.p12 (raíz)
     *    5. Ruta absoluta desde user.dir
     *    6. Un nivel arriba (../certs)
     *
     * @param certPath Ruta original del certificado
     * @return Archivo del certificado o null si no existe
     */
    private static File buscarCertificado(String certPath) {
        // 1. Ruta exacta del properties
        File file = new File(certPath);
        if (file.exists()) {
            System.out.println("[DEBUG] Certificado encontrado en: " + file.getAbsolutePath());
            return file;
        }

        // 2. Buscar en resources
        try {
            java.net.URL resourceUrl = ServerMain.class.getClassLoader().getResource("certs/mi_clave.p12");
            if (resourceUrl != null) {
                file = new File(resourceUrl.getPath());
                if (file.exists()) {
                    System.out.println("[DEBUG] Certificado encontrado en resources: " + file.getAbsolutePath());
                    return file;
                }
            }
        } catch (Exception e) {
            // Ignorar
        }

        // 3. Buscar en src/main/resources/certs
        file = new File("src/main/resources/certs/mi_clave.p12");
        if (file.exists()) {
            System.out.println("[DEBUG] Certificado encontrado en: " + file.getAbsolutePath());
            return file;
        }

        // 4. Buscar en certs/ (raíz)
        file = new File("certs/mi_clave.p12");
        if (file.exists()) {
            System.out.println("[DEBUG] Certificado encontrado en: " + file.getAbsolutePath());
            return file;
        }

        // 5. Buscar con ruta absoluta desde user.dir
        String userDir = System.getProperty("user.dir");
        file = new File(userDir, "src/main/resources/certs/mi_clave.p12");
        if (file.exists()) {
            System.out.println("[DEBUG] Certificado encontrado en: " + file.getAbsolutePath());
            return file;
        }

        // 6. Buscar un nivel arriba
        file = new File("../certs/mi_clave.p12");
        if (file.exists()) {
            System.out.println("[DEBUG] Certificado encontrado en: " + file.getAbsolutePath());
            return file;
        }

        return null;
    }

    /**
     * Crea la estructura de directorios necesaria.
     *
     * // Objetivo
     *    Establecer la jerarquía de carpetas requerida por el sistema
     *    para el almacenamiento de datos, logs y certificados.
     *
     * // Directorios creados
     *    - data/               : Raíz de datos
     *    - data/pacientes/     : Archivos CSV de pacientes
     *    - data/virus/         : Archivos FASTA de virus
     *    - data/muestras/      : Muestras de ADN por paciente
     *    - data/reportes/      : Reportes generados
     *    - logs/               : Archivos de log
     *    - src/main/resources/certs/ : Certificados SSL
     *
     * // Archivos iniciales
     *    - data/pacientes/pacientes.csv con cabecera
     */
    private static void crearEstructuraDirectorios() {
        String[] directorios = {
                "data",
                "data/pacientes",
                "data/virus",
                "data/muestras",
                "data/reportes",
                "logs",
                "src/main/resources/certs"
        };

        for (String dir : directorios) {
            File folder = new File(dir);
            if (!folder.exists() && folder.mkdirs()) {
                System.out.println("[INFO] Directorio creado: " + dir);
            }
        }

        // Crear pacientes.csv si no existe
        File csvFile = new File("data/pacientes/pacientes.csv");
        if (!csvFile.exists()) {
            try {
                Files.writeString(csvFile.toPath(), "documento,nombre,apellido,edad,correo,genero,ciudad,pais\n");
                System.out.println("[INFO] Archivo pacientes.csv creado");
            } catch (IOException e) {
                System.err.println("[WARN] No se pudo crear pacientes.csv: " + e.getMessage());
            }
        }
    }
}