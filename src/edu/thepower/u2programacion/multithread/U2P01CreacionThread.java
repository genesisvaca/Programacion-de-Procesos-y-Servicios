package edu.thepower.u2programacion.multithread;

/*
 * ------------------------------------------------------------
 * COMENTARIO GENERAL DEL PROGRAMA
 * ------------------------------------------------------------
 * Este programa demuestra tres formas distintas de crear y ejecutar hilos (threads)
 * en Java. El objetivo es comprender cómo se puede implementar la concurrencia
 * utilizando distintas aproximaciones:
 *
 * 1. Implementando la interfaz Runnable (más flexible y recomendable).
 * 2. Extendiendo directamente la clase Thread (herencia directa).
 * 3. Usando una expresión lambda para crear el hilo de forma anónima.
 *
 * Cada hilo imprimirá su nombre e ID, lo que permite observar cómo la JVM
 * asigna recursos y ejecuta múltiples hilos en paralelo.
 *
 * Este ejemplo forma parte de la Unidad 2 (Programación Multihilo) y tiene como
 * propósito enseñar las diferencias prácticas entre los tres métodos de creación
 * de hilos en Java.
 */


/*
 * ------------------------------------------------------------
 * CLASE ThreadImplements: Implementa la interfaz Runnable
 * ------------------------------------------------------------
 * Esta clase representa un hilo que se ejecutará cuando su método run()
 * sea invocado por un objeto Thread.
 *
 * Se elige implementar Runnable en lugar de extender Thread porque permite
 * que la clase pueda heredar de otra clase, evitando la limitación de la herencia simple.
 */
class ThreadImplements implements Runnable {

    @Override
    public void run() {
        /*
         * BLOQUE: Lógica del hilo implementado con Runnable
         * -----------------------------------------------
         * Cuando el hilo se inicia mediante Thread.start(), la JVM ejecuta este método run().
         * En este caso, simplemente muestra el nombre e ID del hilo actual.
         */
        System.out.println("El nombre del thread es: " + Thread.currentThread().getName());
        System.out.println("El ID del thread es: " + Thread.currentThread().threadId());
    }
}


/*
 * ------------------------------------------------------------
 * CLASE PRINCIPAL U2P01CreacionThread
 * ------------------------------------------------------------
 * Esta clase hereda directamente de Thread, demostrando la segunda forma
 * de crear un hilo en Java (herencia).
 *
 * Además, contiene el método main(), donde se crean e inician varios hilos
 * usando las tres técnicas: herencia, Runnable y expresión lambda.
 */
public class U2P01CreacionThread extends Thread {

    /*
     * BLOQUE: Sobrescritura del método run()
     * --------------------------------------
     * Este método contiene el código que se ejecutará cuando el hilo
     * creado a partir de esta clase sea lanzado con start().
     */
    @Override
    public void run() {
        System.out.println("El nombre del thread es: " + Thread.currentThread().getName());
        System.out.println("El ID del thread es: " + Thread.currentThread().threadId());
    }

    public static void main(String[] args) {

        /*
         * BLOQUE 1: Creación de un hilo usando una expresión lambda
         * ---------------------------------------------------------
         * Se crea un objeto Thread pasando como primer argumento una expresión lambda
         * que define el método run() de forma anónima.
         *
         * - La lambda ejecuta dos líneas simples: imprime el nombre e ID del hilo.
         * - El segundo parámetro del constructor ("Thread Lambda") asigna un nombre
         *   al hilo para poder identificarlo fácilmente.
         */
        Thread t1 = new Thread(() -> {
            System.out.println("El nombre del thread es: " + Thread.currentThread().getName());
            System.out.println("El ID del thread es: " + Thread.currentThread().threadId());
        }, "Thread Lambda");


        /*
         * BLOQUE 2: Creación de un hilo a través de herencia
         * ---------------------------------------------------
         * Aquí se instancia un objeto de la clase U2P01CreacionThread, que extiende Thread.
         *
         * Al pasar este objeto al constructor Thread (junto con un nombre),
         * se crea un hilo que ejecutará el método run() definido en esta clase.
         */
        Thread t2 = new Thread(new U2P01CreacionThread(), "Thread Herencia");


        /*
         * BLOQUE 3: Creación múltiple de hilos usando Runnable
         * -----------------------------------------------------
         * Se crea un bucle que lanza 6 hilos distintos (i = 0 a 5),
         * cada uno basado en una instancia de ThreadImplements.
         *
         * El nombre de cada hilo se personaliza con el número del bucle ("Thread Implements0", etc.)
         * Esto simula la creación dinámica de hilos con diferentes tareas o identificadores.
         */
        for (int i = 0; i <= 5; i++) {

            // Cada iteración crea un nuevo hilo asociado a la implementación Runnable
            Thread t3 = new Thread(new ThreadImplements(), "Thread Implements" + i);

            // Se inicia la ejecución del hilo: JVM ejecutará su método run() en paralelo
            t3.start();
        }


        /*
         * BLOQUE 4: Inicio de los hilos principales t1 y t2
         * --------------------------------------------------
         * Una vez creados, los hilos deben iniciarse con start() para que
         * la JVM los ejecute en paralelo (nunca se debe llamar run() directamente).
         */
        t1.start();
        t2.start();
    }
}
