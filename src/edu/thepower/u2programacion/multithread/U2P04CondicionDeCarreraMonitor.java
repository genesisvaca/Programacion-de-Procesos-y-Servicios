package edu.thepower.u2programacion.multithread;

public class U2P04CondicionDeCarreraMonitor {

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: Variables estáticas compartidas
       Objetivo: tener un escenario donde varios hilos acceden a un
       mismo dato y ver cómo lo protegemos con synchronized.
       ───────────────────────────────────────────────────────────── */

    // Constante que indica cuántas iteraciones queremos hacer en la prueba
    // Aquí también se usa como "tiempo de espera" para simular una operación lenta.
    // Tipo/alcance: static final -> es de la clase y no cambia.
    private static final int ITERACIONES = 1_000_000;

    // Valor fijo que usaremos para modificar el contador (sumar o restar).
    // Dominio: representa la cantidad que un hilo quiere aplicar a la variable compartida.
    private static final int VALOR = 10;

    // Variable compartida entre todos los hilos que representa el recurso crítico.
    // Dominio: es el dato que puede sufrir condición de carrera si no se sincroniza.
    // Tipo/alcance: static -> todos los hilos ven la misma instancia.
    private static int contador = 0;


    /* ─────────────────────────────────────────────────────────────
       BLOQUE 2: Métdo sincronizado de escritura/modificación
       Objetivo: modificar de forma segura la variable compartida.
       Importante: al ser static synchronized, el monitor es la CLASE.
       ───────────────────────────────────────────────────────────── */

    // Métdo que incrementa (o decrementa) el contador de forma EXCLUSIVA.
    // synchronized en un métdo estático => solo 1 hilo de esta clase
    // puede estar aquí o en otro métdo estático synchronized al mismo tiempo.
    private static synchronized void incrementarContador (int num){

        // Mensaje de traza para ver cuándo un hilo entra al monitor.
        System.out.println("Entrando en incrementarContador");
        try {
            // Simulamos que esta operación tarda mucho.
            // Esto aumenta la probabilidad de que otros hilos quieran entrar
            // y se queden BLOQUEADOS esperando el monitor.
            Thread.sleep(ITERACIONES);
        } catch (InterruptedException e) {
            // Si el hilo es interrumpido mientras "duerme", lo propagamos como Runtime.
            // En un programa real quizá lo manejarías de otra forma.
            throw new RuntimeException(e);
        }

        // Sección crítica REAL: aquí sí tocamos la variable compartida.
        // Gracias al synchronized, este contador += num es atómico respecto al resto de hilos.
        contador += num;

        // Mensaje de traza para ver cuándo el hilo sale del monitor.
        System.out.println("Saliendo de incrementarContador");
    }


    /* ─────────────────────────────────────────────────────────────
       BLOQUE 3: Métdo sincronizado de lectura
       Objetivo: leer el valor del contador de forma igualmente protegida,
       para que la lectura no vea un estado a medias.
       ───────────────────────────────────────────────────────────── */

    // Métdo de lectura también synchronized para que no haya lecturas
    // mientras otro hilo está escribiendo. Se bloquea sobre el mismo monitor.
    public static synchronized int getContador(){

        // Indicamos que hemos entrado en la sección protegida.
        System.out.println("Entrando en getContador");
        try {
            // Igual que antes, simulamos operación lenta.
            // Esto es útil para demostrar que mientras un hilo está leyendo,
            // otro no puede entrar a escribir porque el monitor está ocupado.
            Thread.sleep(ITERACIONES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Indicamos que vamos a salir del métdo sincronizado.
        System.out.println("Saliendo de getContador");

        // Devolvemos el valor ACTUAL del contador, ya seguro.
        return contador;
    }


    public static void main(String[] args) {

        /* ─────────────────────────────────────────────────────────
           BLOQUE 4 (COMENTADO): Escenario completo de prueba
           Objetivo: crear dos hilos que compiten por la misma variable:
             - uno suma VALOR muchas veces
             - otro resta VALOR muchas veces
           Ambos usan el MISMO métdo sincronizado -> no hay condición de carrera.
           ───────────────────────────────────────────────────────── */

        /*
        Thread threadIncrementar = new Thread( ()-> {

            // Traza para saber que este hilo ha empezado su trabajo.
            System.out.println("Iniciando ejecución incrementar");

            // Bucle que simula muchas operaciones concurrentes sobre la variable.
            // Si este métdo NO fuera synchronized, aquí habría condición de carrera.
            for (int i = 0; i < ITERACIONES; i++) {
                // Llamamos al métdo que modifica el contador de forma segura.
                incrementarContador(VALOR);
            }

            // Traza de fin de trabajo de este hilo.
            System.out.println("Finalizando ejecución incrementar");
        });

        // Thread para decrementar el valor de la variable contador en "ITERACIONES" veces
        Thread threadDecrementar = new Thread( ()-> {
            System.out.println("Iniciando ejecución decrementar");
            for (int i = 0; i < ITERACIONES; i++) {
                // Reutilizamos el mismo métdo, pero pasándole un número negativo;
                // así demostramos que un mismo punto crítico sirve para sumar y restar.
                incrementarContador(-VALOR);
            }

            System.out.println("Finalizando ejecución decrementar");

        });

        // Ponemos en marcha los dos hilos para que compitan por el monitor.
        threadIncrementar.start();
        threadDecrementar.start();

        try{
            // join() hace que el hilo principal espere a que acaben los dos hilos,
            // así no imprimimos el resultado antes de tiempo.
            threadIncrementar.join();
            threadDecrementar.join();
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        // Al final, leemos el valor del contador con el métdo sincronizado
        // para asegurarnos de que nadie más lo está modificando en ese momento.
        System.out.println("El valor final de contador es: " + getContador());
        */


        /* ─────────────────────────────────────────────────────────
           BLOQUE 5: Versión mínima / demo de acceso
           Objetivo: mostrar cómo crear hilos que llaman a los métodos
           sincronizados, sin el bucle grande.
           NOTA: aquí faltan los .start(), así que realmente no se ejecutan.
           ───────────────────────────────────────────────────────── */

        // Hilo que va a intentar modificar el contador sumando VALOR.
        // Dominio: sería el "productor" de cambios.
        Thread accesoIncrementarContador = new Thread(()-> {
            incrementarContador(VALOR);
        });

        // Hilo que va a intentar leer el contador.
        // Dominio: sería el "consumidor" de datos.
        Thread accesoGetContador = new Thread(()->{
            getContador();
        });

        // NOTA IMPORTANTE PARA EL EXAMEN:
        // Aquí no se hace accesoIncrementarContador.start() ni accesoGetContador.start(),
        // así que tal como está el código, los hilos NO se ejecutan.
        // Esto es típico de un ejemplo de clase donde el profe quiere enseñar
        // solo la parte de sincronización, no la ejecución real.
    }

}
