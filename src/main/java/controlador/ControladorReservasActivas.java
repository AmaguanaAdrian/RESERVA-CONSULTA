/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import modelo.Reserva;
import modelo.ReservaDAO;
import vista.ReservasActivas;

/**
 *
 * @author amagu
 */
public class ControladorReservasActivas {

    private ReservasActivas vista;
    private ReservaDAO reservaDAO;
    private DefaultTableModel modeloReservas;

    public ControladorReservasActivas(ReservasActivas vista) {
        this.vista = vista;
        this.reservaDAO = new ReservaDAO();

        configurarTabla();
        cargarReservasActivas();
        aplicarEstilosTabla();
    }

    // ================= CONFIGURAR TABLA =================
    private void configurarTabla() {
        // Columnas: ID, Estudiante, Libro, Fecha Reserva, Fecha Límite, Estado
        modeloReservas = new DefaultTableModel(new String[]{"ID", "Estudiante", "Libro", "Fecha Reserva", "Fecha Límite", "Estado"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        vista.jtb_ReservasActivas.setModel(modeloReservas);
    }

    // ================= CARGAR RESERVAS ACTIVAS =================
    private void cargarReservasActivas() {
        modeloReservas.setRowCount(0);
        List<Object[]> reservas = reservaDAO.listarReservasActivas();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (Object[] r : reservas) {
            modeloReservas.addRow(new Object[]{
                r[0],
                r[1],
                r[2],
                ((LocalDateTime) r[3]).format(formatter),
                ((LocalDateTime) r[4]).format(formatter),
                r[5]
            });
        }
    }

    // ================= APLICAR ESTILOS A LA TABLA =================
    private void aplicarEstilosTabla() {
        JTable tabla = vista.jtb_ReservasActivas;

        // Configurar altura de filas
        tabla.setRowHeight(30);

        // Configurar fuente
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Configurar selección
        tabla.setSelectionBackground(new Color(220, 247, 255));
        tabla.setSelectionForeground(Color.BLACK);

        // Configurar líneas de la tabla
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);

        // Configurar header de la tabla
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(0, 123, 255)); // Azul
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Configurar ancho de columnas
        tabla.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tabla.getColumnModel().getColumn(1).setPreferredWidth(150); // Estudiante
        tabla.getColumnModel().getColumn(2).setPreferredWidth(200); // Libro
        tabla.getColumnModel().getColumn(3).setPreferredWidth(150); // Fecha Reserva
        tabla.getColumnModel().getColumn(4).setPreferredWidth(150); // Fecha Límite
        tabla.getColumnModel().getColumn(5).setPreferredWidth(100); // Estado

        // Aplicar renderer para filas alternadas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                // Filas alternadas
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }

                // Resaltar estado (opcional)
                String estado = (String) table.getModel().getValueAt(row, 5);
                if ("RESERVADA".equals(estado)) {
                    ((JLabel) c).setForeground(new Color(40, 167, 69)); // Verde para RESERVADA
                } else if ("CANCELADA".equals(estado)) {
                    ((JLabel) c).setForeground(new Color(220, 53, 69)); // Rojo para CANCELADA
                } else {
                    ((JLabel) c).setForeground(Color.BLACK);
                }

                return c;
            }
        });
    }

    // ================= MÉTODO PARA REFRESCAR DATOS =================
    public void refrescarDatos() {
        cargarReservasActivas();
    }
}
