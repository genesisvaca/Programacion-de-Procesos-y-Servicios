package edu.thepower.u2programacion.multithread;

/*
 * ------------------------------------------------------------
 * COMENTARIO GENERAL DEL PROGRAMA
 * ------------------------------------------------------------
 * Este programa demuestra dos patrones de "hilo durmiente" (Sleeping Thread)
 * y cómo despertarlos usando interrupciones:
 *
 * 1) Bloqueo explícito con Thread.sleep(...): el hilo queda bloqueado y despierta
 *    cuando recibe una interrupción (InterruptedException).
 * 2) Bucle que corre hasta que el hilo es marcado como interrumpido: se usa
 *    Thread.interrupted() como condición de parada (al recibir interrupt(), sale).
 *
 * El hilo principal (main) crea un hilo "durmiente", imprime su propio progreso,
 * duerme 5 segundos, lo interrumpe (lo "despierta" del sleep), vuelve a dormir
 * otros 5 segundos y lo interrumpe otra vez para que salga del bucle y termine.
 *
 * Puntos clave para estudiar:
 * - interrupt() sobre un hilo en sleep lanza InterruptedException y limpia el flag.
 * - Thread.interrupted() consulta y LIMPIA el flag de interrupción (método estático).
 * - Si quieres que el nombre del hilo sea correcto en cada hilo, no lo caches en una
 *   constante estática al cargar la clase; usa Thread.currentThread().getName() en tiempo real.
 */
public class U2P03SleepingThreads implements Runnable{

    /*
     * NOMBRETHREAD (String, static final)
     * -----------------------------------
     * Objetivo: prefijo de log para identificar el hilo que escribe.
     * Importante: se inicializa al cargar la clase con el nombre DEL HILO QUE CARGA LA CLASE
     * (normalmente "main"). Por tanto, usar esta constante dentro de otros hilos imprimirá
     * el nombre del hilo "main", no del hilo actual, lo cual puede inducir a confusión.
     * En aplicaciones reales, es preferible construir el prefijo dinámicamente dentro de run()
     * con Thread.currentThread().getName().
     */
    private static final String NOMBRETHREAD = "["+ Thread.currentThread().getName()+ "]";;

    // Sleeping Thread 1
    @Override
    public void run(){

        // LOG de inicio de este Runnable (ojo: usa la constante estática comentada arriba)
        System.out.println(NOMBRETHREAD + "Iniciando ejecución");

        try {
            /*
             * BLOQUE 1: Hilo durmiente basado en sleep
             * ----------------------------------------
             * Duerme "mucho tiempo" (Long.MAX_VALUE ms). Este sleep es interrumpible.
             * Cuando otro hilo llama t1.interrupt(), se lanza InterruptedException y
             * el hilo sale del estado bloqueado (sleep).
             */
            Thread.sleep(Long.MAX_VALUE);

        } catch (InterruptedException e){
            /*
             * Al interrumpirse durante sleep:
             * - Se lanza InterruptedException.
             * - El flag de interrupción queda LIMPIO (false) tras la excepción.
             * Este log marca el "primer despertar".
             */
            System.out.println(NOMBRETHREAD + " Despertando 1");

        }

        /*
         * BLOQUE 2: Hilo durmiente basado en bucle y flag de interrupción
         * ---------------------------------------------------------------
         * Alternativa a sleep: un bucle que continúa mientras NO esté interrumpido.
         * Thread.interrupted() devuelve true si el hilo está interrumpido y, además,
         * LIMPIA el flag. Por ello:
         * - Justo después del catch, el flag está limpio -> el bucle entra y sigue corriendo.
         * - Cuando desde main se vuelve a llamar interrupt(), la próxima evaluación
         *   de Thread.interrupted() devolverá true (y limpiará el flag), por lo que
         *   !true = false -> el bucle termina y el hilo continúa.
         *
         * Nota: este bucle está vacío -> es un "busy-wait". En producción, deberías
         * hacer un pequeño sleep o usar mecanismos de espera más eficientes.
         */
        while (!Thread.interrupted()){
            // Busy-wait hasta recibir una segunda interrupción
        }
        System.out.println(NOMBRETHREAD + " Despertando 2");


    }

    public static void main(String[] args) {

        /*
         * BLOQUE 3: Preparación y arranque del hilo durmiente
         * ---------------------------------------------------
         * Se crea un Thread con una instancia de U2P03SleepingThreads y un nombre
         * descriptivo para los logs. Se inicia inmediatamente.
         */
        Thread t1 = new Thread(new U2P03SleepingThreads(), "Sleeping Thread");
        t1.start();


        // Log del hilo principal (usando la misma constante estática: mostrará [main])
        System.out.println(NOMBRETHREAD + "Iniciando ejecución");

        /*
         * BLOQUE 4: Ciclo de pausas e interrupciones desde el hilo principal
         * ------------------------------------------------------------------
         * El main duerme 5 segundos y luego interrumpe al hilo t1. Repite el proceso
         * dos veces. Efectos:
         * - Primera interrupt(): despierta del sleep (lanza InterruptedException)
         *   -> imprime "Despertando 1".
         * - Segunda interrupt(): hace que el bucle while(!Thread.interrupted()) salga
         *   -> imprime "Despertando 2" y el hilo termina.
         *
         * El rango (i = 3; i < 5) da exactamente 2 iteraciones (3 y 4); es arbitrario,
         * pero suficiente para demostrar ambos "despertares".
         */
        for (int i = 3; i < 5 ; i++) {

            System.out.println(NOMBRETHREAD + " - Sleeping Thread 5 sec");
            try {
                Thread.sleep(5000); // Pausa del hilo principal antes de interrumpir
            } catch (InterruptedException e) {
                // Si interrumpen al hilo principal, aquí propagan como RuntimeException
                throw new RuntimeException(e);
            }
            System.out.println(NOMBRETHREAD + " - Despertando al thread durmiente");
            t1.interrupt(); // Señal de interrupción al hilo t1
        }
    }
}
