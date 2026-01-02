/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import BDD.Config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author amagu
 */
public class UsuarioDAO {

    public String obtenerRol(String cedula, String contrasena) {

        String sql = """
        SELECT rol
        FROM usuarios
        WHERE cedula = ?
          AND contrasena = ?
          AND estado = 'ACTIVO'
    """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);
            ps.setString(2, contrasena);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("rol");
            }

        } catch (SQLException e) {
            System.out.println("Error login: " + e.getMessage());
        }

        return null;
    }

    public boolean agregarBibliotecario(Usuario u) {
        String sql = """
            INSERT INTO usuarios (cedula, contrasena, rol, estado)
            VALUES (?, ?, 'BIBLIOTECARIO', 'ACTIVO')
        """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getCedula());
            ps.setString(2, u.getContrasena());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error agregar bibliotecario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listarBibliotecarios() {
        List<Usuario> lista = new ArrayList<>();

        String sql = """
            SELECT * FROM usuarios
            WHERE rol = 'BIBLIOTECARIO'
        """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setCedula(rs.getString("cedula"));
                u.setEstado(rs.getString("estado"));
                lista.add(u);
            }

        } catch (SQLException e) {
            System.out.println("Error listar bibliotecarios: " + e.getMessage());
        }

        return lista;
    }

    public boolean editarBibliotecario(Usuario u) {
        String sql = """
            UPDATE usuarios
            SET cedula = ?, estado = ?
            WHERE id_usuario = ? AND rol = 'BIBLIOTECARIO'
        """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getCedula());
            ps.setString(2, u.getEstado());
            ps.setInt(3, u.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error editar bibliotecario: " + e.getMessage());
            return false;
        }
    }

    public boolean eliminarBibliotecario(int idUsuario) {
        String sql = """
            DELETE FROM usuarios
            WHERE id_usuario = ? AND rol = 'BIBLIOTECARIO'
        """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error eliminar bibliotecario: " + e.getMessage());
            return false;
        }
    }
}
