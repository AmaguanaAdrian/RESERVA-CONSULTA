/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controlador;

import Utilidades.AppUtils;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import modelo.Usuario;
import modelo.UsuarioDAO;
import vista.DialogBibliotecario;
import vista.DialogEstudiante;
import vista.GestionEstudiantes;

/**
 *
 * @author amagu
 */
public class ControladorGestionEstudiantes implements ActionListener {

    private GestionEstudiantes vista;
    private UsuarioDAO usuarioDAO;
    private DefaultTableModel modeloEstudiantes;

    public ControladorGestionEstudiantes(GestionEstudiantes vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();

        configurarTablaMejorada();
        cargarEstudiantes();
        aplicarEstilosVisuales();
        configurarMenuClickDerechoMejorado();
        configurarBusquedaEnTiempoReal();
        configurarPlaceholderBusqueda();

        vista.jbtn_AgregarEstudiantes.addActionListener(this);
    }

    // ================= TABLA =================
    private void configurarTablaMejorada() {
        modeloEstudiantes = new DefaultTableModel(
                new Object[]{"Cédula", "Nombres", "Apellidos", "Correo", "Estado", "Carrera"}, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        vista.jTable1.setModel(modeloEstudiantes);
        AppUtils.estilizarTabla(vista.jTable1, new Color(0, 51, 102));
    }

    private void cargarEstudiantes() {
        modeloEstudiantes.setRowCount(0);
        List<Usuario> lista = usuarioDAO.listarEstudiantes();

        for (Usuario u : lista) {
            modeloEstudiantes.addRow(new Object[]{
                u.getCedula(),
                u.getNombres(),
                u.getApellidos(),
                u.getCorreo(),
                u.getEstado(),
                u.getCarrera()
            });
        }

        // Animación de actualización
        AppUtils.animarActualizacion(vista.jTable1);
    }

    // ================= BÚSQUEDA =================
    private void configurarBusquedaEnTiempoReal() {
        if (vista.jtxt_BuscarEstudiantes != null) {
            vista.jtxt_BuscarEstudiantes.getDocument().addDocumentListener(
                    new DocumentListener() {
                @Override
                public void insertUpdate(DocumentEvent e) {
                    buscarEstudiantes();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    buscarEstudiantes();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    buscarEstudiantes();
                }
            });
        }
    }

    private void buscarEstudiantes() {
        String texto = vista.jtxt_BuscarEstudiantes != null
                ? vista.jtxt_BuscarEstudiantes.getText().trim() : "";

        if (texto.isEmpty() || texto.equals("Buscar estudiantes...")) {
            cargarEstudiantes();
            return;
        }

        modeloEstudiantes.setRowCount(0);
        List<Usuario> estudiantes = usuarioDAO.listarEstudiantes();

        for (Usuario u : estudiantes) {
            if (u.getCedula().toLowerCase().contains(texto.toLowerCase())
                    || u.getNombres().toLowerCase().contains(texto.toLowerCase())
                    || u.getApellidos().toLowerCase().contains(texto.toLowerCase())
                    || u.getCorreo().toLowerCase().contains(texto.toLowerCase())
                    || u.getEstado().toLowerCase().contains(texto.toLowerCase())) {

                modeloEstudiantes.addRow(new Object[]{
                    u.getCedula(),
                    u.getNombres(),
                    u.getApellidos(),
                    u.getCorreo(),
                    u.getEstado()
                });
            }
        }
    }

    private void configurarPlaceholderBusqueda() {
        if (vista.jtxt_BuscarEstudiantes != null) {
            String placeholderText = "Buscar estudiantes...";
            vista.jtxt_BuscarEstudiantes.setText(placeholderText);
            vista.jtxt_BuscarEstudiantes.setForeground(Color.GRAY);

            vista.jtxt_BuscarEstudiantes.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (vista.jtxt_BuscarEstudiantes.getText().equals(placeholderText)) {
                        vista.jtxt_BuscarEstudiantes.setText("");
                        vista.jtxt_BuscarEstudiantes.setForeground(Color.BLACK);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (vista.jtxt_BuscarEstudiantes.getText().isEmpty()) {
                        vista.jtxt_BuscarEstudiantes.setText(placeholderText);
                        vista.jtxt_BuscarEstudiantes.setForeground(Color.GRAY);
                    }
                }
            });

            // Hover effect usando AppUtils
            AppUtils.agregarEfectoHoverCampo(
                    vista.jtxt_BuscarEstudiantes,
                    new Color(0, 123, 255),
                    new Color(206, 212, 218)
            );
        }
    }

    // ================= EVENTOS =================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtn_AgregarEstudiantes) {
            abrirDialogRegistro(null);
        }
    }

    // ================= DIALOG REGISTRO / EDICIÓN =================
    private void abrirDialogRegistro(Usuario existente) {
        Window parent = SwingUtilities.getWindowAncestor(vista);
        DialogEstudiante dialog = new DialogEstudiante((Frame) parent, true);

        dialog.txtRol.setText("ESTUDIANTE");
        dialog.txtRol.setEnabled(false);

        // Ocultar errores usando AppUtils
        AppUtils.ocultarErroresDialogo(
                dialog.lblErrorCedula,
                dialog.lblErrorNombres,
                dialog.lblErrorApellidos,
                dialog.lblErrorEmail,
                dialog.lblErrorPassword,
                dialog.lblErrorCarrera
        );

        if (existente != null) {
            dialog.setTitle("Editar Estudiante");
            dialog.txtCedula.setText(existente.getCedula());
            dialog.txtCedula.setEnabled(false);
            dialog.txtNombres.setText(existente.getNombres());
            dialog.txtApellidos.setText(existente.getApellidos());
            dialog.txtEmail.setText(existente.getCorreo());
            dialog.txtCarrera.setText(existente.getCarrera() != null ? existente.getCarrera() : "");
            dialog.cmbEstado.setSelectedItem(existente.getEstado());
        } else {
            dialog.setTitle("Agregar Estudiante");
            AppUtils.soloNumeros(dialog.txtCedula, 10);
        }

        configurarValidacionesConEnter(dialog, existente);
        validarCedulaTiempoReal(dialog, existente);

        dialog.btnGuardar.addActionListener(e -> guardarEstudiante(dialog, existente));
        dialog.btnCancelar.addActionListener(e -> dialog.dispose());

        dialog.btnVerPass1.addActionListener(e -> AppUtils.togglePassword(dialog.txtPassword));
        dialog.btnVerPass2.addActionListener(e -> AppUtils.togglePassword(dialog.txtConfirmar));

        // Posicionamiento usando AppUtils
        Window parentWindow = SwingUtilities.getWindowAncestor(vista);
        AppUtils.posicionarDialogoIzquierda(dialog, parentWindow);

        // Mostrar el diálogo (sin animación para evitar errores)
        dialog.setVisible(true);
    }

    // ================= VALIDACIONES =================
    private void configurarValidacionesConEnter(DialogEstudiante d, Usuario existente) {
        // Cédula
        AppUtils.validarConEnter(d.txtCedula, d.txtNombres, () -> {
            AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
            return validarCampoCedula(d, existente);
        });

        // Nombres
        AppUtils.validarConEnter(d.txtNombres, d.txtApellidos, () -> {
            AppUtils.limpiarError(d.txtNombres, d.lblErrorNombres);
            return validarCampoNombres(d);
        });

        // Apellidos
        AppUtils.validarConEnter(d.txtApellidos, d.txtEmail, () -> {
            AppUtils.limpiarError(d.txtApellidos, d.lblErrorApellidos);
            return validarCampoApellidos(d);
        });

        // Email
        AppUtils.validarConEnter(d.txtEmail, d.txtPassword, () -> {
            AppUtils.limpiarError(d.txtEmail, d.lblErrorEmail);
            return validarCampoEmail(d);
        });

        // Password
        AppUtils.validarConEnter(d.txtPassword, d.txtConfirmar, () -> {
            return validarCampoPassword(d, existente);
        });

        // Confirmar Password
        AppUtils.validarConEnter(d.txtConfirmar, d.txtCarrera, () -> {
            return validarConfirmacionPassword(d, existente);
        });

        // Carrera (ejecuta botón guardar)
        AppUtils.enterEjecutaBoton(d.txtCarrera, d.btnGuardar, () -> {
            return validarCampoCarrera(d);
        });
    }

    private boolean validarCampoCedula(DialogEstudiante d, Usuario existente) {
        String cedula = d.txtCedula.getText().trim();

        if (cedula.isEmpty()) {
            AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "La cédula es obligatoria");
            return false;
        }

        if (!AppUtils.validarCedulaEcuatoriana(cedula)) {
            AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula inválida");
            return false;
        }

        if (existente == null && usuarioDAO.existeCedula(cedula)) {
            AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula ya registrada");
            return false;
        }

        return true;
    }

    private boolean validarCampoNombres(DialogEstudiante d) {
        String nombres = d.txtNombres.getText().trim();

        if (nombres.isEmpty()) {
            AppUtils.marcarError(d.txtNombres, d.lblErrorNombres, "Los nombres son obligatorios");
            return false;
        }

        if (nombres.split("\\s+").length < 2) {
            AppUtils.marcarError(d.txtNombres, d.lblErrorNombres, "Ingrese mínimo 2 nombres");
            return false;
        }
        return true;
    }

    private boolean validarCampoApellidos(DialogEstudiante d) {
        String apellidos = d.txtApellidos.getText().trim();

        if (apellidos.isEmpty()) {
            AppUtils.marcarError(d.txtApellidos, d.lblErrorApellidos, "Los apellidos son obligatorios");
            return false;
        }

        if (apellidos.split("\\s+").length < 2) {
            AppUtils.marcarError(d.txtApellidos, d.lblErrorApellidos, "Ingrese mínimo 2 apellidos");
            return false;
        }
        return true;
    }

    private boolean validarCampoEmail(DialogEstudiante d) {
        String email = d.txtEmail.getText().trim();

        if (email.isEmpty()) {
            AppUtils.marcarError(d.txtEmail, d.lblErrorEmail, "El correo es obligatorio");
            return false;
        }

        if (!AppUtils.validarCorreo(email)) {
            AppUtils.marcarError(d.txtEmail, d.lblErrorEmail, "Correo inválido");
            return false;
        }
        return true;
    }

    private boolean validarCampoPassword(DialogEstudiante d, Usuario existente) {
        String password = new String(d.txtPassword.getPassword());

        if (existente == null) {
            if (password.isEmpty()) {
                AppUtils.marcarError(d.txtPassword, d.lblErrorPassword, "La contraseña es obligatoria");
                return false;
            }

            if (password.length() < 8) {
                AppUtils.marcarError(d.txtPassword, d.lblErrorPassword, "Mínimo 8 caracteres");
                return false;
            }

            if (!AppUtils.validarPassword(password)) {
                AppUtils.marcarError(d.txtPassword, d.lblErrorPassword, "Debe contener letras y números");
                return false;
            }
        } else if (!password.isEmpty() && password.length() < 8) {
            AppUtils.marcarError(d.txtPassword, d.lblErrorPassword, "Mínimo 8 caracteres");
            return false;
        }

        return true;
    }

    private boolean validarConfirmacionPassword(DialogEstudiante d, Usuario existente) {
        String password = new String(d.txtPassword.getPassword());
        String confirmar = new String(d.txtConfirmar.getPassword());

        if (existente == null || !password.isEmpty()) {
            if (confirmar.isEmpty()) {
                AppUtils.marcarError(d.txtConfirmar, d.lblErrorPassword, "Confirme la contraseña");
                return false;
            }

            if (!password.equals(confirmar)) {
                AppUtils.marcarError(d.txtConfirmar, d.lblErrorPassword, "Las contraseñas no coinciden");
                return false;
            }
        }

        return true;
    }

    private boolean validarCampoCarrera(DialogEstudiante d) {
        String carrera = d.txtCarrera.getText().trim();

        if (carrera.isEmpty()) {
            AppUtils.marcarError(d.txtCarrera, d.lblErrorCarrera, "La carrera es obligatoria");
            return false;
        }

        return true;
    }

    private void validarCedulaTiempoReal(DialogEstudiante d, Usuario existente) {
        d.txtCedula.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                validar();
            }

            public void removeUpdate(DocumentEvent e) {
                validar();
            }

            public void changedUpdate(DocumentEvent e) {
                validar();
            }

            private void validar() {
                String cedula = d.txtCedula.getText().trim();

                if (cedula.isEmpty()) {
                    AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
                    return;
                }

                if (!AppUtils.validarCedulaEcuatoriana(cedula)) {
                    AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula inválida");
                    return;
                }

                if (existente == null && usuarioDAO.existeCedula(cedula)) {
                    AppUtils.marcarError(d.txtCedula, d.lblErrorCedula, "Cédula ya registrada");
                    return;
                }

                AppUtils.limpiarError(d.txtCedula, d.lblErrorCedula);
            }
        });
    }

    // ================= GUARDAR =================
    private void guardarEstudiante(DialogEstudiante d, Usuario existente) {
        // Validar todos los campos
        if (!validarFormularioCompleto(d, existente)) {
            AppUtils.animarError(d.btnGuardar);
            return;
        }

        Usuario u = new Usuario();
        u.setCedula(d.txtCedula.getText().trim());
        u.setNombres(d.txtNombres.getText().trim());
        u.setApellidos(d.txtApellidos.getText().trim());
        u.setCorreo(d.txtEmail.getText().trim());
        u.setCarrera(d.txtCarrera.getText().trim());  // Campo específico de estudiante
        u.setEstado(d.cmbEstado.getSelectedItem().toString());
        u.setRol("ESTUDIANTE");

        String password = new String(d.txtPassword.getPassword());
        if (!password.isEmpty()) {
            u.setContrasena(password);
        }

        boolean ok;
        String mensaje;
        if (existente != null) {
            u.setIdUsuario(existente.getIdUsuario());
            ok = usuarioDAO.editarEstudiante(u);
            mensaje = ok ? "Estudiante actualizado exitosamente" : "Error al actualizar el estudiante";
        } else {
            ok = usuarioDAO.agregarEstudiante(u);
            mensaje = ok ? "Estudiante registrado exitosamente" : "Error al registrar el estudiante";
        }

        if (ok) {
            cargarEstudiantes();
            AppUtils.mostrarMensajeExito(vista, mensaje, "Éxito");
            d.dispose();
        } else {
            AppUtils.animarError(d.btnGuardar);
            AppUtils.mostrarMensajeAdvertencia(vista, mensaje, "Error");
        }
    }

    private boolean validarFormularioCompleto(DialogEstudiante d, Usuario existente) {
        return validarCampoCedula(d, existente)
                && validarCampoNombres(d)
                && validarCampoApellidos(d)
                && validarCampoEmail(d)
                && validarCampoPassword(d, existente)
                && validarConfirmacionPassword(d, existente)
                && validarCampoCarrera(d);
    }

    // ================= ESTILOS =================
    private void aplicarEstilosVisuales() {
        // Aplicar estilo al botón usando AppUtils
        AppUtils.estiloBotonModerno(
                vista.jbtn_AgregarEstudiantes,
                new Color(40, 167, 69),
                "Agregar Estudiante"
        );
    }

    // ================= MENU =================
    private void configurarMenuClickDerechoMejorado() {
        JPopupMenu menu = new JPopupMenu();

        JMenuItem editar = new JMenuItem("Editar");
        JMenuItem eliminar = new JMenuItem("Eliminar");

        // Aplicar estilo a los items del menú
        AppUtils.estiloItemMenu(editar);
        AppUtils.estiloItemMenu(eliminar);

        editar.addActionListener(e -> editarEstudiante());
        eliminar.addActionListener(e -> eliminarEstudiante());

        menu.add(editar);
        menu.addSeparator();
        menu.add(eliminar);

        vista.jTable1.setComponentPopupMenu(menu);

        // Doble click para editar
        vista.jTable1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editarEstudiante();
                }
            }
        });
    }

    private void editarEstudiante() {
        int fila = vista.jTable1.getSelectedRow();
        if (fila == -1) {
            AppUtils.mostrarMensajeAdvertencia(vista, "Seleccione un estudiante para editar", "Advertencia");
            return;
        }

        String cedula = modeloEstudiantes.getValueAt(fila, 0).toString();
        // Usar el método del DAO que busca directamente por cédula
        Usuario u = usuarioDAO.buscarEstudiantePorCedula(cedula);

        if (u != null) {
            abrirDialogRegistro(u);
        } else {
            AppUtils.mostrarMensajeAdvertencia(vista,
                    "No se encontró al estudiante con cédula: " + cedula,
                    "Error"
            );
        }
    }
    private void eliminarEstudiante() {
        int fila = vista.jTable1.getSelectedRow();
        if (fila == -1) {
            AppUtils.mostrarMensajeAdvertencia(vista, "Seleccione un estudiante para eliminar", "Advertencia");
            return;
        }

        String cedula = modeloEstudiantes.getValueAt(fila, 0).toString();
        String nombres = modeloEstudiantes.getValueAt(fila, 1).toString();
        String apellidos = modeloEstudiantes.getValueAt(fila, 2).toString();

        // Usar el método del DAO que busca directamente por cédula
        Usuario u = usuarioDAO.buscarEstudiantePorCedula(cedula);

        if (u != null) {
            // Confirmación usando AppUtils
            boolean confirmado = AppUtils.mostrarConfirmacionPersonalizada(
                    vista,
                    "¿Está seguro de eliminar al estudiante?\n\n"
                    + "Cédula: " + cedula + "\n"
                    + "Nombre: " + nombres + " " + apellidos + "\n"
                    + "Carrera: " + (u.getCarrera() != null ? u.getCarrera() : "No especificada") + "\n\n"
                    + "Esta acción no se puede deshacer.",
                    "Confirmar Eliminación"
            );

            if (confirmado) {
                boolean eliminado = usuarioDAO.eliminarEstudiante(u.getIdUsuario());

                if (eliminado) {
                    cargarEstudiantes();
                    AppUtils.mostrarMensajeExito(vista,
                            "Estudiante eliminado exitosamente:\n"
                            + "Cédula: " + cedula + "\n"
                            + "Nombre: " + nombres + " " + apellidos,
                            "Éxito"
                    );
                } else {
                    AppUtils.mostrarMensajeAdvertencia(vista,
                            "Error al eliminar el estudiante:\n"
                            + "Cédula: " + cedula + "\n"
                            + "Nombre: " + nombres + " " + apellidos,
                            "Error"
                    );
                }
            }
        } else {
            AppUtils.mostrarMensajeAdvertencia(vista,
                    "No se encontró al estudiante con cédula: " + cedula,
                    "Error"
            );
        }
    }
}
