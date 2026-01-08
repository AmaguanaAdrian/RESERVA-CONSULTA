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
public class ControladorGestionBibliotecarios implements ActionListener {

    private GestionBibliotecarios panel;
    private UsuarioDAO usuarioDAO;
    private DefaultTableModel modeloTabla;

    public ControladorGestionBibliotecarios(GestionBibliotecarios panel) {
        this.panel = panel;
        this.usuarioDAO = new UsuarioDAO();

        configurarTabla();
        cargarBibliotecarios();
        configurarMenuClickDerecho();

        panel.jbtn_agregarBiblioteario.addActionListener(this);
        panel.jbtn_buscarBibliotecario.addActionListener(this);
    }

    // ================= EVENTOS PRINCIPALES =================
    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == panel.jbtn_agregarBiblioteario) {
            abrirDialogRegistro();
        }

        if (e.getSource() == panel.jbtn_buscarBibliotecario) {
            buscarBibliotecario();
        }
    }

    // ================= DIALOG REGISTRO =================
    private void abrirDialogRegistro() {

        Window parent = SwingUtilities.getWindowAncestor(panel);
        DialogBibliotecario dialog = new DialogBibliotecario((Frame) parent, true);

        // Estado inicial
        dialog.lblErrorCedula.setVisible(false);
        dialog.lblErrorPassword.setVisible(false);

        // Rol fijo
        dialog.txtRol.setText("BIBLIOTECARIO");
        dialog.txtRol.setEnabled(false);

        // Restricción cédula
        AppUtils.soloNumeros(dialog.txtCedula, 10);

        // Botones
        dialog.btnGuardar.addActionListener(e -> guardarBibliotecario(dialog));
        dialog.btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.btnVerPass1.addActionListener(e
                -> AppUtils.togglePassword(dialog.txtPassword));

        dialog.btnVerPass2.addActionListener(e
                -> AppUtils.togglePassword(dialog.txtConfirmar));

        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }

    // ================= GUARDAR =================
    private void guardarBibliotecario(DialogBibliotecario dialog) {

        boolean valido = true;

        String cedula = dialog.txtCedula.getText().trim();
        String nombres = dialog.txtNombres.getText().trim();
        String apellidos = dialog.txtApellidos.getText().trim();
        String correo = dialog.txtEmail.getText().trim();
        String password = new String(dialog.txtPassword.getPassword());
        String confirmar = new String(dialog.txtConfirmar.getPassword());

        // Limpiar errores
        AppUtils.limpiarError(dialog.txtCedula, dialog.lblErrorCedula);
        AppUtils.limpiarError(dialog.txtPassword, dialog.lblErrorPassword);

        // ---------- VALIDACIONES ----------
        if (cedula.isEmpty() || !AppUtils.validarCedulaEcuatoriana(cedula)) {
            AppUtils.marcarError(dialog.txtCedula, dialog.lblErrorCedula,
                    "Cédula inválida");
            valido = false;
        }

        if (nombres.isEmpty() || apellidos.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                    "Nombres y apellidos son obligatorios");
            valido = false;
        }

        if (correo.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                    "El correo es obligatorio");
            valido = false;
        }

        if (password.isEmpty()
                || !AppUtils.validarPassword(password)
                || !password.equals(confirmar)) {

            AppUtils.marcarError(dialog.txtPassword, dialog.lblErrorPassword,
                    "Contraseña inválida o no coincide");
            valido = false;
        }

        if (!valido) {
            return;
        }

        // ---------- GUARDAR ----------
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

    // ================= TABLA =================
    private void configurarTabla() {

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Cédula", "Rol", "Estado"}, 0) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        panel.jtb_Bibliotecarios.setModel(modeloTabla);
    }

    private void cargarBibliotecarios() {

        modeloTabla.setRowCount(0);

        for (Usuario u : usuarioDAO.listarBibliotecarios()) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(),
                u.getCedula(),
                u.getRol(),
                u.getEstado()
            });
        }
    }

    // ================= BUSCAR =================
    private void buscarBibliotecario() {

        String texto = panel.jtxt_buscarBibliotecario.getText().trim();
        modeloTabla.setRowCount(0);

        for (Usuario u : usuarioDAO.listarBibliotecarios()) {
            if (u.getCedula().contains(texto)) {
                modeloTabla.addRow(new Object[]{
                    u.getIdUsuario(),
                    u.getCedula(),
                    u.getRol(),
                    u.getEstado()
                });
            }
        }
    }

    // ================= MENU CONTEXTUAL =================
    private void configurarMenuClickDerecho() {

        JPopupMenu menu = new JPopupMenu();

        JMenuItem editar = new JMenuItem("Editar");
        JMenuItem eliminar = new JMenuItem("Eliminar");

        editar.addActionListener(e -> editarBibliotecario());
        eliminar.addActionListener(e -> eliminarBibliotecario());

        menu.add(editar);
        menu.add(eliminar);

        panel.jtb_Bibliotecarios.setComponentPopupMenu(menu);
    }

    // ================= EDITAR =================
    private void editarBibliotecario() {

        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(panel,
                    "Seleccione un bibliotecario");
            return;
        }

        Usuario u = new Usuario();
        u.setIdUsuario((int) modeloTabla.getValueAt(fila, 0));
        u.setCedula((String) modeloTabla.getValueAt(fila, 1));
        u.setEstado((String) modeloTabla.getValueAt(fila, 3));

        Window parent = SwingUtilities.getWindowAncestor(panel);
        DialogBibliotecario dialog
                = new DialogBibliotecario((Frame) parent, true);

        dialog.txtCedula.setText(u.getCedula());
        dialog.txtCedula.setEnabled(false);
        dialog.cmbEstado.setSelectedItem(u.getEstado());

        dialog.btnGuardar.addActionListener(e -> {

            String nuevaPass
                    = new String(dialog.txtPassword.getPassword());

            if (!nuevaPass.isEmpty()
                    && AppUtils.validarPassword(nuevaPass)) {
                u.setContrasena(nuevaPass);
            }

            u.setEstado(dialog.cmbEstado.getSelectedItem().toString());

            if (usuarioDAO.editarBibliotecario(u)) {
                cargarBibliotecarios();
                JOptionPane.showMessageDialog(dialog,
                        "Bibliotecario actualizado");
                dialog.dispose();
            }
        });

        dialog.btnCancelar.addActionListener(e -> dialog.dispose());
        dialog.setLocationRelativeTo(panel);
        dialog.setVisible(true);
    }

    // ================= ELIMINAR =================
    private void eliminarBibliotecario() {

        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) {
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(panel,
                "¿Eliminar bibliotecario?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION);

        if (op == JOptionPane.YES_OPTION) {
            usuarioDAO.eliminarBibliotecario(id);
            cargarBibliotecarios();
        }
    }
}
