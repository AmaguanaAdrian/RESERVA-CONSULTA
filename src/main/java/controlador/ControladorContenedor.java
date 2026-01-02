/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import java.awt.BorderLayout;
import vista.VistaContenedor;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vista.ConsultaCatalogo;
import vista.GestionBibliotecarios;
import vista.GestionCatalogo;
import vista.GestionEstudiantes;
import vista.MisReservas;
import vista.ReservasActivas;
/**
 *
 * @author amagu
 */

public class ControladorContenedor implements ActionListener {

    private VistaContenedor vista;
    private String rol;

    public ControladorContenedor(VistaContenedor vista, String rol) {
        this.vista = vista;
        this.rol = rol;

        vista.jlb_Roles.setText("ROL: " + rol);

        configurarVistaSegunRol();
        cargarPanelInicial();

        vista.jbtn_1.addActionListener(this);
        vista.jbtn_2.addActionListener(this);
        vista.jbtn_3.addActionListener(this);
    }

    // 🔐 Mostrar botones según rol
    private void configurarVistaSegunRol() {

        switch (rol) {
            case "ADMIN" -> {
                vista.jbtn_1.setText("Bibliotecarios");
                vista.jbtn_1.setVisible(true);

                vista.jbtn_2.setVisible(false);
                vista.jbtn_3.setVisible(false);
            }

            case "BIBLIOTECARIO" -> {
                vista.jbtn_1.setText("Estudiantes");
                vista.jbtn_2.setText("Catálogo");
                vista.jbtn_3.setText("Reservas");

                vista.jbtn_1.setVisible(true);
                vista.jbtn_2.setVisible(true);
                vista.jbtn_3.setVisible(true);
            }

            case "ESTUDIANTE" -> {
                vista.jbtn_1.setVisible(false);

                vista.jbtn_2.setText("Catálogo");
                vista.jbtn_3.setText("Mis Reservas");

                vista.jbtn_2.setVisible(true);
                vista.jbtn_3.setVisible(true);
            }
        }
    }

    // 🚀 Panel que se carga apenas inicia sesión
    private void cargarPanelInicial() {

        switch (rol) {
            case "ADMIN" -> mostrarGestionBibliotecarios();
            case "BIBLIOTECARIO" -> mostrarGestionCatalogo();
            case "ESTUDIANTE" -> mostrarConsultaCatalogo();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == vista.jbtn_1) {

            if (rol.equals("ADMIN")) {
                mostrarGestionBibliotecarios();
            }

            if (rol.equals("BIBLIOTECARIO")) {
                mostrarGestionEstudiantes();
            }
        }

        if (e.getSource() == vista.jbtn_2) {

            if (rol.equals("BIBLIOTECARIO")) {
                mostrarGestionCatalogo();
            }

            if (rol.equals("ESTUDIANTE")) {
                mostrarConsultaCatalogo();
            }
        }

        if (e.getSource() == vista.jbtn_3) {

            if (rol.equals("BIBLIOTECARIO")) {
                mostrarReservasActivas();
            }

            if (rol.equals("ESTUDIANTE")) {
                mostrarMisReservas();
            }
        }
    }

    // 🔧 MÉTODO GENÉRICO PARA CAMBIAR PANEL
    private void cambiarPanel(JPanel panel) {

        vista.jPanel3.removeAll();
        vista.jPanel3.setLayout(new BorderLayout());

        vista.jPanel3.add(panel, BorderLayout.CENTER);
        vista.jPanel3.revalidate();
        vista.jPanel3.repaint();
    }

    // ====== PANELES ADMIN ======
    private void mostrarGestionBibliotecarios() {
        GestionBibliotecarios panel = new GestionBibliotecarios();
        new ControladorGestionBibliotecarios(panel);
        cambiarPanel(panel);
    }

    // ====== PANELES BIBLIOTECARIO ======
    private void mostrarGestionEstudiantes() {
        GestionEstudiantes panel = new GestionEstudiantes();
        cambiarPanel(panel);
    }

    private void mostrarGestionCatalogo() {
        GestionCatalogo panel = new GestionCatalogo();
        cambiarPanel(panel);
    }

    private void mostrarReservasActivas() {
        ReservasActivas panel = new ReservasActivas();
        cambiarPanel(panel);
    }

    // ====== PANELES ESTUDIANTE ======
    private void mostrarConsultaCatalogo() {
        ConsultaCatalogo panel = new ConsultaCatalogo();
        cambiarPanel(panel);
    }

    private void mostrarMisReservas() {
        MisReservas panel = new MisReservas();
        cambiarPanel(panel);
    }
}
