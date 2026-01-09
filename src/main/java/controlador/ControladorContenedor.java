/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import vista.VistaContenedor;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import vista.VistaLogin;
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

    private final VistaContenedor vista;
    private final String rol;

    public ControladorContenedor(VistaContenedor vista, String rol) {
        this.vista = vista;
        this.rol = rol;

        configurarLabelRol();
        quitarFocusBotones();
        configurarVistaSegunRol();
        cargarPanelInicial();
        
        // Ajustar tamaño y centrar (esto se hará de forma asíncrona)
        ajustarTamanioVentanaUniversal();
        
        configurarVentana();

        vista.jbtn_1.addActionListener(this);
        vista.jbtn_2.addActionListener(this);
        vista.jbtn_3.addActionListener(this);
        vista.jButton1.addActionListener(this);
    }

    // ===================== VENTANA =====================
    private void configurarVentana() {
        vista.setResizable(false);
        // El centrado se hace en ajustarTamanioVentanaUniversal()
    }

    // ===================== CENTRADO UNIVERSAL =====================
    private void centrarVentanaEnPantalla() {
        // Obtener tamaño de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        
        // Obtener tamaño de la ventana
        Dimension windowSize = vista.getSize();
        
        // Calcular posición centrada
        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;
        
        // Establecer posición
        vista.setLocation(x, y);
    }

    // ===================== AJUSTE DE TAMAÑO UNIVERSAL =====================
    private void ajustarTamanioVentanaUniversal() {
        // Forzar cálculo de layout
        vista.revalidate();
        vista.repaint();
        
        // Esperar a que los componentes se rendericen
        SwingUtilities.invokeLater(() -> {
            // Calcular el tamaño mínimo necesario basado en los componentes visibles
            int anchoMinimo = calcularAnchoMinimo();
            int altoMinimo = calcularAltoMinimo();
            
            // Establecer un tamaño seguro que funcione para todos los roles
            int anchoSeguro = Math.max(anchoMinimo, 800); // Mínimo 800px de ancho
            int altoSeguro = Math.max(altoMinimo, 600);   // Mínimo 600px de alto
            
            // Ajustar tamaño de la ventana
            vista.setSize(new Dimension(anchoSeguro, altoSeguro));
            vista.setMinimumSize(new Dimension(anchoSeguro, altoSeguro));
            
            // Centrar la ventana después de ajustar el tamaño
            centrarVentanaEnPantalla();
            
            // Forzar repintado
            vista.revalidate();
            vista.repaint();
        });
    }
    
    // ===================== CÁLCULO DE ANCHO MÍNIMO =====================
    private int calcularAnchoMinimo() {
        int anchoTotal = 0;
        
        // Calcular ancho de los botones visibles
        if (vista.jpBtn1.isVisible()) {
            anchoTotal += vista.jpBtn1.getPreferredSize().width;
        }
        if (vista.jpBtn2.isVisible()) {
            anchoTotal += vista.jpBtn2.getPreferredSize().width;
        }
        if (vista.jpBtn3.isVisible()) {
            anchoTotal += vista.jpBtn3.getPreferredSize().width;
        }
        
        // Añadir espacio para el botón cerrar sesión y márgenes
        anchoTotal += vista.jButton1.getPreferredSize().width + 100;
        
        // Añadir espacio para el panel lateral (si existe)
        if (vista.jPanel2 != null) {
            anchoTotal += vista.jPanel2.getPreferredSize().width;
        }
        
        return anchoTotal;
    }
    
    // ===================== CÁLCULO DE ALTO MÍNIMO =====================
    private int calcularAltoMinimo() {
        int altoTotal = 400; // Altura base
        
        // Añadir altura del contenido del panel principal
        if (vista.jPanel3 != null && vista.jPanel3.getComponentCount() > 0) {
            Component contenido = vista.jPanel3.getComponent(0);
            altoTotal = Math.max(altoTotal, contenido.getPreferredSize().height + 150);
        }
        
        return altoTotal;
    }

    // ===================== LABEL ROL =====================
    private void configurarLabelRol() {
        vista.jlb_Roles.setText(rol);
        vista.jlb_Roles.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // ===================== FOCUS =====================
    private void quitarFocusBotones() {
        JButton[] botones = {
            vista.jbtn_1, vista.jbtn_2, vista.jbtn_3, vista.jButton1
        };
        for (JButton b : botones) {
            b.setFocusPainted(false);
            b.setFocusable(false);
        }
    }

    // ===================== ROLES =====================
    private void configurarVistaSegunRol() {
        // Ocultar TODOS los paneles de botones
        vista.jpBtn1.setVisible(false);
        vista.jpBtn2.setVisible(false);
        vista.jpBtn3.setVisible(false);

        switch (rol) {
            case "ADMIN" -> {
                vista.jbtn_1.setText("Bibliotecarios");
                vista.jpBtn1.setVisible(true);
                // Establecer tamaño preferido para botones ADMIN
                vista.jpBtn1.setPreferredSize(new Dimension(200, 50));
            }

            case "BIBLIOTECARIO" -> {
                vista.jbtn_1.setText("Estudiantes");
                vista.jbtn_2.setText("G.Catálogo");
                vista.jbtn_3.setText("Reservas");

                vista.jpBtn1.setVisible(true);
                vista.jpBtn2.setVisible(true);
                vista.jpBtn3.setVisible(true);
                
                // Asegurar tamaño adecuado para botones
                vista.jpBtn1.setPreferredSize(new Dimension(150, 50));
                vista.jpBtn2.setPreferredSize(new Dimension(150, 50));
                vista.jpBtn3.setPreferredSize(new Dimension(150, 50));
            }

            case "ESTUDIANTE" -> {
                vista.jbtn_2.setText("Catálogo");
                vista.jbtn_3.setText("Mis Reservas");

                vista.jpBtn2.setVisible(true);
                vista.jpBtn3.setVisible(true);
                
                // Asegurar tamaño adecuado para botones ESTUDIANTE
                vista.jpBtn2.setPreferredSize(new Dimension(150, 50));
                vista.jpBtn3.setPreferredSize(new Dimension(180, 50));
            }
        }

        vista.jPanel2.revalidate();
        vista.jPanel2.repaint();
    }

    // ===================== PANEL INICIAL =====================
    private void cargarPanelInicial() {
        switch (rol) {
            case "ADMIN" -> mostrarGestionBibliotecarios();
            case "BIBLIOTECARIO", "ESTUDIANTE" -> mostrarGestionCatalogo();
        }
    }

    // ===================== EVENTOS =====================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtn_1) {
            if (rol.equals("ADMIN")) mostrarGestionBibliotecarios();
            if (rol.equals("BIBLIOTECARIO")) mostrarEstudiantes();
        }

        if (e.getSource() == vista.jbtn_2) {
            mostrarGestionCatalogo();
        }

        if (e.getSource() == vista.jbtn_3) {
            if (rol.equals("BIBLIOTECARIO")) mostrarReservas();
            if (rol.equals("ESTUDIANTE")) mostrarMisReservas();
        }

        if (e.getSource() == vista.jButton1) {
            cerrarSesion();
        }
    }

    // ===================== CAMBIAR PANEL =====================
    private void cambiarPanel(JPanel panel) {
        vista.jPanel3.removeAll();
        vista.jPanel3.setLayout(new BorderLayout());
        vista.jPanel3.add(panel, BorderLayout.CENTER);
        vista.jPanel3.revalidate();
        vista.jPanel3.repaint();
        
        // Recalcular tamaño después de cambiar panel y recentrar
        SwingUtilities.invokeLater(() -> {
            ajustarTamanioVentanaUniversal();
        });
    }

    // ===================== NAVEGACIÓN =====================
    private void mostrarGestionBibliotecarios() {
        GestionBibliotecarios p = new GestionBibliotecarios();
        new ControladorGestionBibliotecarios(p);
        cambiarPanel(p);
    }

    private void mostrarEstudiantes() {
        GestionEstudiantes p = new GestionEstudiantes();
        new ControladorGestionEstudiantes(p);
        cambiarPanel(p);
    }

    private void mostrarGestionCatalogo() {
        GestionCatalogo p = new GestionCatalogo();
        new ControladorGestionCatalogo(p);
        cambiarPanel(p);
    }

    private void mostrarReservas() {
        ReservasActivas p = new ReservasActivas();
        new ControladorReservasActivas(p);
        cambiarPanel(p);
    }

    private void mostrarMisReservas() {
        cambiarPanel(new MisReservas());
    }

    // ===================== CERRAR SESIÓN =====================
    private void cerrarSesion() {
        int op = JOptionPane.showConfirmDialog(
                vista,
                "¿Desea cerrar sesión?",
                "Cerrar sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {
            vista.dispose();
            VistaLogin login = new VistaLogin();
            new ControladorLogin(login);
            // Centrar ventana de login
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        }
    }
}