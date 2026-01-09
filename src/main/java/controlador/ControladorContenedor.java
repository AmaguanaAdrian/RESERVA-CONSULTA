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
import modelo.Sesion;
import modelo.Usuario;
import modelo.UsuarioDAO;
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
    private final Sesion sesion;
    private final UsuarioDAO usuarioDAO;

    public ControladorContenedor(VistaContenedor vista) {
        this.vista = vista;
        this.sesion = Sesion.getInstancia();
        this.usuarioDAO = new UsuarioDAO();

        // Validar que haya una sesión activa
        if (!validarSesionActiva()) {
            cerrarYSalir();
            return;
        }

        // Obtener usuario de la sesión
        Usuario usuario = sesion.getUsuario();
        String rol = usuario.getRol();

        configurarInterfazSegunRol(rol);
        mostrarInfoUsuario();
        quitarFocusBotones();
        cargarPanelInicial(rol);

        // Ajustar tamaño y centrar
        ajustarTamanioVentanaUniversal();

        configurarVentana();

        // Asignar listeners
        vista.jbtn_1.addActionListener(this);
        vista.jbtn_2.addActionListener(this);
        vista.jbtn_3.addActionListener(this);
        vista.jButton1.addActionListener(this);
    }

    // ===================== VALIDACIÓN DE SESIÓN =====================
    private boolean validarSesionActiva() {
        if (sesion.getUsuario() == null) {
            JOptionPane.showMessageDialog(vista,
                    "No hay una sesión activa. Será redirigido al login.",
                    "Sesión expirada",
                    JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // ===================== INFORMACIÓN DEL USUARIO =====================
    private void mostrarInfoUsuario() {
        Usuario usuario = sesion.getUsuario();

        // Mostrar nombre y apellidos
        if (vista.jlb_Roles != null) {
            // Usamos jlb_Roles para mostrar el nombre (puedes cambiar el nombre del label después)
            vista.jlb_Roles.setText(usuario.getNombres() + " " + usuario.getApellidos());
            vista.jlb_Roles.setHorizontalAlignment(SwingConstants.CENTER);
        }

        // Si es estudiante, mostrar información adicional
        if (sesion.esEstudiante()) {
            mostrarInfoEstudiante();
        }
    }

    private void mostrarInfoEstudiante() {
        Integer idEstudiante = sesion.getIdEstudiante();

        if (idEstudiante != null) {
            // Contar reservas hoy
            int reservasHoy = usuarioDAO.contarReservasHoy(idEstudiante);

            // Mostrar en algún label disponible (puedes ajustar según tu interfaz)
            // Si tienes un label para esto, úsalo, sino puedes mostrar en tooltip o console
            System.out.println(" - Reservas hoy: " + reservasHoy + "/3");

            // Si el estudiante ya alcanzó el límite, podemos deshabilitar ciertas funciones
            if (reservasHoy >= 3) {
                vista.jbtn_2.setToolTipText("Límite de 3 reservas diarias alcanzado");
            }
        }
    }

    // ===================== VENTANA =====================
    private void configurarVentana() {
        vista.setResizable(false);
    }

    // ===================== CENTRADO UNIVERSAL =====================
    private void centrarVentanaEnPantalla() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension windowSize = vista.getSize();

        int x = (screenSize.width - windowSize.width) / 2;
        int y = (screenSize.height - windowSize.height) / 2;

        vista.setLocation(x, y);
    }

    // ===================== AJUSTE DE TAMAÑO UNIVERSAL =====================
    // ===================== AJUSTE DE TAMAÑO UNIVERSAL =====================
    private void ajustarTamanioVentanaUniversal() {
        vista.revalidate();
        vista.repaint();

        SwingUtilities.invokeLater(() -> {
            int anchoMinimo = calcularAnchoMinimo();
            int altoMinimo = calcularAltoMinimo();

            // 1. Definimos tamaños base
            int anchoSeguro = Math.max(anchoMinimo, 800);
            int altoSeguro = Math.max(altoMinimo, 600);

            // 2. APLICAR EXCEPCIÓN PARA ESTUDIANTE
            // Si el rol es ESTUDIANTE, forzamos un ancho mayor (ej. 1100 o 1200)
            // para que la cuadrícula de libros quepa cómodamente.
            if ("ESTUDIANTE".equals(sesion.getUsuario().getRol())) {
                anchoSeguro = 1150; // Ajusta este valor según el ancho de tus tarjetas de libros
                altoSeguro = 750;  // También podemos darle un poco más de alto
            }

            vista.setSize(new Dimension(anchoSeguro, altoSeguro));
            vista.setMinimumSize(new Dimension(anchoSeguro, altoSeguro));

            centrarVentanaEnPantalla();

            vista.revalidate();
            vista.repaint();
        });
    }

    // ===================== CÁLCULO DE ANCHO MÍNIMO =====================
    private int calcularAnchoMinimo() {
        int anchoTotal = 0;

        if (vista.jpBtn1.isVisible()) {
            anchoTotal += vista.jpBtn1.getPreferredSize().width;
        }
        if (vista.jpBtn2.isVisible()) {
            anchoTotal += vista.jpBtn2.getPreferredSize().width;
        }
        if (vista.jpBtn3.isVisible()) {
            anchoTotal += vista.jpBtn3.getPreferredSize().width;
        }

        anchoTotal += vista.jButton1.getPreferredSize().width + 100;

        if (vista.jPanel2 != null) {
            anchoTotal += vista.jPanel2.getPreferredSize().width;
        }

        return anchoTotal;
    }

    // ===================== CÁLCULO DE ALTO MÍNIMO =====================
    private int calcularAltoMinimo() {
        int altoTotal = 400;

        if (vista.jPanel3 != null && vista.jPanel3.getComponentCount() > 0) {
            Component contenido = vista.jPanel3.getComponent(0);
            altoTotal = Math.max(altoTotal, contenido.getPreferredSize().height + 150);
        }

        return altoTotal;
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

    // ===================== CONFIGURACIÓN POR ROL =====================
    private void configurarInterfazSegunRol(String rol) {
        // Ocultar TODOS los paneles de botones
        vista.jpBtn1.setVisible(false);
        vista.jpBtn2.setVisible(false);
        vista.jpBtn3.setVisible(false);

        switch (rol) {
            case "ADMIN" -> {
                vista.jbtn_1.setText("Bibliotecarios");
                vista.jpBtn1.setVisible(true);
                vista.jpBtn1.setPreferredSize(new Dimension(200, 50));
            }

            case "BIBLIOTECARIO" -> {
                vista.jbtn_1.setText("Estudiantes");
                vista.jbtn_2.setText("G.Catálogo");
                vista.jbtn_3.setText("Reservas");

                vista.jpBtn1.setVisible(true);
                vista.jpBtn2.setVisible(true);
                vista.jpBtn3.setVisible(true);

                vista.jpBtn1.setPreferredSize(new Dimension(150, 50));
                vista.jpBtn2.setPreferredSize(new Dimension(150, 50));
                vista.jpBtn3.setPreferredSize(new Dimension(150, 50));
            }

            case "ESTUDIANTE" -> {
                vista.jbtn_2.setText("Catálogo");
                vista.jbtn_3.setText("Mis Reservas");

                vista.jpBtn2.setVisible(true);
                vista.jpBtn3.setVisible(true);

                vista.jpBtn2.setPreferredSize(new Dimension(150, 50));
                vista.jpBtn3.setPreferredSize(new Dimension(180, 50));
            }
        }

        // Actualizar el label de rol (si quieres mantenerlo separado del nombre)
        // Podrías añadir un label específico para el rol si lo necesitas
        vista.jPanel2.revalidate();
        vista.jPanel2.repaint();
    }

    // ===================== CARGA INICIAL =====================
    private void cargarPanelInicial(String rol) {
        switch (rol) {
            case "ADMIN" ->
                mostrarGestionBibliotecarios();
            case "BIBLIOTECARIO" ->
                mostrarGestionCatalogo();
            case "ESTUDIANTE" ->
                mostrarConsultaCatalogo();
        }
    }

    // ===================== EVENTOS =====================
    @Override
    public void actionPerformed(ActionEvent e) {
        Usuario usuario = sesion.getUsuario();
        String rol = usuario.getRol();

        if (e.getSource() == vista.jbtn_1) {
            if ("ADMIN".equals(rol)) {
                mostrarGestionBibliotecarios();
            } else if ("BIBLIOTECARIO".equals(rol)) {
                mostrarEstudiantes();
            }
        }

        if (e.getSource() == vista.jbtn_2) {
            if ("ESTUDIANTE".equals(rol)) {
                mostrarConsultaCatalogo();
            } else {
                mostrarGestionCatalogo();
            }
        }

        if (e.getSource() == vista.jbtn_3) {
            if ("BIBLIOTECARIO".equals(rol)) {
                mostrarReservas();
            } else if ("ESTUDIANTE".equals(rol)) {
                mostrarMisReservas();
            }
        }

        if (e.getSource() == vista.jButton1) {
            cerrarSesion();
        }
    }

    // ===================== CAMBIO DE PANELES =====================
    private void cambiarPanel(JPanel panel) {
        vista.jPanel3.removeAll();
        vista.jPanel3.setLayout(new BorderLayout());
        vista.jPanel3.add(panel, BorderLayout.CENTER);
        vista.jPanel3.revalidate();
        vista.jPanel3.repaint();

        SwingUtilities.invokeLater(() -> {
            ajustarTamanioVentanaUniversal();
        });
    }

    private void mostrarGestionBibliotecarios() {
        GestionBibliotecarios p = new GestionBibliotecarios();
        new ControladorGestionBibliotecarios(p);
        cambiarPanel(p);
    }

    private void mostrarConsultaCatalogo() {
        ConsultaCatalogo p = new ConsultaCatalogo();
        new ControladorConsultaCatalogo(p);
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
        MisReservas p = new MisReservas();
        new ControladorMisReservas(p);
        cambiarPanel(p);
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
            // Limpiar la sesión antes de cerrar
            sesion.cerrarSesion();

            // Cerrar esta ventana
            vista.dispose();

            // Abrir ventana de login
            VistaLogin login = new VistaLogin();
            new ControladorLogin(login);
            login.setLocationRelativeTo(null);
            login.setVisible(true);
        }
    }

    private void cerrarYSalir() {
        vista.dispose();
        VistaLogin login = new VistaLogin();
        new ControladorLogin(login);
        login.setLocationRelativeTo(null);
        login.setVisible(true);
    }
}
