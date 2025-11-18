package edu.thepower.u2programacion.multithread;

// ───────────────────────────────────────────────────────────────
// COMENTARIO GENERAL DEL PROGRAMA
// ----------------------------------------------------------------
// Este programa demuestra el uso de **hilos tipo daemon** en Java.
//
// Se crean tres hilos:
//   - t1 y t2 → hilos normales que ejecutan tareas durante 10 segundos.
//   - heartBeat → hilo "daemon" que imprime un "latido" continuo para
//                 indicar que la aplicación sigue viva.
//
// Cuando los hilos normales (t1 y t2) terminan, el hilo daemon se detiene
// automáticamente, ya que los hilos daemon solo viven mientras existan
// hilos no daemon en ejecución.
//
// En contextos reales, un hilo daemon se usa para tareas en segundo plano,
// como monitorización, registros o mantenimiento periódico.
// ───────────────────────────────────────────────────────────────
public class U2P06ServiciosDaemon {

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: CONSTANTE DE TIEMPO MÁXIMO
       ----------------------------------------------------------------
       Se usa para establecer el tiempo total de ejecución del programa.
       Todos los hilos normales se ejecutan hasta que se supera este límite.
       ───────────────────────────────────────────────────────────── */

    // Calcula el momento (en milisegundos desde el inicio del sistema)
    // en el que los hilos deben detenerse: ahora + 10.000 ms (10 segundos).
    final static long TIEMPO_MAXIMO = System.currentTimeMillis() + 10000;


    /* ─────────────────────────────────────────────────────────────
       BLOQUE 2: MÉTODO MAIN
       ----------------------------------------------------------------
       Crea y lanza los tres hilos (t1, t2 y heartBeat), demostrando
       la diferencia entre hilos normales y daemon.
       ───────────────────────────────────────────────────────────── */
    public static void main(String[] args) {

        // ───── HILO 1: ejecuta una tarea cada 0,5 segundos ─────
        Thread t1 = new Thread(() -> {
            // El bucle continúa mientras no se supere el tiempo máximo.
            while (TIEMPO_MAXIMO > System.currentTimeMillis()) {
                System.out.println("Iniciando t1");
                try {
                    // Pausa 500 milisegundos entre ejecuciones (0,5 s).
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // Si el hilo es interrumpido, lanzamos excepción.
                    throw new RuntimeException(e);
                }
            }
        });

        // ───── HILO 2: ejecuta una tarea cada 1 segundo ─────
        Thread t2 = new Thread(() -> {
            while (TIEMPO_MAXIMO > System.currentTimeMillis()) {
                System.out.println("Iniciando t2");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // En este hilo simplemente ignoramos la interrupción.
                }
            }
        });


        /* ─────────────────────────────────────────────────────────
           HILO DAEMON: heartBeat (latido)
           ----------------------------------------------------------------
           Este hilo se ejecuta en bucle infinito, simulando un servicio
           que muestra que el programa “sigue vivo”. Se ejecuta en
           segundo plano, y su ciclo se detendrá automáticamente
           cuando no queden hilos de usuario activos (t1 y t2 terminen).
           ---------------------------------------------------------------- */
        Thread heartBeat = new Thread(() -> {
            while (true) {
                System.out.println("Pum Pum ··> heartBeat");
                try {
                    // Frecuencia del latido: 100 ms (0,1 s)
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // Convierto el hilo heartBeat en un hilo daemon.
        // Los hilos daemon se ejecutan en segundo plano y no impiden
        // que el programa termine. Cuando acaban los hilos principales,
        // el daemon se detiene automáticamente.
        heartBeat.setDaemon(true);

        // ───── Lanzamiento de los hilos ─────
        System.out.println("Inicio ejecución Threads");

        // Inicia el primer hilo (t1)
        t1.start();

        // Ejemplo comentado: cómo cambiar la prioridad del hilo.
        // t1.setPriority(Thread.MIN_PRIORITY);

        // Inicia el segundo hilo (t2)
        t2.start();

        // Inicia el hilo daemon (latido)
        heartBeat.start();

        System.out.println("Threads ejecutándose");
    }
}
