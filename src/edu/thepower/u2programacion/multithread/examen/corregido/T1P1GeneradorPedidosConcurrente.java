package edu.thepower.u2programacion.multithread.examen.corregido;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/* ──────────────────────────────────────────────────────────────────────────────
   COMENTARIO GENERAL DEL PROGRAMA
   ------------------------------------------------------------------------------
   Este programa simula un “generador de pedidos” concurrente:

   - Crea 10 hilos (trabajadores) y cada hilo registra 10 pedidos (total esperado: 100).
   - Cada pedido tiene: ID único secuencial (atómico), cliente (Cliente-0..9) y fecha.
   - Se guarda un histórico de pedidos y un contador de pedidos por cliente.
   - Al finalizar, se muestran: todos los pedidos, total, y el desglose por cliente,
     comprobando que la suma por cliente coincide con el total.

   Puntos clave de concurrencia:
   - ID único con AtomicInteger → evita colisiones entre hilos.
   - Histórico (ArrayList) protegido con synchronized → escritura segura.
   - Contador por cliente con ConcurrentHashMap<String, AtomicInteger> → updates atómicos.
   ──────────────────────────────────────────────────────────────────────────────
*/

/* Top-level class no utilizada en el resto del fichero.
   Se puede eliminar sin afectar al programa; dejarla puede confundir porque
   más abajo existe otra clase ‘Pedido’ (anidada) que es la que realmente se usa. */
class Pedido{
    private int id;
    private String cliente;
    private long fechaMs;
}

public class T1P1GeneradorPedidosConcurrente {

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: MODELO DE DOMINIO DEL PEDIDO (clase interna)
       ----------------------------------------------------------------
       Esta ES la clase que realmente se usa en el programa (no la de arriba).
       Diseñada para ser thread-safe en la generación del identificador.
       ───────────────────────────────────────────────────────────── */
    static class Pedido{

        // Generador de IDs global y seguro para hilos:
        // AtomicInteger garantiza que cada incremento es atómico (1,2,3,... sin repetición).
        private static AtomicInteger generadorId;

        // Formateador de fecha compartido: se usa solo para toString() al final.
        private static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        // Campos de instancia del pedido: datos “propios” de cada pedido.
        private String id;        // ID secuencial único, almacenado como String para formateo cómodo.
        private String cliente;   // “Cliente-<n>”, n entre 0 y 9.
        private long fechaMs;     // Momento de creación en milisegundos (System.currentTimeMillis).

        // Bloque estático: se ejecuta una sola vez al cargar la clase.
        // Aquí inicializamos el contador atómico de IDs.
        static {
            generadorId = new AtomicInteger(0);
        }

        // Constructor: crea un pedido con cliente dado y asigna fecha e ID.
        public Pedido(String cliente) {
            // incrementAndGet() → operación atómica; evita IDs duplicados si varios hilos construyen a la vez.
            this.id = String.valueOf(generadorId.incrementAndGet());
            this.cliente = cliente;
            this.fechaMs = System.currentTimeMillis(); // fecha de creación (se usa luego para formatear).
        }

        // Representación legible del pedido con fecha formateada (para el informe final).
        @Override
        public String toString(){
            return "| ID Pedido: " + id + " | Cliente: " + cliente + " | Fecha: " + sdf.format(fechaMs) + " |";
        }
    }

    public static void main(String[] args) {

        /* ─────────────────────────────────────────────────────────
           BLOQUE 2: PARÁMETROS DE LA PRUEBA Y ESTRUCTURAS COMPARTIDAS
           ----------------------------------------------------------------
           Explican “qué problema resuelve” cada variable:
           - MAXTHREADS: cuántos hilos simulan usuarios/procesos.
           - MAXPEDIDOS: cuántos pedidos genera cada hilo.
           - random: asignar clientes aleatorios Cliente-0..9.
           - pedidos: histórico global (lista compartida) → requiere sincronización al escribir.
           - threads: para poder join() y esperar a todos.
           - pedidosPorCliente: desglose por cliente, con updates atómicos.
           ───────────────────────────────────────────────────────── */
        final int MAXTHREADS = 10;     // Nº de hilos "usuarios" concurrentes (dominio: paralelismo del sistema).
        final int MAXPEDIDOS = 10;     // Nº de pedidos que emite cada hilo (dominio: carga de trabajo por hilo).
        Random random = new Random();  // Origen pseudoaleatorio para asignar clientes (se usa solo para elegir “Cliente-n”).

        List<Pedido> pedidos = new ArrayList<>();            // Histórico general. No es thread-safe → sincronizaremos en el add.
        List<Thread> threads = new ArrayList<>();            // Para guardar referencias a los hilos y poder hacer join().
        Map<String, AtomicInteger> pedidosPorCliente = new ConcurrentHashMap<>(); // Contador por cliente (seguro para concurrencia).

        /* ─────────────────────────────────────────────────────────
           BLOQUE 3: CREACIÓN Y LANZAMIENTO DE HILOS
           ----------------------------------------------------------------
           Cada hilo genera MAXPEDIDOS pedidos, asignando cliente aleatorio
           y registrando el pedido en el histórico y en el contador por cliente.
           ───────────────────────────────────────────────────────── */
        for (int i = 0; i < MAXTHREADS; i++) {

            Thread hilo = new Thread(() -> {
                for (int j = 0; j < MAXPEDIDOS; j++) {

                    // Construcción del nombre de cliente en el dominio: “Cliente-0..9”
                    String cliente = "Cliente-" + random.nextInt(10);

                    // Creación del pedido (ID único atómico y fecha actual).
                    Pedido pedido = new Pedido(cliente);

                    // Histórico: ArrayList no es thread-safe → protegemos la inserción.
                    synchronized (pedidos){
                        pedidos.add(pedido);
                    }

                    // Contador por cliente: ConcurrentHashMap + AtomicInteger → update atómico sin synchronized explícito.
                    pedidosPorCliente
                            .computeIfAbsent(cliente, k -> new AtomicInteger())
                            .incrementAndGet();
                }
            });

            // Lanzamos el hilo (empieza a generar pedidos en paralelo) y lo guardamos para el join().
            hilo.start();
            threads.add(hilo);
        }

        System.out.println("Todos los Threads están en ejecución");

        /* ─────────────────────────────────────────────────────────
           BLOQUE 4: SINCRONIZACIÓN DE FIN (join)
           ----------------------------------------------------------------
           Esperamos a que TODOS los hilos terminen antes de reportar resultados,
           evitando lecturas inconsistentes.
           ───────────────────────────────────────────────────────── */
        for (Thread thread : threads) {
            try {
                thread.join(); // Bloquea el main hasta que termine cada hilo.
            } catch (InterruptedException e) {
                throw  new RuntimeException(e);
            }
        }
        System.out.println("Todos los Threads han concluido");

        /* ─────────────────────────────────────────────────────────
           BLOQUE 5: INFORME FINAL – HISTÓRICO Y CONSISTENCIA
           ----------------------------------------------------------------
           Se imprime:
           1) El histórico completo de pedidos (con fecha).
           2) El total de pedidos (debe ser MAXTHREADS * MAXPEDIDOS).
           3) El desglose por cliente y la suma de ese desglose, que debe
              coincidir con el total del histórico.
           ───────────────────────────────────────────────────────── */
        System.out.println("\n***************Pedidos realizados por los clientes****************\n");
        for (Pedido pedido : pedidos) {
            System.out.println(pedido);
            System.out.println("-------------------------------------------------------------------");
        }

        // 5.1 Total de pedidos en el histórico (tamaño de la lista)
        System.out.println("\nNúmero total de pedidos: " + pedidos.size());

        // 5.2 Desglose por cliente (y sumar ese desglose para comprobar consistencia)
        int contador = 0;
        System.out.println("\n********Cantidad de pedidos por clientes*******");
        for (String cliente : pedidosPorCliente.keySet()) {
            // Nota: pedidosPorCliente.get(cliente) es un AtomicInteger → .toString() imprime el valor
            System.out.println("| El cliente " + cliente + " ha realizado " + pedidosPorCliente.get(cliente) + " pedidos |");
            contador += pedidosPorCliente.get(cliente).intValue();
        }

        // 5.3 Suma del desglose por cliente
        System.out.println("\nNumero total de pedidos por cliente: " + contador);

        // 5.4 Misma información usando forEach/streams (equivalente, estilo funcional)
        System.out.println("\n****Cantidad de pedidos por clientes .forEach***");
        pedidosPorCliente.forEach((key, value) -> {
            System.out.println("| El cliente " + key + " ha realizado " + value.get() + " pedidos |");
        });

        System.out.println("\nNumero total de pedidos por cliente: " +
                pedidosPorCliente.values().stream().mapToInt(p -> p.get()).sum());
    }
}
