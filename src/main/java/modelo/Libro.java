package modelo;

public class Libro {
    private int idLibro;
    private String titulo;
    private int cantidad;
    private String autor; // Nombre del autor
    private String genero; // Nombre del género

    public Libro() {}
    // Getters y Setters
    public int getIdLibro() { return idLibro; }
    public void setIdLibro(int idLibro) { this.idLibro = idLibro; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }
    public String getGenero() { return genero; }
    public void setGenero(String genero) { this.genero = genero; }
}