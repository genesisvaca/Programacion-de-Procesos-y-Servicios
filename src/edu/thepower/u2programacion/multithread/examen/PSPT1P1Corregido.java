package edu.thepower.u2programacion.multithread.examen;

public class PSPT1P1Corregido {

    // Reutilizo las mismas clases internas para no repetir código
    static class Impresora {
        public synchronized void imprimir(String doc) {
            System.out.println(Thread.currentThread().getName() + " imprime: " + doc);
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
    }

    static class Scanner {
        public synchronized void scan(String doc) {
            System.out.println(Thread.currentThread().getName() + " escanea: " + doc);
            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }
    }

    public static void main(String[] args) {
        Impresora impresora = new Impresora();
        Scanner scanner = new Scanner();

        // 📌 Definimos un orden de bloqueo común para TODOS:
        // 1º bloquear impresora
        // 2º bloquear scanner
        // Así evito que un hilo bloquee scanner y otro impresora al revés.

        Thread tA = new Thread(() -> {
            synchronized (impresora) {
                System.out.println(Thread.currentThread().getName() + " acceso a impresora...");
                impresora.imprimir("Documento A");

                synchronized (scanner) {
                    scanner.scan("Documento A");
                }
            }
        }, "Tarea-A");

        Thread tB = new Thread(() -> {
            // 👀 ANTES: este hilo bloqueaba primero scanner
            // 👇 AHORA: lo cambiamos para que también bloquee primero impresora
            synchronized (impresora) {
                System.out.println(Thread.currentThread().getName() + " acceso a impresora...");
                // aunque esta tarea “lógicamente” quiera escanear primero,
                // a nivel de bloqueo seguimos el mismo orden para evitar deadlock
                synchronized (scanner) {
                    // aquí respetamos el flujo real de la tarea
                    scanner.scan("Documento B");
                    impresora.imprimir("Documento B");
                }
            }
        }, "Tarea-B");

        tA.start();
        tB.start();
    }


}