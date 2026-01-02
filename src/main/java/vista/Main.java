/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package vista;
import vista.VistaLogin;
import controlador.ControladorLogin;
/**
 *
 * @author amagu
 */


public class Main {

    public static void main(String[] args) {

        // Crear vista
        VistaLogin login = new VistaLogin();

        // Inyectar controlador
        new ControladorLogin(login);
        
        // Mostrar login
        login.setVisible(true);
    }
}
