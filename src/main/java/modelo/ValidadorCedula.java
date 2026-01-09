/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author amagu
 */
public class ValidadorCedula {

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

        int suma = 0;
        for (int i = 0; i < 9; i++) {
            int digito = Character.getNumericValue(cedula.charAt(i));
            if (i % 2 == 0) {
                digito *= 2;
                if (digito > 9) digito -= 9;
            }
            suma += digito;
        }

        int verificador = 10 - (suma % 10);
        if (verificador == 10) verificador = 0;

        return verificador == Character.getNumericValue(cedula.charAt(9));
    }
}
