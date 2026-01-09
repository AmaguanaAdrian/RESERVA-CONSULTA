package controlador;

import Utilidades.AppUtils;
import java.awt.Color;
import vista.VistaLogin;
import vista.VistaContenedor;
import modelo.UsuarioDAO;
import modelo.ValidadorCedula; // Importamos tu validador
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;
import modelo.Sesion;
import modelo.Usuario;


public class ControladorLogin implements ActionListener {

    private VistaLogin vista;
    private UsuarioDAO usuarioDAO;

    public ControladorLogin(VistaLogin vista) {
        this.vista = vista;
        this.usuarioDAO = new UsuarioDAO();

        // Escuchar el botón de inicio de sesión
        this.vista.jbtn_loginAdmin.addActionListener(this);

        // --- RESTRICCIÓN: Solo números en el campo Usuario (Cédula) ---
        this.vista.jtxt_Usuario.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                // Si no es un número y no es la tecla de borrar, ignorar el evento
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
                // Limitar a 10 caracteres (longitud de la cédula)
                if (vista.jtxt_Usuario.getText().length() >= 10 && 
                    c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                    e.consume();
                }
            }
        });

        // Permitir ENTER para iniciar sesión
        this.vista.jtxt_Usuario.addActionListener(e -> procesarLogin());
        this.vista.jpf_Contrasena.addActionListener(e -> procesarLogin());
        
        // Establecer foco inicial
        this.vista.jtxt_Usuario.requestFocusInWindow();
        
        // Estilo visual al botón (opcional)
        if (AppUtils.class != null) {
            AppUtils.estiloBotonModerno(vista.jbtn_loginAdmin, new Color(0, 123, 255), "Iniciar Sesión");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtn_loginAdmin) {
            procesarLogin();
        }
    }

    private void procesarLogin() {
        String cedula = vista.jtxt_Usuario.getText().trim();
        String contrasena = new String(vista.jpf_Contrasena.getPassword()).trim();

        // 1. Validar campos vacíos
        if (cedula.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(vista, 
                "Ingrese usuario y contraseña",
                "Campos vacíos", 
                JOptionPane.WARNING_MESSAGE);
            
            if (cedula.isEmpty()) {
                vista.jtxt_Usuario.requestFocus();
            } else {
                vista.jpf_Contrasena.requestFocus();
            }
            return;
        }

        // 2. Validar longitud de cédula
        if (cedula.length() != 10) {
            JOptionPane.showMessageDialog(vista, 
                "La cédula debe tener 10 dígitos",
                "Cédula inválida", 
                JOptionPane.WARNING_MESSAGE);
            vista.jtxt_Usuario.requestFocus();
            vista.jtxt_Usuario.selectAll();
            return;
        }

        // 3. Validar Cédula Ecuatoriana
        if (!ValidadorCedula.validarCedulaEcuatoriana(cedula)) {
            JOptionPane.showMessageDialog(vista, 
                "La cédula ingresada no es válida",
                "Cédula Incorrecta", 
                JOptionPane.ERROR_MESSAGE);
            vista.jtxt_Usuario.requestFocus();
            vista.jtxt_Usuario.selectAll();
            return;
        }

        // 4. Intentar autenticar al usuario
        Usuario usuario = usuarioDAO.autenticarUsuario(cedula, contrasena);

        if (usuario == null) {
            // Mostrar mensaje de error específico
            if (usuarioDAO.usuarioExiste(cedula)) {
                JOptionPane.showMessageDialog(vista,
                    "Contraseña incorrecta. Por favor, intente de nuevo.",
                    "Error de autenticación",
                    JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(vista,
                    "Usuario no encontrado. Verifique la cédula.",
                    "Usuario no registrado",
                    JOptionPane.WARNING_MESSAGE);
            }
            
            // Limpiar campo de contraseña y dar foco
            vista.jpf_Contrasena.setText("");
            vista.jpf_Contrasena.requestFocus();
            return;
        }

        // 5. Verificar que el usuario esté activo
        if (!"ACTIVO".equals(usuario.getEstado())) {
            JOptionPane.showMessageDialog(vista,
                "Su cuenta está " + usuario.getEstado().toLowerCase() + ". Contacte al administrador.",
                "Cuenta inactiva",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 6. LOGIN EXITOSO - Configurar sesión SILENCIOSAMENTE
        configurarSesion(usuario);

        // 7. Abrir contenedor principal SIN mostrar mensaje de bienvenida
        abrirContenedor();
    }

    private void configurarSesion(Usuario usuario) {
        Sesion sesion = Sesion.getInstancia();
        sesion.setUsuario(usuario);

        // Si es estudiante, obtener y guardar su id_estudiante
        if ("ESTUDIANTE".equals(usuario.getRol())) {
            Integer idEstudiante = usuarioDAO.obtenerIdEstudiante(usuario.getIdUsuario());
            
            if (idEstudiante != null) {
                sesion.setIdEstudiante(idEstudiante);
            } else {
                // Esto no debería pasar, pero por seguridad
                System.err.println("ERROR: Usuario es ESTUDIANTE pero no tiene id_estudiante en BD");
                // NO mostramos mensaje aquí para no interrumpir el flujo
            }
        }
        
        // DEBUG: Mostrar información de sesión (solo en consola)
        System.out.println("=== SESIÓN INICIADA ===");
        System.out.println("Usuario: " + usuario.getNombres() + " " + usuario.getApellidos());
        System.out.println("Rol: " + usuario.getRol());
        System.out.println("ID Estudiante: " + sesion.getIdEstudiante());
        System.out.println("======================");
    }

    private void abrirContenedor() {
        VistaContenedor contenedor = new VistaContenedor();
        
        // El ControladorContenedor se encargará de mostrar la info en los labels
        new ControladorContenedor(contenedor);

        // Configurar ventana
        contenedor.pack();
        contenedor.setLocationRelativeTo(null);
        contenedor.setVisible(true);

        // Cerrar ventana de login
        vista.dispose();
    }

}