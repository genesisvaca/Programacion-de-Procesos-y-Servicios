package edu.thepower.u1programacion.multiproceso;

import java.io.*;

public class U1P04ContadorVocal {

    /*Programa que recibe una vocal,
una clase que va a tener un métdo que va  a recibir como parametros una vocal
 y el combre de un archivo, contar las veces que aparece esa volcal en el contenido del archivo,
 tiene que imprimir el numero total de vocales, solo el numero

 2. 5 procesos cada uno con una vocal y cada proceso cuenta cuantas vocales tiene cada archivo, el resultado de cada proceso se debe guardar en un archivo
 */
    //public static final String ARCHIVO = "resource/vocales.txt";

    public void contadorVocal (char vocal, String archivo){


        int contador = 0;
        try (BufferedReader in = new BufferedReader(new FileReader(archivo))){
            String line = "";

            // Mientras dentro de la linea hay algo distinto a null entra en el bucle
            while ((line = in.readLine()) != null){
                for(int i = 0; i < line.length(); i++){
                    // Si la vocal se encuentra entre uno de los
                    // carácteres de la linea sumo uno al contador
                    if(line.charAt(i) == vocal){
                        contador++;
                    }
                }
            }


        }catch (FileNotFoundException e){
            System.err.println("Archivo "+ archivo+ " no encontrado");
            throw new RuntimeException();
        }catch (IOException e){
            System.err.println("Error en lectura de archivo " + archivo);
            throw new RuntimeException();
        }
        System.out.println(contador);
    }
    public static void main (String[] args) {

        U1P04ContadorVocal v = new U1P04ContadorVocal();
        v.contadorVocal('a', "./resources/vocales.txt");
    }
}
