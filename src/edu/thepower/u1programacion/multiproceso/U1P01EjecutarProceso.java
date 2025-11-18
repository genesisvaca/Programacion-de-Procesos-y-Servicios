package edu.thepower.u1programacion.multiproceso;

import java.io.IOException;

// ───────────────────────────────────────────────────────────────
// COMENTARIO GENERAL DEL PROGRAMA
// ----------------------------------------------------------------
// Este programa demuestra cómo lanzar un proceso del sistema
// (por ejemplo, el bloc de notas, calculadora, navegador, etc.)
// desde una aplicación Java.
//
// Utiliza la clase ProcessBuilder, que permite ejecutar comandos
// externos y crear nuevos procesos independientes del programa Java.
//
// En este caso, el programa abre "notepad" (Bloc de notas en Windows),
// pero el métdo launcher(String programa) permite reutilizarlo
// para ejecutar cualquier otro programa del sistema.
// ───────────────────────────────────────────────────────────────
public class U1P01EjecutarProceso {

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: MÉTDO PRINCIPAL (main)
       ----------------------------------------------------------------
       - Punto de entrada del programa.
       - Crea una instancia de la clase y llama al métdo launcher().
       - Este ejemplo lanza el Bloc de notas de Windows ("notepad").
       ───────────────────────────────────────────────────────────── */
    public static void main(String[] args) {

        // Se crea una instancia de la clase para poder usar el métdo launcher().
        U1P01EjecutarProceso p = new U1P01EjecutarProceso();

        // Se llama al métdo launcher con el nombre del programa a ejecutar.
        // En este caso: "notepad" → abre el bloc de notas.
        p.launcher("notepad");
    }

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 2: MÉTDO launcher()
       ----------------------------------------------------------------
       - Este métdo encapsula la lógica de ejecución de un proceso.
       - Recibe el nombre del programa que se quiere ejecutar.
       - Usa la clase ProcessBuilder para crear el proceso.
       - Si hay error (por ejemplo, programa no existe), se captura
         la excepción IOException y se informa al usuario.
       ───────────────────────────────────────────────────────────── */
    public void launcher(String programa) {

        // Se crea un objeto ProcessBuilder con el nombre del programa.
        // Este objeto prepara el entorno para ejecutar ese comando.
        // Ejemplo: new ProcessBuilder("notepad") → abrirá el bloc de notas.
        ProcessBuilder pb = new ProcessBuilder(programa);

        try {
            // Se inicia la ejecución del proceso.
            // Este comando lanza un nuevo proceso del sistema operativo,
            // independiente del programa Java actual.
            pb.start();

        } catch (IOException e) {
            // Si ocurre un error al intentar iniciar el proceso (por ejemplo,
            // si el programa no existe o el sistema no lo encuentra),
            // se muestra un mensaje de error en la salida de error estándar.
            System.err.println("Error al iniciar el proceso " + programa);

            // printStackTrace imprime la traza completa del error,
            // útil para depurar o ver la causa exacta del fallo.
            e.printStackTrace();
        }
    }
}
