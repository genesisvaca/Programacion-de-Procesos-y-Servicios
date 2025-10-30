package edu.thepower.u2programacion.multithread.examen;

public class PSPT1P1Corregido extends Thread {

    static class Impresora {
        public synchronized void imprimir(String doc) {
            System.out.println(Thread.currentThread().getName() + " imprime: " +
                    doc);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {

            }
        }
    }

    static class Scanner {
        public synchronized void scan(String doc) {
            System.out.println(Thread.currentThread().getName() + " escanea: " + doc);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored){

            }
        }
    }

    public static void main(String[] args) {
        Impresora impresora = new Impresora();
        Scanner scanner = new Scanner();

        Thread tA = new Thread(() -> {
            synchronized (impresora) {
                System.out.println(Thread.currentThread().getName() + " acceso a "+
                        impresora + ")");
                impresora.imprimir("Documento A");
                synchronized (scanner) {
                    scanner.scan("Documento A");
                }
            }
        }, "Tarea-A");


        Thread tB = new Thread(() -> {
            synchronized (scanner) {
                System.out.println(Thread.currentThread().getName() + " acceso a " +
                        scanner+ ")");
                scanner.scan("Documento B");
                synchronized (impresora) {
                    impresora.imprimir("Documento B");
                }
            }
        }, "Tarea-B");

        tA.start();
        tB.start();
    }
}