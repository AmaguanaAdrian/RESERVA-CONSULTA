/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author amagu
 */

public class LibroCompleto extends Libro {
    private String nombreAutor;
    private String nombreGenero;

    public LibroCompleto() {
    }

    public LibroCompleto(int idLibro, String titulo, int cantidadDisponible, 
                         int idAutor, int idGenero, String nombreAutor, String nombreGenero) {
        super(idLibro, titulo, cantidadDisponible, idAutor, idGenero);
        this.nombreAutor = nombreAutor;
        this.nombreGenero = nombreGenero;
    }

    // Getters y Setters
    public String getNombreAutor() {
        return nombreAutor;
    }

    public void setNombreAutor(String nombreAutor) {
        this.nombreAutor = nombreAutor;
    }

    public String getNombreGenero() {
        return nombreGenero;
    }

    public void setNombreGenero(String nombreGenero) {
        this.nombreGenero = nombreGenero;
    }
}
