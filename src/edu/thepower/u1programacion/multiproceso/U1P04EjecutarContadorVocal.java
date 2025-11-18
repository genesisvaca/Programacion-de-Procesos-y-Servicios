package edu.thepower.u1programacion.multiproceso;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// ───────────────────────────────────────────────────────────────
// COMENTARIO GENERAL DEL PROGRAMA
// ----------------------------------------------------------------
// Este programa lanza 5 procesos hijos (uno por vocal a, e, i, o, u)
// que ejecutan la clase U1P04ContadorVocal como proceso independiente,
// redirigiendo la salida de cada proceso a un archivo distinto.
//
// Flujo:
//  1) Prepara constantes para construir el comando: java -cp <ruta> <Clase> <vocal> <archivo>
//  2) Crea el directorio "salida/" si no existe.
//  3) Lanza 5 procesos (uno por cada vocal) con ProcessBuilder, redirigiendo su salida a ./salida/<vocal>.txt
//  4) Espera a que terminen TODOS los procesos (waitFor()).
//  5) Lee los ficheros generados, muestra los conteos y calcula el total.
//
// Objetivo didáctico:
//  - Practicar multiproceso con ProcessBuilder
//  - Redirección de stdout/stderr a ficheros
//  - Sincronización padre–hijo con waitFor()
// ───────────────────────────────────────────────────────────────
public class U1P04EjecutarContadorVocal {

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: CONSTANTES DE CONFIGURACIÓN DEL PROCESO
       ----------------------------------------------------------------
       Todas son final porque no cambian durante la ejecución y se
       usan para ensamblar el comando y las rutas de salida.
       ───────────────────────────────────────────────────────────── */

    // Ejecutable de la JVM para lanzar el proceso hijo.
    private static final String JAVA = "java";

    // Opción de la JVM para indicar el classpath que usará el proceso hijo.
    private static final String CP = "-cp";

    // Ruta a los .class compilados para que el proceso hijo encuentre la clase a ejecutar.
    // Dominio: sin un classpath correcto, el hijo no podría cargar U1P04ContadorVocal.
    private static final String CLASSPATH = "C:\\Users\\AlumnoAfternoon\\Documents\\PSP\\out\\production\\PSP";

    // Clase con main() que contará las vocales. Se ejecuta en el proceso hijo.
    private static final String CLASE = "edu.thepower.u1programacion.multiproceso.U1P04ContadorVocal";

    // Archivo de texto sobre el que contarán las vocales todos los procesos hijos.
    private static final String ARCHIVO = "./resources/vocales.txt";

    // Conjunto de vocales a procesar (un proceso por cada entrada).
    private static final String[] VOCALES = {"a","e","i","o","u"};

    // Carpeta donde se volcarán los resultados de cada proceso hijo.
    private static final String SALIDA = "./salida/";

    // Extensión de los archivos de resultado. Cada proceso escribe un número en <vocal>.txt
    private static final String EXTENSION = ".txt";


    /* ─────────────────────────────────────────────────────────────
       BLOQUE 2: MÉTDO PRINCIPAL – ORQUESTA LOS PROCESOS HIJOS
       ----------------------------------------------------------------
       - Crea el directorio de salida si no existe.
       - Lanza un proceso por vocal.
       - Espera a que todos terminen.
       - Lee los ficheros de salida y suma los resultados.
       ───────────────────────────────────────────────────────────── */
    public static void main(String[] args) {

        // Lista para retener los objetos Process devueltos por pb.start().
        // Dominio: necesitamos esperar a que todos acaben → waitFor() a cada uno.
        List<Process> procesos = new ArrayList<>();

        // ───── Paso 1: crear/validar el directorio de salida ─────
        File directorioSalida = new File("salida");
        if (directorioSalida.mkdir())
            System.out.println("El directorio de salida se ha creado satisfactoriamente");
        else
            System.err.println("El directorio de salida ya existe, melón");

        // ───── Paso 2: lanzar un proceso por cada vocal ─────
        for (int i = 0; i < VOCALES.length; i++) {
            // Construye el comando:
            // java -cp <CLASSPATH> <CLASE> <vocal> <archivo>
            ProcessBuilder pb = new ProcessBuilder(JAVA, CP, CLASSPATH, CLASE, VOCALES[i], ARCHIVO);

            // Redirige la salida estándar del hijo a un fichero: ./salida/<vocal>.txt
            // Cada proceso escribirá un entero (el conteo) en su propio archivo.
            pb.redirectOutput(new File(SALIDA + VOCALES[i] + EXTENSION));

            // Redirige la salida de error del hijo a la consola del padre (útil para ver fallos).
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);

            try {
                // Lanza el proceso hijo de forma asíncrona y guarda el handle.
                procesos.add(pb.start());
            } catch (IOException e) {
                // Si no se puede lanzar el proceso (classpath mal, clase no encontrada, etc.), abortamos.
                throw new RuntimeException(e);
            }
        }

        System.out.println("Finalizado el conteo de vocales.");

        // ───── Paso 3: sincronización – esperar a que todos los hijos terminen ─────
        for (Process proceso : procesos) {
            try {
                proceso.waitFor(); // Bloquea hasta que el proceso hijo finaliza.
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // ───── Paso 4: leer los ficheros de salida y acumular el total ─────
        // Mostrar por consola el número total de cada vocal y, aparte, sumar el total.
        int acumulador = 0;

        for (int i = 0; i < VOCALES.length; i++) {
            BufferedReader br = null;
            try {
                // Abre el archivo de salida generado por el proceso de esa vocal.
                br = new BufferedReader(new FileReader(SALIDA + VOCALES[i] + EXTENSION));

                // Cada fichero debe contener una sola línea con un número (conteo de esa vocal).
                int n = Integer.parseInt(br.readLine());

                // Muestra el resultado por vocal.
                System.out.println("El número de vocales " + VOCALES[i] + " es: " + n);

                // Suma al acumulador global.
                acumulador += n;

            } catch (IOException e) {
                // Errores de I/O: no se puede abrir/leer el fichero de salida.
                throw new RuntimeException(e);

            } catch (NumberFormatException e) {
                // El archivo no contenía un número (posible error en el proceso hijo).
                System.err.println("El archivo " + SALIDA + VOCALES[i] + EXTENSION + " no contenía un número.");

            } finally {
                // Cierra el lector si llegó a abrirse.
                try {
                    if (br != null) br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // Muestra el total de vocales (suma de todas las vocales contadas por los procesos).
        System.out.println("El total de vocales es: " + acumulador);
    }
}
