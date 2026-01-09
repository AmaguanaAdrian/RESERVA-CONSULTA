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
public class ReservaDAO {
    public List<Reserva> listarReservasActivas() {
        List<Reserva> lista = new ArrayList<>();

        String sql = """
        SELECT r.id_reserva, r.id_estudiante, r.id_libro, 
               r.fecha_reserva, r.fecha_limite, r.estado
        FROM reservas r
        WHERE r.estado = 'RESERVADA'
        ORDER BY r.fecha_reserva DESC
    """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Reserva r = new Reserva();
                r.setIdReserva(rs.getInt("id_reserva"));
                r.setIdEstudiante(rs.getInt("id_estudiante"));
                r.setIdLibro(rs.getInt("id_libro"));

                // Convertir java.sql.Timestamp a LocalDateTime
                java.sql.Timestamp timestampReserva = rs.getTimestamp("fecha_reserva");
                java.sql.Timestamp timestampLimite = rs.getTimestamp("fecha_limite");

                if (timestampReserva != null) {
                    r.setFechaReserva(timestampReserva.toLocalDateTime());
                }
                if (timestampLimite != null) {
                    r.setFechaLimite(timestampLimite.toLocalDateTime());
                }

                r.setEstado(Reserva.EstadoReserva.valueOf(rs.getString("estado")));
                lista.add(r);
            }

        } catch (SQLException ex) {
            System.err.println("Error al listar reservas activas: " + ex.getMessage());
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
}
