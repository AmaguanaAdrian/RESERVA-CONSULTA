/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author amagu
 */
public class Usuario {

    // Atributos privados
    private int idUsuario;
    private String cedula;
    private String contrasena;
    private String nombres;
    private String apellidos;
    private String correo;
    private String rol;
    private String estado;
    private int reservasHoy;
    private Integer idEstudiante;
    private String carrera;

    // 1. Constructor vacío (necesario para muchos frameworks como Hibernate o Spring)
    public Usuario() {
    }

    // 2. Constructor con todos los parámetros
    public Usuario(int idUsuario, String cedula, String contrasena, String nombres,
            String apellidos, String correo, String rol, String estado) {
        this.idUsuario = idUsuario;
        this.cedula = cedula;
        this.contrasena = contrasena;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.rol = rol;
        this.estado = estado;
    }

    // 3. Métodos Getter y Setter
    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
        public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getCarrera() {
        return carrera;
    }

    public void setCarrera(String carrera) {
        this.carrera = carrera;
    }

    public int getReservasHoy() {
        return reservasHoy;
    }

    public void setReservasHoy(int reservasHoy) {
        this.reservasHoy = reservasHoy;
    }

    // 4. Método toString (para imprimir el objeto fácilmente)
    @Override
    public String toString() {
        return "Usuario{"
                + "idUsuario=" + idUsuario
                + ", cedula='" + cedula + '\''
                + ", nombres='" + nombres + '\''
                + ", apellidos='" + apellidos + '\''
                + ", correo='" + correo + '\''
                + ", rol='" + rol + '\''
                + ", estado='" + estado + '\''
                + '}';
    }
}
