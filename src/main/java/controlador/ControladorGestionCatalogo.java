package controlador;
import modelo.Libro;
import modelo.LibroDAO;
import vista.GestionCatalogo;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ControladorGestionCatalogo {
    private GestionCatalogo vista;
    private LibroDAO dao;

    public ControladorGestionCatalogo(GestionCatalogo vista) {
        this.vista = vista;
        this.dao = new LibroDAO();
        llenarTabla();
    }

    public void llenarTabla() {
        DefaultTableModel modelo = new DefaultTableModel();
        modelo.addColumn("ID");
        modelo.addColumn("Título");
        modelo.addColumn("Autor");
        modelo.addColumn("Género");
        modelo.addColumn("Stock");

        List<Libro> libros = dao.listarCatalogo();
        for (Libro l : libros) {
            modelo.addRow(new Object[]{
                l.getIdLibro(), l.getTitulo(), l.getAutor(), l.getGenero(), l.getCantidad()
            });
        }
        vista.jtb_Libros.setModel(modelo); // Asegúrate que tu JTable sea public
    }
}