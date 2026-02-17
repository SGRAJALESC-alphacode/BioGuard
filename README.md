
## Requerimientos del Sistema

### Funcionales Implementados

**Gestión de Pacientes y Catálogo Viral**
- Registro de pacientes en archivo pacientes.csv
- Validación de documentos duplicados
- Consulta de información de pacientes
- Carga de virus desde archivos FASTA
- Almacenamiento persistente de virus

**Diagnóstico y Análisis de ADN**
- Procesamiento de muestras FASTA por paciente
- Organización de muestras en carpetas individuales
- Detección de virus en secuencias de ADN
- Generación de CSV con posiciones de virus detectados
- Reporte de pacientes de alto riesgo (más de 3 virus altamente infecciosos)
- Reporte de mutaciones comparando muestras históricas

### Técnicos Implementados
- Comunicación mediante Sockets TCP/IP con certificados SSL
- Concurrencia con hilos nativos de Java
- Lógica de negocio centralizada en servidor
- Persistencia exclusiva en archivos CSV y FASTA
- Excepciones personalizadas
- Documentación JavaDoc

## Guía de Instalación

### Requisitos Previos
- **Amazon Corretto 25** o superior
- Java JDK 25 (Corretto)
- Certificados SSL generados
- Git (opcional)

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/SGRAJALESC-alphacode/BioGuard.git
   cd BioGuard
