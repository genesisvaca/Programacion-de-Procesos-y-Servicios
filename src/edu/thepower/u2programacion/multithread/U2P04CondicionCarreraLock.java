package edu.thepower.u2programacion.multithread;

import java.util.concurrent.locks.ReentrantLock;

/*
 * ------------------------------------------------------------
 * COMENTARIO GENERAL DEL PROGRAMA
 * ------------------------------------------------------------
 * Este programa demuestra cómo evitar una *condición de carrera* (race condition)
 * cuando dos hilos acceden y modifican una misma variable compartida.
 *
 * Problema: Si dos hilos modifican una variable (contador) al mismo tiempo sin
 * control, los resultados son inconsistentes (no deterministas).
 *
 * Solución: Se usa un objeto ReentrantLock para garantizar *exclusión mutua*:
 * sólo un hilo puede ejecutar el bloque crítico (incrementarContador) a la vez.
 *
 * En el ejemplo:
 * - Un hilo incrementa el contador 1 millón de veces (+10 cada vez).
 * - Otro hilo lo decrementa 1 millón de veces (-10 cada vez).
 * - El uso del lock asegura que el resultado final sea 0, evitando interferencias.
 */
public class U2P04CondicionCarreraLock {

    /*
     * VARIABLES ESTÁTICAS DE CONFIGURACIÓN Y SINCRONIZACIÓN
     * -----------------------------------------------------
     * - ITERACIONES: número de veces que cada hilo modificará el contador.
     * - VALOR: cantidad a sumar/restar por iteración.
     * - contador: recurso compartido entre hilos (variable crítica).
     * - lock: objeto de tipo ReentrantLock que controla el acceso al contador.
     *
     * ReentrantLock permite bloquear secciones de código concretas (más flexibles
     * que 'synchronized'), garantizando exclusión mutua manual con lock()/unlock().
     */
    private static final int ITERACIONES = 1_000_000;
    private static final int VALOR = 10;
    private static int contador = 0;
    private static ReentrantLock lock = new ReentrantLock();


    /*
     * MÉTODO: incrementarContador
     * ---------------------------
     * Este método representa la *sección crítica*, es decir, el fragmento donde
     * varios hilos podrían acceder simultáneamente al mismo recurso (contador).
     *
     * Uso de ReentrantLock:
     * - lock.lock(): bloquea la sección para que solo un hilo pueda entrar.
     * - finally { lock.unlock(); }: garantiza liberar el bloqueo incluso si ocurre
     *   una excepción. Si no se liberara, el resto de hilos quedaría bloqueado.
     *
     * Parámetro:
     * - num: cantidad que se suma al contador (puede ser negativa para restar).
     */
    private static void incrementarContador(int num) {

        System.out.println("Entrando en incrementarContador");

        // Bloqueo explícito de la sección crítica
        lock.lock();
        try {
            // Operación crítica: modificar el contador compartido
            contador += num;

        } finally {
            // Liberación del bloqueo: asegura que otros hilos puedan entrar
            lock.unlock();
        }

        System.out.println("Saliendo de incrementarContador");
    }

    /*
     * MÉTODO: getContador
     * -------------------
     * Devuelve el valor actual del contador.
     * Aquí no se usa lock() porque solo se está leyendo el valor una vez que
     * todos los hilos han terminado (join garantiza que ya no hay concurrencia).
     */
    public static int getContador() {

        System.out.println("Entrando en getContador");
        System.out.println("Saliendo de getContador");
        return contador;
    }

    public static void main(String[] args) {

        /*
         * BLOQUE 1: CREACIÓN DE THREADS
         * -----------------------------
         * Se crean dos hilos anónimos mediante expresiones lambda:
         * - threadIncrementar: suma VALOR al contador en cada iteración.
         * - threadDecrementar: resta VALOR al contador en cada iteración.
         *
         * Ambos comparten el mismo recurso (contador), por lo que sin lock()
         * habría una condición de carrera.
         */
        Thread threadIncrementar = new Thread(() -> {

            System.out.println("Iniciando ejecución incrementar");

            for (int i = 0; i < ITERACIONES; i++) {
                incrementarContador(VALOR);
            }

            System.out.println("Finalizando ejecución incrementar");
        });

        // Thread para decrementar el valor de la variable contador
        Thread threadDecrementar = new Thread(() -> {

            System.out.println("Iniciando ejecución decrementar");

            for (int i = 0; i < ITERACIONES; i++) {
                incrementarContador(-VALOR);
            }

            System.out.println("Finalizando ejecución decrementar");
        });


        /*
         * BLOQUE 2: EJECUCIÓN CONCURRENTE
         * --------------------------------
         * Se inician ambos hilos a la vez.
         * Los métodos run() de ambos ejecutan la misma función crítica
         * (incrementarContador), protegida por el ReentrantLock.
         */
        threadIncrementar.start();
        threadDecrementar.start();


        /*
         * BLOQUE 3: SINCRONIZACIÓN FINAL
         * -------------------------------
         * Se usa join() para esperar a que ambos hilos terminen antes de
         * continuar. Esto garantiza que getContador() se ejecute solo cuando
         * ya no haya operaciones concurrentes en curso.
         */
        try {
            // join bloquea el hilo principal hasta que cada hilo termine
            threadIncrementar.join();
            threadDecrementar.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        /*
         * BLOQUE 4: RESULTADO FINAL
         * -------------------------
         * Se imprime el valor final del contador, que debería ser 0 si
         * el lock ha funcionado correctamente (ya que los incrementos y
         * decrementos se cancelan mutuamente).
         */
        System.out.println("El valor final de contador es: " + getContador());
    }
}
