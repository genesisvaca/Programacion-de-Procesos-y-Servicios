package edu.thepower.u2programacion.multithread;

public class U2P00ThreadTesting {

    /*
     * ------------------------------------------------------------
     * COMENTARIO GENERAL DEL PROGRAMA
     * ------------------------------------------------------------
     * Este programa sirve para explorar las propiedades del hilo principal
     * (thread principal) que ejecuta la aplicación Java. Es una introducción
     * al uso de la clase Thread dentro del contexto de programación concurrente.
     *
     * El objetivo es entender que, incluso si no se crean hilos adicionales,
     * tdo programa Java se ejecuta dentro de al menos un hilo llamado "main".
     *
     * Se utilizan distintos métodos de la clase Thread para obtener información
     * sobre ese hilo actual: su nombre, identificador único (ID), prioridad,
     * estado y grupo al que pertenece.
     *
     * En resumen, este programa NO crea nuevos hilos, sino que consulta y muestra
     * las características del hilo principal en el que corre el métdo main().
     */

    public static void main(String[] args) {

        /*
         * BLOQUE: Información del hilo actual
         * -----------------------------------
         * La clase Thread representa un hilo de ejecución en Java.
         * Con Thread.currentThread() accedemos al objeto Thread que representa
         * el hilo en curso (en este caso, el hilo "main").
         *
         * A partir de este objeto, se consultan diferentes propiedades:
         * - Nombre del hilo (útil para identificarlo)
         * - ID único del hilo (asignado por la JVM)
         * - Prioridad del hilo (valor numérico entre 1 y 10)
         * - Estado actual (RUNNABLE, WAITING, TERMINATED, etc.)
         * - Grupo al que pertenece (organización lógica de hilos)
         *
         * Cada línea imprime una propiedad distinta para observar sus valores
         * en tiempo de ejecución.
         */

        // Muestra el nombre del hilo actual (por defecto suele ser "main")
        System.out.println("El nombre del thread es: " + Thread.currentThread().getName());

        // Muestra el identificador único del hilo, asignado automáticamente por la JVM
        System.out.println("El ID del thread es: " + Thread.currentThread().threadId());

        // Muestra la prioridad del hilo actual (por defecto suele ser 5 en un rango de 1–10)
        System.out.println("La prioridad del thread es: " + Thread.currentThread().getPriority());

        // Muestra el estado actual del hilo (por ejemplo: RUNNABLE mientras está ejecutando main)
        System.out.println("El estado del thread es: " + Thread.currentThread().getState());

        // Muestra el grupo de hilos al que pertenece (por defecto "main" también)
        System.out.println("El grupo del thread es: " + Thread.currentThread().getThreadGroup());
    }
}
