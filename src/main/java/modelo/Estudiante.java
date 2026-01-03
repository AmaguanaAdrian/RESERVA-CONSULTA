package modelo;

public class Estudiante {
    private int idEstudiante;
    private int idUsuario;
    private String nombres;
    private String apellidos;
    private String correo;
    private String cedula; // Para mostrar en tablas

    public Estudiante() {}
    // Getters y Setters...
    public int getIdEstudiante() { return idEstudiante; }
    public void setIdEstudiante(int id) { this.idEstudiante = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String n) { this.nombres = n; }
    public String getCorreo() { return correo; }
    public void setCorreo(String c) { this.correo = c; }
}