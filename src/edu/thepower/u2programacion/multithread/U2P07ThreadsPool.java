package edu.thepower.u2programacion.multithread;

import java.util.Map;
import java.util.concurrent.*;  // incluye todas las clases usadas: ExecutorService, TimeUnit, etc.
import java.util.concurrent.atomic.AtomicInteger;

public class U2P07ThreadsPool {

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: CONFIGURACIÓN DEL POOL DE HILOS
       ----------------------------------------------------------------
       Un "pool de hilos" (Thread Pool) es un conjunto de hilos que
       permanecen vivos esperando tareas. En lugar de crear un hilo nuevo
       por cada trabajo (costoso), el pool reutiliza los mismos hilos,
       mejorando rendimiento y control de concurrencia.
       ───────────────────────────────────────────────────────────── */

    // Número máximo de hilos simultáneos en el pool.
    // Aquí se fija en 10, pero se podrían ajustar según el hardware.
    final static int MAX_POOL_SIZE = 10;


    /* ─────────────────────────────────────────────────────────────
       BLOQUE 2: MÉTODO PRINCIPAL
       ----------------------------------------------------------------
       El programa crea un pool de 10 hilos y lanza 50 tareas concurrentes.
       Cada tarea imprime su nombre y registra cuántas veces ha sido usada
       por el pool. Al final, muestra el número de ejecuciones por hilo
       y el total global.
       ───────────────────────────────────────────────────────────── */
    public static void main(String[] args) {

        // ───── 2.1 Crear un pool de tamaño fijo ─────
        // Executors.newFixedThreadPool(n) crea un grupo con n hilos.
        // Los hilos quedan disponibles permanentemente hasta que el pool se cierre.
        ExecutorService pool = Executors.newFixedThreadPool(MAX_POOL_SIZE);

        // ───── 2.2 Mapa para registrar cuántas veces se usa cada hilo ─────
        // ConcurrentHashMap: versión thread-safe del HashMap.
        // Key → nombre del hilo, Value → contador atómico de ejecuciones.
        Map<String, AtomicInteger> poolsMaps = new ConcurrentHashMap<>();


        // ───── 2.3 Enviar 50 tareas al pool ─────
        // Aunque haya solo 10 hilos, el pool ejecutará las 50 tareas
        // distribuyéndolas entre los hilos disponibles conforme terminen.
        for (int i = 0; i < 50; i++) {

            // pool.submit() añade una tarea al pool (cada tarea es un Runnable).
            pool.submit(() -> {

                // computeIfAbsent → si el hilo aún no está registrado en el mapa,
                // lo agrega con un contador atómico inicializado a 0.
                // incrementAndGet() → suma 1 de forma atómica (sin synchronized).
                poolsMaps
                        .computeIfAbsent(Thread.currentThread().getName(), k -> new AtomicInteger())
                        .incrementAndGet();

                // Imprime el nombre del hilo que ejecuta la tarea actual.
                System.out.println("[ " + Thread.currentThread().getName() + " saludos ]");

            });
        }

        System.out.println("****************************");

        // ───── 2.4 Cierre del pool ─────
        // shutdown(): indica que no se aceptarán nuevas tareas.
        // Las tareas ya enviadas se completan antes de cerrar.
        pool.shutdown();

        try {
            // awaitTermination espera hasta 10 segundos a que todas las tareas terminen.
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                // Si pasado el tiempo siguen tareas pendientes, se fuerzan a detener.
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            // Si el hilo principal es interrumpido, fuerza el cierre inmediato del pool.
            pool.shutdownNow();
        }

        /* ─────────────────────────────────────────────────────────────
           BLOQUE 3: MOSTRAR RESULTADOS
           ----------------------------------------------------------------
           Tras finalizar todas las tareas, mostramos:
           1. Cuántas veces se ejecutó cada hilo.
           2. El número total de ejecuciones sumando todos los hilos.
           ───────────────────────────────────────────────────────────── */

        System.out.println("\n********************************************");

        // Recorremos el mapa concurrente: clave = hilo, valor = número de ejecuciones.
        poolsMaps.forEach((key, value) -> {
            System.out.println("[ " + key + " se ha ejecutado: " + value.get() + " veces ]");
        });

        System.out.println("********************************************");

        // Calcula el total de tareas ejecutadas sumando todos los contadores.
        // stream() → convierte los valores del mapa en flujo
        // mapToInt(v -> v.get()) → extrae el número de ejecuciones de cada AtomicInteger
        // sum() → los acumula.
        System.out.println("\nTotal de ejecuciones threads: " +
                poolsMaps.values().stream().mapToInt(v -> v.get()).sum());
    }
}
