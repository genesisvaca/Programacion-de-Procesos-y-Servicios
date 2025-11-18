package edu.thepower.u1programacion.multiproceso;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

// ───────────────────────────────────────────────────────────────
// COMENTARIO GENERAL DEL PROGRAMA
// ----------------------------------------------------------------
// Este programa demuestra tres formas distintas de manejar la salida
// de un proceso hijo ejecutado desde Java.
//
// El proceso hijo en este caso es una nueva máquina virtual de Java
// que ejecuta el comando `java -version`, el cual imprime información
// sobre la versión de la JVM instalada.
//
// OBJETIVOS DEL PROGRAMA:
// 1. Aprender a lanzar un proceso Java desde otro programa Java.
// 2. Capturar o redirigir la salida del proceso hijo.
// 3. Practicar la comunicación entre procesos padre e hijo (flujo estándar).
//
// Métodos mostrados:
//   - Redirección directa (INHERIT).
//   - Lectura de la salida con BufferedReader.
//   - Redirección a archivos de texto (salida y error).
// ───────────────────────────────────────────────────────────────
public class U1P02EjecutarProcesoJava {

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: CONSTANTES DE CONFIGURACIÓN
       ----------------------------------------------------------------
       Se definen las constantes necesarias para construir el comando
       que ejecutará el proceso hijo.
       ───────────────────────────────────────────────────────────── */

    // Nombre del ejecutable. "java" lanza una nueva máquina virtual.
    private static final String JAVA = "java";

    // Argumento que indica al comando que muestre la versión de Java.
    // (equivalente a ejecutar: java -version)
    private static final String VERSION = "-version";


    /* ─────────────────────────────────────────────────────────────
       BLOQUE 2: MÉTDO PRINCIPAL
       ----------------------------------------------------------------
       Contiene tres posibles formas de redirigir o capturar la salida
       de un proceso hijo. Solo una está activa (la tercera, a fichero),
       las otras dos se dejan comentadas como demostración.
       ───────────────────────────────────────────────────────────── */
    public static void main(String[] args) {

        // Se crea un ProcessBuilder con los argumentos necesarios.
        // Esto equivale a escribir en consola:
        // > java -version
        ProcessBuilder pb = new ProcessBuilder(JAVA, VERSION);

        /* =========================================================
           OPCIÓN 1 – Heredar la salida del proceso hijo
           ---------------------------------------------------------
           Si se descomenta este bloque, el proceso hijo (java -version)
           mostrará su salida directamente en la consola del programa padre.
           Esto se hace con redirectOutput / redirectError y Redirect.INHERIT.
           ========================================================= */

        // pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
        // pb.redirectError(ProcessBuilder.Redirect.INHERIT);


        /* =========================================================
           OPCIÓN 2 – Capturar la salida del proceso hijo
           ---------------------------------------------------------
           En lugar de redirigir directamente, aquí se leen las salidas
           del proceso hijo (standard output y error) a través de flujos
           de entrada (InputStream), usando un BufferedReader.
           ========================================================= */

        /*
        try {
            // *1 redirige el flujo de error (stderr) al de salida (stdout)
            // así solo necesitamos leer un flujo.
            pb.redirectErrorStream(true);

            // Se lanza el proceso y se obtiene un objeto Process,
            // que representa la ejecución del proceso hijo.
            Process p = pb.start();

            // Se crea un BufferedReader para leer la salida del proceso.
            // InputStreamReader adapta el flujo binario a texto (UTF-8).
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(p.getInputStream())
            );

            // Se leen las líneas generadas por el proceso hijo.
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);  // Muestra la salida del hijo.
            }

            // (Alternativa sin redirectErrorStream:)
            // Si no redirigimos los errores, tendríamos que leer otro
            // flujo aparte: p.getErrorStream().
            // BufferedReader brError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            // ...
        } catch (IOException e) {
            System.err.println("Error al iniciar el proceso");
            e.printStackTrace();
        }
        */


        /* =========================================================
           OPCIÓN 3 – Redirigir la salida y el error a ficheros
           ---------------------------------------------------------
           Esta es la versión activa. En lugar de mostrar en consola,
           el programa guarda la salida y los errores del proceso
           en archivos dentro de la carpeta "resources".
           ========================================================= */

        // Redirige la salida estándar (stdout) del proceso hijo
        // al fichero "./resources/salida.txt".
        pb.redirectOutput(new File("./resources/salida.txt"));

        // Redirige la salida de error (stderr) al fichero "./resources/error.txt".
        pb.redirectError(new File("./resources/error.txt"));

        try {
            // Inicia la ejecución del proceso hijo.
            // Esto crea una nueva JVM que ejecuta "java -version".
            pb.start();
        } catch (IOException e) {
            // Si hay algún error (por ejemplo, no encuentra el comando),
            // se lanza una excepción para avisar del fallo.
            throw new RuntimeException(e);
        }
    }
}
