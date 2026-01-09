/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidades;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 *
 * @author amagu
 */
public final class AppUtils {

    private AppUtils() {
        // Evita instancias
    }

    // ===================== INTERFAZ FUNCIONAL =====================
    @FunctionalInterface
    public interface ValidadorCampo {

        boolean validar();
    }

    // ===================== FILTROS =====================
    public static void soloNumeros(JTextField campo, int maxLength) {
        AbstractDocument doc = (AbstractDocument) campo.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(FilterBypass fb, int offset, String string,
                    AttributeSet attr) throws BadLocationException {

                if (string == null) {
                    return;
                }

                if (string.matches("\\d+")
                        && fb.getDocument().getLength() + string.length() <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text,
                    AttributeSet attrs) throws BadLocationException {

                if (text == null) {
                    return;
                }

                if (text.matches("\\d+")
                        && fb.getDocument().getLength() - length + text.length() <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    // ===================== VALIDACIONES =====================
    public static boolean validarCedulaEcuatoriana(String cedula) {

        if (cedula == null || !cedula.matches("\\d{10}")) {
            return false;
        }

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) {
            return false;
        }

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito >= 6) {
            return false;
        }

        int[] coef = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        for (int i = 0; i < coef.length; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coef[i];
            if (valor >= 10) {
                valor -= 9;
            }
            suma += valor;
        }

        int verificador = (10 - (suma % 10)) % 10;
        int ultimo = Character.getNumericValue(cedula.charAt(9));

        return verificador == ultimo;
    }

    public static boolean validarPassword(String pass) {
        return pass != null && pass.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    }

    public static boolean validarCorreo(String correo) {
        return correo != null
                && correo.matches("^[\\w._%+-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
    }

    // ===================== UI (ERRORES) =====================
    public static void marcarError(JComponent campo, JLabel label, String mensaje) {
        campo.setBorder(BorderFactory.createLineBorder(Color.RED));
        label.setText(mensaje);
        label.setVisible(true);
    }

    public static void limpiarError(JComponent campo, JLabel label) {
        campo.setBorder(UIManager.getLookAndFeel()
                .getDefaults().getBorder("TextField.border"));
        label.setVisible(false);
    }

    public static void marcarCampoRojo(JComponent campo) {
        campo.setBorder(BorderFactory.createLineBorder(Color.RED));
    }

    public static void limpiarCampo(JComponent campo) {
        campo.setBorder(UIManager.getLookAndFeel()
                .getDefaults().getBorder("TextField.border"));
    }

    // ===================== PASSWORD =====================
    public static void togglePassword(JPasswordField campo) {
        if (campo.getEchoChar() == 0) {
            campo.setEchoChar('•');
        } else {
            campo.setEchoChar((char) 0);
        }
    }

    // ===================== ENTER → VALIDAR → SIGUIENTE =====================
    public static void validarConEnter(
            JComponent campoActual,
            JComponent campoSiguiente,
            ValidadorCampo validador
    ) {
        campoActual.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (validador.validar()) {
                        if (campoSiguiente != null) {
                            campoSiguiente.requestFocus();
                        }
                    }
                }
            }
        });
    }

    // ===================== ENTER → BOTÓN =====================
    public static void enterEjecutaBoton(
            JComponent campo,
            JButton boton,
            ValidadorCampo validador
    ) {
        campo.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (validador.validar()) {
                        boton.doClick();
                    }
                }
            }
        });
    }
    // Agrega estos métodos a tu clase AppUtils:

// ===================== ESTILOS DE BOTÓN =====================
    public static void estiloBotonModerno(JButton boton, Color colorBase, String texto) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        boton.setBackground(colorBase);
        boton.setForeground(Color.WHITE);
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(colorBase.darker(), 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setFocusPainted(false);
        boton.setText(texto);

        // Efecto hover
        boton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                boton.setBackground(colorBase.brighter());
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorBase.darker(), 2),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                boton.setBackground(colorBase);
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorBase.darker(), 1),
                        BorderFactory.createEmptyBorder(10, 20, 10, 20)
                ));
            }
        });
    }

// ===================== ESTILOS DE TABLA =====================
    public static void estilizarTabla(JTable tabla, Color colorHeader) {
        tabla.setRowHeight(35);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setSelectionBackground(new Color(220, 247, 255));
        tabla.setSelectionForeground(new Color(0, 51, 102));
        tabla.setGridColor(new Color(230, 230, 230));
        tabla.setShowHorizontalLines(true);
        tabla.setShowVerticalLines(false);
        tabla.setIntercellSpacing(new Dimension(0, 1));

        // Header personalizado
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(colorHeader);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        // Renderer para filas alternadas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value,
                        isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(248, 249, 250));
                }

                // Centrar columna ID
                if (column == 0) {
                    ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                }

                return c;
            }
        });
    }

// ===================== MENÚ CONTEXTUAL =====================
    public static void estiloItemMenu(JMenuItem item) {
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(220, 247, 255));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
        });
    }

// ===================== EFECTOS HOVER =====================
    public static void agregarEfectoHoverCampo(JTextField campo, Color colorHover, Color colorNormal) {
        campo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorHover, 1),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                campo.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(colorNormal),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }

// ===================== POSICIONAMIENTO DE DIÁLOGOS =====================
    public static void posicionarDialogoIzquierda(Window dialog, Window parent) {
        // Obtener posición y tamaño del padre
        Point parentLocation = parent.getLocation();
        Dimension parentSize = parent.getSize();

        // Obtener tamaño del diálogo
        dialog.pack();
        Dimension dialogSize = dialog.getSize();

        // Calcular posición a la izquierda del padre
        int x = parentLocation.x - dialogSize.width;
        int y = parentLocation.y + (parentSize.height - dialogSize.height) / 2;

        // Si no cabe a la izquierda, poner a la derecha
        if (x < 0) {
            x = parentLocation.x + parentSize.width;
        }

        // Asegurar que no se salga de la pantalla en Y
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        if (y < 0) {
            y = 0;
        }
        if (y + dialogSize.height > screenSize.height) {
            y = screenSize.height - dialogSize.height;
        }

        dialog.setLocation(x, y);
    }

// ===================== ANIMACIONES =====================
    public static void animarAperturaDialogo(Window dialog) {
        dialog.setOpacity(0f);

        Timer timer = new Timer(10, new ActionListener() {
            float opacity = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (opacity < 1f) {
                    opacity += 0.05f;
                    dialog.setOpacity(opacity);
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

    public static void vibrarComponente(Component componente) {
        Timer timer = new Timer(50, new ActionListener() {
            int count = 0;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (count < 6) {
                    int offset = (count % 2 == 0) ? 5 : -5;
                    componente.setLocation(componente.getX() + offset, componente.getY());
                    count++;
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        timer.start();
    }

// ===================== MENSAJES MEJORADOS =====================
    public static void mostrarMensajeExito(Component parent, String mensaje, String titulo) {
        JOptionPane.showMessageDialog(parent,
                mensaje,
                titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarMensajeInfo(Component parent, String mensaje, String titulo) {
        JOptionPane.showMessageDialog(parent,
                mensaje,
                titulo,
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void mostrarMensajeAdvertencia(Component parent, String mensaje, String titulo) {
        JOptionPane.showMessageDialog(parent,
                mensaje,
                titulo,
                JOptionPane.WARNING_MESSAGE);
    }

    public static boolean mostrarConfirmacionPersonalizada(Component parent, String mensaje, String titulo) {
        int opcion = JOptionPane.showConfirmDialog(parent,
                mensaje,
                titulo,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        return opcion == JOptionPane.YES_OPTION;
    }

// ===================== UTILIDADES DE DIÁLOGO =====================
    public static void ocultarErroresDialogo(JComponent... componentes) {
        for (JComponent componente : componentes) {
            if (componente instanceof JLabel) {
                ((JLabel) componente).setVisible(false);
            }
        }
    }

    public static void limpiarErroresDialogo(JComponent... componentes) {
        for (JComponent componente : componentes) {
            limpiarError(componente, null);
        }
    }
}
