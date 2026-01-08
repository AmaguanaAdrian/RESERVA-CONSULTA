package controlador;

import vista.VistaLogin;
import vista.VistaContenedor;
import modelo.UsuarioDAO;
import modelo.ValidadorCedula; // Importamos tu validador
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JOptionPane;

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
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume(); 
                }
                // Opcional: Limitar a 10 caracteres (longitud de la cédula)
                if (vista.jtxt_Usuario.getText().length() >= 10) {
                    e.consume();
                }
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == vista.jbtn_loginAdmin) {
            procesarLogin();
        }
    }

    private void procesarLogin() {
        String cedula = vista.jtxt_Usuario.getText().trim();
        // Usamos el JPasswordField corregido
        String contrasena = new String(vista.jpf_Contrasena.getPassword()).trim();

        // 1. Validar campos vacíos
        if (cedula.isEmpty() || contrasena.isEmpty()) {
            JOptionPane.showMessageDialog(vista, "Ingrese usuario y contraseña", 
                    "Campos vacíos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // 2. Validar Cédula Ecuatoriana usando tu clase modelo
        if (!ValidadorCedula.validarCedulaEcuatoriana(cedula)) {
            JOptionPane.showMessageDialog(vista, "La cédula ingresada no es válida", 
                    "Cédula Incorrecta", JOptionPane.ERROR_MESSAGE);
            return; // Detenemos el proceso si la cédula está mal
        }

        // 3. Si la cédula es válida, consultamos el Rol en la BD
        String rolBD = usuarioDAO.obtenerRol(cedula, contrasena);

        if (rolBD == null) {
            JOptionPane.showMessageDialog(vista, "Usuario o contraseña incorrectos", 
                    "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            abrirContenedor(rolBD);
        }
    }

    private void abrirContenedor(String rol) {
        VistaContenedor contenedor = new VistaContenedor();
        new ControladorContenedor(contenedor, rol);

        contenedor.pack();
        contenedor.setLocationRelativeTo(null);
        contenedor.setVisible(true);

        vista.dispose(); 
    }
}