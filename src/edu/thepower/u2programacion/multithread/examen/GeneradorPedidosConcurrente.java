package edu.thepower.u2programacion.multithread.examen;
/**
 * 1. Generador de pedidos
 * Puntos: 7
 * Objetivo
 * Una empresa desea implementar un módulo para registrar pedidos de forma
 * concurrente.
 * Este módulo será utilizado por varios hilos que simulan distintos procesos o usuarios
 * generando pedidos de manera simultánea sobre un mismo servicio compartido.
 * Cada pedido deberá tener: - - -
 * Un identificador único.
 * Nombre del cliente.
 * Fecha asociada al momento de creación del pedido.
 * Además, el sistema debe mantener: - -
 * Tareas
 * Un registro histórico de todos los pedidos.
 * Un contador agregado por cliente que indique cuántos pedidos ha realizado cada
 * cliente.
 * El programa cumplirá con los siguientes propósitos:
 * a) Registrar pedidos concurrentemente.
 * o Cada pedido debe disponer de un identificador numérico secuencial único,
 * sin repeticiones.
 * o Varios hilos podrán solicitar nuevos identificadores al mismo tiempo.
 * o Cada pedido generado por un hilo debe almacenarse en una estructura que
 * actúe como registro general (histórico de pedidos).
 * o Cada registro de un pedido debe conservar toda su información, es decir,
 * identificador, nombre del cliente y fecha de realización.
 * b) Contabilizar el número de pedidos por cliente.
 * o Se debe mantener una estructura adicional que contabilice, para cada
 * cliente, cuántos pedidos ha generado.
 * o Al finalizar la ejecución, la suma total de pedidos registrados por cliente
 * debe coincidir con el número de pedidos generados.
 * c) Ejecutar múltiples hilos simultáneamente.
 * o El programa debe crear 10 hilos y cada uno de ellos registrará 10 pedidos.
 * o Todos los hilos trabajarán sobre el mismo servicio compartido.
 * d) Comprobar la consistencia de los resultados.
 * o Al finalizar la ejecución, deberá mostrarse la siguiente información:
 * 1. Listado de todos y cada uno de los pedidos, incluyendo su
 * información: identificador, nombre cliente y fecha (formato aa-MM
 * yy HH:mm:ss).
 * 2. Número total de pedidos.
 * 3. Listado de todos los clientes junto con el número de pedidos
 * generados/realizados.
 * 4. Número total de pedidos realizados por todos los clientes, a partir
 * de la suma de los pedidos realizados por cada uno.
 * o El número total de pedidos registrados deberá coincidir con la suma de los
 * pedidos por cliente.
 * Observaciones
 * Implementar el código de manera que el programa funcione correctamente bajo
 * condiciones de concurrencia, garantizando la consistencia de los datos y evitando:
 * • Condiciones de carrera.
 * • Lecturas o escrituras inconsistentes.
 * • Problemas de visibilidad o sincronización entre hilos.
 * Notas
 * 1. El nombre de cada cliente tendrá el formato “Cliente-<num>”, donde <num> será
 * un valor numérico, generado aleatoriamente, comprendido entre 0 y 9. Por ejemplo:
 * “Cliente-0”, “Cliente-1”,…, “Cliente-9”.
 * 2. La fecha actual, en milisegundos, se puede obtener a través del comando
 * System.currentTimeMillis()
 * 3. Para convertir una fecha en milisegundos a formato “dd/MM/aa HH:mm:ss” utilizar
 * el siguiente código:
 * SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
 * formato.format (fecha);
 * donde fecha es un dato de tipo long que contiene una fecha en milisegundos.
 * Entregables
 * • Archivo fuente .java con el código correspondiente para resolver el ejercicio
 * planteado.
 */

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * PRUEBA PRÁCTICA – PARTE 1
 * Objetivo: registrar pedidos concurrentemente desde varios hilos sobre un
 * servicio compartido, garantizando que:
 *  - los IDs son únicos y secuenciales
 *  - se guarda un histórico de pedidos
 *  - se cuenta cuántos pedidos hace cada cliente
 *  - al final las cuentas cuadran
 */
public class GeneradorPedidosConcurrente {

    /* ─────────────────────────────────────────────────────────
       1. MODELO DE DOMINIO: Pedido
       Representa la información mínima que pide el enunciado.
       ───────────────────────────────────────────────────────── */
    static class Pedido {
        private final int id;          // identificador único, secuencial
        private final String cliente;  // nombre del cliente: "Cliente-x"
        private final long fechaMs;    // fecha en milisegundos (momento creación)

        public Pedido(int id, String cliente, long fechaMs) {
            this.id = id;
            this.cliente = cliente;
            this.fechaMs = fechaMs;
        }

        public int getId() {
            return id;
        }

        public String getCliente() {
            return cliente;
        }

        public long getFechaMs() {
            return fechaMs;
        }

        // Para mostrar el pedido con la fecha en el formato que pide el enunciado.
        public String toString() {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            String fechaFormateada = formato.format(new Date(fechaMs));
            return "Pedido{id=" + id +
                    ", cliente='" + cliente + '\'' +
                    ", fecha=" + fechaFormateada +
                    '}';
        }
    }

    /* ─────────────────────────────────────────────────────────
       2. SERVICIO COMPARTIDO DE PEDIDOS
       Esta es la "pieza" importante del ejercicio.
       Todos los hilos van a usar LA MISMA instancia de este servicio.
       Aquí es donde hay que evitar la condición de carrera.
       ───────────────────────────────────────────────────────── */
    static class ServicioPedidos {

        // Generador de IDs seguro para hilos.
        // AtomicInteger garantiza que cada incremento es atómico → no hay IDs duplicados.
        private final AtomicInteger generadorIds = new AtomicInteger(0);

        // Registro histórico de todos los pedidos.
        // Usamos List normal pero protegida con synchronized al insertar.
        // También se podría usar una colección concurrente, pero así
        // se ve claramente la sincronización.
        private final List<Pedido> historico = new ArrayList<>();

        // Contador de pedidos por cliente.
        // ConcurrentHashMap → varios hilos pueden actualizar a la vez.
        // value = AtomicInteger para poder incrementar de forma atómica.
        private final Map<String, AtomicInteger> pedidosPorCliente = new ConcurrentHashMap<>();

        // Random para asignar clientes "Cliente-0"..."Cliente-9"
        private final Random random = new Random();

        /**
         * Método principal del servicio: crea un nuevo pedido.
         * Este método es el que van a llamar los hilos.
         */
        public void registrarPedido() {
            // 1. Generar un id único y secuencial
            int id = generadorIds.incrementAndGet(); // 1,2,3,...

            // 2. Elegir un cliente aleatorio del 0 al 9
            String cliente = "Cliente-" + random.nextInt(10);

            // 3. Obtener la fecha actual en ms
            long fecha = System.currentTimeMillis();

            // 4. Crear el pedido
            Pedido pedido = new Pedido(id, cliente, fecha);

            // 5. Guardar en el histórico
            // Como ArrayList no es thread-safe, protegemos el add.
            synchronized (historico) {
                historico.add(pedido);
            }

            // 6. Actualizar el contador de pedidos por cliente
            // computeIfAbsent → si no existe el cliente, lo crea con contador 0
            pedidosPorCliente
                    .computeIfAbsent(cliente, k -> new AtomicInteger(0))
                    .incrementAndGet();
        }

        // Devuelve copia del histórico (para leer al final).
        public List<Pedido> getHistorico() {
            // devolvemos una copia para evitar que desde fuera se toque la lista real
            synchronized (historico) {
                return new ArrayList<>(historico);
            }
        }

        public Map<String, AtomicInteger> getPedidosPorCliente() {
            return pedidosPorCliente;
        }

        public int getTotalPedidos() {
            // el total de pedidos es el tamaño del histórico
            synchronized (historico) {
                return historico.size();
            }
        }
    }

    public static void main(String[] args) {

        // ─────────────────────────────────────────────────────
        // 3. Crear el servicio compartido
        // ─────────────────────────────────────────────────────
        ServicioPedidos servicio = new ServicioPedidos();

        // ─────────────────────────────────────────────────────
        // 4. Crear los 10 hilos
        // Cada hilo va a registrar 10 pedidos → 100 en total.
        // ─────────────────────────────────────────────────────
        List<Thread> hilos = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Thread t = new Thread(() -> {
                for (int j = 0; j < 10; j++) {
                    servicio.registrarPedido();
                    // (Opcional) pequeña pausa para que se mezclen los hilos
                    // try { Thread.sleep(5); } catch (InterruptedException ignored) {}
                }
            }, "Generador-" + i);
            hilos.add(t);
            t.start();
        }

        // ─────────────────────────────────────────────────────
        // 5. Esperar a que todos los hilos terminen
        // ─────────────────────────────────────────────────────
        for (Thread t : hilos) {
            try {
                t.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        // ─────────────────────────────────────────────────────
        // 6. Mostrar resultados y comprobar consistencia
        // ─────────────────────────────────────────────────────

        // 6.1 Listado completo de pedidos
        System.out.println("===== HISTÓRICO DE PEDIDOS =====");
        List<Pedido> listaPedidos = servicio.getHistorico();
        // Orden natural ya será por id porque los generamos secuencialmente,
        // pero si quisieras asegurar puedes ordenarlos aquí.
        for (Pedido p : listaPedidos) {
            System.out.println(p);
        }

        // 6.2 Número total de pedidos
        int total = servicio.getTotalPedidos();
        System.out.println("\nTotal de pedidos registrados: " + total);

        // 6.3 Listado de clientes con sus contadores
        System.out.println("\n===== PEDIDOS POR CLIENTE =====");
        int sumaPorClientes = 0;
        for (Map.Entry<String, AtomicInteger> e : servicio.getPedidosPorCliente().entrySet()) {
            String cliente = e.getKey();
            int cuenta = e.getValue().get();
            System.out.println(cliente + " → " + cuenta + " pedidos");
            sumaPorClientes += cuenta;
        }

        // 6.4 Comprobación de consistencia
        System.out.println("\nSuma de pedidos por cliente: " + sumaPorClientes);
        System.out.println("¿Coincide con el total? " + (sumaPorClientes == total));
    }
}
