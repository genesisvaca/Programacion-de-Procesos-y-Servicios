package edu.thepower.u1programacion.multiproceso;

import java.io.IOException;
import java.util.Scanner;

public class U1P03Sumador {

    /* ───────────────────────────────────────────────────────────────
       COMENTARIO GENERAL DEL PROGRAMA
       ----------------------------------------------------------------
       Esta clase representa el *proceso hijo* ejecutado por el programa
       U1P03ExecSumador.

       Su función es muy simple:
       → Recibe dos números como argumentos (args[0] y args[1])
       → Calcula la suma de todos los enteros comprendidos entre ellos,
         incluyendo los extremos.
       → Muestra el resultado en pantalla.

       Cada proceso creado por U1P03ExecSumador ejecuta esta clase de forma
       independiente, por lo que se pueden tener varios sumadores ejecutándose
       en paralelo, cada uno con sus propios argumentos.
       ─────────────────────────────────────────────────────────────── */


    /* ───────────────────────────────────────────────────────────────
       BLOQUE 1: MÉTDO PRIVADO sumar()
       ----------------------------------------------------------------
       Este métdo realiza la lógica principal de cálculo. No es estático
       porque se invoca desde una instancia de la clase (en el main).
       ─────────────────────────────────────────────────────────────── */
    private void sumar(int num1, int num2){
        // Variable local que almacenará el resultado acumulado.
        // Dominio: se usa como acumulador de la suma de todos los números
        // entre num1 y num2 (inclusive).
        int suma = 0;

        // Aseguramos que el primer número sea el menor.
        // Si los argumentos llegan invertidos, se intercambian.
        // Así el bucle for podrá ir de menor a mayor sin errores.
        if (num1 > num2){
            int aux = num1;  // Variable auxiliar para intercambio de valores.
            num1 = num2;
            num2 = aux;
        }

        // Bucle que recorre todos los enteros desde num1 hasta num2.
        // En cada iteración, añade el valor actual al acumulador "suma".
        for (int i = num1; i <= num2; i++) {
            suma += i;
        }

        // Al finalizar, se imprime el resultado formateado.
        // Ejemplo: “La suma de los numeros entre 3 y 6 es: 18”
        System.out.println("La suma de los numeros entre " + num1 +
                " y " + num2 + " es: " + suma);
    }


    /* ───────────────────────────────────────────────────────────────
       BLOQUE 2: MÉTDO PRINCIPAL (main)
       ----------------------------------------------------------------
       Este main actúa como punto de entrada del proceso hijo.
       Los argumentos son recibidos desde el proceso principal (ExecSumador).
       ─────────────────────────────────────────────────────────────── */
    public static void main(String[] args) {

        // Se crea una instancia de la clase para poder usar el método sumar(),
        // que no es estático.
        U1P03Sumador test = new U1P03Sumador();

        // Convierte los argumentos de tipo String a int y llama al método.
        // Integer.parseInt() lanza excepción si no son números válidos.
        // args[0] = primer número aleatorio
        // args[1] = segundo número aleatorio
        test.sumar(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
    }
}
