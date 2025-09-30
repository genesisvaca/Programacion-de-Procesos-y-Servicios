package edu.thepower.u1programacion.multiproceso;

import java.io.IOException;
import java.lang.annotation.Inherited;

public class U1P03ExecSumador {

    // Crear una constante para ejecutar
    private static final String JAVA = "java";
    private static final String CLASE = "edu.thepower.u1programacion.multiproceso.U1P03Sumador";
    private static final String CP = "-cp";
    private static final String CLASSPATH = "C:\\Users\\AlumnoAfternoon\\Documents\\Programacion-de-Procesos-y-Servicios\\Programacion-de-Procesos-y-Servicios\\out\\production\\Programacion-de-Procesos-y-Servicios";

    public static void main (String[] args){

        ProcessBuilder pb = new ProcessBuilder(JAVA,CP, CLASSPATH, CLASE, "10","20");
        try {
            pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); // Con esto conseguimos la salida estándar
            pb.redirectError(ProcessBuilder.Redirect.INHERIT); // COn esto conseguimos la  salida de error
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }




    }
}
