/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BDD;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * @author amagu
 */
public class Config {
    // Datos de configuración (Cámbialos si es necesario)
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_reservas";
    private static final String USER = "root";
    private static final String PASS = "12345678";

    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (SQLException e) {
            System.err.println("Error de conexión: " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        if (getConexion() != null) {
            System.out.println("✅ ¡Conectado con éxito!");
        }
    }
}
