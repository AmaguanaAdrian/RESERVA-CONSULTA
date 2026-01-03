package modelo;
import BDD.Config;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReservaDAO {
    
    public String realizarReserva(int idEstudiante, int idLibro) {
        String sql = "{CALL sp_crear_reserva(?, ?)}";
        try (Connection con = Config.getConexion(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setInt(1, idEstudiante);
            cs.setInt(2, idLibro);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) return rs.getString("mensaje");
        } catch (SQLException e) {
            return "Error: " + e.getMessage();
        }
        return "Error desconocido";
    }

    public boolean cancelarReserva(int idReserva) {
        String sql = "UPDATE reservas SET estado = 'CANCELADA' WHERE id_reserva = ?";
        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idReserva);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Reserva> listarPorEstudiante(int idUsuario) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    public List<Reserva> listarTodas() {
    List<Reserva> lista = new ArrayList<>();
    String sql = "SELECT r.id_reserva, l.titulo, r.fecha_reserva, r.estado FROM reservas r JOIN libros l ON r.id_libro = l.id_libro";
    // ... mismo proceso que el anterior pero sin el WHERE ...
    return lista;
}
}