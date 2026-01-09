/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;
import Utilidades.AppUtils;
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
import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import vista.DialogAutorGenero;
import vista.DialogLibros;
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
    
    private String modoActual = "AUTORES";

    public ControladorGestionCatalogo(GestionCatalogo vista) {
        this.vista = vista;

        aplicarEstilosVisuales();
        configurarTablasMejoradas();
        cargarTodo();
        configurarMenusContextuales();
        agregarListenersBusqueda();
        agregarEfectosHover();

        // Asignar listeners a los botones
        vista.jbtn_agregarLibro.addActionListener(this);
        vista.jbtn_agregarAutor.addActionListener(this);
        vista.jbtn_agregarGenero.addActionListener(this);
    }

    // ================= MEJORAS VISUALES =================
    private void aplicarEstilosVisuales() {
        // Estilo para botones principales
        AppUtils.estiloBotonModerno(vista.jbtn_agregarLibro, new Color(40, 167, 69), "Agregar Libro");
        AppUtils.estiloBotonModerno(vista.jbtn_agregarAutor, new Color(23, 162, 184), "Agregar Autor");
        AppUtils.estiloBotonModerno(vista.jbtn_agregarGenero, new Color(108, 117, 125), "Agregar Género");

        // Estilo para pestañas
        if (vista.jtbt_Catalogo != null) {
            vista.jtbt_Catalogo.setFont(new Font("Segoe UI", Font.BOLD, 13));
            vista.jtbt_Catalogo.setForeground(new Color(0, 51, 102));
        }
    }

    private void agregarEfectosHover() {
        // Efecto para el campo de búsqueda de libros
        if (vista.jtxt_buscarLibros != null) {
            AppUtils.agregarEfectoHoverCampo(vista.jtxt_buscarLibros, 
                new Color(0, 123, 255), new Color(206, 212, 218));
        }
    }

    // ================= CONFIGURACIÓN DE TABLAS =================
    private void configurarTablasMejoradas() {
        // Tabla Libros
        modeloLibros = new DefaultTableModel(new String[]{"ID", "Título", "Cantidad", "Autor", "Género"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vista.jtb_Libros.setModel(modeloLibros);
        AppUtils.estilizarTabla(vista.jtb_Libros, new Color(0, 51, 102));

        // Tabla Autores
        modeloAutores = new DefaultTableModel(new String[]{"ID", "Autor"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vista.jtb_Autores.setModel(modeloAutores);
        AppUtils.estilizarTabla(vista.jtb_Autores, new Color(23, 162, 184));

        // Tabla Géneros
        modeloGeneros = new DefaultTableModel(new String[]{"ID", "Género"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vista.jtb_Generos.setModel(modeloGeneros);
        AppUtils.estilizarTabla(vista.jtb_Generos, new Color(108, 117, 125));
    }

    // ================= CARGAR DATOS =================
    private void cargarTodo() {
        cargarAutores();
        cargarGeneros();
        cargarLibros();
//        cargarComboAutores();
//        cargarComboGeneros();
    }

    private void cargarLibros() {
        modeloLibros.setRowCount(0);
        List<Libro> libros = libroDAO.listarLibros();
        
        for (Libro l : libros) {
            String nombreAutor = obtenerNombreAutor(l.getIdAutor());
            String nombreGenero = obtenerNombreGenero(l.getIdGenero());
            
            modeloLibros.addRow(new Object[]{
                l.getIdLibro(),
                l.getTitulo(),
                l.getCantidadDisponible(),
                nombreAutor,
                nombreGenero
            });
        }
    }

    private String obtenerNombreAutor(int idAutor) {
        for (Autor a : autorDAO.listarAutores()) {
            if (a.getIdAutor() == idAutor) {
                return a.getNombreAutor();
            }
        }
        return "Desconocido";
    }

    private String obtenerNombreGenero(int idGenero) {
        for (Genero g : generoDAO.listarGeneros()) {
            if (g.getIdGenero() == idGenero) {
                return g.getNombreGenero();
            }
        }
        return "Desconocido";
    }

    private void cargarAutores() {
        modeloAutores.setRowCount(0);
        List<Autor> autores = autorDAO.listarAutores();
        
        for (Autor a : autores) {
            modeloAutores.addRow(new Object[]{
                a.getIdAutor(),
                a.getNombreAutor()
            });
        }
    }

    private void cargarGeneros() {
        modeloGeneros.setRowCount(0);
        List<Genero> generos = generoDAO.listarGeneros();
        
        for (Genero g : generos) {
            modeloGeneros.addRow(new Object[]{
                g.getIdGenero(),
                g.getNombreGenero()
            });
        }
    }

//    private void cargarComboAutores() {
//        vista.jcbx_Autores.removeAllItems();
//        vista.jcbx_Autores.addItem("Seleccione un autor");
//        for (Autor a : autorDAO.listarAutores()) {
//            vista.jcbx_Autores.addItem(a.getIdAutor() + " - " + a.getNombreAutor());
//        }
//    }
//
//    private void cargarComboGeneros() {
//        vista.jcbx_Generos.removeAllItems();
//        vista.jcbx_Generos.addItem("Seleccione un género");
//        for (Genero g : generoDAO.listarGeneros()) {
//            vista.jcbx_Generos.addItem(g.getIdGenero() + " - " + g.getNombreGenero());
//        }
//    }

    // ================= MENÚS CONTEXTUALES =================
    private void configurarMenusContextuales() {
        // Menú contextual para Libros
        configurarMenuContextualTabla(vista.jtb_Libros, "LIBROS");
        
        // Menú contextual para Autores
        configurarMenuContextualTabla(vista.jtb_Autores, "AUTORES");
        
        // Menú contextual para Géneros
        configurarMenuContextualTabla(vista.jtb_Generos, "GENEROS");
    }

    private void configurarMenuContextualTabla(JTable tabla, String tipo) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(new Color(206, 212, 218)));
        
        JMenuItem editar = new JMenuItem("Editar");
        JMenuItem eliminar = new JMenuItem("Eliminar");
        
        editar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        eliminar.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        AppUtils.estiloItemMenu(editar);
        AppUtils.estiloItemMenu(eliminar);

        editar.addActionListener(e -> {
            if (tipo.equals("LIBROS")) editarLibro();
            else if (tipo.equals("AUTORES")) editarAutor();
            else if (tipo.equals("GENEROS")) editarGenero();
        });

        eliminar.addActionListener(e -> {
            if (tipo.equals("LIBROS")) eliminarLibro();
            else if (tipo.equals("AUTORES")) eliminarAutor();
            else if (tipo.equals("GENEROS")) eliminarGenero();
        });

        menu.add(editar);
        menu.addSeparator();
        menu.add(eliminar);
        
        tabla.setComponentPopupMenu(menu);
        
        // Doble click para editar
        tabla.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (tipo.equals("LIBROS")) editarLibro();
                    else if (tipo.equals("AUTORES")) editarAutor();
                    else if (tipo.equals("GENEROS")) editarGenero();
                }
            }
        });
    }

    // ================= BÚSQUEDA EN TIEMPO REAL =================
    private void agregarListenersBusqueda() {
        // Búsqueda en tiempo real para Libros
        if (vista.jtxt_buscarLibros != null) {
            vista.jtxt_buscarLibros.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    buscarLibros();
                }
                @Override
                public void removeUpdate(DocumentEvent e) {
                    buscarLibros();
                }
                @Override
                public void changedUpdate(DocumentEvent e) {
                    buscarLibros();
                }
            });
        }
    }

    private void buscarLibros() {
        String texto = vista.jtxt_buscarLibros != null ? 
                      vista.jtxt_buscarLibros.getText().trim().toLowerCase() : "";
        
        if (texto.isEmpty()) {
            cargarLibros();
            return;
        }

        modeloLibros.setRowCount(0);
        for (Libro l : libroDAO.listarLibros()) {
            String nombreAutor = obtenerNombreAutor(l.getIdAutor()).toLowerCase();
            String nombreGenero = obtenerNombreGenero(l.getIdGenero()).toLowerCase();
            
            if (l.getTitulo().toLowerCase().contains(texto) ||
                nombreAutor.contains(texto) ||
                nombreGenero.contains(texto) ||
                String.valueOf(l.getIdLibro()).contains(texto)) {
                
                modeloLibros.addRow(new Object[]{
                    l.getIdLibro(),
                    l.getTitulo(),
                    l.getCantidadDisponible(),
                    obtenerNombreAutor(l.getIdAutor()),
                    obtenerNombreGenero(l.getIdGenero())
                });
            }
        }
    }

    // ================= EVENTOS DE BOTONES =================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtn_agregarLibro) {
            abrirDialogoLibro(null);
        }
        
        if (e.getSource() == vista.jbtn_agregarAutor) {
            abrirDialogoAutorGenero("AUTOR", null);
        }
        
        if (e.getSource() == vista.jbtn_agregarGenero) {
            abrirDialogoAutorGenero("GENERO", null);
        }
    }

    // ================= DIALOGO LIBROS =================
    private void abrirDialogoLibro(Libro libroExistente) {
        Window parent = SwingUtilities.getWindowAncestor(vista);
        DialogLibros dialog = new DialogLibros((Frame) parent, true);
        
        // Configurar según si es edición o nuevo
        if (libroExistente != null) {
            dialog.setTitle("Editar Libro");
            dialog.txtTitulo.setText(libroExistente.getTitulo());
            dialog.txtCantidad.setText(String.valueOf(libroExistente.getCantidadDisponible()));
            
            // Seleccionar autor y género en los combos
            seleccionarEnCombo(dialog.cmbAutor, libroExistente.getIdAutor());
            seleccionarEnCombo(dialog.cmbGenero, libroExistente.getIdGenero());
        } else {
            dialog.setTitle("Agregar Nuevo Libro");
        }
        
        // Ocultar errores inicialmente
        dialog.lblErrorTitulo.setVisible(false);
        dialog.lblErrorCantidad.setVisible(false);
        
        // Cargar combos
        cargarCombosDialogo(dialog);
        
        // Validaciones
        AppUtils.validarConEnter(dialog.txtTitulo, dialog.txtCantidad, () -> {
            AppUtils.limpiarError(dialog.txtTitulo, dialog.lblErrorTitulo);
            if (dialog.txtTitulo.getText().trim().isEmpty()) {
                AppUtils.marcarError(dialog.txtTitulo, dialog.lblErrorTitulo, 
                    "El título es requerido");
                return false;
            }
            return true;
        });
        
        AppUtils.validarConEnter(dialog.txtCantidad, dialog.btnGuardarLB, () -> {
            AppUtils.limpiarError(dialog.txtCantidad, dialog.lblErrorCantidad);
            try {
                int cantidad = Integer.parseInt(dialog.txtCantidad.getText().trim());
                if (cantidad < 0) {
                    AppUtils.marcarError(dialog.txtCantidad, dialog.lblErrorCantidad,
                        "La cantidad no puede ser negativa");
                    return false;
                }
                return true;
            } catch (NumberFormatException ex) {
                AppUtils.marcarError(dialog.txtCantidad, dialog.lblErrorCantidad,
                    "Debe ingresar un número válido");
                return false;
            }
        });
        
        AppUtils.enterEjecutaBoton(dialog.txtCantidad, dialog.btnGuardarLB, () -> true);
        
        // Botón Guardar
        dialog.btnGuardarLB.addActionListener(ev -> {
            if (validarDialogoLibro(dialog)) {
                guardarLibro(dialog, libroExistente);
            }
        });
        
        dialog.btnCancelarLB.addActionListener(ev -> dialog.dispose());
        
        // Posicionar a la izquierda
        posicionarDialogoIzquierda(dialog, parent);
        dialog.setVisible(true);
    }
    
    private void cargarCombosDialogo(DialogLibros dialog) {
        dialog.cmbAutor.removeAllItems();
        dialog.cmbGenero.removeAllItems();
        
        dialog.cmbAutor.addItem("Seleccione un autor");
        dialog.cmbGenero.addItem("Seleccione un género");
        
        for (Autor a : autorDAO.listarAutores()) {
            dialog.cmbAutor.addItem(a.getIdAutor() + " - " + a.getNombreAutor());
        }
        
        for (Genero g : generoDAO.listarGeneros()) {
            dialog.cmbGenero.addItem(g.getIdGenero() + " - " + g.getNombreGenero());
        }
    }
    
    private void seleccionarEnCombo(JComboBox<String> combo, int id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            String item = combo.getItemAt(i);
            if (item.startsWith(id + " - ")) {
                combo.setSelectedIndex(i);
                break;
            }
        }
    }
    
    private boolean validarDialogoLibro(DialogLibros dialog) {
        boolean valido = true;
        
        // Validar título
        if (dialog.txtTitulo.getText().trim().isEmpty()) {
            AppUtils.marcarError(dialog.txtTitulo, dialog.lblErrorTitulo,
                "El título es requerido");
            valido = false;
        }
        
        // Validar cantidad
        try {
            int cantidad = Integer.parseInt(dialog.txtCantidad.getText().trim());
            if (cantidad < 0) {
                AppUtils.marcarError(dialog.txtCantidad, dialog.lblErrorCantidad,
                    "La cantidad no puede ser negativa");
                valido = false;
            }
        } catch (NumberFormatException e) {
            AppUtils.marcarError(dialog.txtCantidad, dialog.lblErrorCantidad,
                "Debe ingresar un número válido");
            valido = false;
        }
        
        // Validar que se haya seleccionado autor y género
        if (dialog.cmbAutor.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(dialog,
                "Debe seleccionar un autor",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            valido = false;
        }
        
        if (dialog.cmbGenero.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(dialog,
                "Debe seleccionar un género",
                "Advertencia", JOptionPane.WARNING_MESSAGE);
            valido = false;
        }
        
        return valido;
    }
    
    private void guardarLibro(DialogLibros dialog, Libro libroExistente) {
        String titulo = dialog.txtTitulo.getText().trim();
        int cantidad = Integer.parseInt(dialog.txtCantidad.getText().trim());
        
        String autorSeleccionado = (String) dialog.cmbAutor.getSelectedItem();
        String generoSeleccionado = (String) dialog.cmbGenero.getSelectedItem();
        
        int idAutor = Integer.parseInt(autorSeleccionado.split(" - ")[0]);
        int idGenero = Integer.parseInt(generoSeleccionado.split(" - ")[0]);
        
        Libro libro = new Libro();
        libro.setTitulo(titulo);
        libro.setCantidadDisponible(cantidad);
        libro.setIdAutor(idAutor);
        libro.setIdGenero(idGenero);
        
        boolean exito;
        String mensaje;
        
        if (libroExistente != null) {
            libro.setIdLibro(libroExistente.getIdLibro());
            exito = libroDAO.editarLibro(libro);
            mensaje = "Libro actualizado exitosamente";
        } else {
            exito = libroDAO.agregarLibro(libro);
            mensaje = "Libro agregado exitosamente";
        }
        
        if (exito) {
            JOptionPane.showMessageDialog(dialog, mensaje, "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            cargarTodo();
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(dialog, "Error al guardar el libro", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= DIALOGO AUTOR/GÉNERO =================
    private void abrirDialogoAutorGenero(String tipo, Object entidadExistente) {
        Window parent = SwingUtilities.getWindowAncestor(vista);
        DialogAutorGenero dialog = new DialogAutorGenero((Frame) parent, true);
        
        modoActual = tipo;
        
        // Configurar según tipo
        if (tipo.equals("AUTOR")) {
            dialog.setTitle("Agregar Nuevo Autor");
            dialog.lblAutorGenero.setText("Ingrese un Autor:");
            if (entidadExistente != null) {
                Autor autor = (Autor) entidadExistente;
                dialog.setTitle("Editar Autor");
                dialog.txtAG.setText(autor.getNombreAutor());
            }
        } else {
            dialog.setTitle("Agregar Nuevo Género");
            dialog.lblAutorGenero.setText("Ingrese un Género:");
            if (entidadExistente != null) {
                Genero genero = (Genero) entidadExistente;
                dialog.setTitle("Editar Género");
                dialog.txtAG.setText(genero.getNombreGenero());
            }
        }
        
        // Ocultar error inicialmente
        dialog.lblErrorAG.setVisible(false);
        
        // Validación
        AppUtils.validarConEnter(dialog.txtAG, dialog.btnGuardarAG, () -> {
            AppUtils.limpiarError(dialog.txtAG, dialog.lblErrorAG);
            if (dialog.txtAG.getText().trim().isEmpty()) {
                AppUtils.marcarError(dialog.txtAG, dialog.lblErrorAG,
                    tipo.equals("AUTOR") ? "El nombre del autor es requerido" : 
                                           "El nombre del género es requerido");
                return false;
            }
            return true;
        });
        
        AppUtils.enterEjecutaBoton(dialog.txtAG, dialog.btnGuardarAG, () -> true);
        
        // Botón Guardar
        dialog.btnGuardarAG.addActionListener(ev -> {
            if (validarDialogoAutorGenero(dialog)) {
                guardarAutorGenero(dialog, tipo, entidadExistente);
            }
        });
        
        dialog.btnCancelarAG.addActionListener(ev -> dialog.dispose());
        
        // Posicionar a la izquierda
        posicionarDialogoIzquierda(dialog, parent);
        dialog.setVisible(true);
    }
    
    private boolean validarDialogoAutorGenero(DialogAutorGenero dialog) {
        if (dialog.txtAG.getText().trim().isEmpty()) {
            AppUtils.marcarError(dialog.txtAG, dialog.lblErrorAG,
                modoActual.equals("AUTOR") ? "El nombre del autor es requerido" : 
                                           "El nombre del género es requerido");
            return false;
        }
        return true;
    }
    
    private void guardarAutorGenero(DialogAutorGenero dialog, String tipo, Object entidadExistente) {
        String nombre = dialog.txtAG.getText().trim();
        boolean exito;
        String mensaje;
        
        if (tipo.equals("AUTOR")) {
            if (entidadExistente != null) {
                Autor autor = (Autor) entidadExistente;
                exito = autorDAO.editarAutor(autor.getIdAutor(), nombre);
                mensaje = "Autor actualizado exitosamente";
            } else {
                exito = autorDAO.agregarAutor(nombre);
                mensaje = "Autor agregado exitosamente";
            }
        } else {
            if (entidadExistente != null) {
                Genero genero = (Genero) entidadExistente;
                exito = generoDAO.editarGenero(genero.getIdGenero(), nombre);
                mensaje = "Género actualizado exitosamente";
            } else {
                exito = generoDAO.agregarGenero(nombre);
                mensaje = "Género agregado exitosamente";
            }
        }
        
        if (exito) {
            JOptionPane.showMessageDialog(dialog, mensaje, "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
            cargarTodo();
            dialog.dispose();
        } else {
            JOptionPane.showMessageDialog(dialog, 
                "Error al guardar. El nombre ya podría existir.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= POSICIONAMIENTO DE DIÁLOGOS =================
    private void posicionarDialogoIzquierda(Window dialog, Window parent) {
        Point parentLocation = parent.getLocation();
        Dimension parentSize = parent.getSize();
        
        dialog.pack();
        Dimension dialogSize = dialog.getSize();
        
        int x = parentLocation.x - dialogSize.width;
        int y = parentLocation.y + (parentSize.height - dialogSize.height) / 2;
        
        if (x < 0) {
            x = parentLocation.x + parentSize.width;
        }
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        
        if (y < 0) y = 0;
        if (y + dialogSize.height > screenSize.height) {
            y = screenSize.height - dialogSize.height;
        }
        
        dialog.setLocation(x, y);
    }

    // ================= CRUD LIBROS (con menú contextual) =================
    private void editarLibro() {
        int fila = vista.jtb_Libros.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista,
                "Seleccione un libro de la tabla",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idLibro = (int) modeloLibros.getValueAt(fila, 0);
        Libro libro = obtenerLibroPorId(idLibro);
        
        if (libro != null) {
            abrirDialogoLibro(libro);
        }
    }
    
    private Libro obtenerLibroPorId(int idLibro) {
        for (Libro l : libroDAO.listarLibros()) {
            if (l.getIdLibro() == idLibro) {
                return l;
            }
        }
        return null;
    }

    private void eliminarLibro() {
        int fila = vista.jtb_Libros.getSelectedRow();
        if (fila == -1) {
            return;
        }

        int idLibro = (int) modeloLibros.getValueAt(fila, 0);
        String titulo = (String) modeloLibros.getValueAt(fila, 1);

        int confirmado = JOptionPane.showConfirmDialog(vista,
            "¿Está seguro de eliminar este libro?\n\n" +
            "Título: " + titulo + "\n" +
            "Esta acción no se puede deshacer.",
            "Confirmar eliminación de libro",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmado == JOptionPane.YES_OPTION) {
            if (libroDAO.eliminarLibro(idLibro)) {
                cargarLibros();
                JOptionPane.showMessageDialog(vista,
                    "Libro eliminado exitosamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista,
                    "Error al eliminar el libro",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================= CRUD AUTORES (con menú contextual) =================
    private void editarAutor() {
        int fila = vista.jtb_Autores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista,
                "Seleccione un autor de la tabla",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idAutor = (int) modeloAutores.getValueAt(fila, 0);
        Autor autor = obtenerAutorPorId(idAutor);
        
        if (autor != null) {
            abrirDialogoAutorGenero("AUTOR", autor);
        }
    }
    
    private Autor obtenerAutorPorId(int idAutor) {
        for (Autor a : autorDAO.listarAutores()) {
            if (a.getIdAutor() == idAutor) {
                return a;
            }
        }
        return null;
    }

    private void eliminarAutor() {
        int fila = vista.jtb_Autores.getSelectedRow();
        if (fila == -1) {
            return;
        }

        int idAutor = (int) modeloAutores.getValueAt(fila, 0);
        String nombreAutor = (String) modeloAutores.getValueAt(fila, 1);

        int confirmado = JOptionPane.showConfirmDialog(vista,
            "¿Está seguro de eliminar este autor?\n\n" +
            "Autor: " + nombreAutor + "\n" +
            "ADVERTENCIA: Esto afectará a todos los libros asociados a este autor.",
            "Confirmar eliminación de autor",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmado == JOptionPane.YES_OPTION) {
            if (autorDAO.eliminarAutor(idAutor)) {
                cargarTodo();
                JOptionPane.showMessageDialog(vista,
                    "Autor eliminado exitosamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista,
                    "No se puede eliminar el autor porque está asociado a uno o más libros",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // ================= CRUD GÉNEROS (con menú contextual) =================
    private void editarGenero() {
        int fila = vista.jtb_Generos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(vista,
                "Seleccione un género de la tabla",
                "Selección requerida",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idGenero = (int) modeloGeneros.getValueAt(fila, 0);
        Genero genero = obtenerGeneroPorId(idGenero);
        
        if (genero != null) {
            abrirDialogoAutorGenero("GENERO", genero);
        }
    }
    
    private Genero obtenerGeneroPorId(int idGenero) {
        for (Genero g : generoDAO.listarGeneros()) {
            if (g.getIdGenero() == idGenero) {
                return g;
            }
        }
        return null;
    }

    private void eliminarGenero() {
        int fila = vista.jtb_Generos.getSelectedRow();
        if (fila == -1) {
            return;
        }

        int idGenero = (int) modeloGeneros.getValueAt(fila, 0);
        String nombreGenero = (String) modeloGeneros.getValueAt(fila, 1);

        int confirmado = JOptionPane.showConfirmDialog(vista,
            "¿Está seguro de eliminar este género?\n\n" +
            "Género: " + nombreGenero + "\n" +
            "ADVERTENCIA: Esto afectará a todos los libros asociados a este género.",
            "Confirmar eliminación de género",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmado == JOptionPane.YES_OPTION) {
            if (generoDAO.eliminarGenero(idGenero)) {
                cargarTodo();
                JOptionPane.showMessageDialog(vista,
                    "Género eliminado exitosamente",
                    "Eliminación exitosa",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista,
                    "No se puede eliminar el género porque está asociado a uno o más libros",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}