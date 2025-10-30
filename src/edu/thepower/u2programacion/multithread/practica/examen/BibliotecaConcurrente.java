package edu.thepower.u2programacion.multithread.practica.examen;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Prestamo {
    private final String usuario;
    private final String libro;

    public Prestamo(String usuario, String libro) {
        this.usuario = usuario;
        this.libro = libro;
    }
    public String getUsuario() { return usuario; }
    public String getLibro() { return libro; }
}

public class BibliotecaConcurrente {

    // lista compartida de préstamos
    private static final List<Prestamo> prestamos =
            Collections.synchronizedList(new ArrayList<>());

    private static final String[] LIBROS = {
            "Java 21", "Redes", "Sistemas", "BBDD", "Concurrencia"
    };

    public static void main(String[] args) {

        int numUsuariosConcurrentes = 8;
        int prestamosPorUsuario = 50;
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numUsuariosConcurrentes; i++) {
            String nombreUsuario = "usuario-" + i;

            Thread t = new Thread(() -> {
                for (int j = 0; j < prestamosPorUsuario; j++) {
                    // escogemos un libro cualquiera
                    String libro = LIBROS[j % LIBROS.length];
                    prestamos.add(new Prestamo(nombreUsuario, libro));
                }
            });
            threads.add(t);
            t.start();
        }

        // esperar a que todos acaben
        for (Thread t : threads) {
            try { t.join(); } catch (InterruptedException e) { throw new RuntimeException(e); }
        }

        // ESTADÍSTICAS simples
        System.out.println("Total de préstamos registrados: " + prestamos.size());

        // por ejemplo, cuántas veces se ha prestado “Java 21”
        long java21 = prestamos.stream()
                .filter(p -> p.getLibro().equals("Java 21"))
                .count();
        System.out.println("Veces que se prestó 'Java 21': " + java21);
    }
}
