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
import java.util.*;

class Pedido {

    private int id;
    private String nombreCliente;

    public Pedido(String nombreCliente, int id) {
        this.nombreCliente = nombreCliente;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

}

public class GeneradorPedidos {

    static List<Pedido> pedidos = Collections.synchronizedList(new ArrayList<>());
    static int contador = 0;

    static synchronized int genId(){

        Random rand = new Random();

        return contador = rand.nextInt(0,10);
    }

    private static List<Thread> hilos = new ArrayList<>();


    private static final int CLIENTE = 10;
    private static final int PEDIDOSCLIENTES = 10;
    private static Date fecha = new Date();
    private static SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yy HH:mm:ss");

    public static void main(String[] args) {
        Pedido pedido = new Pedido("Cliente ", genId());

        for(int i = 0; i < CLIENTE; i++){
            Thread thread = new Thread(()->{
               for (int j = 0; j < PEDIDOSCLIENTES; j++){
                   pedidos.add(new Pedido( "Cliente",genId()));
                   System.out.println(Thread.currentThread().getName()+": "+pedido.getId()+" "+pedido.getNombreCliente() + formato.format(fecha));
               }
            });
            hilos.add(thread);
            thread.start();
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        System.out.println("****** Lista de pedidos *******");
        System.out.println("|\tPEDIDO\t|\tFECHA\t\t\t|");
        for( Thread t : hilos){
            System.out.println("| " + t.getName() +  " | " + formato.format(fecha) + " |");
        }



    }

}
