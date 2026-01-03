package modelo;
import BDD.Config;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {
    // Para llenar la tabla de Catálogo
    public List<Libro> listarCatalogo() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM vista_libros"; // Usamos la vista que creamos
        try (Connection con = Config.getConexion(); 
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Libro l = new Libro();
                l.setIdLibro(rs.getInt("id_libro"));
                l.setTitulo(rs.getString("titulo"));
                l.setCantidad(rs.getInt("cantidad_disponible"));
                l.setAutor(rs.getString("nombre_autor"));
                l.setGenero(rs.getString("nombre_genero"));
                lista.add(l);
            }
        } catch (SQLException e) { System.out.println("Error: " + e.getMessage()); }
        return lista;
    }

    public boolean eliminarLibro(int id) {
        String sql = "DELETE FROM libros WHERE id_libro = ?";
        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }
}