/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import  modelo.Usuario;
/**
 *
 * @author amagu
 */

public class Sesion {
    private static Sesion instancia;
    private Usuario usuario;
    private Integer idEstudiante;

    private Sesion() {}

    public static Sesion getInstancia() {
        if (instancia == null) {
            instancia = new Sesion();
        }
        return instancia;
    }

    // Getters y Setters
    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Integer getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(Integer idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public void cerrarSesion() {
        this.usuario = null;
        this.idEstudiante = null;
        instancia = null;
    }

    public boolean esEstudiante() {
        return usuario != null && 
               "ESTUDIANTE".equals(usuario.getRol()) && 
               idEstudiante != null;
    }
}
