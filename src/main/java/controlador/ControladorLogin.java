/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import vista.VistaLogin;
import vista.VistaContenedor;
import modelo.UsuarioDAO;
/**
 *
 * @author amagu
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

public class ControladorLogin implements ActionListener {

    private VistaLogin vista;
    private UsuarioDAO usuarioDAO;

    public ControladorLogin(VistaLogin vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();

        vista.jbtn_loginAdmin.addActionListener(this);
        vista.jbtn_loginBibliotecario.addActionListener(this);
        vista.jbtn_loginEstudiante.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vista.jbtn_loginAdmin) {
            procesarLogin("ADMIN");
        } else if (e.getSource() == vista.jbtn_loginBibliotecario) {
            procesarLogin("BIBLIOTECARIO");
        } else if (e.getSource() == vista.jbtn_loginEstudiante) {
            procesarLogin("ESTUDIANTE");
        }
    }

    private void procesarLogin(String rolEsperado) {

        String cedula = vista.jtxt_Usuario.getText().trim();
        String contrasena = new String(vista.jtxt_Contraseña.getText()).trim();

        if (cedula.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Ingrese usuario y contraseña",
                    "Campos vacíos",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String rolBD = usuarioDAO.obtenerRol(cedula, contrasena);

        if (rolBD == null) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Usuario o contraseña incorrectos",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        if (!rolBD.equals(rolEsperado)) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Acceso denegado para este rol",
                    "Acceso restringido",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // ================= ABRIR CONTENEDOR =================
        VistaContenedor contenedor = new VistaContenedor();
        new ControladorContenedor(contenedor, rolBD);

        contenedor.pack();                      // 🔥 OBLIGATORIO
        contenedor.setLocationRelativeTo(null); // 🔥 CENTRAR
        contenedor.setVisible(true);            // 🔥 MOSTRAR

        vista.dispose(); // cerrar login
    }
}

