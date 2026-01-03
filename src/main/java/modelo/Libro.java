/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author amagu
 */
public class Libro {

    private int idLibro;
    private String titulo;
    private int cantidadDisponible;
    private int idAutor;
    private int idGenero;

    public Libro() {
    }

    public Libro(int idLibro, String titulo, int cantidadDisponible, int idAutor, int idGenero) {
        this.idLibro = idLibro;
        this.titulo = titulo;
        this.cantidadDisponible = cantidadDisponible;
        this.idAutor = idAutor;
        this.idGenero = idGenero;
    }

    // GETTERS Y SETTERS

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public int getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(int idGenero) {
        this.idGenero = idGenero;
    }
}

