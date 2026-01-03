package controlador;

import modelo.Reserva;
import modelo.ReservaDAO;
import vista.MisReservas;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ControladorMisReservas {
    private MisReservas vista;
    private ReservaDAO dao;
    private int idUsuario;

    public ControladorMisReservas(MisReservas vista, int idUsuario) {
        this.vista = vista;
        this.dao = new ReservaDAO();
        this.idUsuario = idUsuario;
        
        llenarTabla();
    }

    public void llenarTabla() {
        // 1. Obtener el modelo de la tabla que está en el JPanel
        DefaultTableModel modelo = (DefaultTableModel) vista.jTable1.getModel();
        
        // 2. Limpiar la tabla por si tiene datos viejos
        modelo.setRowCount(0);
        
        // 3. Traer los datos desde la base de datos
        List<Reserva> lista = dao.listarPorEstudiante(idUsuario);
        
        // 4. Agregar cada reserva como una fila
        for (Reserva r : lista) {
            Object[] fila = {
                r.getIdReserva(),
                r.getTituloLibro(),
                r.getFechaReserva(),
                r.getFechaExpiracion(),
                r.getEstado()
            };
            modelo.addRow(fila);
        }
    }
}