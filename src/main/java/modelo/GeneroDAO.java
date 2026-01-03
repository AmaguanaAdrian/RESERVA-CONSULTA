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
public class GeneroDAO {

    public List<Genero> listarGeneros() {
        List<Genero> lista = new ArrayList<>();
        String sql = "SELECT * FROM generos ORDER BY nombre_genero";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Genero g = new Genero();
                g.setIdGenero(rs.getInt("id_genero"));
                g.setNombreGenero(rs.getString("nombre_genero"));
                lista.add(g);
            }

        } catch (SQLException e) {
            System.err.println("Error listar géneros: " + e.getMessage());
        }

        return lista;
    }

    public boolean agregarGenero(String nombre) {
        String sql = "INSERT INTO generos(nombre_genero) VALUES (?)";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error agregar género: " + e.getMessage());
            return false;
        }
    }

    public boolean editarGenero(int id, String nombre) {
        String sql = "UPDATE generos SET nombre_genero=? WHERE id_genero=?";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error editar género: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarGenero(int id) {
        String sql = "DELETE FROM generos WHERE id_genero=?";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("No se puede eliminar género (está en uso)");
            return false;
        }
    }
}
