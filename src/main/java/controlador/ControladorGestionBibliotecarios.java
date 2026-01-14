/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Utilidades.AppUtils;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import modelo.UsuarioDAO;
import vista.DialogBibliotecario;
import vista.GestionBibliotecarios;
import java.util.List;
import modelo.Usuario;
import javax.swing.table.DefaultTableModel;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
// ... otros imports

/**
 *
 * @author amagu
 */
public class ControladorGestionBibliotecarios implements ActionListener {

    private GestionBibliotecarios panel;
    private UsuarioDAO usuarioDAO;
    private DefaultTableModel modeloTabla;

    public ControladorGestionBibliotecarios(GestionBibliotecarios panel) {
        this.panel = panel;
        this.usuarioDAO = new UsuarioDAO();

        configurarTablaMejorada();
        cargarBibliotecarios();
        aplicarEstilosVisuales();
        configurarMenuClickDerechoMejorado();
        configurarBusquedaTiempoReal();
        configurarPlaceholderBusqueda();

        panel.jbtn_agregarBiblioteario.addActionListener(this);
    }

    // ================= TABLA =================
    private void configurarTablaMejorada() {
        modeloTabla = new DefaultTableModel(
                new Object[]{"Cédula", "Nombres", "Apellidos", "Correo", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        panel.jtb_Bibliotecarios.setModel(modeloTabla);
        AppUtils.estilizarTabla(panel.jtb_Bibliotecarios, new Color(0, 51, 102));
    }

    private void cargarBibliotecarios() {
        modeloTabla.setRowCount(0);
        List<Usuario> lista = usuarioDAO.listarBibliotecarios();

        for (Usuario u : lista) {
            modeloTabla.addRow(new Object[]{
                u.getCedula(),
                u.getNombres(),
                u.getApellidos(),
                u.getCorreo(),
                u.getEstado()
            });
        }
        
        // Animación de actualización
        AppUtils.animarActualizacion(panel.jtb_Bibliotecarios);
    }

    // ================= BÚSQUEDA =================
    private void configurarBusquedaTiempoReal() {
        panel.jtxt_buscarBibliotecario.getDocument().addDocumentListener(
                new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { buscar(); }
            public void removeUpdate(DocumentEvent e) { buscar(); }
            public void changedUpdate(DocumentEvent e) { buscar(); }

            private void buscar() {
                buscarBibliotecario();
            }
        });
    }

    private void buscarBibliotecario() {
        String texto = panel.jtxt_buscarBibliotecario.getText().trim();

        modeloTabla.setRowCount(0);

        if (texto.isEmpty() || texto.equals("Buscar por cédula...")) {
            cargarBibliotecarios();
            return;
        }

        Usuario u = usuarioDAO.buscarPorCedula(texto);

        if (u != null) {
            modeloTabla.addRow(new Object[]{
                u.getCedula(),
                u.getNombres(),
                u.getApellidos(),
                u.getCorreo(),
                u.getEstado()
            });
        }
    }

    private void configurarPlaceholderBusqueda() {
        String placeholderText = "Buscar por cédula...";
        panel.jtxt_buscarBibliotecario.setText(placeholderText);
        panel.jtxt_buscarBibliotecario.setForeground(Color.GRAY);

        panel.jtxt_buscarBibliotecario.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (panel.jtxt_buscarBibliotecario.getText().equals(placeholderText)) {
                    panel.jtxt_buscarBibliotecario.setText("");
                    panel.jtxt_buscarBibliotecario.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (panel.jtxt_buscarBibliotecario.getText().isEmpty()) {
                    panel.jtxt_buscarBibliotecario.setText(placeholderText);
                    panel.jtxt_buscarBibliotecario.setForeground(Color.GRAY);
                }
            }
        });

        // Hover effect usando AppUtils
        AppUtils.agregarEfectoHoverCampo(
                panel.jtxt_buscarBibliotecario,
                new Color(0, 123, 255),
                new Color(206, 212, 218)
        );
    }

    // ================= EVENTOS =================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == panel.jbtn_agregarBiblioteario) {
            abrirDialogRegistro(null);
        }
    }

    // ================= DIALOG REGISTRO / EDICIÓN =================
    private void abrirDialogRegistro(Usuario existente) {
        Window parent = SwingUtilities.getWindowAncestor(panel);
        DialogBibliotecario dialog = new DialogBibliotecario((Frame) parent, true);

        dialog.txtRol.setText("BIBLIOTECARIO");
        dialog.txtRol.setEnabled(false);

        // Ocultar errores usando AppUtils
        AppUtils.ocultarErroresDialogo(
            dialog.lblErrorCedula, 
            dialog.lblErrorNombres, 
            dialog.lblErrorApellidos, 
            dialog.lblErrorEmail, 
            dialog.lblErrorPassword
        );

        if (existente != null) {
            dialog.setTitle("Editar Bibliotecario");
            dialog.txtCedula.setText(existente.getCedula());
            dialog.txtCedula.setEnabled(false);
            dialog.txtNombres.setText(existente.getNombres());
            dialog.txtApellidos.setText(existente.getApellidos());
            dialog.txtEmail.setText(existente.getCorreo());
            dialog.cmbEstado.setSelectedItem(existente.getEstado());
        } else {
            dialog.setTitle("Agregar Bibliotecario");
            AppUtils.soloNumeros(dialog.txtCedula, 10);
        }

        configurarValidacionesConEnter(dialog, existente);
        validarCedulaTiempoReal(dialog, existente);

        dialog.btnGuardar.addActionListener(e -> guardarBibliotecario(dialog, existente));
        dialog.btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.btnVerPass1.addActionListener(e -> AppUtils.togglePassword(dialog.txtPassword));
        dialog.btnVerPass2.addActionListener(e -> AppUtils.togglePassword(dialog.txtConfirmar));

        // Posicionamiento usando AppUtils
        Window parentWindow = SwingUtilities.getWindowAncestor(panel);
        AppUtils.posicionarDialogoIzquierda(dialog, parentWindow);
        
        // Mostrar el diálogo (sin animación para evitar errores)
        dialog.setVisible(true);
    }

    // ================= VALIDACIONES =================
    private void configurarValidacionesConEnter(DialogBibliotecario d, Usuario existente) {
        // Cédula
        AppUtils.validarConEnter(d.txtCedula, d.txtNombres, () -> {
            AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
            return validarCampoCedula(d, existente);
        });

        // Nombres
        AppUtils.validarConEnter(d.txtNombres, d.txtApellidos, () -> {
            AppUtils.limpiarError(d.txtNombres, d.lblErrorNombres);
            return validarCampoNombres(d);
        });

        // Apellidos
        AppUtils.validarConEnter(d.txtApellidos, d.txtEmail, () -> {
            AppUtils.limpiarError(d.txtApellidos, d.lblErrorApellidos);
            return validarCampoApellidos(d);
        });

        // Email
        AppUtils.validarConEnter(d.txtEmail, d.txtPassword, () -> {
            AppUtils.limpiarError(d.txtEmail, d.lblErrorEmail);
            return validarCampoEmail(d);
        });

        // Password
        AppUtils.validarConEnter(d.txtPassword, d.txtConfirmar, () -> {
            return validarCampoPassword(d, existente);
        });

        // Confirmar Password (ejecuta botón guardar)
        AppUtils.enterEjecutaBoton(d.txtConfirmar, d.btnGuardar, () -> {
            return validarConfirmacionPassword(d, existente);
        });
    }

    private boolean validarCampoCedula(DialogBibliotecario d, Usuario existente) {
        String cedula = d.txtCedula.getText().trim();
        
        if (cedula.isEmpty()) {
            AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "La cédula es obligatoria");
            return false;
        }

        if (!AppUtils.validarCedulaEcuatoriana(cedula)) {
            AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula inválida");
            return false;
        }

        if (existente == null && usuarioDAO.existeCedula(cedula)) {
            AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula ya registrada");
            return false;
        }

        return true;
    }

    private boolean validarCampoNombres(DialogBibliotecario d) {
        String nombres = d.txtNombres.getText().trim();
        
        if (nombres.isEmpty()) {
            AppUtils.marcarError(d.txtNombres, d.lblErrorNombres, "Los nombres son obligatorios");
            return false;
        }
        
        if (nombres.split("\\s+").length < 2) {
            AppUtils.marcarError(d.txtNombres, d.lblErrorNombres, "Ingrese mínimo 2 nombres");
            return false;
        }
        return true;
    }

    private boolean validarCampoApellidos(DialogBibliotecario d) {
        String apellidos = d.txtApellidos.getText().trim();
        
        if (apellidos.isEmpty()) {
            AppUtils.marcarError(d.txtApellidos, d.lblErrorApellidos, "Los apellidos son obligatorios");
            return false;
        }
        
        if (apellidos.split("\\s+").length < 2) {
            AppUtils.marcarError(d.txtApellidos, d.lblErrorApellidos, "Ingrese mínimo 2 apellidos");
            return false;
        }
        return true;
    }

    private boolean validarCampoEmail(DialogBibliotecario d) {
        String email = d.txtEmail.getText().trim();
        
        if (email.isEmpty()) {
            AppUtils.marcarError(d.txtEmail, d.lblErrorEmail, "El correo es obligatorio");
            return false;
        }
        
        if (!AppUtils.validarCorreo(email)) {
            AppUtils.marcarError(d.txtEmail, d.lblErrorEmail, "Correo inválido");
            return false;
        }
        return true;
    }

    private boolean validarCampoPassword(DialogBibliotecario d, Usuario existente) {
        String password = new String(d.txtPassword.getPassword());
        
        if (existente == null) {
            if (password.isEmpty()) {
                AppUtils.marcarError(d.txtPassword, d.lblErrorPassword, "La contraseña es obligatoria");
                return false;
            }
            
            if (password.length() < 8) {
                AppUtils.marcarError(d.txtPassword, d.lblErrorPassword, "Mínimo 8 caracteres");
                return false;
            }
            
            if (!AppUtils.validarPassword(password)) {
                AppUtils.marcarError(d.txtPassword, d.lblErrorPassword, "Debe contener letras y números");
                return false;
            }
        } else if (!password.isEmpty() && password.length() < 8) {
            AppUtils.marcarError(d.txtPassword, d.lblErrorPassword, "Mínimo 8 caracteres");
            return false;
        }
        
        return true;
    }

    private boolean validarConfirmacionPassword(DialogBibliotecario d, Usuario existente) {
        String password = new String(d.txtPassword.getPassword());
        String confirmar = new String(d.txtConfirmar.getPassword());
        
        if (existente == null || !password.isEmpty()) {
            if (confirmar.isEmpty()) {
                AppUtils.marcarError(d.txtConfirmar, d.lblErrorPassword, "Confirme la contraseña");
                return false;
            }
            
            if (!password.equals(confirmar)) {
                AppUtils.marcarError(d.txtConfirmar, d.lblErrorPassword, "Las contraseñas no coinciden");
                return false;
            }
        }
        
        return true;
    }

    private void validarCedulaTiempoReal(DialogBibliotecario d, Usuario existente) {
        d.txtCedula.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { validar(); }
            public void removeUpdate(DocumentEvent e) { validar(); }
            public void changedUpdate(DocumentEvent e) { validar(); }

            private void validar() {
                String cedula = d.txtCedula.getText().trim();

                if (cedula.isEmpty()) {
                    AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
                    return;
                }

                if (!AppUtils.validarCedulaEcuatoriana(cedula)) {
                    AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula inválida");
                    return;
                }

                if (existente == null && usuarioDAO.existeCedula(cedula)) {
                    AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula ya registrada");
                    return;
                }

                AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
            }
        });
    }

    // ================= GUARDAR =================
    private void guardarBibliotecario(DialogBibliotecario d, Usuario existente) {
        // Validar todos los campos
        if (!validarFormularioCompleto(d, existente)) {
            AppUtils.animarError(d.btnGuardar);
            return;
        }

        Usuario u = new Usuario();
        u.setCedula(d.txtCedula.getText().trim());
        u.setNombres(d.txtNombres.getText().trim());
        u.setApellidos(d.txtApellidos.getText().trim());
        u.setCorreo(d.txtEmail.getText().trim());
        u.setEstado(d.cmbEstado.getSelectedItem().toString());
        u.setRol("BIBLIOTECARIO");

        String password = new String(d.txtPassword.getPassword());
        if (!password.isEmpty()) {
            u.setContrasena(password);
        }

        boolean ok;
        String mensaje;
        if (existente != null) {
            u.setIdUsuario(existente.getIdUsuario());
            ok = usuarioDAO.editarBibliotecario(u);
            mensaje = ok ? "Bibliotecario actualizado exitosamente" : "Error al actualizar el bibliotecario";
        } else {
            ok = usuarioDAO.agregarBibliotecario(u);
            mensaje = ok ? "Bibliotecario registrado exitosamente" : "Error al registrar el bibliotecario";
        }

        if (ok) {
            cargarBibliotecarios();
            AppUtils.mostrarMensajeExito(panel, mensaje, "Éxito");
            d.dispose();
        } else {
            AppUtils.animarError(d.btnGuardar);
            AppUtils.mostrarMensajeAdvertencia(panel, mensaje, "Error");
        }
    }

    private boolean validarFormularioCompleto(DialogBibliotecario d, Usuario existente) {
        return validarCampoCedula(d, existente) &&
               validarCampoNombres(d) &&
               validarCampoApellidos(d) &&
               validarCampoEmail(d) &&
               validarCampoPassword(d, existente) &&
               validarConfirmacionPassword(d, existente);
    }

    // ================= ESTILOS =================
    private void aplicarEstilosVisuales() {
        // Aplicar estilo al botón usando AppUtils
        AppUtils.estiloBotonModerno(
            panel.jbtn_agregarBiblioteario, 
            new Color(40, 167, 69), 
            "Agregar Bibliotecario"
        );
    }

    // ================= MENU =================
    private void configurarMenuClickDerechoMejorado() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem editar = new JMenuItem("Editar");
        JMenuItem eliminar = new JMenuItem("Eliminar");

        // Aplicar estilo a los items del menú
        AppUtils.estiloItemMenu(editar);
        AppUtils.estiloItemMenu(eliminar);

        editar.addActionListener(e -> editarBibliotecario());
        eliminar.addActionListener(e -> eliminarBibliotecario());

        menu.add(editar);
        menu.add(eliminar);

        panel.jtb_Bibliotecarios.setComponentPopupMenu(menu);
    }

    private void editarBibliotecario() {
        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) {
            AppUtils.mostrarMensajeAdvertencia(panel, "Seleccione un bibliotecario para editar", "Advertencia");
            return;
        }

        String cedula = modeloTabla.getValueAt(fila, 0).toString();
        Usuario u = usuarioDAO.buscarPorCedula(cedula);

        if (u != null) {
            abrirDialogRegistro(u);
        }
    }

    private void eliminarBibliotecario() {
        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) {
            AppUtils.mostrarMensajeAdvertencia(panel, "Seleccione un bibliotecario para eliminar", "Advertencia");
            return;
        }

        String cedula = modeloTabla.getValueAt(fila, 0).toString();
        Usuario u = usuarioDAO.buscarPorCedula(cedula);

        if (u != null) {
            // Confirmación usando AppUtils
            boolean confirmado = AppUtils.mostrarConfirmacionPersonalizada(
                panel,
                "¿Está seguro de eliminar al bibliotecario con cédula " + cedula + "?",
                "Confirmar Eliminación"
            );

            if (confirmado) {
                boolean eliminado = usuarioDAO.eliminarBibliotecario(u.getIdUsuario());
                
                if (eliminado) {
                    cargarBibliotecarios();
                    AppUtils.mostrarMensajeExito(panel, "Bibliotecario eliminado exitosamente", "Éxito");
                } else {
                    AppUtils.mostrarMensajeAdvertencia(panel, "Error al eliminar el bibliotecario", "Error");
                }
            }
        }
    }
}
