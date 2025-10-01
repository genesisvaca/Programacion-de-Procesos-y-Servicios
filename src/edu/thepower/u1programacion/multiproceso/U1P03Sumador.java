package edu.thepower.u1programacion.multiproceso;

import java.io.IOException;
import java.util.Scanner;

public class U1P03Sumador {
    // Métdo que suma dos números
    private void sumar(int num1, int num2){
        int suma = 0;
        // Crear variable auxiliar que intercambia el valor haciendo que el primer valor sea inferior
        if  (num1 > num2){
            int aux = num1;
            num1 = num2;
            num2 = aux;
        }
        for (int i = num1; i <= num2; i++) {
            suma += i;
        }
        System.out.println("La suma de los numeros entre "+ num1 + " y "+ num2 + " es: " + suma);
    }


    public static void main(String[] args) {

        U1P03Sumador test = new U1P03Sumador();
        test.sumar(Integer.parseInt(args[0]),Integer.parseInt(args[1]));
    }
}
