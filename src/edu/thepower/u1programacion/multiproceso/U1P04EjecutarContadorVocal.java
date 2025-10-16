package edu.thepower.u1programacion.multiproceso;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Versión corregida
public class U1P04EjecutarContadorVocal {

    private static final String JAVA = "java";
    private static final String CP = "-cp";
    private static final String CLASSPATH = "C:\\Users\\AlumnoAfternoon\\Documents\\PSP\\out\\production\\PSP";
    private static final String CLASE = "edu.thepower.u1programacion.multiproceso.U1P04ContadorVocal";
    private static final String ARCHIVO = "./resources/vocales.txt";
    private static final String[] VOCALES = {"a","e","i","o","u"};
    private static final String SALIDA = "./salida/";
    private static final String EXTENSION = ".txt";

    public static void main(String[] args) {

        List<Process> procesos = new ArrayList<>();
        //Creacion directorio Salida
        File directorioSalida = new File("salida");
        if (directorioSalida.mkdir())
            System.out.println("El directorio de salida se ha creado satisfactoriamente");
        else
            System.err.println("El directorio de salida ya existe, melón");



        for (int i = 0; i < VOCALES.length; i++) {
            ProcessBuilder pb = new ProcessBuilder(JAVA, CP, CLASSPATH, CLASE, VOCALES[i], ARCHIVO);
            pb.redirectOutput(new File(SALIDA + VOCALES[i] + EXTENSION));
            pb.redirectError(ProcessBuilder.Redirect.INHERIT);
            try {
                procesos.add(pb.start());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Finalizado el conteo de vocales.");

        for (Process proceso : procesos) {
            try {
                proceso.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
        //Mostrar por consola el numero total de cad vocal y aparte sumar el total de vocales y mostrarlo
        int acumulador = 0;
        for(int i = 0;i < VOCALES.length; i++) {

            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(SALIDA + VOCALES[i] + EXTENSION));
                int n = Integer.parseInt(br.readLine());
                System.out.println("El número de vocales " + VOCALES[i] + " es: " + n);
                acumulador += n;
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NumberFormatException e) {
                System.err.println("El archivo " + SALIDA + VOCALES[i] + EXTENSION + " no contenía un número.");
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        System.out.println("El total de vocales es: " + acumulador);


    }
}
/*
public class U2P04EjecutarContadorVocal {


    private static final String JAVA = "java";
    private static final String CLASE = "edu.thepower.u1programacion.multiproceso.U1P04ContadorVocal";
    private static final String CP = "-cp";
    private static final String CLASSPATH = "C:\\Users\\AlumnoAfternoon\\Documents\\Programacion-de-Procesos-y-Servicios\\Programacion-de-Procesos-y-Servicios\\out\\production\\Programacion-de-Procesos-y-Servicios";
    private static final String ARCHIVO = "./resources/vocales.txt";
    private static final String[] VOCALES= {"a","e","i","o","u"};
    private static final String SALIDA = "./salida/";
    private static final String EXTENSION = ".txt";

    static public  void main(String[] args) {

        List<Process> procesos = new ArrayList<>();
        // Crear directorio de salida
        File dirSaldia = new File("salida");

        if(dirSaldia.mkdirs()){
            System.out.println("El directorio de salida se ha creado correctamente!");
        } else {
            System.out.println("El directorio de salida ya existe!");
        }



        for ( int i = 0 ; i < VOCALES.length ; i++){
            ProcessBuilder pb = new ProcessBuilder(JAVA  ,CP, CLASSPATH,CLASE,VOCALES[i],ARCHIVO);
            try {
                pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); // Con esto conseguimos la salida estándar
                pb.redirectOutput(new File(SALIDA + "resultado_" + VOCALES[i] + EXTENSION));
                pb.redirectError(ProcessBuilder.Redirect.INHERIT); // COn esto conseguimos la salida de error
                procesos.add(pb.start());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("Finalizado el proceso del contador de vocales!");

        // Creamos un objeto que pasa por un condicional revisando que los procesos hayan terminado
        for ( Process proceso: procesos){
            try {
                proceso.waitFor();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        int acumulador = 0;


        // Mostrar el numero total de vocal por archivo y el total de vocales
        for (int i = 0; i < VOCALES.length ; i++ ){

            BufferedReader br = null;

            try {
                br = new BufferedReader(new FileReader(SALIDA + "resultado_" + VOCALES[i] + EXTENSION));

                int num= Integer.parseInt(br.readLine());

                System.out.println("El número de " + VOCALES[i] + " es: " + num);
                acumulador += num;


            } catch (IOException e){
                throw new RuntimeException();
            } catch (NumberFormatException e) {
                System.out.println("El archivo" + SALIDA + "resultado_" + VOCALES[i] + EXTENSION +" no contenía la vocal");
            }finally{
                try {
                    br.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("El total de vocales es: " + acumulador);


    }
}
*/