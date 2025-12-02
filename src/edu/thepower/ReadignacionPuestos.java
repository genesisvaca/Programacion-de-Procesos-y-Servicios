package edu.thepower;

import java.util.*;

public class ReadignacionPuestos {

    private static final int MAX_ALUMNOS = 16;
    private static List<Integer> puestos = new ArrayList();
    private static Map<Integer, String> asignaciones = new HashMap();

    private static String[] nombres = {"Génesis", "Pablo", "Luisa", "Alex", "Sergio G", "Mario",
            "Astrid", "Esteban", "Victor", "Claudia", "Sergio M", "Marcos", "David", "Sebastián", "Aaron", "Johan"};

    private static List<String> alumnos = Arrays.asList(nombres);

    public static void main(String[] args) {
        for (int i = 0 ; i <= MAX_ALUMNOS ; i++){
            puestos.add(i);
        }

        System.out.println("Reasignando asientos...");
        Collections.shuffle(alumnos);
        Collections.shuffle(puestos);

        System.out.println("Resultados del sorte:");

        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < MAX_ALUMNOS; i++){
            System.out.print("El puesto para el alumno " + alumnos.get(i) + " es... ");
            sc.nextLine();
            System.out.println(puestos.get(i) + "\n");
            asignaciones.put(puestos.get(i), alumnos.get(i));
        }

        System.out.println("Sorteo finalizado, este es el resultado: ");

        for (Map.Entry e : asignaciones.entrySet()){
            System.out.println( "Puesto: " + e.getKey() + " | Alumno: " + e.getValue());
        }

        /*
        // Lo mismo de arriba
        asignaciones.entrySet().forEach(e ->{
            System.out.println( e.getKey() + " : " + e.getValue());
        });*/

    }
}
