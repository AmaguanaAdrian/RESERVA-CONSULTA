package controlador;

import vista.ConsultaCatalogo;
import modelo.LibroCompleto;
import modelo.LibroDAO;
import modelo.ReservaDAO;
import modelo.Sesion;
import Utilidades.AppUtils;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Controlador para la consulta del catálogo de libros.
 * Maneja la visualización dinámica, búsqueda y reservas.
 * @author amagu
 */
public class ControladorConsultaCatalogo implements ActionListener {

    private ConsultaCatalogo vista;
    private LibroDAO libroDAO = new LibroDAO();
    private ReservaDAO reservaDAO = new ReservaDAO();

    // Arrays para manejar componentes de forma dinámica
    private JPanel[] panelesLibros;
    private JLabel[] lblTitulos;
    private JLabel[] lblAutores;
    private JLabel[] lblGeneros;
    private JLabel[] lblDisponibles;
    private JButton[] btnReservar;

    // Lista de libros actualmente mostrados
    private List<LibroCompleto> librosActuales;
    private int idEstudianteActual;

    public ControladorConsultaCatalogo(ConsultaCatalogo vista) {
        this.vista = vista;

        // 1. Inicializar componentes
        inicializarArraysComponentes();

        // 2. Configurar Interfaz y Eventos
        aplicarEstilosVisuales();
        configurarEfectosHover();
        configurarBotonesReserva();
        configurarBotonPdf(); // <--- NUEVO
        
        // 3. Cargar datos iniciales
        verificarSesionEstudiante();
        cargarLibrosIniciales();
        configurarBusquedaEnTiempoReal();
        configurarEventosPaneles();
    }

    // ================= INICIALIZACIÓN DE COMPONENTES =================
    private void inicializarArraysComponentes() {
        panelesLibros = new JPanel[]{
            vista.libro1Panel, vista.libro2Panel, vista.libro3Panel,
            vista.libro4Panel, vista.libro5Panel, vista.libro6Panel
        };

        lblTitulos = new JLabel[]{
            vista.lblTitulo1, vista.lblTitulo2, vista.lblTitulo3,
            vista.lblTitulo4, vista.lblTitulo5, vista.lblTitulo6
        };

        lblAutores = new JLabel[]{
            vista.lblAutor1, vista.lblAutor2, vista.lblAutor3,
            vista.lblAutor4, vista.lblAutor5, vista.lblAutor6
        };

        lblGeneros = new JLabel[]{
            vista.lblGenero1, vista.lblGenero2, vista.lblGenero3,
            vista.lblGenero4, vista.lblGenero5, vista.lblGenero6
        };

        lblDisponibles = new JLabel[]{
            vista.lblDisponible1, vista.lblDisponible2, vista.lblDisponible3,
            vista.lblDisponible4, vista.lblDisponible5, vista.lblDisponible6
        };

        btnReservar = new JButton[]{
            vista.btnReservar1, vista.btnReservar2, vista.btnReservar3,
            vista.btnReservar4, vista.btnReservar5, vista.btnReservar6
        };

        // Asignar listeners a botones de reserva
        for (JButton btn : btnReservar) {
            btn.addActionListener(this);
        }
    }

    private void configurarBotonPdf() {
        // Asumiendo que el botón en tu vista se llama btnGenerarPdf
        // Si tiene otro nombre en NetBeans, cámbialo aquí.
        if (vista.btnGenerarPdf != null) {
            vista.btnGenerarPdf.addActionListener(e -> generarReportePDF());
        }
    }

    // ================= MEJORAS VISUALES =================
    private void aplicarEstilosVisuales() {
        Color colorReserva = new Color(40, 167, 69); // Verde Bootstrap
        for (JButton btn : btnReservar) {
            AppUtils.estiloBotonModerno(btn, colorReserva, "Reservar");
        }

        if (vista.jtxt_buscarLibros != null) {
            vista.jtxt_buscarLibros.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vista.jtxt_buscarLibros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }

        for (JPanel panel : panelesLibros) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            panel.setBackground(Color.WHITE);
        }

        for (JLabel lbl : lblTitulos) {
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(new Color(33, 37, 41));
        }
    }

    private void configurarEfectosHover() {
        for (JPanel panel : panelesLibros) {
            panel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    panel.setBackground(new Color(248, 249, 250));
                    panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(0, 123, 255), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));
                }
                @Override
                public void mouseExited(MouseEvent e) {
                    panel.setBackground(Color.WHITE);
                    panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(233, 236, 239), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));
                }
            });
        }
    }

    private void configurarEventosPaneles() {
        for (int i = 0; i < panelesLibros.length; i++) {
            final int index = i;
            panelesLibros[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && btnReservar[index].isEnabled()) {
                        reservarLibro(index);
                    }
                }
            });
        }
    }

    private void verificarSesionEstudiante() {
        Sesion sesion = Sesion.getInstancia();
        if (!sesion.esEstudiante()) {
            deshabilitarBotonesReserva();
            return;
        }

        idEstudianteActual = sesion.getIdEstudiante();
        if (vista.lblNombreEstudiante != null) {
            int reservasActivasHoy = reservaDAO.contarReservasEstudianteHoy(idEstudianteActual);
            int limite = 3;
            vista.lblNombreEstudiante.setText("Reservas activas hoy: " + reservasActivasHoy + " / " + limite);
            
            if (reservasActivasHoy >= limite) {
                vista.lblNombreEstudiante.setForeground(Color.RED);
                deshabilitarBotonesReserva();
            } else {
                vista.lblNombreEstudiante.setForeground(new Color(0, 102, 0));
            }
        }
    }

    private void deshabilitarBotonesReserva() {
        for (JButton btn : btnReservar) {
            btn.setEnabled(false);
            if (!Sesion.getInstancia().esEstudiante()) btn.setText("Inicie sesión");
        }
    }

    // ================= CARGA Y VISUALIZACIÓN DE DATOS =================
    private void cargarLibrosIniciales() {
        librosActuales = libroDAO.listarLibrosDisponibles(6);
        mostrarLibrosEnPaneles();
    }

    private void mostrarLibrosEnPaneles() {
        // CORRECCIÓN: Ocultar paneles Y botones para evitar "fantasmas"
        for (int i = 0; i < panelesLibros.length; i++) {
            panelesLibros[i].setVisible(false);
            btnReservar[i].setVisible(false); 
        }

        // Mostrar libros recuperados
        for (int i = 0; i < librosActuales.size() && i < 6; i++) {
            LibroCompleto libro = librosActuales.get(i);
            mostrarLibroEnPanel(libro, i);
        }
    }

    private void mostrarLibroEnPanel(LibroCompleto libro, int index) {
        panelesLibros[index].setVisible(true);
        btnReservar[index].setVisible(true); // <--- Asegurar que el botón se vea
        
        lblTitulos[index].setText(truncarTexto(libro.getTitulo(), 25));
        lblAutores[index].setText("Autor: " + libro.getNombreAutor());
        lblGeneros[index].setText("Género: " + libro.getNombreGenero());
        lblDisponibles[index].setText("Disponibles: " + libro.getCantidadDisponible());
        
        btnReservar[index].putClientProperty("id_libro", libro.getIdLibro());
        
        if (libro.getCantidadDisponible() > 0) {
            lblDisponibles[index].setForeground(new Color(40, 167, 69));
            btnReservar[index].setEnabled(true);
            btnReservar[index].setText("Reservar");
        } else {
            lblDisponibles[index].setForeground(new Color(220, 53, 69));
            btnReservar[index].setEnabled(false);
            btnReservar[index].setText("Agotado");
        }
    }

    private String truncarTexto(String texto, int maxLength) {
        if (texto == null) return "";
        return (texto.length() <= maxLength) ? texto : texto.substring(0, maxLength - 3) + "...";
    }

    // ================= BÚSQUEDA Y REPORTES =================
    private void configurarBusquedaEnTiempoReal() {
        if (vista.jtxt_buscarLibros != null) {
            vista.jtxt_buscarLibros.getDocument().addDocumentListener(new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { buscarLibros(); }
                public void removeUpdate(DocumentEvent e) { buscarLibros(); }
                public void changedUpdate(DocumentEvent e) { buscarLibros(); }
            });

            // Placeholder logic
            vista.jtxt_buscarLibros.setText("Buscar por título, autor o género...");
            vista.jtxt_buscarLibros.setForeground(Color.GRAY);
            vista.jtxt_buscarLibros.addFocusListener(new java.awt.event.FocusAdapter() {
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (vista.jtxt_buscarLibros.getText().equals("Buscar por título, autor o género...")) {
                        vista.jtxt_buscarLibros.setText("");
                        vista.jtxt_buscarLibros.setForeground(Color.BLACK);
                    }
                }
            });
        }
    }

    private void buscarLibros() {
        String termino = vista.jtxt_buscarLibros.getText().trim();
        if (termino.isEmpty() || termino.equals("Buscar por título, autor o género...")) {
            cargarLibrosIniciales();
        } else {
            librosActuales = libroDAO.buscarLibros(termino, 6);
            mostrarLibrosEnPaneles();
        }
    }

    private void generarReportePDF() {
        if (librosActuales == null || librosActuales.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "No hay libros en la lista para exportar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Aquí llamarás a tu clase de utilidad para PDF
        JOptionPane.showMessageDialog(vista, "Iniciando descarga del catálogo en PDF...", "Reporte", JOptionPane.INFORMATION_MESSAGE);
    }

    // ================= ACCIONES DE RESERVA =================
    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < btnReservar.length; i++) {
            if (e.getSource() == btnReservar[i]) {
                reservarLibro(i);
                break;
            }
        }
    }

    private void reservarLibro(int indicePanel) {
        if (!Sesion.getInstancia().esEstudiante()) {
            JOptionPane.showMessageDialog(vista, "Inicie sesión como estudiante.", "Sesión", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Object idLibroObj = btnReservar[indicePanel].getClientProperty("id_libro");
        if (idLibroObj == null) return;
        
        int idLibro = (int) idLibroObj;

        if (reservaDAO.contarReservasHoy(idEstudianteActual) >= 3) {
            JOptionPane.showMessageDialog(vista, "Límite de 3 reservas diarias alcanzado.", "Límite", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista, "¿Confirmar reserva de: " + lblTitulos[indicePanel].getText() + "?", "Confirmar", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (reservaDAO.registrarReserva(idEstudianteActual, idLibro)) {
                mostrarMensajeExito();
                refrescarVista();
                verificarSesionEstudiante();
            }
        }
    }

    private void mostrarMensajeExito() {
        JOptionPane.showMessageDialog(vista, "¡Reserva exitosa! Tienes 24 horas para recoger el libro.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    public void refrescarVista() {
        buscarLibros(); // Refresca manteniendo el criterio de búsqueda
        for (JPanel panel : panelesLibros) {
            AppUtils.animarRefresco(panel);
        }
    }

    private void configurarBotonesReserva() {
        for (JButton btn : btnReservar) {
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(new Color(30, 147, 59)); }
                public void mouseExited(MouseEvent e) { if (btn.isEnabled()) btn.setBackground(new Color(40, 167, 69)); }
            });
        }
    }
}