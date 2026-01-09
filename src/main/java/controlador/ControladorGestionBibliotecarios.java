/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.DialogBibliotecario;
import vista.GestionBibliotecarios;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import Utilidades.AppUtils;
/**
 *
 * @author amagu
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.util.List;

public class ControladorGestionBibliotecarios implements ActionListener {

    private GestionBibliotecarios panel;
    private UsuarioDAO usuarioDAO;
    private DefaultTableModel modeloTabla;

    public ControladorGestionBibliotecarios(GestionBibliotecarios panel) {
        this.panel = panel;
        this.usuarioDAO = new UsuarioDAO();

        aplicarEstilosVisuales();
        configurarTablaMejorada();
        cargarBibliotecarios();
        configurarMenuClickDerechoMejorado();
        agregarEfectosHover();

        panel.jbtn_agregarBiblioteario.addActionListener(this);
        panel.jbtn_buscarBibliotecario.addActionListener(this);
        
        panel.jtxt_buscarBibliotecario.addActionListener(e -> buscarBibliotecario());
    }

    // ================= MEJORAS VISUALES =================
    private void aplicarEstilosVisuales() {
        // Estilo para el título principal
        if (panel.jLabel1 != null) {
            panel.jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 18));
            panel.jLabel1.setForeground(new Color(0, 51, 102));
        }
        
        if (panel.jLabel2 != null) {
            panel.jLabel2.setFont(new Font("Segoe UI", Font.BOLD, 14));
            panel.jLabel2.setForeground(new Color(102, 102, 102));
        }

        // Estilo para botones principales
        AppUtils.estiloBotonModerno(panel.jbtn_agregarBiblioteario, new Color(40, 167, 69), "Agregar");
        AppUtils.estiloBotonModerno(panel.jbtn_buscarBibliotecario, new Color(23, 162, 184), "Buscar");

        // Estilo para campo de búsqueda
        if (panel.jtxt_buscarBibliotecario != null) {
            panel.jtxt_buscarBibliotecario.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(206, 212, 218)),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            panel.jtxt_buscarBibliotecario.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            panel.jtxt_buscarBibliotecario.setForeground(new Color(73, 80, 87));
            
            // Placeholder mejorado
            panel.jtxt_buscarBibliotecario.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (panel.jtxt_buscarBibliotecario.getText().equals("Buscar por cédula...")) {
                        panel.jtxt_buscarBibliotecario.setText("");
                        panel.jtxt_buscarBibliotecario.setForeground(new Color(73, 80, 87));
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (panel.jtxt_buscarBibliotecario.getText().isEmpty()) {
                        panel.jtxt_buscarBibliotecario.setText("Buscar por cédula...");
                        panel.jtxt_buscarBibliotecario.setForeground(new Color(150, 150, 150));
                    }
                }
            });
            
            if (panel.jtxt_buscarBibliotecario.getText().isEmpty()) {
                panel.jtxt_buscarBibliotecario.setText("Buscar por cédula...");
                panel.jtxt_buscarBibliotecario.setForeground(new Color(150, 150, 150));
            }
        }

        // Estilo para el separador
        if (panel.jSeparator1 != null) {
            panel.jSeparator1.setForeground(new Color(222, 226, 230));
            panel.jSeparator1.setBackground(new Color(222, 226, 230));
        }
    }

    private void agregarEfectosHover() {
        // Efecto para el campo de búsqueda
        if (panel.jtxt_buscarBibliotecario != null) {
            AppUtils.agregarEfectoHoverCampo(panel.jtxt_buscarBibliotecario, 
                new Color(0, 123, 255), new Color(206, 212, 218));
        }
    }

    // ================= TABLA MEJORADA =================
    private void configurarTablaMejorada() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Cédula", "Rol", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return columnIndex == 0 ? Integer.class : String.class;
            }
        };

        panel.jtb_Bibliotecarios.setModel(modeloTabla);
        AppUtils.estilizarTabla(panel.jtb_Bibliotecarios, new Color(0, 51, 102));
    }

    private void cargarBibliotecarios() {
        modeloTabla.setRowCount(0);
        List<Usuario> bibliotecarios = usuarioDAO.listarBibliotecarios();
        
        if (bibliotecarios.isEmpty()) {
            JOptionPane.showMessageDialog(panel, 
                "No hay bibliotecarios registrados.",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            for (Usuario u : bibliotecarios) {
                modeloTabla.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getCedula(),
                    u.getRol(),
                    u.getEstado()
                });
            }
        }
    }

    // ================= MENÚ CONTEXTUAL MEJORADO =================
    private void configurarMenuClickDerechoMejorado() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        
        JMenuItem editar = new JMenuItem("Editar");
        editar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        JMenuItem eliminar = new JMenuItem("Eliminar");
        eliminar.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        editar.addActionListener(e -> editarBibliotecario());
        eliminar.addActionListener(e -> eliminarBibliotecario());

        menu.add(editar);
        menu.addSeparator();
        menu.add(eliminar);
        
        panel.jtb_Bibliotecarios.setComponentPopupMenu(menu);
        
        // Doble click para editar
        panel.jtb_Bibliotecarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarBibliotecario();
                }
            }
        });
    }

    // ================= EVENTOS =================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == panel.jbtn_agregarBiblioteario) {
            abrirDialogRegistro();
        }

        if (e.getSource() == panel.jbtn_buscarBibliotecario) {
            buscarBibliotecario();
        }
    }

    // ================= DIALOG MEJORADO CON POSICIÓN A LA IZQUIERDA =================
    private void abrirDialogRegistro() {
        Window parent = SwingUtilities.getWindowAncestor(panel);
        DialogBibliotecario dialog = new DialogBibliotecario((Frame) parent, true);

        ocultarErrores(dialog);

        dialog.txtRol.setText("BIBLIOTECARIO");
        dialog.txtRol.setEnabled(false);

        AppUtils.soloNumeros(dialog.txtCedula, 10);

        configurarEnter(dialog);

        dialog.btnGuardar.addActionListener(e -> guardarBibliotecario(dialog));
        dialog.btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.btnVerPass1.addActionListener(e
                -> AppUtils.togglePassword(dialog.txtPassword));

        dialog.btnVerPass2.addActionListener(e
                -> AppUtils.togglePassword(dialog.txtConfirmar));

        // POSICIONAR A LA IZQUIERDA
        posicionarDialogoIzquierda(dialog, parent);
        
        dialog.setVisible(true);
    }

    // ================= POSICIONAR DIALOGO A LA IZQUIERDA =================
    private void posicionarDialogoIzquierda(Dialog dialog, Window parent) {
        // Obtener posición y tamaño del padre
        Point parentLocation = parent.getLocation();
        Dimension parentSize = parent.getSize();
        
        // Obtener tamaño del diálogo
        dialog.pack();
        Dimension dialogSize = dialog.getSize();
        
        // Calcular posición a la izquierda del padre
        int x = parentLocation.x - dialogSize.width;
        int y = parentLocation.y + (parentSize.height - dialogSize.height) / 2;
        
        // Si no cabe a la izquierda, poner a la derecha
        if (x < 0) {
            x = parentLocation.x + parentSize.width;
        }
        
        // Asegurar que no se salga de la pantalla en Y
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        
        if (y < 0) y = 0;
        if (y + dialogSize.height > screenSize.height) {
            y = screenSize.height - dialogSize.height;
        }
        
        dialog.setLocation(x, y);
    }

    // ================= ENTER =================
    private void configurarEnter(DialogBibliotecario d) {
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

    // ================= GUARDAR =================
    private void guardarBibliotecario(DialogBibliotecario dialog) {
        limpiarErrores(dialog);
        boolean valido = true;

        String cedula = dialog.txtCedula.getText().trim();
        String nombres = dialog.txtNombres.getText().trim();
        String apellidos = dialog.txtApellidos.getText().trim();
        String correo = dialog.txtEmail.getText().trim();
        String password = new String(dialog.txtPassword.getPassword());
        String confirmar = new String(dialog.txtConfirmar.getPassword());

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

        if (!AppUtils.validarPassword(password) || !password.equals(confirmar)) {
            AppUtils.marcarError(dialog.txtPassword, dialog.lblErrorPassword,
                    "Contraseña inválida o no coincide");
            AppUtils.marcarCampoRojo(dialog.txtConfirmar);
            valido = false;
        }

        if (!valido) {
            return;
        }

        Usuario u = new Usuario();
        u.setCedula(cedula);
        u.setNombres(nombres);
        u.setApellidos(apellidos);
        u.setCorreo(correo);
        u.setContrasena(password);
        u.setEstado(dialog.cmbEstado.getSelectedItem().toString());
        u.setRol("BIBLIOTECARIO");

        if (usuarioDAO.agregarBibliotecario(u)) {
            cargarBibliotecarios();
            JOptionPane.showMessageDialog(dialog,
                    "Bibliotecario registrado correctamente",
                    "Registro exitoso",
                    JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        }
    }

    // ================= UTIL =================
    private void ocultarErrores(DialogBibliotecario d) {
        d.lblErrorCedula.setVisible(false);
        d.lblErrorNombres.setVisible(false);
        d.lblErrorApellidos.setVisible(false);
        d.lblErrorEmail.setVisible(false);
        d.lblErrorPassword.setVisible(false);
    }

    private void limpiarErrores(DialogBibliotecario d) {
        AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
        AppUtils.limpiarError(d.txtNombres, d.lblErrorNombres);
        AppUtils.limpiarError(d.txtApellidos, d.lblErrorApellidos);
        AppUtils.limpiarError(d.txtEmail, d.lblErrorEmail);
        AppUtils.limpiarError(d.txtPassword, d.lblErrorPassword);
        AppUtils.limpiarCampo(d.txtConfirmar);
    }

    // ================= BÚSQUEDA =================
    private void buscarBibliotecario() {
        String txt = panel.jtxt_buscarBibliotecario.getText().trim();
        
        if (txt.isEmpty() || txt.equals("Buscar por cédula...")) {
            cargarBibliotecarios();
            return;
        }

        modeloTabla.setRowCount(0);
        boolean encontrado = false;
        
        for (Usuario u : usuarioDAO.listarBibliotecarios()) {
            if (u.getCedula().contains(txt)) {
                modeloTabla.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getCedula(),
                    u.getRol(),
                    u.getEstado()
                });
                encontrado = true;
            }
        }

        if (!encontrado) {
            JOptionPane.showMessageDialog(panel,
                "No se encontraron bibliotecarios con la cédula: " + txt,
                "Búsqueda sin resultados",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ================= EDITAR =================
    private void editarBibliotecario() {
        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(panel,
                "Seleccione un bibliotecario de la tabla",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario u = new Usuario();
        u.setIdUsuario((int) modeloTabla.getValueAt(fila, 0));
        u.setCedula((String) modeloTabla.getValueAt(fila, 1));
        u.setEstado((String) modeloTabla.getValueAt(fila, 3));

        Window parent = SwingUtilities.getWindowAncestor(panel);
        DialogBibliotecario dialog = new DialogBibliotecario((Frame) parent, true);

        dialog.txtCedula.setText(u.getCedula());
        dialog.txtCedula.setEnabled(false);
        dialog.cmbEstado.setSelectedItem(u.getEstado());

        dialog.btnGuardar.addActionListener(e -> {
            String nuevaPass = new String(dialog.txtPassword.getPassword());

            if (!nuevaPass.isEmpty() && AppUtils.validarPassword(nuevaPass)) {
                u.setContrasena(nuevaPass);
            }

            u.setEstado(dialog.cmbEstado.getSelectedItem().toString());

            if (usuarioDAO.editarBibliotecario(u)) {
                cargarBibliotecarios();
                JOptionPane.showMessageDialog(dialog,
                    "Bibliotecario actualizado correctamente",
                    "Actualización exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            }
        });

        dialog.btnCancelar.addActionListener(e -> dialog.dispose());
        
        posicionarDialogoIzquierda(dialog, parent);
        dialog.setVisible(true);
    }

    // ================= ELIMINAR =================
    private void eliminarBibliotecario() {
        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) {
            return;
        }

        String cedula = (String) modeloTabla.getValueAt(fila, 1);
        int id = (int) modeloTabla.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(panel,
            "¿Está seguro de eliminar al bibliotecario?\n\n" +
            "Cédula: " + cedula + "\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (op == JOptionPane.YES_OPTION) {
            if (usuarioDAO.eliminarBibliotecario(id)) {
                cargarBibliotecarios();
                
                JOptionPane.showMessageDialog(panel,
                    "Bibliotecario eliminado correctamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}