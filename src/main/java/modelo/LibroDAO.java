/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import BDD.Config;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author amagu
 */
public class LibroDAO {
    
    public List<Libro> listarLibros() {

        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                libro.setIdAutor(rs.getInt("id_autor"));
                libro.setIdGenero(rs.getInt("id_genero"));

                lista.add(libro);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar libros: " + e.getMessage());
        }

        return lista;
    }

    public boolean agregarLibro(Libro libro) {

        String sql = "INSERT INTO libros (titulo, cantidad_disponible, id_autor, id_genero) VALUES (?,?,?,?)";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getCantidadDisponible());
            ps.setInt(3, libro.getIdAutor());
            ps.setInt(4, libro.getIdGenero());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al agregar libro: " + e.getMessage());
            return false;
        }
    }

    public boolean editarLibro(Libro libro) {

        String sql = """
            UPDATE libros
            SET titulo = ?, cantidad_disponible = ?, id_autor = ?, id_genero = ?
            WHERE id_libro = ?
        """;

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setInt(2, libro.getCantidadDisponible());
            ps.setInt(3, libro.getIdAutor());
            ps.setInt(4, libro.getIdGenero());
            ps.setInt(5, libro.getIdLibro());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al editar libro: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarLibro(int idLibro) {

        String sql = "DELETE FROM libros WHERE id_libro = ?";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }
}
