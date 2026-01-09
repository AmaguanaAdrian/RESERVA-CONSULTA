/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Utilidades.AppUtils;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import modelo.ReservaDAO;
import modelo.Sesion;
import vista.MisReservas;

/**
 *
 * @author amagu
 */
public class ControladorMisReservas implements ActionListener {

    private MisReservas vista;
    private ReservaDAO reservaDAO;
    private DefaultTableModel modeloReservas;

    public ControladorMisReservas(MisReservas vista) {
        this.vista = vista;
        this.reservaDAO = new ReservaDAO();

        aplicarEstilosVisuales();
        configurarTablaMejorada();
        cargarReservasEstudiante();
        configurarInteraccionesTabla();
    }

    // ================= MEJORAS VISUALES =================
    private void aplicarEstilosVisuales() {
        if (vista.jLabel1 != null) {
            vista.jLabel1.setFont(new Font("Segoe UI", Font.BOLD, 20));
            vista.jLabel1.setForeground(new Color(0, 51, 102));
        }
        if (vista.jSeparator1 != null) {
            vista.jSeparator1.setForeground(new Color(0, 123, 255));
        }
        if (vista.getParent() != null) {
            vista.setBackground(new Color(248, 249, 250));
        }
    }

    // ================= CONFIGURACIÓN DE TABLA =================
    private void configurarTablaMejorada() {
        // Columna "ID Reserva" eliminada de la lista
        String[] columnas = {
            "Título del Libro", 
            "Fecha Reserva", 
            "Fecha Límite", 
            "Estado"
        };

        modeloReservas = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return String.class;
            }
        };

        vista.jtb_ReservasEstudiantes.setModel(modeloReservas);
        AppUtils.estilizarTabla(vista.jtb_ReservasEstudiantes, new Color(0, 51, 102));
        
        configurarAnchoColumnas();
        
        vista.jtb_ReservasEstudiantes.setAutoCreateRowSorter(true);
        vista.jtb_ReservasEstudiantes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }

    private void configurarAnchoColumnas() {
        TableColumnModel columnModel = vista.jtb_ReservasEstudiantes.getColumnModel();
        
        // Ajuste de índices (0 ahora es Título)
        columnModel.getColumn(0).setPreferredWidth(300);  // Título
        columnModel.getColumn(1).setPreferredWidth(120);  // Fecha Reserva
        columnModel.getColumn(2).setPreferredWidth(120);  // Fecha Límite
        columnModel.getColumn(3).setPreferredWidth(100);  // Estado
        
        // Aplicar renderers a los nuevos índices
        vista.jtb_ReservasEstudiantes.getColumnModel().getColumn(3).setCellRenderer(new EstadoRenderer());
    }

    // ================= CARGAR DATOS =================
    private void cargarReservasEstudiante() {
        Sesion sesion = Sesion.getInstancia();
        // Nota: Asegúrate de que el método esEstudiante() exista en tu clase Sesion
        Integer idEstudiante = sesion.getIdEstudiante();
        
        if (idEstudiante == null) {
            JOptionPane.showMessageDialog(vista, "No se pudo identificar al estudiante.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        modeloReservas.setRowCount(0);

        java.util.List<java.util.Map<String, Object>> reservas = 
            reservaDAO.listarReservasPorEstudiante(idEstudiante);

        if (reservas.isEmpty()) {
            modeloReservas.addRow(new Object[]{
                "No hay reservas", "-", "-", "-"
            });
            vista.jtb_ReservasEstudiantes.setEnabled(false);
            return;
        }

        for (java.util.Map<String, Object> reserva : reservas) {
            // El id_reserva ya no se añade a la fila del modelo de la tabla
            modeloReservas.addRow(new Object[]{
                reserva.get("titulo"),
                formatearFecha(reserva.get("fecha_reserva")),
                formatearFecha(reserva.get("fecha_limite")),
                reserva.get("estado")
            });
        }

        resaltarReservasPorVencer();
        vista.jtb_ReservasEstudiantes.setRowHeight(30);
    }

    private String formatearFecha(Object fecha) {
        if (fecha == null) return "N/A";
        if (fecha instanceof java.sql.Timestamp) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
            return sdf.format((java.sql.Timestamp) fecha);
        }
        return fecha.toString();
    }

    private void resaltarReservasPorVencer() {
        javax.swing.table.DefaultTableCellRenderer renderer = new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (column == 2) { // Ahora la fecha límite es la columna 2
                    try {
                        String fechaStr = value.toString();
                        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");
                        java.util.Date fechaLimite = sdf.parse(fechaStr);
                        java.util.Date ahora = new java.util.Date();
                        
                        long diferenciaHoras = (fechaLimite.getTime() - ahora.getTime()) / (60 * 60 * 1000);
                        
                        if (diferenciaHoras < 0) {
                            c.setBackground(new Color(255, 228, 228));
                            c.setForeground(new Color(220, 53, 69));
                        } else if (diferenciaHoras < 24) {
                            c.setBackground(new Color(255, 243, 205));
                            c.setForeground(new Color(255, 193, 7));
                        }
                    } catch (Exception e) {}
                }
                return c;
            }
        };
        vista.jtb_ReservasEstudiantes.getColumnModel().getColumn(2).setCellRenderer(renderer);
    }

    // ================= CONFIGURAR INTERACCIONES =================
    private void configurarInteraccionesTabla() {
        vista.jtb_ReservasEstudiantes.setToolTipText(
            "<html>Doble clic para ver detalles de la reserva</html>"
        );

        vista.jtb_ReservasEstudiantes.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    mostrarDetallesReserva();
                }
            }
        });

        configurarMenuContextual();
    }

    private void configurarMenuContextual() {
        JPopupMenu menuContextual = new JPopupMenu();
        JMenuItem verDetalles = new JMenuItem("Ver detalles");
        JMenuItem refrescar = new JMenuItem("Refrescar");
        
        verDetalles.addActionListener(e -> mostrarDetallesReserva());
        refrescar.addActionListener(e -> refrescarReservas());
        
        menuContextual.add(verDetalles);
        menuContextual.addSeparator();
        menuContextual.add(refrescar);
        
        vista.jtb_ReservasEstudiantes.setComponentPopupMenu(menuContextual);
    }

    private void mostrarDetallesReserva() {
        int filaSeleccionada = vista.jtb_ReservasEstudiantes.getSelectedRow();
        if (filaSeleccionada == -1) return;
        
        int modeloFila = vista.jtb_ReservasEstudiantes.convertRowIndexToModel(filaSeleccionada);
        
        // Ajuste de obtención de datos por índices
        String titulo = modeloReservas.getValueAt(modeloFila, 0).toString();
        String fechaReserva = modeloReservas.getValueAt(modeloFila, 1).toString();
        String fechaLimite = modeloReservas.getValueAt(modeloFila, 2).toString();
        String estado = modeloReservas.getValueAt(modeloFila, 3).toString();
        
        String detalles = "<html><div style='width: 250px;'>" +
            "<h3 style='color: #0066cc;'>Detalles de Reserva</h3>" +
            "<b>Libro:</b> " + titulo + "<br>" +
            "<b>Fecha Reserva:</b> " + fechaReserva + "<br>" +
            "<b>Fecha Límite:</b> " + fechaLimite + "<br>" +
            "<b>Estado:</b> " + estado +
            "</div></html>";
        
        JOptionPane.showMessageDialog(vista, detalles, "Detalles", JOptionPane.INFORMATION_MESSAGE);
    }

    private void refrescarReservas() {
        cargarReservasEstudiante();
    }

    // ================= RENDERER DE ESTADO =================
    private class EstadoRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            JLabel label = (JLabel) c;
            label.setHorizontalAlignment(JLabel.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            if (value != null) {
                String estado = value.toString();
                if (estado.contains("RESERVADA")) label.setForeground(new Color(40, 167, 69));
                else if (estado.contains("CANCELADA")) label.setForeground(new Color(108, 117, 125));
            }
            return label;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {}
}