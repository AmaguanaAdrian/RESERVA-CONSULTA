/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import modelo.Libro;
import modelo.LibroDAO;
import vista.GestionCatalogo;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import modelo.Autor;
import modelo.AutorDAO;
import modelo.Genero;
import modelo.GeneroDAO;

/**
 *
 * @author amagu
 */
public class ControladorGestionCatalogo implements ActionListener {

    private GestionCatalogo vista;

    private LibroDAO libroDAO = new LibroDAO();
    private AutorDAO autorDAO = new AutorDAO();
    private GeneroDAO generoDAO = new GeneroDAO();

    private DefaultTableModel modeloLibros;
    private DefaultTableModel modeloAutores;
    private DefaultTableModel modeloGeneros;

    public ControladorGestionCatalogo(GestionCatalogo vista) {
        this.vista = vista;

        // LIBROS
        vista.jbtn_agregarCatalogo.addActionListener(this);
        vista.jbtn_editarCatalogo.addActionListener(this);
        vista.jbtn_eliminarCatalogo.addActionListener(this);

        // AUTORES
        vista.jbtn_agregarAutor.addActionListener(this);
        vista.jtbn_editarAutor.addActionListener(this);
        vista.jbtn_eliminarAutor.addActionListener(this);

        // GENEROS
        vista.jbtn_agregarGenero.addActionListener(this);
        vista.jtbn_editarGeneros.addActionListener(this);
        vista.jbtn_eliminarGeneros.addActionListener(this);

        cargarTodo();
    }

    // ================== CARGAS ==================

    private void cargarTodo() {
        cargarAutores();
        cargarGeneros();
        cargarLibros();
        cargarComboAutores();
        cargarComboGeneros();
    }

    private void cargarLibros() {
        modeloLibros = new DefaultTableModel(
                new String[]{"ID", "Título", "Cantidad", "Autor", "Género"}, 0
        );

        for (Libro l : libroDAO.listarLibros()) {
            modeloLibros.addRow(new Object[]{
                l.getIdLibro(),
                l.getTitulo(),
                l.getCantidadDisponible(),
                l.getIdAutor(),
                l.getIdGenero()
            });
        }

        vista.jtb_Libros.setModel(modeloLibros);
    }

    private void cargarAutores() {
        modeloAutores = new DefaultTableModel(
                new String[]{"ID", "Autor"}, 0
        );

        for (Autor a : autorDAO.listarAutores()) {
            modeloAutores.addRow(new Object[]{
                a.getIdAutor(),
                a.getNombreAutor()
            });
        }

        vista.jtb_Autores.setModel(modeloAutores);
    }

    private void cargarGeneros() {
        modeloGeneros = new DefaultTableModel(
                new String[]{"ID", "Género"}, 0
        );

        for (Genero g : generoDAO.listarGeneros()) {
            modeloGeneros.addRow(new Object[]{
                g.getIdGenero(),
                g.getNombreGenero()
            });
        }

        vista.jtb_Generos.setModel(modeloGeneros);
    }

    private void cargarComboAutores() {
        vista.jcbx_Autores.removeAllItems();
        for (Autor a : autorDAO.listarAutores()) {
            vista.jcbx_Autores.addItem(a.getIdAutor() + " - " + a.getNombreAutor());
        }
    }

    private void cargarComboGeneros() {
        vista.jcbx_Generos.removeAllItems();
        for (Genero g : generoDAO.listarGeneros()) {
            vista.jcbx_Generos.addItem(g.getIdGenero() + " - " + g.getNombreGenero());
        }
    }

    // ================== EVENTOS ==================

    @Override
    public void actionPerformed(ActionEvent e) {

        // LIBROS
        if (e.getSource() == vista.jbtn_agregarCatalogo) agregarLibro();
        if (e.getSource() == vista.jbtn_editarCatalogo) editarLibro();
        if (e.getSource() == vista.jbtn_eliminarCatalogo) eliminarLibro();

        // AUTORES
        if (e.getSource() == vista.jbtn_agregarAutor) agregarAutor();
        if (e.getSource() == vista.jtbn_editarAutor) editarAutor();
        if (e.getSource() == vista.jbtn_eliminarAutor) eliminarAutor();

        // GENEROS
        if (e.getSource() == vista.jbtn_agregarGenero) agregarGenero();
        if (e.getSource() == vista.jtbn_editarGeneros) editarGenero();
        if (e.getSource() == vista.jbtn_eliminarGeneros) eliminarGenero();
    }

    // ================== CRUD LIBROS ==================

    private void agregarLibro() {
        try {
            String titulo = JOptionPane.showInputDialog(vista, "Título del libro:");
            if (titulo == null || titulo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Debe ingresar un título", 
                                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cantStr = JOptionPane.showInputDialog(vista, "Cantidad:");
            if (cantStr == null || cantStr.trim().isEmpty()) return;
            
            int cantidad = Integer.parseInt(cantStr.trim());
            
            if (cantidad < 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad no puede ser negativa", 
                                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (vista.jcbx_Autores.getItemCount() == 0) {
                JOptionPane.showMessageDialog(vista, "No hay autores disponibles. Agregue uno primero.", 
                                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (vista.jcbx_Generos.getItemCount() == 0) {
                JOptionPane.showMessageDialog(vista, "No hay géneros disponibles. Agregue uno primero.", 
                                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idAutor = Integer.parseInt(
                    vista.jcbx_Autores.getSelectedItem().toString().split(" - ")[0]
            );
            int idGenero = Integer.parseInt(
                    vista.jcbx_Generos.getSelectedItem().toString().split(" - ")[0]
            );

            Libro l = new Libro();
            l.setTitulo(titulo.trim());
            l.setCantidadDisponible(cantidad);
            l.setIdAutor(idAutor);
            l.setIdGenero(idGenero);

            if (libroDAO.agregarLibro(l)) {
                cargarLibros();
                JOptionPane.showMessageDialog(vista, "Libro agregado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al agregar el libro", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser un número válido", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error inesperado: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarLibro() {
        try {
            int fila = vista.jtb_Libros.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(vista, "Seleccione un libro de la tabla", 
                                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idLibro = (int) modeloLibros.getValueAt(fila, 0);
            
            String titulo = JOptionPane.showInputDialog(vista, "Nuevo título:");
            if (titulo == null || titulo.trim().isEmpty()) {
                JOptionPane.showMessageDialog(vista, "Debe ingresar un título", 
                                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cantStr = JOptionPane.showInputDialog(vista, "Nueva cantidad:");
            if (cantStr == null || cantStr.trim().isEmpty()) return;
            
            int cantidad = Integer.parseInt(cantStr.trim());
            
            if (cantidad < 0) {
                JOptionPane.showMessageDialog(vista, "La cantidad no puede ser negativa", 
                                            "Advertencia", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idAutor = Integer.parseInt(
                    vista.jcbx_Autores.getSelectedItem().toString().split(" - ")[0]
            );
            int idGenero = Integer.parseInt(
                    vista.jcbx_Generos.getSelectedItem().toString().split(" - ")[0]
            );

            Libro l = new Libro();
            l.setIdLibro(idLibro);
            l.setTitulo(titulo.trim());
            l.setCantidadDisponible(cantidad);
            l.setIdAutor(idAutor);
            l.setIdGenero(idGenero);

            if (libroDAO.editarLibro(l)) {
                cargarLibros();
                JOptionPane.showMessageDialog(vista, "Libro actualizado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al actualizar el libro", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista, "La cantidad debe ser un número válido", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista, "Error inesperado: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarLibro() {
        int fila = vista.jtb_Libros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un libro de la tabla", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(vista, 
                            "¿Está seguro de eliminar este libro?", 
                            "Confirmar eliminación", 
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            int idLibro = (int) modeloLibros.getValueAt(fila, 0);

            if (libroDAO.eliminarLibro(idLibro)) {
                cargarLibros();
                JOptionPane.showMessageDialog(vista, "Libro eliminado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, "Error al eliminar el libro", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== CRUD AUTORES ==================

    private void agregarAutor() {
        String nombre = vista.jtxt_nombreAutor.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese un nombre de autor", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (autorDAO.agregarAutor(nombre)) {
            cargarAutores();
            cargarComboAutores();
            vista.jtxt_nombreAutor.setText("");
            JOptionPane.showMessageDialog(vista, "Autor agregado exitosamente", 
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "Error al agregar el autor", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarAutor() {
        int fila = vista.jtb_Autores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un autor de la tabla", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = vista.jtxt_nombreAutor.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese un nombre de autor", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloAutores.getValueAt(fila, 0);

        if (autorDAO.editarAutor(id, nombre)) {
            cargarAutores();
            cargarComboAutores();
            vista.jtxt_nombreAutor.setText("");
            JOptionPane.showMessageDialog(vista, "Autor actualizado exitosamente", 
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "Error al actualizar el autor", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarAutor() {
        int fila = vista.jtb_Autores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un autor de la tabla", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(vista, 
                            "¿Está seguro de eliminar este autor?\n" +
                            "Esta acción podría afectar a los libros asociados.", 
                            "Confirmar eliminación", 
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = (int) modeloAutores.getValueAt(fila, 0);
            
            if (autorDAO.eliminarAutor(id)) {
                cargarAutores();
                cargarComboAutores();
                cargarLibros(); // Recargar libros por si alguno quedó sin autor
                vista.jtxt_nombreAutor.setText("");
                JOptionPane.showMessageDialog(vista, "Autor eliminado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, 
                    "No se puede eliminar el autor porque está asociado a uno o más libros", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================== CRUD GENEROS ==================

    private void agregarGenero() {
        String nombre = vista.jtxt_nombreGenero.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese un nombre de género", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (generoDAO.agregarGenero(nombre)) {
            cargarGeneros();
            cargarComboGeneros();
            vista.jtxt_nombreGenero.setText("");
            JOptionPane.showMessageDialog(vista, "Género agregado exitosamente", 
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "Error al agregar el género", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editarGenero() {
        int fila = vista.jtb_Generos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un género de la tabla", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String nombre = vista.jtxt_nombreGenero.getText().trim();
        
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese un nombre de género", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id = (int) modeloGeneros.getValueAt(fila, 0);

        if (generoDAO.editarGenero(id, nombre)) {
            cargarGeneros();
            cargarComboGeneros();
            vista.jtxt_nombreGenero.setText("");
            JOptionPane.showMessageDialog(vista, "Género actualizado exitosamente", 
                                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista, "Error al actualizar el género", 
                                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarGenero() {
        int fila = vista.jtb_Generos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista, "Seleccione un género de la tabla", 
                                        "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmacion = JOptionPane.showConfirmDialog(vista, 
                            "¿Está seguro de eliminar este género?\n" +
                            "Esta acción podría afectar a los libros asociados.", 
                            "Confirmar eliminación", 
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.QUESTION_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            int id = (int) modeloGeneros.getValueAt(fila, 0);
            
            if (generoDAO.eliminarGenero(id)) {
                cargarGeneros();
                cargarComboGeneros();
                cargarLibros(); // Recargar libros por si alguno quedó sin género
                vista.jtxt_nombreGenero.setText("");
                JOptionPane.showMessageDialog(vista, "Género eliminado exitosamente", 
                                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista, 
                    "No se puede eliminar el género porque está asociado a uno o más libros", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
