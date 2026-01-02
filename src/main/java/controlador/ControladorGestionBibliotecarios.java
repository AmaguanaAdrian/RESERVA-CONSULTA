/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import vista.GestionBibliotecarios;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 *
 * @author amagu
 */

public class ControladorGestionBibliotecarios implements ActionListener {

    private GestionBibliotecarios panel;
    private DefaultTableModel modeloTabla;

    public ControladorGestionBibliotecarios(GestionBibliotecarios panel) {
        this.panel = panel;

        // Configurar tabla
        configurarTabla();

        // Escuchar botones
        panel.jbtn_agregarBiblioteario.addActionListener(this);
        panel.jbtn_buscarBibliotecario.addActionListener(this);
        panel.jbtn_editarBibliotecario.addActionListener(this);
        panel.jbtn_eliminarBibliotecario.addActionListener(this);
    }

    private void configurarTabla() {

        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("ID");
        modeloTabla.addColumn("Cédula");
        modeloTabla.addColumn("Nombres");
        modeloTabla.addColumn("Apellidos");
        modeloTabla.addColumn("Estado");

        panel.jtb_Bibliotecarios.setModel(modeloTabla);
    }

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

    // ===== MÉTODOS LÓGICOS =====

    private void agregarBibliotecario() {
        JOptionPane.showMessageDialog(panel, "Agregar bibliotecario (pendiente)");
    }

    private void buscarBibliotecario() {
        String texto = panel.jtxt_buscarBibliotecario.getText();
        JOptionPane.showMessageDialog(panel, "Buscar: " + texto);
    }

    private void editarBibliotecario() {
        JOptionPane.showMessageDialog(panel, "Editar bibliotecario (pendiente)");
    }

    private void eliminarBibliotecario() {
        JOptionPane.showMessageDialog(panel, "Eliminar bibliotecario (pendiente)");
    }
}
