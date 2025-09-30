package edu.thepower.u1programacion.multiproceso;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

// Ejecutar desde un programa java una máquina virtual que va a ejecutar un programa
public class U1P02EjecutarProcesoJava {
    // Crear una constante para ejecutar
    private static final String JAVA = "java";
    private static final String VERSION = "-version";

    public static void main(String[] args) {

        ProcessBuilder pb = new ProcessBuilder(JAVA,  VERSION);
        // 1 Redirigir la salida del proceso hijo al proceso padre
        // pb.redirectInput(ProcessBuilder.Redirect.INHERIT);
        // pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        // 2 Mediante BufferReader, capturar con un String,
        // que se comunique el proceso que lanza y el que ejecuta,
        // construyendo un canal de comunicación
        /*try {
            pb.redirectErrorStream(true); // *1 Nos ahorramos el segundo flujo de BufferReader redirigiéndolo el error a la estándar
            Process p = pb.start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }

            /*br = new BufferedReader(new InputStreamReader(p.getErrorStream()));

            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }1
        } catch (IOException e) {
            System.err.println("Error al iniciar el proceso");
            e.printStackTrace();
        }*/


        // Opción 3 Volcar salida a Fichero
        pb.redirectOutput(new File("./resources/salida.txt")); // Crea fichero salida.txt en resources
        pb.redirectError(new File("./resources/error.txt"));
        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}