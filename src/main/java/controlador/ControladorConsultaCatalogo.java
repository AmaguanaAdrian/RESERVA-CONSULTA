package controlador;
import modelo.*;
import vista.ConsultaCatalogo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ControladorConsultaCatalogo {
    private ConsultaCatalogo vista;
    private LibroDAO libroDao;
    private ReservaDAO reservaDao;

    public ControladorConsultaCatalogo(ConsultaCatalogo vista) {
        this.vista = vista;
        this.libroDao = new LibroDAO();
        this.reservaDao = new ReservaDAO();
        
        cargarTabla();

        // Acción del botón Reservar (asumiendo que se llama jButton1)
        this.vista.jButton1.addActionListener(e -> {
            int fila = vista.jTable1.getSelectedRow();
            if (fila != -1) {
                int idLibro = (int) vista.jTable1.getValueAt(fila, 0);
                String msj = reservaDao.realizarReserva(Sesion.idEstudianteLogueado, idLibro);
                JOptionPane.showMessageDialog(vista, msj);
                cargarTabla(); // Refrescar stock
            }
        });
    }

    private void cargarTabla() {
        DefaultTableModel modelo = (DefaultTableModel) vista.jTable1.getModel();
        modelo.setRowCount(0);
        List<Libro> libros = libroDao.listarCatalogo();
        for (Libro l : libros) {
            modelo.addRow(new Object[]{l.getIdLibro(), l.getTitulo(), l.getAutor(), l.getCantidad()});
        }
    }
}