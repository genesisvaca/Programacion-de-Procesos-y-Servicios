package edu.thepower.u2programacion.multithread.examen;

public class PSPT1P2 {

    // Recurso compartido 1
    static class Impresora {
        // synchronized: si un hilo está imprimiendo, otro no puede entrar aquí.
        public synchronized void imprimir(String doc) {
            System.out.println(Thread.currentThread().getName() + " imprime: " + doc);
            try {
                Thread.sleep(50); // simula tiempo de impresión
            } catch (InterruptedException ignored) {
            }
        }
    }

    // Recurso compartido 2
    static class Scanner {
        public synchronized void scan(String doc) {
            System.out.println(Thread.currentThread().getName() + " escanea: " + doc);
            try {
                Thread.sleep(50); // simula tiempo de escaneo
            } catch (InterruptedException ignored){
            }
        }
    }

    public static void main(String[] args) {
        Impresora impresora = new Impresora();
        Scanner scanner = new Scanner();

        // Tarea-A: primero impresora, luego scanner
        Thread tA = new Thread(() -> {
            // 🔴 Aquí se bloquea primero el objeto impresora
            synchronized (impresora) {
                System.out.println(Thread.currentThread().getName() + " acceso a impresora...");
                impresora.imprimir("Documento A");

                // 🔴 Y después intenta bloquear el scanner
                synchronized (scanner) {
                    scanner.scan("Documento A");
                }
            }
        }, "Tarea-A");


        // Tarea-B: primero scanner, luego impresora
        Thread tB = new Thread(() -> {
            // 🔴 Aquí se bloquea primero el objeto scanner
            synchronized (scanner) {
                System.out.println(Thread.currentThread().getName() + " acceso a escáner...");
                scanner.scan("Documento B");

                // 🔴 Y después intenta bloquear la impresora
                synchronized (impresora) {
                    impresora.imprimir("Documento B");
                }
            }
        }, "Tarea-B");

        tA.start();
        tB.start();
    }
}
