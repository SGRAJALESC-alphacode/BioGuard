package org.BioGuard;

public class Paciente {
    private String documento;
    private String nombre;
    private String apellido;
    private int edad;
    private String correo;
    private String genero;
    private String ciudad;
    private String pais;

    // Constructor vacío para Gson
    public Paciente() {}

    public Paciente(String documento, String nombre, String apellido, int edad,
                    String correo, String genero, String ciudad, String pais) {
        this.documento = documento;
        this.nombre = nombre;
        this.apellido = apellido;
        this.edad = edad;
        this.correo = correo;
        this.genero = genero;
        this.ciudad = ciudad;
        this.pais = pais;
    }

    // Getters y Setters
    public String getDocumento() { return documento; }
    public void setDocumento(String documento) { this.documento = documento; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }

    // Método para convertir a CSV
    public String toCsvString() {
        return String.join(",",
                documento,
                nombre,
                apellido,
                String.valueOf(edad),
                correo,
                genero,
                ciudad,
                pais
        );
    }

    // Método estático para crear desde CSV
    public static Paciente fromCsvString(String linea) {
        String[] partes = linea.split(",");
        if (partes.length >= 8) {
            Paciente p = new Paciente();
            p.setDocumento(partes[0]);
            p.setNombre(partes[1]);
            p.setApellido(partes[2]);
            p.setEdad(Integer.parseInt(partes[3]));
            p.setCorreo(partes[4]);
            p.setGenero(partes[5]);
            p.setCiudad(partes[6]);
            p.setPais(partes[7]);
            return p;
        }
        return null;
    }
}