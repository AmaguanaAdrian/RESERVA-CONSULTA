/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.DialogBibliotecario;
import vista.GestionBibliotecarios;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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

        aplicarEstilosVisuales();
        configurarTablaMejorada();
        cargarBibliotecarios();
        configurarMenuClickDerechoMejorado();
        configurarBusquedaTiempoReal();
        agregarEfectosHover();

        panel.jbtn_agregarBiblioteario.addActionListener(this);
    }

    // ================= ESTILOS GENERALES =================
    private void aplicarEstilosVisuales() {
        panel.jtxt_buscarBibliotecario.setText("Buscar por cédula...");
        panel.jtxt_buscarBibliotecario.setForeground(Color.GRAY);
    }

    private void agregarEfectosHover() {
        panel.jtxt_buscarBibliotecario.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.jtxt_buscarBibliotecario
                        .setBorder(BorderFactory.createLineBorder(new Color(0, 123, 255)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.jtxt_buscarBibliotecario
                        .setBorder(BorderFactory.createLineBorder(Color.GRAY));
            }
        });
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
        estilizarTabla(panel.jtb_Bibliotecarios, new Color(0, 51, 102));
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

        // Centrar columna Estado
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tabla.getColumnModel().getColumn(4).setCellRenderer(center);
    }

    // ================= CARGAR =================
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
    }

    // ================= BUSQUEDA TIEMPO REAL =================
    private void configurarBusquedaTiempoReal() {
        panel.jtxt_buscarBibliotecario.getDocument()
                .addDocumentListener(new DocumentListener() {

                    @Override
                    public void insertUpdate(DocumentEvent e) {
                        buscar();
                    }

                    @Override
                    public void removeUpdate(DocumentEvent e) {
                        buscar();
                    }

                    @Override
                    public void changedUpdate(DocumentEvent e) {
                        buscar();
                    }
                });
    }

    private void buscar() {
        String texto = panel.jtxt_buscarBibliotecario.getText().trim();

        if (texto.isEmpty() || texto.equals("Buscar por cédula...")) {
            cargarBibliotecarios();
            return;
        }

        modeloTabla.setRowCount(0);
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

    // ================= MENU CONTEXTUAL =================
    private void configurarMenuClickDerechoMejorado() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));

        JMenuItem editar = new JMenuItem("Editar");
        JMenuItem eliminar = new JMenuItem("Eliminar");

        editar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        eliminar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        editar.addActionListener(e -> editarBibliotecario());
        eliminar.addActionListener(e -> eliminarBibliotecario());

        menu.add(editar);
        menu.addSeparator();
        menu.add(eliminar);

        panel.jtb_Bibliotecarios.setComponentPopupMenu(menu);

        panel.jtb_Bibliotecarios.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarBibliotecario();
                }
            }
        });
    }

    // ================= EDITAR =================
    private void editarBibliotecario() {
        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) return;

        String cedula = modeloTabla.getValueAt(fila, 0).toString();
        Usuario u = usuarioDAO.buscarPorCedula(cedula);
        if (u == null) return;

        Window parent = SwingUtilities.getWindowAncestor(panel);
        DialogBibliotecario d = new DialogBibliotecario((Frame) parent, true);

        d.txtCedula.setText(u.getCedula());
        d.txtCedula.setEnabled(false);
        d.txtNombres.setText(u.getNombres());
        d.txtApellidos.setText(u.getApellidos());
        d.txtEmail.setText(u.getCorreo());
        d.cmbEstado.setSelectedItem(u.getEstado());

        d.txtRol.setText("BIBLIOTECARIO");
        d.txtRol.setEnabled(false);

        ocultarErrores(d);

        d.btnGuardar.addActionListener(e -> {
            u.setNombres(d.txtNombres.getText().trim());
            u.setApellidos(d.txtApellidos.getText().trim());
            u.setCorreo(d.txtEmail.getText().trim());
            u.setEstado(d.cmbEstado.getSelectedItem().toString());

            String pass = new String(d.txtPassword.getPassword());
            if (!pass.isEmpty()) {
                u.setContrasena(pass);
            }

            if (usuarioDAO.editarBibliotecario(u)) {
                cargarBibliotecarios();
                d.dispose();
            }
        });

        d.btnCancelar.addActionListener(e -> d.dispose());
        d.setLocationRelativeTo(panel);
        d.setVisible(true);
    }

    // ================= ELIMINAR =================
    private void eliminarBibliotecario() {
        int fila = panel.jtb_Bibliotecarios.getSelectedRow();
        if (fila == -1) return;

        String cedula = modeloTabla.getValueAt(fila, 0).toString();
        Usuario u = usuarioDAO.buscarPorCedula(cedula);

        if (u != null && usuarioDAO.eliminarBibliotecario(u.getIdUsuario())) {
            cargarBibliotecarios();
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

    // ================= EVENTOS =================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == panel.jbtn_agregarBiblioteario) {
            abrirDialogRegistro();
        }
    }

    private void abrirDialogRegistro() {
        Window parent = SwingUtilities.getWindowAncestor(panel);
        DialogBibliotecario d = new DialogBibliotecario((Frame) parent, true);

        d.txtRol.setText("BIBLIOTECARIO");
        d.txtRol.setEnabled(false);

        ocultarErrores(d);

        d.btnGuardar.addActionListener(e -> {
            Usuario u = new Usuario();
            u.setCedula(d.txtCedula.getText().trim());
            u.setNombres(d.txtNombres.getText().trim());
            u.setApellidos(d.txtApellidos.getText().trim());
            u.setCorreo(d.txtEmail.getText().trim());
            u.setContrasena(new String(d.txtPassword.getPassword()));
            u.setEstado(d.cmbEstado.getSelectedItem().toString());
            u.setRol("BIBLIOTECARIO");

            if (usuarioDAO.agregarBibliotecario(u)) {
                cargarBibliotecarios();
                d.dispose();
            }
        });

        d.btnCancelar.addActionListener(e -> d.dispose());
        d.setLocationRelativeTo(panel);
        d.setVisible(true);
    }
}
