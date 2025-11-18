package edu.thepower.u2programacion.multithread.examen;

public class PSPT1P1Corregido {

    /* ─────────────────────────────────────────────────────────────
       COMENTARIO GENERAL DEL PROGRAMA
       ----------------------------------------------------------------
       Este programa corrige un caso típico de DEADLOCK entre dos recursos
       (Impresora y Scanner). La idea clave es IMPONER UN ORDEN ÚNICO DE
       BLOQUEO para todos los hilos:
         1º se adquiere el lock de 'impresora'
         2º se adquiere el lock de 'scanner'
       Así evitamos que un hilo bloquee A→B y otro B→A simultáneamente.
       ───────────────────────────────────────────────────────────── */

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 1: Clases de dominio (recursos compartidos)
       ----------------------------------------------------------------
       Estas clases representan los recursos críticos a sincronizar.
       Se usan métodos synchronized para simular operaciones exclusivas
       (solo un hilo puede usar cada recurso a la vez).
       ───────────────────────────────────────────────────────────── */

    // Recurso compartido 1: Impresora
    // synchronized en imprimir() → exclusión mutua a nivel de instancia
    static class Impresora {
        public synchronized void imprimir(String doc) {
            System.out.println(Thread.currentThread().getName() + " imprime: " + doc);
            try { Thread.sleep(50); } catch (InterruptedException ignored) {} // Simula trabajo del recurso
        }
    }

    // Recurso compartido 2: Scanner
    // synchronized en scan() → exclusión mutua a nivel de instancia
    static class Scanner {
        public synchronized void scan(String doc) {
            System.out.println(Thread.currentThread().getName() + " escanea: " + doc);
            try { Thread.sleep(50); } catch (InterruptedException ignored) {} // Simula trabajo del recurso
        }
    }

    /* ─────────────────────────────────────────────────────────────
       BLOQUE 2: main – orquestación de hilos y orden de bloqueo
       ----------------------------------------------------------------
       - Se crean instancias de los recursos (impresora, scanner).
       - Se lanzan dos hilos (Tarea-A y Tarea-B).
       - Ambos respetan el MISMO ORDEN DE BLOQUEO: primero impresora,
         luego scanner. Con esto se rompe el ciclo de espera circular
         que causa el deadlock.
       ───────────────────────────────────────────────────────────── */
    public static void main(String[] args) {
        Impresora impresora = new Impresora(); // Recurso 1 del dominio
        Scanner scanner = new Scanner();       // Recurso 2 del dominio

        // 📌 Regla global de sincronización: siempre bloquear en el mismo orden
        //    1) impresora -> 2) scanner

        // Hilo A: primero usa impresora, luego scanner para el mismo documento
        Thread tA = new Thread(() -> {
            // Bloque sincronizado sobre 'impresora': nadie más puede "entrar" en ese lock
            synchronized (impresora) {
                System.out.println(Thread.currentThread().getName() + " acceso a impresora...");
                impresora.imprimir("Documento A"); // Uso exclusivo del recurso 1

                // Manteniendo el lock de impresora, ahora adquirimos el de scanner
                synchronized (scanner) {
                    scanner.scan("Documento A");   // Uso exclusivo del recurso 2
                } // Se libera el lock de scanner aquí
            } // Se libera el lock de impresora aquí
        }, "Tarea-A");

        // Hilo B: aunque “lógicamente” quiera escanear e imprimir en orden distinto,
        //         a nivel de BLOQUEO respeta el mismo orden (impresora -> scanner)
        Thread tB = new Thread(() -> {
            // 👀 Antes el deadlock ocurría porque este hilo bloqueaba primero scanner.
            //    Ahora imponemos el MISMO orden que tA: primero impresora.
            synchronized (impresora) {
                System.out.println(Thread.currentThread().getName() + " acceso a impresora...");
                // Aunque en lógica de negocio se escanee primero, el ORDEN DE BLOQUEO
                // se mantiene para evitar deadlock.
                synchronized (scanner) {
                    // Flujo funcional de la tarea B (ya con ambos locks):
                    scanner.scan("Documento B");
                    impresora.imprimir("Documento B");
                } // Se libera el lock de scanner
            } // Se libera el lock de impresora
        }, "Tarea-B");

        // Lanzamos ambos hilos en paralelo; su cooperación segura depende del orden de bloqueo
        tA.start();
        tB.start();
    }
}
