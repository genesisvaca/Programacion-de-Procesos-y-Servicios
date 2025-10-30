package edu.thepower.u2programacion.multithread;

import java.util.concurrent.atomic.AtomicInteger;

/*
 * ------------------------------------------------------------
 * COMENTARIO GENERAL DEL PROGRAMA
 * ------------------------------------------------------------
 * Este programa demuestra cómo evitar una condición de carrera usando
 * variables atómicas (AtomicInteger) en lugar de bloqueos explícitos
 * como ReentrantLock o 'synchronized'.
 *
 * Objetivo:
 * - Dos hilos (uno incrementador y uno decrementador) modifican la misma
 *   variable 'contador' simultáneamente.
 * - Gracias a AtomicInteger, cada operación de suma/resta se realiza
 *   de forma atómica, evitando inconsistencias sin necesidad de sincronización manual.
 *
 * Resultado esperado:
 * - El valor final del contador debe ser 0, ya que ambos hilos realizan
 *   el mismo número de incrementos y decrementos (+10 y -10 por iteración).
 *
 * Ventaja:
 * - AtomicInteger ofrece operaciones atómicas a nivel de CPU (lock-free),
 *   lo que lo hace más eficiente y simple que usar locks en variables simples.
 */
public class U2P04CondicionDeCarreraAtomicVars {

    /*
     * CONSTANTES DE CONFIGURACIÓN
     * ---------------------------
     * ITERACIONES: número de veces que cada hilo modificará el contador.
     * VALOR: cantidad a sumar o restar por cada iteración.
     *
     * Ambas son constantes para asegurar reproducibilidad del experimento.
     */
    private static final int ITERACIONES = 1_000_000;
    private static final int VALOR = 10;

    /*
     * contador (AtomicInteger)
     * ------------------------
     * Variable compartida entre hilos.
     * Reemplaza a un int normal para evitar condiciones de carrera.
     *
     * AtomicInteger garantiza exclusión mutua *solo sobre esta variable*,
     * sin necesidad de sincronizar manualmente el método.
     *
     * Métodos importantes:
     * - addAndGet(n): suma n al valor actual de forma atómica y devuelve el resultado.
     * - get(): obtiene el valor actual de forma segura (lectura atómica).
     */
    private static AtomicInteger contador = new AtomicInteger(0);


    /*
     * MÉTODO: incrementarContador
     * ---------------------------
     * Aumenta o disminuye el valor de 'contador' según el parámetro recibido.
     * El uso de addAndGet() hace que la operación sea atómica, es decir:
     * no puede ser interrumpida ni mezclada con otra operación concurrente.
     *
     * Parámetro:
     * - num: valor a sumar (positivo o negativo).
     */
    private static void incrementarContador(int num){
        contador.addAndGet(num);
    }

    /*
     * MÉTODO: getContador
     * -------------------
     * Devuelve el valor actual del contador.
     * Uso de get() para lectura atómica (aunque no es estrictamente necesario
     * tras finalizar los hilos).
     */
    public static int getContador(){
        return contador.get();
    }


    public static void main(String[] args) {

        /*
         * BLOQUE 1: CREACIÓN DE THREADS
         * -----------------------------
         * Se crean dos hilos anónimos con expresiones lambda:
         * - threadIncrementar: suma VALOR al contador ITERACIONES veces.
         * - threadDecrementar: resta VALOR al contador ITERACIONES veces.
         *
         * Ambos hilos comparten la misma variable contador (recurso crítico),
         * pero AtomicInteger evita la interferencia sin necesidad de locks.
         */
        Thread threadIncrementar = new Thread(() -> {

            System.out.println("Iniciando ejecución incrementar");

            for (int i = 0; i < ITERACIONES; i++) {
                incrementarContador(VALOR);
            }

            System.out.println("Finalizando ejecución incrementar");
        });

        // Hilo para decrementar el valor de la variable contador
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
         * Se inician ambos hilos a la vez, ejecutando su lógica en paralelo.
         */
        threadIncrementar.start();
        threadDecrementar.start();


        /*
         * BLOQUE 3: SINCRONIZACIÓN FINAL
         * -------------------------------
         * Se usa join() para esperar a que ambos hilos terminen
         * antes de consultar e imprimir el valor final del contador.
         *
         * join() bloquea el hilo principal hasta que el hilo invocado finaliza.
         */
        try {
            threadIncrementar.join();
            threadDecrementar.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


        /*
         * BLOQUE 4: RESULTADO FINAL
         * -------------------------
         * Al finalizar ambos hilos, el valor del contador debe ser 0,
         * ya que se realizaron incrementos y decrementos equivalentes.
         */
        System.out.println("El valor final de contador es: " + getContador());
    }
}
