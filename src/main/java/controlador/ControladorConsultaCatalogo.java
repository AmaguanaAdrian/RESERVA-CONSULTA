/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.awt.event.MouseListener;
import java.util.List;
import utilidades.ReporteadorPDF;

/**
 *
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

        // Inicializar arrays con los componentes de la vista
        inicializarArraysComponentes();

        // Verificar sesión
        verificarSesionEstudiante();

        // Aplicar estilos básicos
        aplicarEstilosVisuales();

        // Cargar libros iniciales
        cargarLibrosIniciales();

        // Configurar búsqueda en tiempo real
        configurarBusquedaEnTiempoReal();

        // Configurar eventos de los paneles
        configurarEventosPaneles();

        // Configurar el botón de generar PDF (si existe)
        configurarBotonGenerarPDF();
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

        // Asignar listeners a todos los botones de reserva
        for (JButton btn : btnReservar) {
            btn.addActionListener(this);
        }
    }

    // ================= CONFIGURAR BOTÓN GENERAR PDF =================
    private void configurarBotonGenerarPDF() {
        // Verificar si el botón existe en la vista
        if (vista.btnGenerarPdf != null) {
            // Aplicar estilo al botón PDF
            Color colorPDF = new Color(31, 115, 183); // Azul Bootstrap
            AppUtils.estiloBotonModerno(vista.btnGenerarPdf, colorPDF, "Generar PDF");

            // Agregar icono de PDF (opcional)
            try {
                // Usar un icono de PDF si tienes recursos, sino dejar solo texto
                vista.btnGenerarPdf.setIcon(new javax.swing.ImageIcon(
                        getClass().getResource("/iconos/pdf_icon.png")
                ));
            } catch (Exception e) {
                // Si no hay icono, solo texto
            }

            // Tooltip informativo
            vista.btnGenerarPdf.setToolTipText(
                    "<html>Generar reporte PDF con el catálogo completo<br>"
                    + "<small>Incluye todos los libros disponibles en el sistema</small></html>"
            );

            // Asignar acción al botón
            vista.btnGenerarPdf.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    generarReportePDF();
                }
            });

            // Efecto hover adicional
            vista.btnGenerarPdf.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (vista.btnGenerarPdf.isEnabled()) {
                        vista.btnGenerarPdf.setBackground(new Color(31, 115, 183));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (vista.btnGenerarPdf.isEnabled()) {
                        vista.btnGenerarPdf.setBackground(colorPDF);
                    }
                }
            });
        }
    }

    // ================= GENERAR REPORTE PDF =================
    private void generarReportePDF() {
        // Mostrar indicador de carga (opcional)
        vista.btnGenerarPdf.setEnabled(false);
        vista.btnGenerarPdf.setText("Generando...");

        // Obtener TODOS los libros del catálogo (no solo los 6 que se muestran)
        // Necesitamos agregar este método al LibroDAO
        List<LibroCompleto> catalogoCompleto = libroDAO.obtenerCatalogoCompleto();

        if (catalogoCompleto == null || catalogoCompleto.isEmpty()) {
            JOptionPane.showMessageDialog(vista,
                    "No hay libros en el catálogo para generar el reporte.",
                    "Catálogo vacío",
                    JOptionPane.WARNING_MESSAGE);

            vista.btnGenerarPdf.setEnabled(true);
            vista.btnGenerarPdf.setText("Generar PDF");
            return;
        }

        // Mostrar diálogo de confirmación
        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "<html><div style='text-align: center;'>"
                + "<h3 style='color: #0066cc;'>Generar Reporte PDF</h3>"
                + "<p>Se generará un reporte PDF con <b>" + catalogoCompleto.size() + " libros</b>.</p>"
                + "<p><small>Seleccione la ubicación para guardar el archivo.</small></p>"
                + "</div></html>",
                "Confirmar generación de PDF",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirmacion != JOptionPane.YES_OPTION) {
            vista.btnGenerarPdf.setEnabled(true);
            vista.btnGenerarPdf.setText("Generar PDF");
            return;
        }

        // Llamar al ReporteadorPDF
        ReporteadorPDF.generarReporteCatalogo(catalogoCompleto);

        // Restaurar estado del botón
        vista.btnGenerarPdf.setEnabled(true);
        vista.btnGenerarPdf.setText("Generar PDF");

        // Feedback visual
        AppUtils.animarActualizacion(vista.btnGenerarPdf);
    }

    // ================= ESTILOS VISUALES =================
    private void aplicarEstilosVisuales() {
        // Estilo básico para botones (ahora usando AppUtils para consistencia)
        Color colorVerde = new Color(40, 167, 69);
        for (JButton btn : btnReservar) {
            AppUtils.estiloBotonModerno(btn, colorVerde, "Reservar");
        }

        // Estilo para el campo de búsqueda
        if (vista.jtxt_buscarLibros != null) {
            vista.jtxt_buscarLibros.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        }
    }

    // ================= CONFIGURAR EVENTOS DE PANELES =================
    private void configurarEventosPaneles() {
        // Doble clic en paneles para reservar
        for (int i = 0; i < panelesLibros.length; i++) {
            final int index = i;
            panelesLibros[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && panelesLibros[index].isVisible()) {
                        reservarLibro(index);
                    }
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    if (panelesLibros[index].isVisible()) {
                        panelesLibros[index].setBackground(new Color(240, 240, 240));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (panelesLibros[index].isVisible()) {
                        panelesLibros[index].setBackground(Color.WHITE);
                    }
                }
            });
        }
    }

    // ================= VERIFICACIÓN DE SESIÓN =================
    private void verificarSesionEstudiante() {
        Sesion sesion = Sesion.getInstancia();
        if (!sesion.esEstudiante()) {
            // No es estudiante, deshabilitar botones
            for (JButton btn : btnReservar) {
                btn.setEnabled(false);
                btn.setText("Inicie sesión");
                btn.setBackground(new Color(200, 200, 200));
            }

            if (vista.lblNombreEstudiante != null) {
                vista.lblNombreEstudiante.setText("Inicie sesión como estudiante");
            }
            return;
        }

        idEstudianteActual = sesion.getIdEstudiante();

        if (vista.lblNombreEstudiante != null) {
            int reservasActivasHoy = reservaDAO.contarReservasEstudianteHoy(idEstudianteActual);
            vista.lblNombreEstudiante.setText("Reservas activas hoy: " + reservasActivasHoy + " / 3");

            // Color según límite
            if (reservasActivasHoy >= 3) {
                vista.lblNombreEstudiante.setForeground(Color.RED);
            } else {
                vista.lblNombreEstudiante.setForeground(new Color(0, 100, 0));
            }
        }
    }

    // ================= CARGA DE DATOS =================
    private void cargarLibrosIniciales() {
        librosActuales = libroDAO.listarLibrosDisponibles(6);
        mostrarLibrosEnPaneles();
    }

    private void mostrarLibrosEnPaneles() {
        // Ocultar TODOS los paneles primero
        ocultarTodosLosPaneles();

        // Si no hay libros, mostrar mensaje
        if (librosActuales == null || librosActuales.isEmpty()) {
            mostrarMensajeNoHayLibros();
            return;
        }

        // Mostrar solo los libros que existen
        for (int i = 0; i < librosActuales.size() && i < panelesLibros.length; i++) {
            LibroCompleto libro = librosActuales.get(i);
            mostrarLibroEnPanel(libro, i);
        }
    }

    private void ocultarTodosLosPaneles() {
        for (int i = 0; i < panelesLibros.length; i++) {
            panelesLibros[i].setVisible(false);
            btnReservar[i].setVisible(false);
            btnReservar[i].setEnabled(false); // IMPORTANTE: deshabilitar también
        }
    }

    private void mostrarMensajeNoHayLibros() {
        // Mostrar solo el primer panel con mensaje
        panelesLibros[0].setVisible(true);
        lblTitulos[0].setText("No hay libros disponibles");
        lblAutores[0].setText("");
        lblGeneros[0].setText("");
        lblDisponibles[0].setText("");
        btnReservar[0].setVisible(false);
        btnReservar[0].setEnabled(false);
    }

    private void mostrarLibroEnPanel(LibroCompleto libro, int index) {
        // Mostrar el panel
        panelesLibros[index].setVisible(true);
        btnReservar[index].setVisible(true);

        // Configurar datos del libro
        lblTitulos[index].setText(truncarTexto(libro.getTitulo(), 25));
        lblAutores[index].setText("Autor: " + libro.getNombreAutor());
        lblGeneros[index].setText("Género: " + libro.getNombreGenero());
        lblDisponibles[index].setText("Disponibles: " + libro.getCantidadDisponible());

        // Guardar el ID del libro en el botón
        btnReservar[index].putClientProperty("id_libro", libro.getIdLibro());

        // Verificar si el estudiante YA TIENE RESERVA ACTIVA de este libro
        boolean yaReservado = verificarSiYaReservado(libro.getIdLibro());

        // Configurar botón según disponibilidad y si ya está reservado
        if (libro.getCantidadDisponible() > 0 && !yaReservado) {
            // Libro disponible y NO reservado → Botón VERDE habilitado
            lblDisponibles[index].setForeground(new Color(0, 100, 0)); // Verde
            configurarBotonHabilitado(btnReservar[index], "Reservar", new Color(40, 167, 69)); // Verde
        } else if (yaReservado) {
            // Ya reservado → Botón ROJO deshabilitado
            lblDisponibles[index].setForeground(new Color(255, 140, 0)); // Naranja para disponibilidad
            configurarBotonYaReservado(btnReservar[index]); // Rojo con texto "Ya reservado"
        } else {
            // Agotado → Botón GRIS deshabilitado
            lblDisponibles[index].setForeground(Color.RED);
            configurarBotonDeshabilitado(btnReservar[index], "Agotado", new Color(108, 117, 125)); // Gris
        }
    }

    private boolean verificarSiYaReservado(int idLibro) {
        // Si no es estudiante, no puede tener reservas
        if (idEstudianteActual <= 0) {
            return false;
        }

        // Verificar si ya tiene reserva activa de este libro
        return reservaDAO.tieneReservaActiva(idEstudianteActual, idLibro);
    }

    private boolean verificarSiPuedeReservar(int idLibro) {
        // Si no es estudiante, no puede reservar
        if (idEstudianteActual <= 0) {
            return false;
        }

        // Verificar límite diario
        if (reservaDAO.contarReservasEstudianteHoy(idEstudianteActual) >= 3) {
            return false;
        }

        // Verificar si ya tiene reserva activa de este libro
        return !reservaDAO.tieneReservaActiva(idEstudianteActual, idLibro);
    }

    private void configurarBotonHabilitado(JButton boton, String texto, Color color) {
        boton.setEnabled(true);
        boton.setText(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }

    private void configurarBotonDeshabilitado(JButton boton, String texto, Color color) {
        boton.setEnabled(false);
        boton.setText(texto);
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setCursor(Cursor.getDefaultCursor());
    }

    private void configurarBotonYaReservado(JButton boton) {

        // 🔴 ELIMINAR TODOS LOS MOUSE LISTENERS PREVIOS (clave del problema)
        for (MouseListener ml : boton.getMouseListeners()) {
            boton.removeMouseListener(ml);
        }

        // Estado visual fijo
        boton.setEnabled(true); // ← importante para que detecte clicks
        boton.setText("Ya reservado");
        Color rojo = new Color(220, 53, 69);
        boton.setBackground(rojo);
        boton.setForeground(Color.WHITE);
        boton.setCursor(Cursor.getDefaultCursor());

        // Tooltip
        boton.setToolTipText("Ya tienes una reserva activa de este libro");

        // 🔒 BLOQUEAR HOVER (no cambia de color)
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(rojo);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(rojo);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(
                        boton,
                        "Este libro ya está reservado por ti.",
                        "Reserva existente",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
    }

    private String truncarTexto(String texto, int maxLength) {
        if (texto.length() <= maxLength) {
            return texto;
        }
        return texto.substring(0, maxLength - 3) + "...";
    }

    // ================= BÚSQUEDA EN TIEMPO REAL =================
    private void configurarBusquedaEnTiempoReal() {
        if (vista.jtxt_buscarLibros != null) {
            vista.jtxt_buscarLibros.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    buscarLibros();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    buscarLibros();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    buscarLibros();
                }
            });
        }
    }

    private void buscarLibros() {
        String termino = vista.jtxt_buscarLibros.getText().trim();

        // Si el texto está vacío, cargar iniciales
        if (termino.isEmpty()) {
            cargarLibrosIniciales();
            return;
        }

        // Buscar libros
        librosActuales = libroDAO.buscarLibros(termino, 6);

        // Mostrar resultados
        if (librosActuales == null || librosActuales.isEmpty()) {
            mostrarMensajeNoResultados(termino);
        } else {
            mostrarLibrosEnPaneles();
        }
    }

    private void mostrarMensajeNoResultados(String termino) {
        // Ocultar todos los paneles
        ocultarTodosLosPaneles();

        // Mostrar mensaje en el primer panel
        panelesLibros[0].setVisible(true);
        lblTitulos[0].setText("No se encontraron resultados");
        lblAutores[0].setText("Búsqueda: \"" + truncarTexto(termino, 20) + "\"");
        lblGeneros[0].setText("");
        lblDisponibles[0].setText("");
        btnReservar[0].setVisible(false);
        btnReservar[0].setEnabled(false);
    }

    // ================= RESERVA DE LIBROS =================
    @Override
    public void actionPerformed(ActionEvent e) {
        // Identificar qué botón de reserva se presionó
        for (int i = 0; i < btnReservar.length; i++) {
            if (e.getSource() == btnReservar[i] && btnReservar[i].isEnabled()) {
                reservarLibro(i);
                break;
            }
        }
    }

    private void reservarLibro(int indicePanel) {
        // Verificar que el panel esté visible
        if (!panelesLibros[indicePanel].isVisible()) {
            return;
        }

        // Verificar sesión
        Sesion sesion = Sesion.getInstancia();
        if (!sesion.esEstudiante()) {
            JOptionPane.showMessageDialog(vista,
                    "Debes iniciar sesión como estudiante para reservar libros.",
                    "Sesión requerida", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener ID del libro
        Object idLibroObj = btnReservar[indicePanel].getClientProperty("id_libro");
        if (idLibroObj == null) {
            JOptionPane.showMessageDialog(vista,
                    "Error: No se pudo identificar el libro.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int idLibro = (int) idLibroObj;

        // Verificar límite de reservas diarias
        if (reservaDAO.contarReservasEstudianteHoy(idEstudianteActual) >= 3) {
            JOptionPane.showMessageDialog(vista,
                    "Has alcanzado el límite de 3 reservas diarias.",
                    "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar si ya tiene reservado este libro
        if (reservaDAO.tieneReservaActiva(idEstudianteActual, idLibro)) {
            JOptionPane.showMessageDialog(vista,
                    "Ya tienes una reserva activa de este libro.",
                    "Reserva duplicada", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Obtener información del libro
        String tituloLibro = lblTitulos[indicePanel].getText();

        // Confirmar reserva
        int confirmacion = JOptionPane.showConfirmDialog(vista,
                "¿Confirmar reserva del libro: " + tituloLibro + "?",
                "Confirmar reserva", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        // Intentar reservar el libro
        boolean exito = reservaDAO.registrarReserva(idEstudianteActual, idLibro);

        if (exito) {
            JOptionPane.showMessageDialog(vista,
                    "✓ Reserva exitosa\n"
                    + "El libro ha sido reservado correctamente.\n"
                    + "Tienes 24 horas para retirarlo.",
                    "Reserva completada", JOptionPane.INFORMATION_MESSAGE);

            // Actualizar la interfaz
            actualizarInterfazDespuesDeReserva(indicePanel);

            // Actualizar contador de reservas
            verificarSesionEstudiante();

            // Refrescar la vista
            refrescarVista();
        }
    }

    private void actualizarInterfazDespuesDeReserva(int indicePanel) {
        // Actualizar disponibilidad
        String textoActual = lblDisponibles[indicePanel].getText();
        try {
            int disponibles = Integer.parseInt(textoActual.replaceAll("[^0-9]", ""));

            if (disponibles > 1) {
                disponibles--;
                lblDisponibles[indicePanel].setText("Disponibles: " + disponibles);

                // Cambiar botón a "Ya reservado" en ROJO
                configurarBotonYaReservado(btnReservar[indicePanel]);

                // Añadir tooltip explicativo
                panelesLibros[indicePanel].setToolTipText(
                        "Ya tienes una reserva activa de este libro. "
                        + "Disponibles: " + disponibles
                );
            } else {
                // Último ejemplar reservado
                lblDisponibles[indicePanel].setText("AGOTADO");
                lblDisponibles[indicePanel].setForeground(Color.RED);
                configurarBotonDeshabilitado(btnReservar[indicePanel], "Agotado", new Color(108, 117, 125));
            }
        } catch (NumberFormatException e) {
            // Si hay error, simplemente recargar
            refrescarVista();
        }
    }

    // ================= MÉTODOS PÚBLICOS =================
    public void refrescarVista() {
        cargarLibrosIniciales();
    }
}
