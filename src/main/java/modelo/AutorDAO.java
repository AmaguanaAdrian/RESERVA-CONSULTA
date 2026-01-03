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


public class AutorDAO {

    public List<Autor> listarAutores() {
        List<Autor> lista = new ArrayList<>();
        String sql = "SELECT * FROM autores ORDER BY nombre_autor";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Autor a = new Autor();
                a.setIdAutor(rs.getInt("id_autor"));
                a.setNombreAutor(rs.getString("nombre_autor"));
                lista.add(a);
            }

        } catch (SQLException e) {
            System.err.println("Error listar autores: " + e.getMessage());
        }

        return lista;
    }

    public boolean agregarAutor(String nombre) {
        String sql = "INSERT INTO autores(nombre_autor) VALUES (?)";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error agregar autor: " + e.getMessage());
            return false;
        }
    }

    public boolean editarAutor(int id, String nombre) {
        String sql = "UPDATE autores SET nombre_autor=? WHERE id_autor=?";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, nombre);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error editar autor: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarAutor(int id) {
        String sql = "DELETE FROM autores WHERE id_autor=?";

        try (Connection con = Config.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("No se puede eliminar autor (está en uso)");
            return false;
        }
    }
}
