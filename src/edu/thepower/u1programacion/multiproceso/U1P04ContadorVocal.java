package edu.thepower.u1programacion.multiproceso;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// ───────────────────────────────────────────────────────────────
// COMENTARIO GENERAL DEL PROGRAMA
// ----------------------------------------------------------------
// Este programa cuenta cuántas veces aparece una vocal específica
// (por ejemplo, 'a' o 'é') dentro de un archivo de texto.
//
// Está pensado para ejecutarse desde otro programa principal
// (Ej. EjecutarContadorVocales), que podría lanzar varios procesos
// paralelos, uno por cada vocal, pasando como argumentos:
//   - La vocal a contar.
//   - La ruta del archivo.
//
// También maneja vocales acentuadas gracias a un mapa de equivalencias.
// Ejemplo de uso desde consola:
//    java U1P04ContadorVocal a ./resources/texto.txt
//
// Resultado: muestra en consola cuántas veces aparece 'a' o 'á'.
// ───────────────────────────────────────────────────────────────
public class U1P04ContadorVocal {

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: ESTRUCTURA DE DATOS ESTÁTICA (MAPA DE VOCALES)
       ----------------------------------------------------------------
       - Este mapa relaciona cada vocal sin tilde con su vocal acentuada.
       - Es estático porque se comparte entre todas las instancias.
       - Se usa para contar tanto la versión normal como la acentuada.
       Ejemplo: 'a' → 'á', 'e' → 'é', etc.
       ───────────────────────────────────────────────────────────── */
    private static final Map<Character, Character> VOCALES;

    static {
        VOCALES = new HashMap<>();
        VOCALES.put('a', 'á');
        VOCALES.put('e', 'é');
        VOCALES.put('i', 'í');
        VOCALES.put('o', 'ó');
        VOCALES.put('u', 'ú');
    }


    /* ─────────────────────────────────────────────────────────────
       BLOQUE 2: MÉTDO contarVocal()
       ----------------------------------------------------------------
       - Cuenta cuántas veces aparece una vocal (con o sin tilde)
         en el archivo especificado.
       - Recibe:
           → char vocal: vocal base a buscar ('a', 'e', etc.)
           → String archivo: ruta al archivo de texto.
       - Abre el archivo con BufferedReader y lo recorre línea por línea.
       - Convierte cada línea a minúsculas para evitar errores
         de coincidencia con mayúsculas.
       - Incrementa un contador cada vez que encuentra la vocal.
       - Si el archivo no existe o no se puede leer, muestra el error.
       ───────────────────────────────────────────────────────────── */
    private void contarVocal(char vocal, String archivo) {

        // Variable local que acumula el número de apariciones.
        int contador = 0;

        // try-with-resources: garantiza que el archivo se cierre automáticamente
        // después de usarlo, incluso si ocurre una excepción.
        try (BufferedReader in = new BufferedReader(new FileReader(archivo))) {

            String line;
            // Se lee el archivo línea por línea hasta que no haya más.
            while ((line = in.readLine()) != null) {

                // Convertimos tdo el texto a minúsculas
                // para comparar sin distinguir entre mayúsculas y minúsculas.
                line = line.toLowerCase();

                // Recorremos cada carácter de la línea
                for (int i = 0; i < line.length(); i++) {
                    // Si el carácter actual coincide con la vocal base
                    // o con su versión acentuada, aumentamos el contador.
                    if (line.charAt(i) == vocal || line.charAt(i) == VOCALES.get(vocal))
                        contador++;
                }
            }

        } catch (FileNotFoundException e) {
            // Se lanza si el archivo no existe.
            System.err.println("Archivo no encontrado: " + archivo);
            throw new RuntimeException(e);

        } catch (IOException e) {
            // Error general de lectura (por ejemplo, permisos, encoding...).
            System.err.println("Error en lectura de archivo: " + archivo);
            throw new RuntimeException(e);
        }

        // Muestra el resultado final del conteo por consola.
        System.out.println(contador);
    }


    /* ─────────────────────────────────────────────────────────────
       BLOQUE 3: MÉTDO PRINCIPAL (main)
       ----------------------------------------------------------------
       - Recibe los argumentos de línea de comandos:
         args[0] → vocal a contar
         args[1] → nombre o ruta del archivo.
       - Crea una instancia de la clase y llama a contarVocal().
       - Este main se usa cuando se ejecuta esta clase de forma individual
         o desde otro proceso (por ejemplo, con ProcessBuilder).
       ───────────────────────────────────────────────────────────── */
    public static void main(String[] args) {

        // Se crea un objeto de la clase (porque el métdo contarVocal no es estático)
        U1P04ContadorVocal test = new U1P04ContadorVocal();

        // Se llama al métdo contarVocal, pasando:
        // - la primera letra del primer argumento como vocal,
        // - la ruta del archivo como segundo argumento.
        // Ejemplo: args = {"a", "./texto.txt"}
        test.contarVocal(args[0].charAt(0), args[1]);
    }

    // ─────────────────────────────────────────────────────────────
    // NOTAS Y COMENTARIOS ADICIONALES (contexto de práctica)
    // ----------------------------------------------------------------
    // • Este programa se puede ejecutar de forma independiente, o bien
    //   desde otro programa que lance varios procesos (por ejemplo:
    //   EjecutarContadorVocales).
    //
    // • En ese caso, el programa principal podría crear 5 procesos:
    //   uno por cada vocal ('a', 'e', 'i', 'o', 'u'), cada uno ejecutando
    //   esta clase y pasando la vocal y el mismo archivo como parámetros.
    //
    // • Así se estaría utilizando programación multiproceso para
    //   procesar el mismo archivo en paralelo, mejorando el rendimiento
    //   y aprovechando la CPU.
    // ─────────────────────────────────────────────────────────────
}
