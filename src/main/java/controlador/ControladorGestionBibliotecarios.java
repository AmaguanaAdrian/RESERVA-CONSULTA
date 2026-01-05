/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.GestionBibliotecarios;
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

        panel.jbtn_agregarBiblioteario.addActionListener(this);
        panel.jbtn_buscarBibliotecario.addActionListener(this);
        panel.jbtn_editarBibliotecario.addActionListener(this);
        panel.jbtn_eliminarBibliotecario.addActionListener(this);
    }

    // ================= TABLA =================

    private void configurarTabla() {

        modeloTabla = new DefaultTableModel(
                new Object[]{"ID", "Cédula", "ROL", "Estado"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabla solo lectura
            }
        };

        panel.jtb_Bibliotecarios.setModel(modeloTabla);
    }

    private void cargarBibliotecarios() {

        modeloTabla.setRowCount(0);

        List<Usuario> lista = usuarioDAO.listarBibliotecarios();

        for (Usuario u : lista) {
            modeloTabla.addRow(new Object[]{
                u.getIdUsuario(),
                u.getCedula(),
                u.getRol(),
                u.getEstado()
            });
        }
    }

    // ================= EVENTOS =================

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == panel.jbtn_agregarBiblioteario) {
            agregarBibliotecario();
        }

        if (e.getSource() == panel.jbtn_buscarBibliotecario) {
            buscarBibliotecario();
        }

        if (e.getSource() == panel.jbtn_editarBibliotecario) {
            editarBibliotecario();
        }

        if (e.getSource() == panel.jbtn_eliminarBibliotecario) {
            eliminarBibliotecario();
        }
    }

    // ================= CRUD =================

    private void agregarBibliotecario() {

        String cedula = JOptionPane.showInputDialog(panel, "Ingrese cédula:");
        String contrasena = JOptionPane.showInputDialog(panel, "Ingrese contraseña:");

        if (cedula == null || contrasena == null || cedula.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "Datos inválidos");
            return;
        }

        Usuario u = new Usuario();
        u.setCedula(cedula);
        u.setContrasena(contrasena);

        if (usuarioDAO.agregarBibliotecario(u)) {
            cargarBibliotecarios();
            JOptionPane.showMessageDialog(panel, "Bibliotecario agregado correctamente");
        } else {
            JOptionPane.showMessageDialog(panel, "Error al agregar bibliotecario");
        }
    }

    private void buscarBibliotecario() {

        String texto = panel.jtxt_buscarBibliotecario.getText().toLowerCase();

        if (texto.isEmpty()) {
            cargarBibliotecarios();
            return;
        }

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

    private void editarBibliotecario() {

        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(panel, "Seleccione un bibliotecario");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);
        String cedulaActual = (String) modeloTabla.getValueAt(fila, 1);
        String estadoActual = (String) modeloTabla.getValueAt(fila, 4);

        String nuevaCedula = JOptionPane.showInputDialog(panel, "Nueva cédula:", cedulaActual);
        String nuevoEstado = JOptionPane.showInputDialog(panel, "Estado (ACTIVO / INACTIVO):", estadoActual);

        if (nuevaCedula == null || nuevoEstado == null) return;

        Usuario u = new Usuario();
        u.setIdUsuario(id);
        u.setCedula(nuevaCedula);
        u.setEstado(nuevoEstado);

        if (usuarioDAO.editarBibliotecario(u)) {
            cargarBibliotecarios();
            JOptionPane.showMessageDialog(panel, "Bibliotecario actualizado");
        } else {
            JOptionPane.showMessageDialog(panel, "Error al editar");
        }
    }

    private void eliminarBibliotecario() {

        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(panel, "Seleccione un bibliotecario");
            return;
        }

        int id = (int) modeloTabla.getValueAt(fila, 0);

        int op = JOptionPane.showConfirmDialog(
                panel,
                "¿Eliminar bibliotecario?",
                "Confirmar",
                JOptionPane.YES_NO_OPTION
        );

        if (op == JOptionPane.YES_OPTION) {
            if (usuarioDAO.eliminarBibliotecario(id)) {
                cargarBibliotecarios();
                JOptionPane.showMessageDialog(panel, "Bibliotecario eliminado");
            } else {
                JOptionPane.showMessageDialog(panel, "Error al eliminar");
            }
        }
    }
}

