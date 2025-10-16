package edu.thepower.u1programacion.multiproceso;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

// Versión corregida en clase
public class U1P04ContadorVocal {

    private static final Map <Character,Character> VOCALES;

    static{
        VOCALES = new HashMap();
        VOCALES.put('a','á');
        VOCALES.put('e','é');
        VOCALES.put('i','í');
        VOCALES.put('o','ó');
        VOCALES.put('u','ú');
    }



    private void contarVocal(char vocal, String archivo) {

        int contador = 0;

        try (BufferedReader in = new BufferedReader( new FileReader(archivo))) {

            String line;
            while ((line = in.readLine()) != null) {

                line = line.toLowerCase();

                for (int i = 0; i < line.length(); i++) {

                    if (line.charAt(i) == vocal || line.charAt(i) == VOCALES.get(vocal) )
                        contador++;
                }
            }

        } catch (FileNotFoundException e) {
            System.err.println("Archivo no encontrado: " + archivo);
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.err.println("Error en lectura de archivo: " + archivo);
            throw new RuntimeException(e);
        }

        System.out.println(contador);

    }


    public static void main(String[] args) {


        U1P04ContadorVocal test = new U1P04ContadorVocal();

        test.contarVocal(args[0].charAt(0),args[1]);
    }
    // Que las vocales salgan de los argumentos desde eñ array de strings del main --Como con los números--
    //Crear ejecutarContadorVocales con 5 procesos

}

/*

  Programa multiproceso que cuenta cuántas veces aparece cada vocal
  en un archivo de texto. Las vocales y la ruta del archivo se pasan
  por argumentos desde la configuración (args).
public class U1P04ContadorVocal {


    private static final Map <Character,Character> VOCALES ;

    // Darle valor a un Mapa estático
    static {
        VOCALES = new HashMap();
        VOCALES.put('a','á');
        VOCALES.put('e','é');
        VOCALES.put('i','í');
        VOCALES.put('o','ó');
        VOCALES.put('u','ú');
    }

    // Métdo que cuenta cuántas veces aparece una vocal en un archivo.
    // Devuelve el número total de apariciones.
    public int contadorVocal(char vocal, String archivo) {
        int contador = 0;

        try (BufferedReader in = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = in.readLine()) != null) {
                linea = linea.toLowerCase();
                for (int i = 0; i < linea.length(); i++) {
                    if (linea.charAt(i) == Character.toLowerCase(vocal)) {
                        contador++;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("✗ Archivo no encontrado: " + archivo);
            throw new RuntimeException();
        } catch (IOException e) {
            System.err.println("✗ Error al leer el archivo: " + archivo);
            throw new RuntimeException();
        }

        System.out.println("Vocal '" + vocal + "' → " + contador);
        return contador;
    }


    //Guarda el resultado de cada conteo en un archivo individual.
    public void guardarResultado(char vocal, int conteo) {
        String nombreSalida = "resultado_" + vocal + ".txt";
        try (PrintWriter out = new PrintWriter(new FileWriter(nombreSalida))) {
            out.println(conteo);
            System.out.println("✓ Resultado guardado en: " + nombreSalida);
        } catch (IOException e) {
            System.err.println("✗ Error al escribir el archivo de salida: " + nombreSalida);
        }
    }

    // Clase interna que representa un proceso (Thread) para una vocal específica.
    static class ProcesoVocal extends Thread {
        private final char vocal;
        private final String archivo;
        private final U1P04ContadorVocal contador;

        public ProcesoVocal(char vocal, String archivo, U1P04ContadorVocal contador) {
            this.vocal = vocal;
            this.archivo = archivo;
            this.contador = contador;
        }

        @Override
        public void run() {
            int total = contador.contadorVocal(vocal, archivo);
            if (total >= 0) {
                contador.guardarResultado(vocal, total);
            }
        }
    }


    // MAIN: Las vocales y el nombre del archivo se reciben por args.
    public static void main(String[] args) {


        // Validación de argumentos
        if (args.length < 2) {
            System.out.println("Uso correcto: java U1P04ContadorVocal [vocales...] [archivo]");
            System.out.println("Ejemplo: a e i o u ./resources/vocales.txt");
            return;
        }

        // Último argumento → nombre del archivo
        String archivo = args[args.length - 1];

        // El resto → vocales
        char[] vocales = new char[args.length - 1];
        for (int i = 0; i < args.length - 1; i++) {
            if (args[i].length() != 1 || !"aeiouAEIOUáéíóú".contains(args[i])) {
                System.out.println("✗ '" + args[i] + "' no es una vocal válida. Se ignora.");
                continue;
            }
            vocales[i] = args[i].toLowerCase().charAt(0);
        }

        U1P04ContadorVocal cv = new U1P04ContadorVocal();

        System.out.println("=== INICIO DE PROCESOS DE CONTEO ===");

        for (char v : vocales) {
            if (v != '\0') { // ignorar posiciones vacías
                ProcesoVocal p = new ProcesoVocal(v, archivo, cv);
                p.start();
            }
        }
    }
    // Corregido


}*/
