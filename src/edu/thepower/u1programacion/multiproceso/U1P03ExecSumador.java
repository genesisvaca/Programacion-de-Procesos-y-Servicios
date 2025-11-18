package edu.thepower.u1programacion.multiproceso;

import java.io.IOException;
import java.util.Random;

public class U1P03ExecSumador {

    /* ───────────────────────────────────────────────────────────────
       COMENTARIO GENERAL DEL PROGRAMA
       ----------------------------------------------------------------
       Este programa demuestra cómo lanzar procesos secundarios en Java
       desde un proceso principal usando la clase ProcessBuilder.

       Cada proceso ejecuta la clase "U1P03Sumador" (otra clase Java)
       que recibe dos números como argumentos, y se encarga de
       **sumar todos los números comprendidos entre ambos**.

       El objetivo es practicar la ejecución de procesos en paralelo
       (múltiples instancias de la JVM ejecutando otra clase) y el paso
       de parámetros al proceso hijo.

       En total, el programa genera 5 procesos, cada uno con dos números
       aleatorios entre 0 y 100, y hereda su salida estándar y de error.
       ─────────────────────────────────────────────────────────────── */


    /* ───────────────────────────────────────────────────────────────
       BLOQUE 1: CONSTANTES DE CONFIGURACIÓN DEL PROCESO
       ----------------------------------------------------------------
       Se definen como constantes porque su valor no cambia y se usan
       varias veces en la configuración de cada ProcessBuilder.
       ─────────────────────────────────────────────────────────────── */

    // Nombre del ejecutable a invocar. En este caso "java" ejecuta
    // el intérprete de Java para lanzar una clase ya compilada.
    private static final String JAVA = "java";

    // Ruta completa (paquete + nombre) de la clase que se ejecutará
    // como proceso secundario. Esa clase debe tener su propio main().
    private static final String CLASE =
            "edu.thepower.u1programacion.multiproceso.U1P03Sumador";

    // Parámetro que indica a la JVM qué classpath utilizar (-cp).
    private static final String CP = "-cp";

    // Ruta absoluta donde se encuentran los .class compilados.
    // En el contexto de este proyecto, apunta a la carpeta /out/production.
    // Es necesaria para que el proceso hijo encuentre la clase a ejecutar.
    private static final String CLASSPATH =
            "C:\\Users\\AlumnoAfternoon\\Documents\\Programacion-de-Procesos-y-Servicios\\Programacion-de-Procesos-y-Servicios\\out\\production\\Programacion-de-Procesos-y-Servicios";

    // Número de procesos (subprogramas) que se lanzarán.
    // Se usa en el bucle principal para repetir 5 veces la ejecución.
    private static final int NUM_PROCESOS = 5;


    /* ───────────────────────────────────────────────────────────────
       BLOQUE 2: MÉTDO PRINCIPAL – GENERACIÓN DE PROCESOS
       ----------------------------------------------------------------
       Este main es el proceso “padre”. Dentro del bucle crea 5
       procesos hijos que ejecutan la clase U1P03Sumador con dos números
       aleatorios como argumentos.
       ─────────────────────────────────────────────────────────────── */
    public static void main (String[] args){

        // Objeto Random: genera números aleatorios entre 0 y 100.
        // Dominio: simula que cada proceso recibe diferentes datos.
        Random r = new Random();

        // Bucle que crea y lanza los 5 procesos.
        for ( int i = 0; i < NUM_PROCESOS ; i++) {

            /* ---------------------------------------------------------
               BLOQUE 2.1: CONSTRUCCIÓN DEL PROCESO
               ---------------------------------------------------------
               ProcessBuilder permite crear procesos externos.
               Recibe una lista de argumentos como si fuese una línea
               de comandos. Ejemplo:
                 java -cp <ruta> <clase> arg1 arg2
               En este caso se pasa la ruta de la clase Sumador y dos
               números aleatorios como argumentos.
               --------------------------------------------------------- */
            ProcessBuilder pb = new ProcessBuilder(
                    JAVA,                     // Comando: ejecutable "java"
                    CP,                       // Opción "-cp"
                    CLASSPATH,                // Ruta donde buscar clases
                    CLASE,                    // Clase a ejecutar (main del sumador)
                    String.valueOf(r.nextInt(0,100)), // Primer número aleatorio
                    String.valueOf(r.nextInt(0,100))  // Segundo número aleatorio
            );

            /* ---------------------------------------------------------
               BLOQUE 2.2: CONFIGURACIÓN Y EJECUCIÓN DEL PROCESO
               ---------------------------------------------------------
               redirectOutput y redirectError indican que el proceso hijo
               usará la misma salida estándar y de error que el proceso padre,
               permitiendo ver los resultados directamente en consola.
               --------------------------------------------------------- */
            try {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
                pb.redirectError(ProcessBuilder.Redirect.INHERIT);

                // Lanza el proceso (asíncronamente, en paralelo).
                // Cada ejecución crea una nueva JVM que ejecuta U1P03Sumador.
                pb.start();

            } catch (IOException e) {
                // Si ocurre un error al iniciar el proceso, lo lanzamos
                // como RuntimeException para detener la ejecución.
                throw new RuntimeException(e);
            }
        }

        /* ---------------------------------------------------------
           BLOQUE 2.3: MENSAJE FINAL
           ---------------------------------------------------------
           Este mensaje se muestra cuando el proceso principal
           termina de lanzar los 5 subprocesos.
           Los procesos hijos pueden seguir ejecutándose en paralelo.
           --------------------------------------------------------- */
        System.out.println("***Finalizado programa principal***");


        /* ---------------------------------------------------------
           BLOQUE 2.4: RESUMEN DE FUNCIONAMIENTO
           ---------------------------------------------------------
           - Se lanzan 5 procesos en paralelo.
           - Cada uno ejecuta la clase U1P03Sumador con 2 argumentos.
           - Los argumentos son aleatorios (0–100).
           - U1P03Sumador (no mostrado aquí) se encarga de sumar todos
             los números comprendidos entre esos dos valores.
           --------------------------------------------------------- */
    }
}
