/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import BDD.Config;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;

/**
 *
 * @author amagu
 */
public class ReservaDAO {

    public List<Object[]> listarReservasActivas() {
        List<Object[]> lista = new ArrayList<>();

        String sql = """
        SELECT 
            id_reserva,
            CONCAT(nombres, ' ', apellidos) AS estudiante,
            titulo,
            fecha_reserva,
            fecha_limite
        FROM vista_reservas_activas
        ORDER BY fecha_reserva DESC
    """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(new Object[]{
                    rs.getInt("id_reserva"),
                    rs.getString("estudiante"),
                    rs.getString("titulo"),
                    rs.getTimestamp("fecha_reserva").toLocalDateTime(),
                    rs.getTimestamp("fecha_limite").toLocalDateTime(),
                    "RESERVADA"
                });
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return lista;
    }

// ================= OBTENER NOMBRE DEL ESTUDIANTE =================
    public String obtenerNombreEstudiante(int idEstudiante) {
        String sql = "SELECT nombres, apellidos FROM usuarios WHERE id_usuario = ?";

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("nombres") + " " + rs.getString("apellidos");
            }

        } catch (SQLException ex) {
            System.err.println("Error al obtener nombre del estudiante: " + ex.getMessage());
        }

        return "Desconocido";
    }

// ================= OBTENER TÍTULO DEL LIBRO =================
    public String obtenerTituloLibro(int idLibro) {
        String sql = "SELECT titulo FROM libros WHERE id_libro = ?";

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("titulo");
            }

        } catch (SQLException ex) {
            System.err.println("Error al obtener título del libro: " + ex.getMessage());
        }

        return "Desconocido";
    }

    public boolean registrarReserva(int idEstudiante, int idLibro) {
        String sql = "{CALL sp_registrar_reserva(?, ?)}";

        try (Connection conn = Config.getConexion(); CallableStatement cstmt = conn.prepareCall(sql)) {

            cstmt.setInt(1, idEstudiante);
            cstmt.setInt(2, idLibro);
            cstmt.execute();

            return true;

        } catch (SQLException e) {
            manejarErrorReserva(e);
            return false;
        }
    }

    public int contarReservasHoy(int idEstudiante) {
        String sql = "SELECT COUNT(*) as total_reservas "
                + "FROM reservas "
                + "WHERE id_estudiante = ? "
                + "AND estado = 'RESERVADA' "
                + "AND DATE(fecha_reserva) = CURDATE()";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEstudiante);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total_reservas");
            }

        } catch (SQLException e) {
            System.err.println("Error al contar reservas: " + e.getMessage());
        }

        return 0;
    }

    private void manejarErrorReserva(SQLException e) {
        String mensajeError = e.getMessage();

        if (mensajeError.contains("No hay stock")) {
            JOptionPane.showMessageDialog(null,
                    "❌ No hay ejemplares disponibles de este libro.",
                    "Stock agotado", JOptionPane.WARNING_MESSAGE);
        } else if (mensajeError.contains("Límite diario")) {
            JOptionPane.showMessageDialog(null,
                    "❌ Has alcanzado el límite de 3 reservas diarias.",
                    "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
        } else if (mensajeError.contains("uq_reserva_activa")) {
            JOptionPane.showMessageDialog(null,
                    "❌ Ya tienes una reserva activa de este libro.",
                    "Reserva duplicada", JOptionPane.WARNING_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "❌ Error al reservar: " + mensajeError,
                    "Error de base de datos", JOptionPane.ERROR_MESSAGE);
        }
    }

    public List<Map<String, Object>> listarReservasPorEstudiante(int idEstudiante) {
        List<Map<String, Object>> reservas = new ArrayList<>();

        String sql = "SELECT r.id_reserva, l.titulo, r.fecha_reserva, r.fecha_limite, r.estado "
                + "FROM reservas r "
                + "JOIN libros l ON r.id_libro = l.id_libro "
                + "WHERE r.id_estudiante = ? "
                + "ORDER BY r.fecha_reserva DESC, r.estado";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEstudiante);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> reserva = new HashMap<>();
                reserva.put("id_reserva", rs.getInt("id_reserva"));
                reserva.put("titulo", rs.getString("titulo"));
                reserva.put("fecha_reserva", rs.getTimestamp("fecha_reserva"));
                reserva.put("fecha_limite", rs.getTimestamp("fecha_limite"));
                reserva.put("estado", rs.getString("estado"));

                reservas.add(reserva);
            }

        } catch (SQLException e) {
            System.err.println("Error al listar reservas por estudiante: " + e.getMessage());
            e.printStackTrace();
        }

        return reservas;
    }

    public int contarReservasEstudianteHoy(int idEstudiante) {
        // Usamos UPPER para ignorar mayúsculas/minúsculas y comparamos solo la parte DATE
        String sql = "SELECT COUNT(*) FROM reservas "
                + "WHERE id_estudiante = ? "
                + "AND UPPER(estado) = 'RESERVADA' "
                + "AND DATE(fecha_reserva) = CURRENT_DATE";

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                int resultado = rs.getInt(1);
                System.out.println("DEBUG: Reservas encontradas para hoy: " + resultado); // Revisa esto en tu consola
                return resultado;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean tieneReservaActiva(int idEstudiante, int idLibro) {
        String sql = "SELECT COUNT(*) as total FROM reservas "
                + "WHERE id_estudiante = ? AND id_libro = ? AND estado = 'RESERVADA'";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idEstudiante);
            pstmt.setInt(2, idLibro);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar reserva activa: " + e.getMessage());
            e.printStackTrace();
        }

        return false;
    }
}
