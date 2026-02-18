# DOCUMENTACIÓN DEL PROYECTO BIOGUARD (INLINE EN CÓDIGO JAVA)

Este documento es una guía rápida de dónde encontrar la documentación del proyecto. Toda la documentación está incrustada como comentarios de cabecera dentro de los archivos Java.

## SERVIDOR (BioGuard-Server)

### Entry Point y Configuración
- **ServerMain.java** (punto de entrada canónico)
  - Objetivo: arrancar servidor, cargar certificados SSL, inicializar directorios

- **TCPServer.java**
  - Objetivo: escuchar conexiones TLS/SSL y delegar a ClientHandler

### Procesamiento de Requests
- **ClientHandler.java**
  - Objetivo: leer comandos en formato COMANDO|PAYLOAD, procesar y enviar respuesta
  - Atributos: socket, gson, pacienteService, virusService, diagnosticoService

### Servicios de Negocio
- **PacienteService.java**
  - Objetivo: registrar, consultar y listar pacientes (persistencia CSV)
  - Responsabilidades: validar, guardar, consultar en data/pacientes/pacientes.csv

- **VirusService.java**
  - Objetivo: guardar y cargar virus (persistencia FASTA)
  - Responsabilidades: validar, guardar en data/virus/, cargar catálogo

- **DiagnosticoService.java**
  - Objetivo: diagnosticar muestras, comparar con catálogo viral, generar reportes
  - Responsabilidades: validar secuencia, guardar muestra, comparar, generar CSV

### Persistencia Adicional
- **PatientCRUD.java**
  - Objetivo: persistencia JSON de pacientes (data/patients/patient_<documento>.json)

### Modelos
- **Paciente.java**
  - Objetivo: representar paciente del sistema
  - Atributos: documento, nombre, apellido, edad, correo, genero, ciudad, pais

- **Diagnostico.java**
  - Objetivo: representar hallazgo de virus en una muestra
  - Atributos: virus, nivel, inicio, fin (posiciones en la secuencia)

## CLIENTE (BioGuard-Client)

### Entry Point
- **Main.java**
  - Objetivo: cliente interactivo de consola para registrar pacientes, cargar virus, diagnosticar
  - Comportamiento: menú interactivo, serialización con Gson, formato COMANDO|payload

### Comunicación
- **TCPClient.java**
  - Objetivo: capa SSL/TLS para conectar al servidor y enviar/recibir mensajes
  - Atributos: serverAddress, serverPort, clientSocket, dataInputStream, dataOutputStream

### Utilidades
- **FastaReader.java**
  - Objetivo: leer y parsear archivos FASTA (virus y muestras)
  - Funciones: leerFasta(), parsearHeaderVirus(), parsearHeaderMuestra()

### Modelos
- **Patient.java**
  - Objetivo: modelo del paciente en el cliente
  - Atributos: documento, nombre, apellido, edad, correo, genero, ciudad, pais

## CÓMO LEER LA DOCUMENTACIÓN

1. Abre cada archivo `.java` mencionado arriba
2. Lee el bloque de comentario al inicio del archivo (después de `package` e `import`)
3. Encontrarás secciones como:
   - `// Objetivo` — descripción del propósito de la clase
   - `// Atributos` — lista de campos y su significado
   - `// Responsabilidades` (en servicios) — qué tareas realiza
   - `// Comportamiento` (en clases de lógica) — cómo funciona el flujo

Ejemplo de formato:
```java
/*
 * // Objetivo
 *    Descripción clara del propósito
 *
 * // Atributos
 *    campo1 : tipo — descripción
 *    campo2 : tipo — descripción
 */
```

## PROTOCOLO DE COMUNICACIÓN

Todos los comandos cliente-servidor siguen el formato:
```
COMANDO|payload
```

Ejemplos:
- `REGISTRAR_PACIENTE|{"documento":"123","nombre":"Juan",...}`
- `CONSULTAR_PACIENTE|123`
- `CARGAR_VIRUS|{"nombre":"Ebola","nivel":"Altamente Infeccioso",...}`
- `DIAGNOSTICAR|{"documento":"123","fecha_muestra":"...","secuencia":"ATCG..."}`
- `REPORTE_ALTO_RIESGO|`
- `REPORTE_MUTACIONES|123`

## PERSISTENCIA

- **Pacientes**: `data/pacientes/pacientes.csv` (PacienteService)
- **Virus**: `data/virus/*.fasta` (VirusService)
- **Muestras**: `data/muestras/<documento>/*.fasta` (DiagnosticoService)
- **Diagnósticos**: `data/muestras/<documento>/diagnostico_*.csv` (DiagnosticoService)
- **Reportes**: `data/reportes/*.csv` (DiagnosticoService)
- **JSON por paciente**: `data/patients/patient_<documento>.json` (PatientCRUD)

## PRÓXIMOS PASOS

1. Compila con Maven: `mvn -DskipTests package`
2. Ejecuta servidor: `java -cp ... org.BioGuard.ServerMain`
3. Ejecuta cliente: `java -cp ... org.BioGuard.Main`
4. Sigue el menú interactivo del cliente

¡Todo está documentado inline en el código Java!

