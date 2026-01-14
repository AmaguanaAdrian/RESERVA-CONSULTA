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
        SELECT 
            id_usuario,
            cedula,
            nombres,
            apellidos,
            correo,
            rol,
            estado
        FROM usuarios
        WHERE rol = 'BIBLIOTECARIO'
    """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                // ✅ IMPORTANTE: Debes setear el ID para poder eliminar después
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setCedula(rs.getString("cedula"));
                u.setNombres(rs.getString("nombres"));
                u.setApellidos(rs.getString("apellidos"));
                u.setCorreo(rs.getString("correo"));
                u.setRol(rs.getString("rol"));
                u.setEstado(rs.getString("estado"));
                lista.add(u);
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error al listar bibliotecarios", "Error BD", JOptionPane.ERROR_MESSAGE);
        }
        return lista;
    }
//CEDULAAAA EXISTE.
    public boolean existeCedula(String cedula) {
        String sql = "SELECT 1 FROM usuarios WHERE cedula = ? LIMIT 1";

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error verificando cédula: " + e.getMessage());
            return true; // por seguridad, asumir que existe
        }
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
        // Cambiamos el UPDATE por DELETE para borrar el registro permanentemente
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            // Es buena práctica verificar si se borró alguna fila
            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0;

        } catch (SQLException ex) {
            // Importante: Si el bibliotecario ya tiene préstamos o reservas registradas, 
            // la base de datos podría dar un error de "Llave Foránea" (Foreign Key).
            JOptionPane.showMessageDialog(null,
                    "No se pudo eliminar el bibliotecario. \n"
                    + "Verifique que no tenga registros asociados (préstamos o reservas).",
                    "Error de Base de Datos",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public Usuario buscarPorCedula(String cedula) {

        Usuario u = null;

        String sql = """
        SELECT id_usuario, cedula, nombres, apellidos, correo, rol, estado
        FROM usuarios
        WHERE cedula = ?
        AND rol = 'BIBLIOTECARIO'
    """;

        try (Connection con = Config.getConexion(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, cedula);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setCedula(rs.getString("cedula"));
                    u.setNombres(rs.getString("nombres"));
                    u.setApellidos(rs.getString("apellidos"));
                    u.setCorreo(rs.getString("correo"));
                    u.setRol(rs.getString("rol"));
                    u.setEstado(rs.getString("estado"));
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Error al buscar bibliotecario por cédula",
                    "Error BD",
                    JOptionPane.ERROR_MESSAGE);
        }

        return u;
    }

    public String obtenerRol(String cedula, String contrasena) {
        String sql = "SELECT rol FROM usuarios WHERE cedula = ? AND contrasena = ? AND estado = 'ACTIVO'";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cedula);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("rol");
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener rol: " + e.getMessage());
        }

        return null;
    }

    // NUEVO MÉTODO: Autenticar usuario y devolver objeto Usuario completo
    public Usuario autenticarUsuario(String cedula, String contrasena) {
        String sql = "SELECT id_usuario, cedula, nombres, apellidos, correo, rol, estado "
                + "FROM usuarios WHERE cedula = ? AND contrasena = ?";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cedula);
            pstmt.setString(2, contrasena);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setCedula(rs.getString("cedula"));
                usuario.setNombres(rs.getString("nombres"));
                usuario.setApellidos(rs.getString("apellidos"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setRol(rs.getString("rol"));
                usuario.setEstado(rs.getString("estado"));

                return usuario;
            }

        } catch (SQLException e) {
            System.err.println("Error en autenticación: " + e.getMessage());
        }

        return null;
    }

    // NUEVO MÉTODO: Verificar si usuario existe (solo por cédula)
    public boolean usuarioExiste(String cedula) {
        String sql = "SELECT COUNT(*) as total FROM usuarios WHERE cedula = ?";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, cedula);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("total") > 0;
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar usuario: " + e.getMessage());
        }

        return false;
    }

    // NUEVO MÉTODO: Obtener usuario por ID
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        String sql = "SELECT id_usuario, cedula, nombres, apellidos, correo, rol, estado "
                + "FROM usuarios WHERE id_usuario = ?";

        try (Connection conn = Config.getConexion(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, idUsuario);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("id_usuario"));
                usuario.setCedula(rs.getString("cedula"));
                usuario.setNombres(rs.getString("nombres"));
                usuario.setApellidos(rs.getString("apellidos"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setRol(rs.getString("rol"));
                usuario.setEstado(rs.getString("estado"));

                return usuario;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }

        return null;
    }
// ================= LISTAR ESTUDIANTES =================
   // ================= LISTAR ESTUDIANTES =================
public List<Usuario> listarEstudiantes() {
    List<Usuario> lista = new ArrayList<>();

    String sql = """
        SELECT u.id_usuario, u.cedula, u.nombres, u.apellidos, 
               u.correo, u.estado, e.carrera, e.id_estudiante
        FROM usuarios u
        INNER JOIN estudiantes e ON u.id_usuario = e.id_usuario
        WHERE u.rol = 'ESTUDIANTE' AND u.estado = 'ACTIVO'
    """;

    try (Connection con = Config.getConexion(); 
         PreparedStatement ps = con.prepareStatement(sql); 
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("id_usuario"));
            u.setCedula(rs.getString("cedula"));
            u.setNombres(rs.getString("nombres"));
            u.setApellidos(rs.getString("apellidos"));
            u.setCorreo(rs.getString("correo"));
            u.setEstado(rs.getString("estado"));
            u.setCarrera(rs.getString("carrera"));
            u.setRol("ESTUDIANTE");
            // Si necesitas almacenar el id_estudiante en la clase Usuario
            // puedes agregar un campo para ello
            lista.add(u);
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,
                "Error al listar estudiantes: " + ex.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
    }

    return lista;
}

// ================= AGREGAR ESTUDIANTE =================
public boolean agregarEstudiante(Usuario u) {
    Connection con = null;
    PreparedStatement psUsuario = null;
    PreparedStatement psEstudiante = null;
    ResultSet generatedKeys = null;
    
    try {
        con = Config.getConexion();
        con.setAutoCommit(false);
        
        // 1. Insertar en la tabla usuarios
        String sqlUsuario = """
            INSERT INTO usuarios (cedula, nombres, apellidos, correo, 
                                 contrasena, estado, rol)
            VALUES (?, ?, ?, ?, ?, ?, 'ESTUDIANTE')
        """;
        
        psUsuario = con.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS);
        psUsuario.setString(1, u.getCedula());
        psUsuario.setString(2, u.getNombres());
        psUsuario.setString(3, u.getApellidos());
        psUsuario.setString(4, u.getCorreo());
        psUsuario.setString(5, u.getContrasena());
        psUsuario.setString(6, u.getEstado());
        
        int filasUsuario = psUsuario.executeUpdate();
        
        if (filasUsuario == 0) {
            throw new SQLException("No se pudo insertar en la tabla usuarios");
        }
        
        // Obtener el ID generado del usuario
        generatedKeys = psUsuario.getGeneratedKeys();
        int idUsuario;
        if (generatedKeys.next()) {
            idUsuario = generatedKeys.getInt(1);
        } else {
            throw new SQLException("No se pudo obtener el ID del usuario insertado");
        }
        
        // 2. Insertar en la tabla estudiantes
        String sqlEstudiante = """
            INSERT INTO estudiantes (id_usuario, carrera)
            VALUES (?, ?)
        """;
        
        psEstudiante = con.prepareStatement(sqlEstudiante);
        psEstudiante.setInt(1, idUsuario);
        psEstudiante.setString(2, u.getCarrera());
        
        int filasEstudiante = psEstudiante.executeUpdate();
        
        if (filasEstudiante == 0) {
            throw new SQLException("No se pudo insertar en la tabla estudiantes");
        }
        
        con.commit();
        return true;
        
    } catch (SQLException ex) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(null,
                "Error al agregar estudiante: " + ex.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
        return false;
        
    } finally {
        try {
            if (generatedKeys != null) generatedKeys.close();
            if (psEstudiante != null) psEstudiante.close();
            if (psUsuario != null) psUsuario.close();
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

// ================= EDITAR ESTUDIANTE =================
public boolean editarEstudiante(Usuario u) {
    Connection con = null;
    PreparedStatement psUsuario = null;
    PreparedStatement psEstudiante = null;
    
    try {
        con = Config.getConexion();
        con.setAutoCommit(false);
        
        // 1. Actualizar en la tabla usuarios
        String sqlUsuario;
        if (u.getContrasena() != null && !u.getContrasena().isEmpty()) {
            sqlUsuario = """
                UPDATE usuarios
                SET nombres = ?, apellidos = ?, correo = ?, 
                    contrasena = ?, estado = ?
                WHERE id_usuario = ? AND rol = 'ESTUDIANTE'
            """;
        } else {
            sqlUsuario = """
                UPDATE usuarios
                SET nombres = ?, apellidos = ?, correo = ?, estado = ?
                WHERE id_usuario = ? AND rol = 'ESTUDIANTE'
            """;
        }
        
        psUsuario = con.prepareStatement(sqlUsuario);
        
        if (u.getContrasena() != null && !u.getContrasena().isEmpty()) {
            psUsuario.setString(1, u.getNombres());
            psUsuario.setString(2, u.getApellidos());
            psUsuario.setString(3, u.getCorreo());
            psUsuario.setString(4, u.getContrasena());
            psUsuario.setString(5, u.getEstado());
            psUsuario.setInt(6, u.getIdUsuario());
        } else {
            psUsuario.setString(1, u.getNombres());
            psUsuario.setString(2, u.getApellidos());
            psUsuario.setString(3, u.getCorreo());
            psUsuario.setString(4, u.getEstado());
            psUsuario.setInt(5, u.getIdUsuario());
        }
        
        int filasUsuario = psUsuario.executeUpdate();
        
        if (filasUsuario == 0) {
            throw new SQLException("No se pudo actualizar en la tabla usuarios");
        }
        
        // 2. Actualizar en la tabla estudiantes
        String sqlEstudiante = """
            UPDATE estudiantes
            SET carrera = ?
            WHERE id_usuario = ?
        """;
        
        psEstudiante = con.prepareStatement(sqlEstudiante);
        psEstudiante.setString(1, u.getCarrera());
        psEstudiante.setInt(2, u.getIdUsuario());
        
        int filasEstudiante = psEstudiante.executeUpdate();
        
        if (filasEstudiante == 0) {
            // Si no existe, insertar (aunque debería existir)
            String sqlInsertEstudiante = """
                INSERT INTO estudiantes (id_usuario, carrera)
                VALUES (?, ?)
            """;
            
            try (PreparedStatement psInsert = con.prepareStatement(sqlInsertEstudiante)) {
                psInsert.setInt(1, u.getIdUsuario());
                psInsert.setString(2, u.getCarrera());
                psInsert.executeUpdate();
            }
        }
        
        con.commit();
        return true;
        
    } catch (SQLException ex) {
        try {
            if (con != null) {
                con.rollback();
            }
        } catch (SQLException rollbackEx) {
            rollbackEx.printStackTrace();
        }
        
        JOptionPane.showMessageDialog(null,
                "Error al actualizar estudiante: " + ex.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
        return false;
        
    } finally {
        try {
            if (psEstudiante != null) psEstudiante.close();
            if (psUsuario != null) psUsuario.close();
            if (con != null) {
                con.setAutoCommit(true);
                con.close();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

// ================= ELIMINAR ESTUDIANTE =================
public boolean eliminarEstudiante(int idUsuario) {
    // Eliminación física (se eliminará en cascada de la tabla estudiantes)
    String sql = """
        DELETE FROM usuarios
        WHERE id_usuario = ? AND rol = 'ESTUDIANTE'
    """;

    try (Connection con = Config.getConexion(); 
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setInt(1, idUsuario);
        int filasEliminadas = ps.executeUpdate();
        
        // Devuelve true si se eliminó al menos un registro
        return filasEliminadas > 0;

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,
                "Error al eliminar estudiante: " + ex.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
        return false;
    }
}

// ================= BUSCAR ESTUDIANTE POR CÉDULA =================
public Usuario buscarEstudiantePorCedula(String cedula) {
    String sql = """
        SELECT u.id_usuario, u.cedula, u.nombres, u.apellidos, 
               u.correo, u.estado, e.carrera, e.id_estudiante
        FROM usuarios u
        INNER JOIN estudiantes e ON u.id_usuario = e.id_usuario
        WHERE u.cedula = ? AND u.rol = 'ESTUDIANTE'
    """;

    try (Connection con = Config.getConexion(); 
         PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, cedula);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            Usuario u = new Usuario();
            u.setIdUsuario(rs.getInt("id_usuario"));
            u.setCedula(rs.getString("cedula"));
            u.setNombres(rs.getString("nombres"));
            u.setApellidos(rs.getString("apellidos"));
            u.setCorreo(rs.getString("correo"));
            u.setEstado(rs.getString("estado"));
            u.setCarrera(rs.getString("carrera"));
            u.setRol("ESTUDIANTE");
            return u;
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(null,
                "Error al buscar estudiante por cédula: " + ex.getMessage(),
                "Error BD",
                JOptionPane.ERROR_MESSAGE);
    }

    return null;
}


// ================= OBTENER ID ESTUDIANTE =================
public Integer obtenerIdEstudiante(int idUsuario) {
    String sql = "SELECT id_estudiante FROM estudiantes WHERE id_usuario = ?";

    try (Connection conn = Config.getConexion(); 
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        pstmt.setInt(1, idUsuario);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            return rs.getInt("id_estudiante");
        }

    } catch (SQLException e) {
        System.err.println("Error al obtener id_estudiante: " + e.getMessage());
    }

    return null;
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

}
