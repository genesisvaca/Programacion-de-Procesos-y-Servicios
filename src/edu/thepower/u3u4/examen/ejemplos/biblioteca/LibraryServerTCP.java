package edu.thepower.u3u4.examen.ejemplos.biblioteca;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LibraryServerTCP {

    private static final int PORT = 7070;

    // Inventario global: id -> libro (compartido por todos los clientes)
    private static final Map<Integer, Book> books = new ConcurrentHashMap<>();
    private static final AtomicInteger idGen = new AtomicInteger(0);

    static class Book {
        final int id;
        final String title;
        final String author;
        final int year;

        // Estado mutable: disponible o prestado
        private boolean available = true;

        Book(int id, String title, String author, int year) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.year = year;
        }

        // Importante: sincronizamos sobre el propio libro para evitar carrera en BORROW/RETURN
        synchronized boolean borrow() {
            if (!available) return false;
            available = false;
            return true;
        }

        synchronized boolean giveBack() {
            if (available) return false;
            available = true;
            return true;
        }

        synchronized boolean isAvailable() {
            return available;
        }

        @Override public String toString() {
            return "ID=" + id + " | \"" + title + "\" | " + author + " (" + year + ") | "
                    + (isAvailable() ? "DISPONIBLE" : "PRESTADO");
        }
    }

    public static void main(String[] args) {
        seed();
        System.out.println("📚 LibraryServerTCP escuchando en puerto " + PORT);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket client = server.accept();
                new Thread(new ClientHandler(client)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Error servidor: " + e.getMessage());
        }
    }

    private static void seed() {
        addBook("Clean Code", "Robert C. Martin", 2008);
        addBook("Effective Java", "Joshua Bloch", 2018);
        addBook("Java Concurrency in Practice", "Goetz", 2006);
    }

    private static Book addBook(String title, String author, int year) {
        int id = idGen.incrementAndGet();
        Book b = new Book(id, title, author, year);
        books.put(id, b);
        return b;
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String who = socket.getRemoteSocketAddress().toString();
            System.out.println("🔌 Cliente conectado: " + who);

            try (socket;
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {

                pw.println("Bienvenido a la Biblioteca TCP.");
                pw.println("Comandos: LIST | ADD titulo|autor|anio | GET id | BORROW id | RETURN id | HELP | BYE");

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String resp = handleCommand(line);
                    pw.println(resp);

                    if (resp.startsWith("BYE")) break;
                }

            } catch (IOException e) {
                System.err.println("⚠️ Error con cliente " + who + ": " + e.getMessage());
            } finally {
                System.out.println("🔚 Fin sesión: " + who);
            }
        }

        private String handleCommand(String line) {
            String[] parts = line.split("\\s+", 2);
            String cmd = parts[0].toUpperCase(Locale.ROOT);
            String args = (parts.length == 2) ? parts[1].trim() : "";

            try {
                switch (cmd) {
                    case "HELP":
                        return "LIST | ADD titulo|autor|anio | GET id | BORROW id | RETURN id | BYE";

                    case "LIST":
                        return listBooks();

                    case "ADD":
                        return addCommand(args);

                    case "GET":
                        return getCommand(args);

                    case "BORROW":
                        return borrowCommand(args);

                    case "RETURN":
                        return returnCommand(args);

                    case "BYE":
                        return "BYE ¡Hasta luego!";

                    default:
                        return "ERR Comando no reconocido (usa HELP)";
                }
            } catch (IllegalArgumentException ex) {
                return "ERR " + ex.getMessage();
            }
        }

        private String listBooks() {
            if (books.isEmpty()) return "OK Inventario vacío";
            StringBuilder sb = new StringBuilder("OK LIBROS\n");
            books.keySet().stream().sorted()
                    .forEach(id -> sb.append(books.get(id)).append("\n"));
            return sb.toString().trim();
        }

        private String addCommand(String args) {
            // Formato: titulo|autor|anio
            String[] p = args.split("\\|");
            if (p.length != 3) return "ERR Uso: ADD titulo|autor|anio";
            String title = p[0].trim();
            String author = p[1].trim();
            int year = parseInt(p[2].trim(), "anio");

            if (title.isBlank() || author.isBlank()) return "ERR título/autor no pueden estar vacíos";

            Book b = addBook(title, author, year);
            return "OK Añadido: " + b;
        }

        private String getCommand(String args) {
            int id = parseInt(args, "id");
            Book b = books.get(id);
            return (b == null) ? "ERR No existe ID=" + id : "OK " + b;
        }

        private String borrowCommand(String args) {
            int id = parseInt(args, "id");
            Book b = books.get(id);
            if (b == null) return "ERR No existe ID=" + id;
            boolean ok = b.borrow();
            return ok ? "OK Prestado: " + b : "ERR Ya estaba prestado";
        }

        private String returnCommand(String args) {
            int id = parseInt(args, "id");
            Book b = books.get(id);
            if (b == null) return "ERR No existe ID=" + id;
            boolean ok = b.giveBack();
            return ok ? "OK Devuelto: " + b : "ERR Ya estaba disponible";
        }

        private int parseInt(String s, String field) {
            try {
                return Integer.parseInt(s.trim());
            } catch (Exception e) {
                throw new IllegalArgumentException(field + " inválido: " + s);
            }
        }
    }
}
