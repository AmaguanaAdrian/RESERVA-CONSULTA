/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Utilidades;
import java.awt.Color;
import javax.swing.*;
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

    // ===================== FILTROS =====================
    public static void soloNumeros(JTextField campo, int maxLength) {
        AbstractDocument doc = (AbstractDocument) campo.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {

            @Override
            public void insertString(FilterBypass fb, int offset, String string,
                                     AttributeSet attr) throws BadLocationException {

                if (string == null) return;

                if (string.matches("\\d+") &&
                        fb.getDocument().getLength() + string.length() <= maxLength) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text,
                                AttributeSet attrs) throws BadLocationException {

                if (text == null) return;

                if (text.matches("\\d+") &&
                        fb.getDocument().getLength() - length + text.length() <= maxLength) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });
    }

    // ===================== VALIDACIONES =====================
    public static boolean validarCedulaEcuatoriana(String cedula) {

        if (cedula == null || !cedula.matches("\\d{10}")) return false;

        int provincia = Integer.parseInt(cedula.substring(0, 2));
        if (provincia < 1 || provincia > 24) return false;

        int tercerDigito = Character.getNumericValue(cedula.charAt(2));
        if (tercerDigito >= 6) return false;

        int[] coef = {2, 1, 2, 1, 2, 1, 2, 1, 2};
        int suma = 0;

        for (int i = 0; i < coef.length; i++) {
            int valor = Character.getNumericValue(cedula.charAt(i)) * coef[i];
            if (valor >= 10) valor -= 9;
            suma += valor;
        }

        int verificador = (10 - (suma % 10)) % 10;
        int ultimo = Character.getNumericValue(cedula.charAt(9));

        return verificador == ultimo;
    }

    public static boolean validarPassword(String pass) {
        return pass != null && pass.matches("^(?=.*[A-Za-z])(?=.*\\d).{8,}$");
    }

    // ===================== UI =====================
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

    public static void togglePassword(JPasswordField campo) {
        if (campo.getEchoChar() == 0) {
            campo.setEchoChar('•');
        } else {
            campo.setEchoChar((char) 0);
        }
    }
}
