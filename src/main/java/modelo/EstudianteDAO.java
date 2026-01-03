package modelo;
import BDD.Config;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EstudianteDAO {
    public boolean registrar(String cedula, String pass, String nom, String ape, String correo) {
        String sql = "{CALL sp_registrar_estudiante(?, ?, ?, ?, ?)}";
        try (Connection con = Config.getConexion(); CallableStatement cs = con.prepareCall(sql)) {
            cs.setString(1, cedula);
            cs.setString(2, pass);
            cs.setString(3, nom);
            cs.setString(4, ape);
            cs.setString(5, correo);
            return cs.execute() || cs.getUpdateCount() > 0;
        } catch (SQLException e) { return false; }
    }

    public List<Estudiante> listar() {
        List<Estudiante> lista = new ArrayList<>();
        String sql = "SELECT e.*, u.cedula FROM estudiantes e JOIN usuarios u ON e.id_usuario = u.id_usuario";
        try (Connection con = Config.getConexion(); Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Estudiante e = new Estudiante();
                e.setIdEstudiante(rs.getInt("id_estudiante"));
                e.setNombres(rs.getString("nombres"));
                e.setCorreo(rs.getString("correo"));
                lista.add(e);
            }
        } catch (SQLException ex) { }
        return lista;
    }
}