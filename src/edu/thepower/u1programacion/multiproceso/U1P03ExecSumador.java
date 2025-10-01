package edu.thepower.u1programacion.multiproceso;

import java.io.IOException;
import java.lang.annotation.Inherited;
import java.util.Random;

public class U1P03ExecSumador {

    // Crear una constante para ejecutar un proceso en otra clase
    private static final String JAVA = "java";
    private static final String CLASE = "edu.thepower.u1programacion.multiproceso.U1P03Sumador";
    private static final String CP = "-cp";
    private static final String CLASSPATH = "C:\\Users\\AlumnoAfternoon\\Documents\\Programacion-de-Procesos-y-Servicios\\Programacion-de-Procesos-y-Servicios\\out\\production\\Programacion-de-Procesos-y-Servicios";
    private static final int NUM_PROCESOS = 5;

    public static void main (String[] args){

        Random r = new Random();
        for ( int i = 0; i < NUM_PROCESOS ; i++) {

            ProcessBuilder pb = new ProcessBuilder(JAVA, CP, CLASSPATH, CLASE, String.valueOf( r.nextInt(0,100)) ,String.valueOf( r.nextInt(0,100)));
            try {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); // Con esto conseguimos la salida estándar
                pb.redirectError(ProcessBuilder.Redirect.INHERIT); // COn esto conseguimos la salida de error
                pb.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("***Finalizado programa principal***");

        // ejecutamos 5 procesos en paralelo, y donde van los argumentos que los argumentos sean aleatorios entre 1 y 100
        //  y cada uno que se encarga de sumar los números que hay entre los dos comprendidos entre los numeros recibidos,

    }
}
