package edu.thepower.u2programacion.multithread.practica.examen;


class AlmacenCoches {
    private int stock;   // nº de coches en este almacén

    public AlmacenCoches(int stock) {
        this.stock = stock;
    }

    public void sacar(int n) {
        if (stock >= n) {
            stock -= n;
        }
    }

    public void meter(int n) {
        stock += n;
    }

    public int getStock() { return stock; }
}

public class ConcesionarioSinDeadlock {

    // TRANSFERIR COCHES ENTRE DOS ALMACENES
    public static void transferir(AlmacenCoches origen, AlmacenCoches destino, int unidades) {
        // ordenar SIEMPRE igual para evitar deadlock
        AlmacenCoches primero = origen.hashCode() < destino.hashCode() ? origen : destino;
        AlmacenCoches segundo = origen.hashCode() < destino.hashCode() ? destino : origen;

        synchronized (primero) {
            synchronized (segundo) {
                origen.sacar(unidades);
                destino.meter(unidades);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        AlmacenCoches a1 = new AlmacenCoches(500);
        AlmacenCoches a2 = new AlmacenCoches(500);

        // hilo 1: mueve coches A1 -> A2
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++)
                transferir(a1, a2, 1);
        });

        // hilo 2: mueve coches A2 -> A1
        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++)
                transferir(a2, a1, 2);
        });

        t1.start();
        t2.start();

        t1.join();
        t2.join();

        System.out.println("Stock A1: " + a1.getStock());
        System.out.println("Stock A2: " + a2.getStock());
        System.out.println("Stock total: " + (a1.getStock() + a2.getStock())); // debe ser 1000
    }
}
