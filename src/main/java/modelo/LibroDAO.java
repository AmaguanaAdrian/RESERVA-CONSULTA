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

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

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

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

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

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al eliminar libro: " + e.getMessage());
            return false;
        }
    }

    public List<LibroCompleto> listarLibrosDisponibles(int limite) {
        List<LibroCompleto> libros = new ArrayList<>();
        String sql = "SELECT l.id_libro, l.titulo, l.cantidad_disponible, "
                + "a.nombre_autor, g.nombre_genero "
                + "FROM libros l "
                + "JOIN autores a ON l.id_autor = a.id_autor "
                + "JOIN generos g ON l.id_genero = g.id_genero "
                + "WHERE l.cantidad_disponible > 0 "
                + "ORDER BY l.titulo "
                + "LIMIT ?";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limite);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LibroCompleto libro = new LibroCompleto(
                        rs.getInt("id_libro"),
                        rs.getString("titulo"),
                        rs.getInt("cantidad_disponible"),
                        0, // idAutor (no necesario para mostrar)
                        0, // idGenero (no necesario para mostrar)
                        rs.getString("nombre_autor"),
                        rs.getString("nombre_genero")
                );
                libros.add(libro);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar libros disponibles: " + e.getMessage());
        }

        return libros;
    }

    public List<LibroCompleto> buscarLibros(String termino, int limite) {
        List<LibroCompleto> libros = new ArrayList<>();
        String sql = "SELECT l.id_libro, l.titulo, l.cantidad_disponible, "
                + "a.nombre_autor, g.nombre_genero "
                + "FROM libros l "
                + "JOIN autores a ON l.id_autor = a.id_autor "
                + "JOIN generos g ON l.id_genero = g.id_genero "
                + "WHERE l.cantidad_disponible > 0 "
                + "AND (l.titulo LIKE ? OR a.nombre_autor LIKE ? OR g.nombre_genero LIKE ?) "
                + "ORDER BY l.titulo "
                + "LIMIT ?";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String terminoBusqueda = "%" + termino + "%";
            pstmt.setString(1, terminoBusqueda);
            pstmt.setString(2, terminoBusqueda);
            pstmt.setString(3, terminoBusqueda);
            pstmt.setInt(4, limite);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                LibroCompleto libro = new LibroCompleto(
                        rs.getInt("id_libro"),
                        rs.getString("titulo"),
                        rs.getInt("cantidad_disponible"),
                        0,
                        0,
                        rs.getString("nombre_autor"),
                        rs.getString("nombre_genero")
                );
                libros.add(libro);
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar libros: " + e.getMessage());
        }

        return libros;
    }
    // En tu LibroDAO, agrega este método para obtener TODOS los libros

    public List<LibroCompleto> obtenerCatalogoCompleto() {
        List<LibroCompleto> libros = new ArrayList<>();

        // Esta consulta obtiene TODOS los libros, no solo los disponibles
        String sql = "SELECT l.id_libro, l.titulo, l.cantidad_disponible, "
                + "a.nombre_autor, g.nombre_genero "
                + "FROM libros l "
                + "JOIN autores a ON l.id_autor = a.id_autor "
                + "JOIN generos g ON l.id_genero = g.id_genero "
                + "ORDER BY l.titulo";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                LibroCompleto libro = new LibroCompleto();
                libro.setIdLibro(rs.getInt("id_libro"));
                libro.setTitulo(rs.getString("titulo"));
                libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                libro.setNombreAutor(rs.getString("nombre_autor"));
                libro.setNombreGenero(rs.getString("nombre_genero"));

                libros.add(libro);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener catálogo completo: " + e.getMessage());
            e.printStackTrace();
        }

        return libros;
    }
}
