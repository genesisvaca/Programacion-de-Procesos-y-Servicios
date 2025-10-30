package edu.thepower.u2programacion.multithread;

import java.util.concurrent.locks.ReentrantLock;

/*
 * ------------------------------------------------------------
 * COMENTARIO GENERAL DEL PROGRAMA
 * ------------------------------------------------------------
 * Objetivo: demostrar cómo evitar una condición de carrera cuando dos hilos
 * modifican una variable compartida (contador) simultáneamente.
 *
 * Estrategia: se protege la "sección crítica" (la actualización de `contador`)
 * con un `ReentrantLock`. Esto garantiza exclusión mutua: a lo sumo un hilo
 * puede ejecutar el bloque protegido al mismo tiempo.
 *
 * Flujo:
 * - Un hilo incrementa el contador ITERACIONES veces (+VALOR).
 * - Otro hilo lo decrementa ITERACIONES veces (-VALOR).
 * - Ambos usan la misma operación segura `incrementarContador(...)` que bloquea
 *   antes de escribir y libera el candado en `finally`.
 * - El hilo principal (main) espera con `join()` y, al final, lee el valor.
 *
 * Resultado esperado: 0 (los incrementos y decrementos se cancelan) siempre que
 * la sincronización sea correcta.
 *
 * Conceptos clave para examen:
 * - Diferencia entre "sección crítica" y "recurso compartido".
 * - Por qué `try/finally` es obligatorio al usar `lock()`.
 * - Por qué `join()` es necesario antes de leer resultados.
 */
public class U2P04CondicionDeCarreraLock {

    /*
     * ESTADO COMPARTIDO (RECURSO CRÍTICO)
     * -----------------------------------
     * Tipo: int (primitivo)
     * Ámbito: estático de clase (visible a todos los métodos estáticos)
     * Propósito de dominio: "contador global" que representa un valor
     * modificado por múltiples hilos de forma concurrente; es precisamente
     * lo que queremos proteger de interleavings peligrosos.
     */
    private static int contador = 0;

    /*
     * MECANISMO DE SINCRONIZACIÓN
     * ---------------------------
     * Tipo: ReentrantLock
     * Ámbito: estático (un candado común para proteger el mismo recurso)
     *
     * Por qué ReentrantLock:
     * - Control explícito de lock/unlock.
     * - Soporta características avanzadas (tryLock, lockInterruptibly, etc.).
     * - Más flexible que `synchronized` cuando necesitamos patrones específicos.
     */
    private static ReentrantLock candado = new ReentrantLock();

    /*
     * BLOQUE LÓGICO: ACTUALIZACIÓN SEGURA DEL CONTADOR
     * ------------------------------------------------
     * Propósito: encapsular la SECCIÓN CRÍTICA; cualquier modificación de `contador`
     * debe pasar por aquí para garantizar exclusión mutua.
     *
     * Diseño:
     * - `lock()` ANTES de tocar el estado para impedir que otro hilo entre a la vez.
     * - `try/finally` para asegurar `unlock()` incluso si hay excepciones.
     * - Trazas de entrada/salida para entender el flujo durante pruebas.
     *
     * Parámetro:
     * - num: delta a aplicar (positivo para sumar; negativo para restar).
     */
    public static void incrementarContador(int num) {

        // Trazabilidad (útil en prácticas para ver el orden de entrada)
        System.out.println("Entrando en incrementarContador");

        // ENTRADA A SECCIÓN CRÍTICA
        candado.lock();
        try {
            // --- SECCIÓN CRÍTICA ---
            // Operación atómica "lógica" (protegida por el candado):
            // leer-modificar-escribir sin interferencia de otros hilos.
            contador += num;
        } finally {
            // SALIDA DE SECCIÓN CRÍTICA: liberamos SIEMPRE el candado
            candado.unlock();
        }

        // Trazabilidad de salida
        System.out.println("Saliendo de incrementarContador");
    }

    /*
     * BLOQUE LÓGICO: LECTURA DEL ESTADO COMPARTIDO
     * --------------------------------------------
     * Propósito: exponer el valor para el reporte final.
     *
     * Nota de diseño:
     * - Aquí no usamos lock porque `main` llama a este método después de `join()`,
     *   cuando ya no hay hilos concurrentes escribiendo.
     * - Si se necesitara leer mientras otros escriben, habría que proteger la
     *   lectura con el mismo candado o usar alternativas (AtomicInteger/volatile).
     */
    public static int getContador() {

        System.out.println("Entrando en getContador");
        System.out.println("Saliendo de getContador");
        return contador;
    }

    /*
     * BLOQUE LÓGICO: ORQUESTACIÓN (main)
     * ----------------------------------
     * Configura dos hilos con cargas opuestas sobre el mismo recurso crítico.
     * Usa `join()` para esperar a que terminen y luego muestra el resultado final.
     *
     * Variables de configuración:
     * - ITERACIONES: tamaño de la carga concurrente (stress test).
     * - VALOR: magnitud del ajuste por iteración (paso).
     */
    public static void main(String[] args) {

        final int ITERACIONES = 1_000_000; // volumen de trabajo concurrente
        final int VALOR = 10;              // paso por iteración

        // Hilo incrementador: simula un productor/aplicador de incrementos seguros
        Thread incrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion incrementador");

            for (int i = 0; i < ITERACIONES; i++) {
                // Operación SEGURA (entra en sección crítica)
                incrementarContador(VALOR);
            }

            System.out.println("Acabando ejecucion incrementador");
        });

        // Hilo decrementador: simula un consumidor/aplicador de decrementos simétricos
        Thread decrementator = new Thread(() -> {

            System.out.println("Iniciando ejecucion decrementador");

            for (int i = 0; i < ITERACIONES; i++) {
                // Misma operación segura, pero con delta negativo
                incrementarContador(-VALOR);
            }

            System.out.println("Acabando ejecucion decrementador");
        });

        // Arranque concurrente de ambos hilos
        incrementator.start();
        decrementator.start();

        // Sincronización final: no seguimos hasta que ambos hayan acabado
        try {
            incrementator.join();
            decrementator.join();
        } catch (InterruptedException e) {
            // Para apuntes: alternativa correcta sería reestablecer el flag de interrupción
            // Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }

        // Reporte final (lectura ya fuera de concurrencia)
        System.out.println("El valor final de contador es: " + getContador());
    }
}

// r.newInt(0,10)
// guardar coches en un List ArrayList
