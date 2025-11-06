package edu.thepower.u2programacion.multithread;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class U2P07ThreadsPool {
    final static int MAX_POOL_SIZE = 10;

    public static void main(String[] args) {

        // Nos crea un Pool de 10 Threads siempre disponibles para ejecutarse
        ExecutorService pool = Executors.newFixedThreadPool(MAX_POOL_SIZE);

        // Cada vez que haga un submit, saber cuantas veces se ejecuta
        Map<String, AtomicInteger> poolsMaps = new ConcurrentHashMap<>();

        for (int i = 0; i < 50 ; i++) {
            // Se encarga de buscar una tarea que esté libre para ejecutarla
            pool.submit(()->{

                poolsMaps.computeIfAbsent(Thread.currentThread().getName(), k -> new AtomicInteger()).incrementAndGet();
                System.out.println("[ " + Thread.currentThread().getName() + " saludos " + " ]");

            });
        }
        System.out.println("****************************");
        // No acepta más trabajo y termina de forma ordenada
        pool.shutdown();

        try {// Pool termina en 10 segundos
            if (!pool.awaitTermination(10, TimeUnit.SECONDS)) {
                pool.shutdownNow();

            }

        } catch (InterruptedException e) {
            pool.shutdownNow();
        }

        System.out.println("\n********************************************");

        poolsMaps.forEach((key, value) -> {
            System.out.println("[ " + key + " se ha ejecutado: " + value.get() + " veces ]");
        });

        System.out.println("********************************************");

        // Contador para saber el total de threads ejecutandose
        System.out.println("\nTotal de ejecuciones threads: " + poolsMaps.values().stream().mapToInt(v-> v.get()).sum());

    }
}
