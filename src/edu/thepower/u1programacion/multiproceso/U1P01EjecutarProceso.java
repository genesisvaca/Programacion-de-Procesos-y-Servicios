package edu.thepower.u1programacion.multiproceso;

import java.io.IOException;
// Crear una clase tenga un métdo que ejecuta un programa
public class U1P01EjecutarProceso {
    public static void main(String[] args) {
        U1P01EjecutarProceso p = new U1P01EjecutarProceso();
        p.launcher("notepad");
    }

    public void launcher(String programa) {
        // Clase para ejecutar programas que se convierten en procesos,
        // a este ProcessBuilder hay que pasarle el programa que queremos ejecutar
        ProcessBuilder pb = new ProcessBuilder(programa);

        try {
            pb.start();
            // Si hay problema al ejecutar damos mensaje de error al usuario,
            // donde se ha producido el problema y demás con printStackTrace
        } catch (IOException e) {
            System.err.println("Error al iniciar el proceso" + programa);
            e.printStackTrace();
        }
    }
}