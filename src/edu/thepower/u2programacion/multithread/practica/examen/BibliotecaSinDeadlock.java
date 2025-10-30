package edu.thepower.u2programacion.multithread.practica.examen;

class LibroCompartido {
    private int ejemplares;

    public LibroCompartido(int ejemplares) {
        this.ejemplares = ejemplares;
    }

    public void prestar(int n) {
        if (ejemplares >= n) {
            ejemplares -= n;
        }
    }

    public void devolver(int n) {
        ejemplares += n;
    }

    public int getEjemplares() {
        return ejemplares;
    }
}

public class BibliotecaSinDeadlock {

    // mover ejemplares de un libro a otro (p. ej. redistribución entre sucursales)
    public static void mover(LibroCompartido origen, LibroCompartido destino, int n) {
        // orden fijo → no deadlock
        LibroCompartido primero = origen.hashCode() < destino.hashCode() ? origen : destino;
        LibroCompartido segundo = origen.hashCode() < destino.hashCode() ? destino : origen;

        synchronized (primero) {
            synchronized (segundo) {
                origen.prestar(n);     // o "quitar" de origen
                destino.devolver(n);   // o "añadir" a destino
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LibroCompartido l1 = new LibroCompartido(300);
        LibroCompartido l2 = new LibroCompartido(300);

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 500; i++)
                mover(l1, l2, 1);
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 500; i++)
                mover(l2, l1, 2);
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Libro 1: " + l1.getEjemplares());
        System.out.println("Libro 2: " + l2.getEjemplares());
        System.out.println("Total: " + (l1.getEjemplares() + l2.getEjemplares())); // debe mantenerse
    }
}
