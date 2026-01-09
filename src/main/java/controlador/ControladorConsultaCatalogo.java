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
import java.util.List;
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
        
        // Aplicar estilos y configuraciones
        aplicarEstilosVisuales();
        configurarEfectosHover();
        configurarBotonesReserva();
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
        
        // Asignar listeners a todos los botones
        for (JButton btn : btnReservar) {
            btn.addActionListener(this);
        }
    }

    // ================= MEJORAS VISUALES =================
    private void aplicarEstilosVisuales() {
        // Estilo para botones de reserva
        Color colorReserva = new Color(40, 167, 69); // Verde Bootstrap
        for (JButton btn : btnReservar) {
            AppUtils.estiloBotonModerno(btn, colorReserva, "Reservar");
        }
        
        // Estilo para el campo de búsqueda
        if (vista.jtxt_buscarLibros != null) {
            vista.jtxt_buscarLibros.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            vista.jtxt_buscarLibros.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }
        
        // Estilo para paneles de libros
        Color colorBorde = new Color(233, 236, 239);
        for (JPanel panel : panelesLibros) {
            panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBorde, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            panel.setBackground(Color.WHITE);
        }
        
        // Estilo para etiquetas de título
        for (JLabel lbl : lblTitulos) {
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
            lbl.setForeground(new Color(33, 37, 41));
        }
        
        // Estilo para etiquetas de autor y género
        for (JLabel lbl : lblAutores) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(new Color(108, 117, 125));
        }
        
        for (JLabel lbl : lblGeneros) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            lbl.setForeground(new Color(108, 117, 125));
        }
        
        // Estilo para etiquetas de disponibilidad
        for (JLabel lbl : lblDisponibles) {
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        }
    }

    private void configurarEfectosHover() {
        // Efecto hover para los paneles de libros
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
        
        // Efecto hover para el campo de búsqueda
        if (vista.jtxt_buscarLibros != null) {
            AppUtils.agregarEfectoHoverCampo(vista.jtxt_buscarLibros, 
                new Color(0, 123, 255), new Color(206, 212, 218));
        }
    }

    private void configurarEventosPaneles() {
        // Doble clic en paneles para reservar
        for (int i = 0; i < panelesLibros.length; i++) {
            final int index = i;
            panelesLibros[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
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
        // Llamada al DAO con la nueva lógica de conteo
        int reservasActivasHoy = reservaDAO.contarReservasEstudianteHoy(idEstudianteActual);
        int limite = 3;

        // Actualización del label
        vista.lblNombreEstudiante.setText("Reservas activas hoy: " + reservasActivasHoy + " / " + limite);
        
        // Feedback visual
        if (reservasActivasHoy >= limite) {
            vista.lblNombreEstudiante.setForeground(Color.RED);
            deshabilitarBotonesReserva(); // Bloquea nuevos intentos
        } else {
            vista.lblNombreEstudiante.setForeground(new Color(0, 102, 0)); // Verde si tiene cupo
        }
    }
}

    private void deshabilitarBotonesReserva() {
        for (JButton btn : btnReservar) {
            btn.setEnabled(false);
            btn.setText("Inicie sesión");
        }
    }

    // ================= CARGA DE DATOS =================
    private void cargarLibrosIniciales() {
        librosActuales = libroDAO.listarLibrosDisponibles(6);
        mostrarLibrosEnPaneles();
    }

    private void mostrarLibrosEnPaneles() {
        // Ocultar todos los paneles primero
        for (JPanel panel : panelesLibros) {
            panel.setVisible(false);
        }
        
        // Mostrar libros en los paneles correspondientes
        for (int i = 0; i < librosActuales.size() && i < 6; i++) {
            LibroCompleto libro = librosActuales.get(i);
            mostrarLibroEnPanel(libro, i);
        }
    }

    private void mostrarLibroEnPanel(LibroCompleto libro, int index) {
        // Mostrar el panel
        panelesLibros[index].setVisible(true);
        
        // Configurar datos
        lblTitulos[index].setText(truncarTexto(libro.getTitulo(), 25));
        lblAutores[index].setText("Autor: " + libro.getNombreAutor());
        lblGeneros[index].setText("Género: " + libro.getNombreGenero());
        lblDisponibles[index].setText("Disponibles: " + libro.getCantidadDisponible());
        
        // Guardar el ID del libro en el botón para referencia
        btnReservar[index].putClientProperty("id_libro", libro.getIdLibro());
        
        // Configurar color según disponibilidad
        if (libro.getCantidadDisponible() > 0) {
            lblDisponibles[index].setForeground(new Color(40, 167, 69)); // Verde
            btnReservar[index].setEnabled(true);
            btnReservar[index].setText("Reservar");
        } else {
            lblDisponibles[index].setForeground(new Color(220, 53, 69)); // Rojo
            btnReservar[index].setEnabled(false);
            btnReservar[index].setText("Agotado");
        }
        
        // Tooltips informativos
        panelesLibros[index].setToolTipText("<html><b>" + libro.getTitulo() + "</b><br>" +
            "Autor: " + libro.getNombreAutor() + "<br>" +
            "Género: " + libro.getNombreGenero() + "<br>" +
            "Disponibles: " + libro.getCantidadDisponible() + "</html>");
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
            
            // Placeholder visual
            vista.jtxt_buscarLibros.setText("Buscar por título, autor o género...");
            vista.jtxt_buscarLibros.setForeground(Color.GRAY);
            
            vista.jtxt_buscarLibros.addFocusListener(new java.awt.event.FocusAdapter() {
                @Override
                public void focusGained(java.awt.event.FocusEvent evt) {
                    if (vista.jtxt_buscarLibros.getText().equals("Buscar por título, autor o género...")) {
                        vista.jtxt_buscarLibros.setText("");
                        vista.jtxt_buscarLibros.setForeground(Color.BLACK);
                    }
                }
                
                @Override
                public void focusLost(java.awt.event.FocusEvent evt) {
                    if (vista.jtxt_buscarLibros.getText().isEmpty()) {
                        vista.jtxt_buscarLibros.setText("Buscar por título, autor o género...");
                        vista.jtxt_buscarLibros.setForeground(Color.GRAY);
                    }
                }
            });
        }
    }

    private void buscarLibros() {
        String termino = vista.jtxt_buscarLibros.getText().trim();
        
        // Si el texto es el placeholder o está vacío, cargar iniciales
        if (termino.isEmpty() || termino.equals("Buscar por título, autor o género...")) {
            cargarLibrosIniciales();
            return;
        }
        
        librosActuales = libroDAO.buscarLibros(termino, 6);
        mostrarLibrosEnPaneles();
    }

    // ================= CONFIGURACIÓN DE BOTONES =================
    private void configurarBotonesReserva() {
        // Los listeners ya se asignaron en inicializarArraysComponentes()
        // Aquí podemos añadir efectos adicionales si es necesario
        for (JButton btn : btnReservar) {
            btn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (btn.isEnabled()) {
                        btn.setBackground(new Color(30, 147, 59)); // Verde más oscuro
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (btn.isEnabled()) {
                        btn.setBackground(new Color(40, 167, 69)); // Verde original
                    }
                }
            });
        }
    }

    // ================= RESERVA DE LIBROS =================
    @Override
    public void actionPerformed(ActionEvent e) {
        // Identificar qué botón de reserva se presionó
        for (int i = 0; i < btnReservar.length; i++) {
            if (e.getSource() == btnReservar[i]) {
                reservarLibro(i);
                break;
            }
        }
    }

    private void reservarLibro(int indicePanel) {
        // Verificar sesión
        if (!Sesion.getInstancia().esEstudiante()) {
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
        if (!verificarLimiteReservasDiarias()) {
            JOptionPane.showMessageDialog(vista,
                "Has alcanzado el límite de 3 reservas diarias.",
                "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Obtener información del libro para el mensaje de confirmación
        String tituloLibro = lblTitulos[indicePanel].getText();
        String autorLibro = lblAutores[indicePanel].getText().replace("Autor: ", "");
        
        // Mostrar diálogo de confirmación con estilo
        int confirmacion = mostrarDialogoConfirmacion(tituloLibro, autorLibro);
        
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Intentar reservar el libro
        boolean exito = reservaDAO.registrarReserva(idEstudianteActual, idLibro);
        
        if (exito) {
            // Mostrar mensaje de éxito con estilo
            mostrarMensajeExito();
            
            // Actualizar disponibilidad en la UI
            actualizarDisponibilidadDespuesDeReserva(indicePanel);
            
            // Refrescar la vista
            refrescarVista();
        } else {
            // El DAO ya mostró el mensaje de error específico
            // Podemos añadir algún feedback visual adicional
            AppUtils.animarError(btnReservar[indicePanel]);
        }
    }

    private boolean verificarLimiteReservasDiarias() {
        int reservasHoy = reservaDAO.contarReservasHoy(idEstudianteActual);
        return reservasHoy < 3; // Límite de 3 reservas diarias
    }

    private int mostrarDialogoConfirmacion(String titulo, String autor) {
        // Crear un panel personalizado para el diálogo
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Icono de advertencia
        JLabel icono = new JLabel(UIManager.getIcon("OptionPane.questionIcon"));
        icono.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        
        // Panel de texto
        JPanel panelTexto = new JPanel(new GridLayout(3, 1, 5, 5));
        panelTexto.add(new JLabel("<html><b>¿Confirmar reserva?</b></html>"));
        panelTexto.add(new JLabel("<html>Título: <font color='#0066cc'>" + titulo + "</font></html>"));
        panelTexto.add(new JLabel("<html>Autor: <font color='#666666'>" + autor + "</font></html>"));
        
        panel.add(icono, BorderLayout.WEST);
        panel.add(panelTexto, BorderLayout.CENTER);
        
        // Mostrar diálogo personalizado
        return JOptionPane.showConfirmDialog(vista, panel,
            "Confirmar reserva", JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
    }

    private void mostrarMensajeExito() {
        // Diálogo de éxito personalizado
        JOptionPane.showMessageDialog(vista,
            "<html><div style='text-align: center;'>" +
            "<h3 style='color: #28a745;'>✓ Reserva exitosa</h3>" +
            "<p>El libro ha sido reservado correctamente.</p>" +
            "<p><small>Tienes 24 horas para retirarlo en la biblioteca.</small></p>" +
            "</div></html>",
            "Reserva completada",
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarDisponibilidadDespuesDeReserva(int indicePanel) {
        String textoActual = lblDisponibles[indicePanel].getText();
        try {
            int disponibles = Integer.parseInt(textoActual.replaceAll("[^0-9]", ""));
            
            if (disponibles > 1) {
                disponibles--;
                lblDisponibles[indicePanel].setText("Disponibles: " + disponibles);
                AppUtils.animarActualizacion(lblDisponibles[indicePanel]);
            } else {
                // Último ejemplar reservado
                lblDisponibles[indicePanel].setText("AGOTADO");
                lblDisponibles[indicePanel].setForeground(new Color(220, 53, 69));
                btnReservar[indicePanel].setEnabled(false);
                btnReservar[indicePanel].setText("Agotado");
                
                // Animación de agotado
                AppUtils.animarAgotado(panelesLibros[indicePanel]);
            }
        } catch (NumberFormatException e) {
            // Si hay error en el parseo, recargar todo
            refrescarVista();
        }
    }

    // ================= MÉTODOS PÚBLICOS =================
    public void refrescarVista() {
        cargarLibrosIniciales();
        
        // Efecto visual de refresco
        for (JPanel panel : panelesLibros) {
            AppUtils.animarRefresco(panel);
        }
    }
    
    public void actualizarSesion() {
        verificarSesionEstudiante();
    }
}