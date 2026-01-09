/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import Utilidades.AppUtils;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.DialogBibliotecario;
import vista.GestionEstudiantes;
/**
 *
 * @author amagu
 */
public class ControladorGestionEstudiantes implements ActionListener {

    private GestionEstudiantes vista;
    private UsuarioDAO usuarioDAO;
    private DefaultTableModel modeloEstudiantes;

    public ControladorGestionEstudiantes(GestionEstudiantes vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();

        aplicarEstilosVisuales();
        configurarTablaMejorada();
        cargarEstudiantes();
        configurarMenuClickDerechoMejorado();
        agregarEfectosHover();
        configurarBusquedaEnTiempoReal();

        vista.jbtn_AgregarEstudiantes.addActionListener(this);
        // Si tienes botón de búsqueda, si no, solo la búsqueda en tiempo real
        // vista.jbtn_BuscarEstudiantes.addActionListener(this);
    }

    // ================= APLICAR ESTILOS =================
    private void aplicarEstilosVisuales() {
        // Estilo para botones
        aplicarEstiloBoton(vista.jbtn_AgregarEstudiantes, new Color(40, 167, 69), "Agregar Estudiante");
        // Si tienes botón de búsqueda, aplicar estilo también
        // aplicarEstiloBoton(vista.jbtn_BuscarEstudiantes, new Color(23, 162, 184), "Buscar");

        // Configurar placeholder en el campo de búsqueda
        if (vista.jtxt_BuscarEstudiantes != null) {
            agregarPlaceholder(vista.jtxt_BuscarEstudiantes, "Buscar estudiantes...");
        }
    }

    private void aplicarEstiloBoton(JButton boton, Color color, String texto) {
        boton.setText(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(color.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(color);
            }
        });
    }

    private void agregarPlaceholder(JTextField campo, String placeholder) {
        campo.setText(placeholder);
        campo.setForeground(Color.GRAY);

        campo.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (campo.getText().equals(placeholder)) {
                    campo.setText("");
                    campo.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (campo.getText().isEmpty()) {
                    campo.setText(placeholder);
                    campo.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void agregarEfectosHover() {
        // Efecto para campo de búsqueda
        if (vista.jtxt_BuscarEstudiantes != null) {
            agregarEfectoHoverCampo(vista.jtxt_BuscarEstudiantes);
        }
    }

    private void agregarEfectoHoverCampo(JTextField campo) {
        campo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                campo.setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                campo.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        });
    }

    // ================= CONFIGURAR TABLA =================
    private void configurarTablaMejorada() {
        // Columnas: Cédula, Nombres, Apellidos, Correo, Estado (sin ID)
        modeloEstudiantes = new DefaultTableModel(new String[]{"Cédula", "Nombres", "Apellidos", "Correo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vista.jTable1.setModel(modeloEstudiantes);
        estilizarTabla(vista.jTable1, new Color(0, 51, 102));
    }

    private void estilizarTabla(JTable tabla, Color colorHeader) {
        tabla.setRowHeight(30);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tabla.setSelectionBackground(new Color(220, 247, 255));
        tabla.setSelectionForeground(Color.BLACK);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(colorHeader);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Centrar algunas columnas si es necesario
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // Si quieres centrar la columna de estado, por ejemplo
        tabla.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
    }

    // ================= CARGAR ESTUDIANTES =================
    private void cargarEstudiantes() {
        modeloEstudiantes.setRowCount(0);
        List<Usuario> estudiantes = usuarioDAO.listarEstudiantes();

        for (Usuario u : estudiantes) {
            modeloEstudiantes.addRow(new Object[]{
                u.getCedula(),
                u.getNombres(),
                u.getApellidos(),
                u.getCorreo(),
                u.getEstado()
            });
        }
    }

    // ================= BÚSQUEDA EN TIEMPO REAL =================
    private void configurarBusquedaEnTiempoReal() {
        if (vista.jtxt_BuscarEstudiantes != null) {
            vista.jtxt_BuscarEstudiantes.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    buscarEstudiantes();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    buscarEstudiantes();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    buscarEstudiantes();
                }
            });
        }
    }

    private void buscarEstudiantes() {
        String texto = vista.jtxt_BuscarEstudiantes != null
                ? vista.jtxt_BuscarEstudiantes.getText().trim().toLowerCase() : "";

        if (texto.isEmpty() || texto.equals("buscar estudiantes...")) {
            cargarEstudiantes();
            return;
        }

        modeloEstudiantes.setRowCount(0);
        for (Usuario u : usuarioDAO.listarEstudiantes()) {
            if (u.getCedula().toLowerCase().contains(texto)
                    || u.getNombres().toLowerCase().contains(texto)
                    || u.getApellidos().toLowerCase().contains(texto)
                    || u.getCorreo().toLowerCase().contains(texto)
                    || u.getEstado().toLowerCase().contains(texto)) {

                modeloEstudiantes.addRow(new Object[]{
                    u.getCedula(),
                    u.getNombres(),
                    u.getApellidos(),
                    u.getCorreo(),
                    u.getEstado()
                });
            }
        }
    }

    // ================= MENÚ CONTEXTUAL =================
    private void configurarMenuClickDerechoMejorado() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));

        JMenuItem editar = new JMenuItem("Editar");
        JMenuItem eliminar = new JMenuItem("Eliminar");

        editar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        eliminar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        editar.addActionListener(e -> editarEstudiante());
        eliminar.addActionListener(e -> eliminarEstudiante());

        menu.add(editar);
        menu.addSeparator();
        menu.add(eliminar);

        vista.jTable1.setComponentPopupMenu(menu);

        // Doble click para editar
        vista.jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarEstudiante();
                }
            }
        });
    }

    // ================= EVENTOS DE BOTONES =================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtn_AgregarEstudiantes) {
            abrirDialogRegistro(null);
        }
    }

    // ================= DIALOGO DE ESTUDIANTE =================
    private void abrirDialogRegistro(Usuario estudianteExistente) {
        Window parent = SwingUtilities.getWindowAncestor(vista);
        DialogBibliotecario dialog = new DialogBibliotecario((Frame) parent, true);

        // Ocultar errores inicialmente
        ocultarErroresDialogo(dialog);

        // Configurar para estudiante
        dialog.txtRol.setText("ESTUDIANTE");
        dialog.txtRol.setEnabled(false);

        // Si es edición, llenar campos
        if (estudianteExistente != null) {
            dialog.setTitle("Editar Estudiante");
            dialog.txtCedula.setText(estudianteExistente.getCedula());
            dialog.txtCedula.setEnabled(false); // No se puede editar la cédula
            dialog.txtNombres.setText(estudianteExistente.getNombres());
            dialog.txtApellidos.setText(estudianteExistente.getApellidos());
            dialog.txtEmail.setText(estudianteExistente.getCorreo());
            dialog.cmbEstado.setSelectedItem(estudianteExistente.getEstado());
        } else {
            dialog.setTitle("Agregar Nuevo Estudiante");
            // Solo números en cédula
            AppUtils.soloNumeros(dialog.txtCedula, 10);
        }

        // Configurar validaciones con Enter
        configurarEnterDialogo(dialog);

        // Botón Guardar
        dialog.btnGuardar.addActionListener(e -> guardarEstudiante(dialog, estudianteExistente));
        dialog.btnCancelar.addActionListener(e -> dialog.dispose());

        // Botones para mostrar/ocultar contraseña
        dialog.btnVerPass1.addActionListener(e -> AppUtils.togglePassword(dialog.txtPassword));
        dialog.btnVerPass2.addActionListener(e -> AppUtils.togglePassword(dialog.txtConfirmar));

        // Posicionar a la izquierda
        posicionarDialogoIzquierda(dialog, parent);
        dialog.setVisible(true);
    }

    private void ocultarErroresDialogo(DialogBibliotecario d) {
        d.lblErrorCedula.setVisible(false);
        d.lblErrorNombres.setVisible(false);
        d.lblErrorApellidos.setVisible(false);
        d.lblErrorEmail.setVisible(false);
        d.lblErrorPassword.setVisible(false);
    }

    private void configurarEnterDialogo(DialogBibliotecario d) {
        // Configurar validaciones con Enter, igual que en bibliotecarios
        AppUtils.validarConEnter(d.txtCedula, d.txtNombres, () -> {
            AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
            if (!AppUtils.validarCedulaEcuatoriana(d.txtCedula.getText().trim())) {
                AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula inválida");
                return false;
            }
            return true;
        });

        AppUtils.validarConEnter(d.txtNombres, d.txtApellidos, () -> {
            AppUtils.limpiarError(d.txtNombres, d.lblErrorNombres);
            if (d.txtNombres.getText().trim().split("\\s+").length < 2) {
                AppUtils.marcarError(d.txtNombres, d.lblErrorNombres,
                        "Ingrese mínimo 2 nombres");
                return false;
            }
            return true;
        });

        AppUtils.validarConEnter(d.txtApellidos, d.txtEmail, () -> {
            AppUtils.limpiarError(d.txtApellidos, d.lblErrorApellidos);
            if (d.txtApellidos.getText().trim().split("\\s+").length < 2) {
                AppUtils.marcarError(d.txtApellidos, d.lblErrorApellidos,
                        "Ingrese mínimo 2 apellidos");
                return false;
            }
            return true;
        });

        AppUtils.validarConEnter(d.txtEmail, d.txtPassword, () -> {
            AppUtils.limpiarError(d.txtEmail, d.lblErrorEmail);
            if (!AppUtils.validarCorreo(d.txtEmail.getText().trim())) {
                AppUtils.marcarError(d.txtEmail, d.lblErrorEmail, "Correo inválido");
                return false;
            }
            return true;
        });

        AppUtils.validarConEnter(d.txtPassword, d.txtConfirmar, () -> {
            AppUtils.limpiarError(d.txtPassword, d.lblErrorPassword);
            if (!AppUtils.validarPassword(
                    new String(d.txtPassword.getPassword()))) {
                AppUtils.marcarError(d.txtPassword, d.lblErrorPassword,
                        "Contraseña inválida");
                return false;
            }
            return true;
        });

        AppUtils.enterEjecutaBoton(d.txtConfirmar, d.btnGuardar, () -> {
            if (!new String(d.txtPassword.getPassword())
                    .equals(new String(d.txtConfirmar.getPassword()))) {
                AppUtils.marcarCampoRojo(d.txtConfirmar);
                return false;
            }
            return true;
        });
    }

    private void guardarEstudiante(DialogBibliotecario dialog, Usuario estudianteExistente) {
        // Limpiar errores
        limpiarErroresDialogo(dialog);

        boolean valido = true;

        String cedula = dialog.txtCedula.getText().trim();
        String nombres = dialog.txtNombres.getText().trim();
        String apellidos = dialog.txtApellidos.getText().trim();
        String correo = dialog.txtEmail.getText().trim();
        String password = new String(dialog.txtPassword.getPassword());
        String confirmar = new String(dialog.txtConfirmar.getPassword());

        // Validaciones
        if (!AppUtils.validarCedulaEcuatoriana(cedula)) {
            AppUtils.marcarError(dialog.txtCedula, dialog.lblErrorCedula, "Cédula inválida");
            valido = false;
        }

        if (nombres.split("\\s+").length < 2) {
            AppUtils.marcarError(dialog.txtNombres, dialog.lblErrorNombres,
                    "Ingrese mínimo 2 nombres");
            valido = false;
        }

        if (apellidos.split("\\s+").length < 2) {
            AppUtils.marcarError(dialog.txtApellidos, dialog.lblErrorApellidos,
                    "Ingrese mínimo 2 apellidos");
            valido = false;
        }

        if (!AppUtils.validarCorreo(correo)) {
            AppUtils.marcarError(dialog.txtEmail, dialog.lblErrorEmail, "Correo inválido");
            valido = false;
        }

        // Solo validar contraseña si es nuevo o si se cambió
        if (estudianteExistente == null || !password.isEmpty()) {
            if (!AppUtils.validarPassword(password) || !password.equals(confirmar)) {
                AppUtils.marcarError(dialog.txtPassword, dialog.lblErrorPassword,
                        "Contraseña inválida o no coincide");
                AppUtils.marcarCampoRojo(dialog.txtConfirmar);
                valido = false;
            }
        }

        if (!valido) {
            return;
        }

        Usuario u = new Usuario();
        u.setCedula(cedula);
        u.setNombres(nombres);
        u.setApellidos(apellidos);
        u.setCorreo(correo);
        if (estudianteExistente == null || !password.isEmpty()) {
            u.setContrasena(password);
        }
        u.setEstado(dialog.cmbEstado.getSelectedItem().toString());
        u.setRol("ESTUDIANTE");

        boolean exito;
        String mensaje;

        if (estudianteExistente != null) {
            u.setIdUsuario(estudianteExistente.getIdUsuario());
            exito = usuarioDAO.editarEstudiante(u);
            mensaje = "Estudiante actualizado correctamente";
        } else {
            exito = usuarioDAO.agregarEstudiante(u);
            mensaje = "Estudiante registrado correctamente";
        }

        if (exito) {
            cargarEstudiantes();
            JOptionPane.showMessageDialog(dialog, mensaje, "Éxito", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        }
    }

    private void limpiarErroresDialogo(DialogBibliotecario d) {
        AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
        AppUtils.limpiarError(d.txtNombres, d.lblErrorNombres);
        AppUtils.limpiarError(d.txtApellidos, d.lblErrorApellidos);
        AppUtils.limpiarError(d.txtEmail, d.lblErrorEmail);
        AppUtils.limpiarError(d.txtPassword, d.lblErrorPassword);
        AppUtils.limpiarCampo(d.txtConfirmar);
    }

    // ================= POSICIONAMIENTO DE DIÁLOGOS =================
    private void posicionarDialogoIzquierda(Window dialog, Window parent) {
        Point parentLocation = parent.getLocation();
        Dimension parentSize = parent.getSize();

        dialog.pack();
        Dimension dialogSize = dialog.getSize();

        int x = parentLocation.x - dialogSize.width;
        int y = parentLocation.y + (parentSize.height - dialogSize.height) / 2;

        if (x < 0) {
            x = parentLocation.x + parentSize.width;
        }

        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        if (y < 0) {
            y = 0;
        }
        if (y + dialogSize.height > screenSize.height) {
            y = screenSize.height - dialogSize.height;
        }

        dialog.setLocation(x, y);
    }

    // ================= CRUD ESTUDIANTES (con menú contextual) =================
    private void editarEstudiante() {
        int fila = vista.jTable1.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista,
                    "Seleccione un estudiante de la tabla",
                    "Selección requerida",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String cedula = (String) modeloEstudiantes.getValueAt(fila, 0);
        Usuario estudiante = obtenerEstudiantePorCedula(cedula);

        if (estudiante != null) {
            abrirDialogRegistro(estudiante);
        }
    }

    private Usuario obtenerEstudiantePorCedula(String cedula) {
        for (Usuario u : usuarioDAO.listarEstudiantes()) {
            if (u.getCedula().equals(cedula)) {
                return u;
            }
        }
        return null;
    }

    private void eliminarEstudiante() {
        int fila = vista.jTable1.getSelectedRow();
        if (fila == -1) {
            return;
        }

        String cedula = (String) modeloEstudiantes.getValueAt(fila, 0);
        String nombres = (String) modeloEstudiantes.getValueAt(fila, 1);
        String apellidos = (String) modeloEstudiantes.getValueAt(fila, 2);

        int confirmado = JOptionPane.showConfirmDialog(vista,
                "¿Está seguro de eliminar este estudiante?\n\n"
                + "Cédula: " + cedula + "\n"
                + "Nombre: " + nombres + " " + apellidos + "\n"
                + "Esta acción no se puede deshacer.",
                "Confirmar eliminación de estudiante",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmado == JOptionPane.YES_OPTION) {
            Usuario estudiante = obtenerEstudiantePorCedula(cedula);
            if (estudiante != null) {
                if (usuarioDAO.eliminarEstudiante(estudiante.getIdUsuario())) {
                    cargarEstudiantes();
                    JOptionPane.showMessageDialog(vista,
                            "Estudiante eliminado exitosamente",
                            "Eliminación exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(vista,
                            "Error al eliminar el estudiante",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
}
