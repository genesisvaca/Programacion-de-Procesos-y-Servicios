package edu.thepower.u2programacion.multithread.examen.corregido;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

class Pedido{

    private int id;
    private String cliente;
    private long fechaMs;

}

public class T1P1GeneradorPedidosConcurrente {

    static class Pedido{

        private static AtomicInteger generadorId;
        private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        private String id;
        private String cliente;
        private long fechaMs;

        // Iniciador de clase, se ejecuta una sola vez
        static {
            generadorId = new AtomicInteger(0);
        }

        public Pedido( String cliente) {
            this.id = String.valueOf(generadorId.incrementAndGet());
            this.cliente = cliente;
            this.fechaMs = System.currentTimeMillis();
        }


        @Override
        public String toString(){
            return "| ID Pedido: " + id + " | Cliente: " + cliente + " | Fecha: " + sdf.format(fechaMs) + " |";
        }
    }

    public static void main(String[] args) {
        final int MAXTHREADS = 10;
        final int MAXPEDIDOS = 10;
        Random random = new Random();

        List<Pedido> pedidos = new ArrayList<>();
        List<Thread> threads = new ArrayList<>();
        Map<String, AtomicInteger> pedidosPorCliente= new ConcurrentHashMap<>();

        // 1. Declaración y ejecución de los threads
        for (int i = 0; i < MAXTHREADS; i++) {

            Thread hilo = new Thread(() ->{
                for (int j = 0; j < MAXPEDIDOS; j++) {
                    // Crear cliente aleatorio
                    String cliente = "Cliente-" + random.nextInt(10);

                    Pedido pedido = new Pedido(cliente);

                    synchronized (pedidos){
                        pedidos.add(pedido);
                    }
                    // System.out.println(pedido);
                    // Sumamos uno a la cantidad de pedidos que hace el cliente
                    pedidosPorCliente.computeIfAbsent(cliente, k -> new AtomicInteger()).incrementAndGet();
                }
            });
            // Ejecución del hilo y almacenamiento en lista de threads
            hilo.start();
            threads.add(hilo);
        }

        System.out.println("Todos los Threads están en ejecución");

        // 2. Esperar a que los hilos se completen
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw  new RuntimeException(e);
            }
        }
        System.out.println("Todos los Threads han concluido");

        // 3. Mostrar estadísticas
        System.out.println("\n***************Pedidos realizados por los clientes****************\n");
        for (Pedido pedido : pedidos) {
            System.out.println(pedido);
            System.out.println("-------------------------------------------------------------------");
        }
        System.out.println("\nNúmero total de pedidos: " + pedidos.size());

        int contador = 0;
        System.out.println("\n********Cantidad de pedidos por clientes*******");
        for (String cliente : pedidosPorCliente.keySet()) {
            System.out.println("| El cliente " + cliente + " ha realizado " + pedidosPorCliente.get(cliente) + " pedidos |");
            contador += pedidosPorCliente.get(cliente).intValue();
        }

        System.out.println("\nNumero total de pedidos por cliente: " + contador);



        System.out.println("\n****Cantidad de pedidos por clientes .forEach***");
        pedidosPorCliente.forEach((key, value) -> {
            System.out.println("| El cliente " + key + " ha realizado " + value.get() + " pedidos |");
        });

        System.out.println("\nNumero total de pedidos por cliente: " + pedidosPorCliente.values().stream().mapToInt(p -> p.get()).sum());



    }
}
