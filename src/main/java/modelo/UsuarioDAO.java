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
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author amagu
 */
public class UsuarioDAO {

    // ================= AGREGAR BIBLIOTECARIO =================
    public boolean agregarBibliotecario(Usuario u) {

        String sqlUsuario = """
            INSERT INTO usuarios
            (cedula, contrasena, nombres, apellidos, correo, rol, estado)
            VALUES (?, ?, ?, ?, ?, 'BIBLIOTECARIO', ?)
        """;

        String sqlBibliotecario = """
            INSERT INTO bibliotecarios (id_usuario)
            VALUES (?)
        """;

        try (Connection con = Config.getConexion()) {

            con.setAutoCommit(false); // 🔐 transacción

            // 1️⃣ insertar usuario
            PreparedStatement psUsuario
                    = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);

            psUsuario.setString(1, u.getCedula());
            psUsuario.setString(2, u.getContrasena());
            psUsuario.setString(3, u.getNombres());
            psUsuario.setString(4, u.getApellidos());
            psUsuario.setString(5, u.getCorreo());
            psUsuario.setString(6, u.getEstado());

            psUsuario.executeUpdate();

            ResultSet rs = psUsuario.getGeneratedKeys();
            if (!rs.next()) {
                con.rollback();
                return false;
            }

            int idUsuario = rs.getInt(1);

            // 2️⃣ insertar bibliotecario
            PreparedStatement psBiblio = con.prepareStatement(sqlBibliotecario);
            psBiblio.setInt(1, idUsuario);
            psBiblio.executeUpdate();

            con.commit();
            return true;

        } catch (SQLIntegrityConstraintViolationException ex) {

            // 🎯 ERRORES CONTROLADOS
            if (ex.getMessage().contains("cedula")) {
                JOptionPane.showMessageDialog(null,
                        "La cédula ya está registrada",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else if (ex.getMessage().contains("correo")) {
                JOptionPane.showMessageDialog(null,
                        "El correo ya está registrado",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null,
                        "Datos duplicados",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
            return false;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al registrar bibliotecario:\n" + ex.getMessage(),
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ================= LISTAR =================
    public List<Usuario> listarBibliotecarios() {

        List<Usuario> lista = new ArrayList<>();

        String sql = """
            SELECT id_usuario, cedula, rol, estado
            FROM usuarios
            WHERE rol = 'BIBLIOTECARIO'
        """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setCedula(rs.getString("cedula"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getString("estado"));
                lista.add(u);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al listar bibliotecarios",
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }

        return lista;
    }

    // ================= EDITAR =================
    public boolean editarBibliotecario(Usuario u) {

        String sql;

        if (u.getContrasena() != null && !u.getContrasena().isEmpty()) {
            sql = """
                UPDATE usuarios
                SET contrasena = ?, estado = ?
                WHERE id_usuario = ?
            """;
        } else {
            sql = """
                UPDATE usuarios
                SET estado = ?
                WHERE id_usuario = ?
            """;
        }

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            if (u.getContrasena() != null && !u.getContrasena().isEmpty()) {
                ps.setString(1, u.getContrasena());
                ps.setString(2, u.getEstado());
                ps.setInt(3, u.getIdUsuario());
            } else {
                ps.setString(1, u.getEstado());
                ps.setInt(2, u.getIdUsuario());
            }

            ps.executeUpdate();
            return true;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al actualizar bibliotecario",
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    // ================= ELIMINAR =================
    public boolean eliminarBibliotecario(int idUsuario) {

        String sql = """
            UPDATE usuarios
            SET estado = 'INACTIVO'
            WHERE id_usuario = ?
        """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.executeUpdate();
            return true;

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "No se pudo eliminar el bibliotecario",
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

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

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null,
                    "Error al iniciar sesión",
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }

        return null; // usuario no válido
    }

}
