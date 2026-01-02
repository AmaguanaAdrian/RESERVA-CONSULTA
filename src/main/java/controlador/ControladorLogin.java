/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import vista.VistaLogin;
import vista.VistaContenedor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import modelo.UsuarioDAO;
/**
 *
 * @author amagu
 */

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
        }

        if (e.getSource() == vista.jbtn_loginBibliotecario) {
            procesarLogin("BIBLIOTECARIO");
        }

        if (e.getSource() == vista.jbtn_loginEstudiante) {
            procesarLogin("ESTUDIANTE");
        }
    }

    private void procesarLogin(String rolEsperado) {

        String cedula = vista.jtxt_Usuario.getText().trim();
        String contrasena = vista.jtxt_Contraseña.getText().trim();

        if (cedula.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Ingrese usuario y contraseña"
            );
            return;
        }

        // 🔹 MODELO decide (BD)
        String rolBD = usuarioDAO.obtenerRol(cedula, contrasena);

        if (rolBD == null) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Usuario o contraseña incorrectos"
            );
            return;
        }

        // 🔐 BLOQUEO POR ROL
        if (!rolBD.equals(rolEsperado)) {
            JOptionPane.showMessageDialog(
                    vista,
                    "Acceso denegado para este rol"
            );
            return;
        }

        // ✅ ACCESO CORRECTO
        VistaContenedor contenedor = new VistaContenedor();
        new ControladorContenedor(contenedor, rolBD);

        contenedor.setVisible(true);
        vista.dispose();
    }
}

