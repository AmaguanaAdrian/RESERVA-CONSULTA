/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package vista;
import controlador.ControladorLogin;
/**
 *
 * @author amagu
 */


import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            VistaLogin login = new VistaLogin();
            new ControladorLogin(login);

            login.pack();                      // Ajusta tamaño real
            login.setLocationRelativeTo(null); // Centra en pantalla
            login.setVisible(true);            // Muestra
        });
    }
}

