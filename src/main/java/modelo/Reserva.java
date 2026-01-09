/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author amagu
 */
import java.time.LocalDateTime;

public class Reserva {

    private int idReserva;
    private int idEstudiante;
    private int idLibro;
    private LocalDateTime fechaReserva;
    private LocalDateTime fechaLimite;
    private EstadoReserva estado;

    // Enumeración para el estado
    public enum EstadoReserva {
        RESERVADA, CANCELADA
    }

    // Constructor vacío
    public Reserva() {
    }

    // Constructor con parámetros
    public Reserva(int idReserva, int idEstudiante, int idLibro, LocalDateTime fechaReserva, LocalDateTime fechaLimite, EstadoReserva estado) {
        this.idReserva = idReserva;
        this.idEstudiante = idEstudiante;
        this.idLibro = idLibro;
        this.fechaReserva = fechaReserva;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public LocalDateTime getFechaReserva() {
        return fechaReserva;
    }

    public void setFechaReserva(LocalDateTime fechaReserva) {
        this.fechaReserva = fechaReserva;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public EstadoReserva getEstado() {
        return estado;
    }

    public void setEstado(EstadoReserva estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Reserva{"
                + "idReserva=" + idReserva
                + ", idEstudiante=" + idEstudiante
                + ", idLibro=" + idLibro
                + ", fechaReserva=" + fechaReserva
                + ", fechaLimite=" + fechaLimite
                + ", estado=" + estado
                + '}';
    }
}
