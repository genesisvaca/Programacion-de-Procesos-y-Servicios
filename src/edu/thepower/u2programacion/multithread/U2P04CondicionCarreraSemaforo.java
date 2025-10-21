package edu.thepower.u2programacion.multithread;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class U2P04CondicionCarreraSemaforo implements Runnable{

    private static long tiempoPrueba = System.currentTimeMillis() + 100;

    // Permite varios Thread a la vez utilizar de forma concurrente varios recursos
    private static Semaphore semaphore = new Semaphore(5, true);
    private static AtomicInteger contador = new AtomicInteger();
    private static final int MAXTHREADS = 5;
    // ConcurrentHashMap garantiza la exclusión mutua es una clase ThreadSafe
    private static Map<String, Integer> map = new ConcurrentHashMap<>();

    @Override
    public void run() {

        String nombre = "[" + Thread.currentThread().getName() + "]";

        while (System.currentTimeMillis() < tiempoPrueba){

            try {
                semaphore.acquire();
                map.put(nombre, map.getOrDefault(nombre, 0) + 1);
                System.out.println(nombre + " Valor insertado en el mapa: " + map.get(nombre));

                // ++ de AtomicInteger
                System.out.println("Adquirido semáforo número: " + contador.incrementAndGet());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            if(contador.get() > MAXTHREADS){
                throw new RuntimeException("Semáforo sobrepasado");
            }

            contador.decrementAndGet();
            // .release() (Soltar el semáforo)
            semaphore.release();
            System.out.println(nombre + " Semáforo liberado");
        }

    }

    public static void main(String[] args) {

        List<Thread> list = new ArrayList<>();

        for (int i = 0 ; i < 10; i++){

            list.add(new Thread(new U2P04CondicionCarreraSemaforo(), "Thread_" + i));
            list.get(i).start();
        }

        // Añadir los .join
        for (Thread h : list){
            try {
                h.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("**********Uso del semáforo por los Threads**********");

        for (String m: map.keySet()){

            System.out.println("El thread " + m + " ha usado el semáforo" + map.get(m));
        }
    }
}
