package edu.thepower.u2programacion.multithread;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * ------------------------------------------------------------
 * COMENTARIO GENERAL DEL PROGRAMA
 * ------------------------------------------------------------
 * Demostración de control de concurrencia utilizando:
 * - Semaphore (con equidad) para limitar el número de hilos que acceden simultáneamente
 *   a una sección crítica (máximo 5 a la vez).
 * - AtomicInteger para llevar un conteo seguro (thread-safe) de los permisos adquiridos.
 * - ConcurrentHashMap para registrar, por hilo, cuántas veces ha conseguido entrar
 *   en la sección protegida por el semáforo sin incurrir en condición de carrera.
 *
 * Flujo:
 * - Cada hilo intenta adquirir un permiso (semaphore.acquire()) dentro de un bucle que
 *   dura ~100 ms desde el arranque (tiempoPrueba).
 * - Al entrar, incrementa su contador personal en el mapa y aumenta el contador global atómico.
 * - Comprueba que el contador global no excede el máximo de permisos configurado (defensa).
 * - Libera el semáforo y continúa hasta que se agota el tiempo de prueba.
 * - El main crea 10 hilos, espera a que terminen (join) y resume cuántas veces cada hilo
 *   utilizó el semáforo.
 */
public class U2P04CondicionCarreraSemaforo implements Runnable{

    /*
     * tiempoPrueba (long, estática)
     * -----------------------------
     * Momento (en ms epoch) hasta el cual los hilos seguirán ejecutándose.
     * Se fija a "ahora + 100 ms": la demostración durará ~0,1 segundos.
     * Dominio: delimitar la ventana de ejecución para observar adquisiciones/liberaciones.
     */
    private static long tiempoPrueba = System.currentTimeMillis() + 100;

    /*
     * semaphore (Semaphore)
     * ---------------------
     * Semáforo con 5 permisos y equidad (fair = true) para que los hilos adquieran
     * permisos en orden FIFO aproximado. Limita la concurrencia máxima a 5 hilos
     * dentro de la sección crítica.
     */
    // Permite varios Thread a la vez utilizar de forma concurrente varios recursos
    private static Semaphore semaphore = new Semaphore(5, true);

    /*
     * contador (AtomicInteger)
     * ------------------------
     * Contador global y atómico que refleja cuántos hilos tienen el permiso adquirido
     * en un instante dado. Sirve para validar (por código) que nunca excedemos MAXTHREADS.
     * Atómico porque se incrementa/decrementa concurrentemente.
     */
    private static AtomicInteger contador = new AtomicInteger();

    /*
     * MAXTHREADS (int, estática final)
     * --------------------------------
     * Límite lógico del número de hilos simultáneos permitidos en la sección crítica.
     * Debe coincidir con el número de permisos del semáforo (5).
     */
    private static final int MAXTHREADS = 5;

    /*
     * map (ConcurrentHashMap<String, Integer>)
     * ----------------------------------------
     * Mapa concurrente para registrar, por nombre de hilo, cuántas veces ha pasado
     * por la sección crítica. ConcurrentHashMap evita condiciones de carrera
     * en operaciones concurrentes típicas de lectura/escritura.
     *
     * Nota: el patrón getOrDefault + put no es atómico per se, pero aquí cada hilo
     * usa una clave única (su propio nombre), por lo que no hay contención entre hilos
     * sobre la misma entrada. Alternativa thread-safe y atómica: map.merge(nombre, 1, Integer::sum).
     */
    // ConcurrentHashMap garantiza la exclusión mutua es una clase ThreadSafe
    private static Map<String, Integer> map = new ConcurrentHashMap<>();

    @Override
    public void run() {

        // nombre: identificador del hilo para logging y para su clave en el mapa de uso.
        String nombre = "[" + Thread.currentThread().getName() + "]";

        // Bucle de trabajo: el hilo intenta usar el recurso mientras no se haya agotado la ventana de prueba
        while (System.currentTimeMillis() < tiempoPrueba){

            try {
                // Adquiere un permiso del semáforo; si no hay disponibles, espera de forma justa (FIFO).
                semaphore.acquire();

                // Actualización del registro por hilo (no atómica compuesta, pero sin contención entre hilos por clave)
                map.put(nombre, map.getOrDefault(nombre, 0) + 1);
                System.out.println(nombre + " Valor insertado en el mapa: " + map.get(nombre));

                // ++ de AtomicInteger (thread-safe): refleja cuántos hilos están "dentro"
                System.out.println("Adquirido semáforo número: " + contador.incrementAndGet());

            } catch (InterruptedException e) {
                // En este ejemplo, si interrumpen al hilo lanzamos RuntimeException (propaga y terminaría el hilo)
                throw new RuntimeException(e);
            }

            // Comprobación defensiva: no debe superar el número de permisos del semáforo.
            if(contador.get() > MAXTHREADS){
                throw new RuntimeException("Semáforo sobrepasado");
            }

            // Salida de la sección crítica: decrementa el contador global ...
            contador.decrementAndGet();
            // ... y libera el permiso del semáforo para que otro hilo pueda entrar.
            // .release() (Soltar el semáforo)
            semaphore.release();
            System.out.println(nombre + " Semáforo liberado");
        }

    }

    public static void main(String[] args) {

        /*
         * BLOQUE: CREACIÓN Y ARRANQUE DE HILOS
         * ------------------------------------
         * Crea 10 hilos que compiten por 5 permisos. Algunos esperarán hasta que
         * otro hilo libere un permiso.
         */
        List<Thread> list = new ArrayList<>();

        for (int i = 0 ; i < 10; i++){

            list.add(new Thread(new U2P04CondicionCarreraSemaforo(), "Thread_" + i));
            list.get(i).start();
        }

        /*
         * BLOQUE: SINCRONIZACIÓN (JOIN)
         * -----------------------------
         * Espera a que todos los hilos terminen antes de imprimir el resumen.
         */
        // Añadir los .join
        for (Thread h : list){
            try {
                h.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        /*
         * BLOQUE: RESUMEN FINAL
         * ---------------------
         * Muestra cuántas veces cada hilo consiguió entrar en la sección crítica.
         */
        System.out.println("**********Uso del semáforo por los Threads**********");

        for (String m: map.keySet()){

            System.out.println("El thread " + m + " ha usado el semáforo" + map.get(m));
        }
    }
}
