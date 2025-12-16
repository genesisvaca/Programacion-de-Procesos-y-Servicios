package edu.thepower.u3u4.examen.ejemplos.biblioteca;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class LibraryHttpServer {

    private static final int PORT = 8080;

    private static final Map<Integer, Book> books = new ConcurrentHashMap<>();
    private static final AtomicInteger idGen = new AtomicInteger(0);

    static class Book {
        final int id;
        final String title;
        final String author;
        final int year;
        private boolean available = true;

        Book(int id, String title, String author, int year) {
            this.id = id;
            this.title = title;
            this.author = author;
            this.year = year;
        }

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
        synchronized boolean isAvailable() { return available; }

        @Override public String toString() {
            return "ID=" + id + " | \"" + title + "\" | " + author + " (" + year + ") | "
                    + (isAvailable() ? "DISPONIBLE" : "PRESTADO");
        }
    }

    public static void main(String[] args) {
        seed();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("🌐 LibraryHttpServer en http://localhost:" + PORT);

            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handle(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Error HTTP: " + e.getMessage());
        }
    }

    private static void seed() {
        add("Clean Code", "Robert C. Martin", 2008);
        add("Effective Java", "Joshua Bloch", 2018);
        add("Java Concurrency in Practice", "Goetz", 2006);
    }

    private static Book add(String title, String author, int year) {
        int id = idGen.incrementAndGet();
        Book b = new Book(id, title, author, year);
        books.put(id, b);
        return b;
    }

    private static void handle(Socket socket) {
        try (socket;
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = socket.getOutputStream()) {

            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            // consumir headers
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) {}

            String[] parts = requestLine.split("\\s+");
            if (parts.length < 2) {
                writeResponse(out, 400, "text/plain; charset=utf-8", "Bad Request");
                return;
            }

            String pathAndQuery = parts[1];
            String path = pathAndQuery;
            String query = "";
            int idx = pathAndQuery.indexOf('?');
            if (idx >= 0) {
                path = pathAndQuery.substring(0, idx);
                query = pathAndQuery.substring(idx + 1);
            }

            if (path.equals("/")) {
                writeResponse(out, 200, "text/html; charset=utf-8", homeHtml());
                return;
            }

            if (path.equals("/books")) {
                writeResponse(out, 200, "text/plain; charset=utf-8", listBooks());
                return;
            }

            if (path.equals("/book")) {
                Map<String, String> q = parseQuery(query);
                writeResponse(out, 200, "text/plain; charset=utf-8", getBook(q));
                return;
            }

            if (path.equals("/add")) {
                Map<String, String> q = parseQuery(query);
                String body = addBookFromQuery(q);
                int status = body.startsWith("ERR") ? 400 : 200;
                writeResponse(out, status, "text/plain; charset=utf-8", body);
                return;
            }

            if (path.equals("/borrow")) {
                Map<String, String> q = parseQuery(query);
                writeResponse(out, 200, "text/plain; charset=utf-8", borrow(q));
                return;
            }

            if (path.equals("/return")) {
                Map<String, String> q = parseQuery(query);
                writeResponse(out, 200, "text/plain; charset=utf-8", giveBack(q));
                return;
            }

            writeResponse(out, 404, "text/plain; charset=utf-8", "Not Found");

        } catch (IOException e) {
            System.err.println("⚠️ Cliente HTTP error: " + e.getMessage());
        }
    }

    private static String listBooks() {
        if (books.isEmpty()) return "Inventario vacío";
        StringBuilder sb = new StringBuilder();
        books.keySet().stream().sorted().forEach(id -> sb.append(books.get(id)).append("\n"));
        return sb.toString().trim();
    }

    private static String getBook(Map<String, String> q) {
        Integer id = parseIntOpt(q.get("id"));
        if (id == null) return "ERR falta id";
        Book b = books.get(id);
        return (b == null) ? "ERR No existe ID=" + id : b.toString();
    }

    private static String addBookFromQuery(Map<String, String> q) {
        String title = q.get("title");
        String author = q.get("author");
        Integer year = parseIntOpt(q.get("year"));
        if (title == null || author == null || year == null) {
            return "ERR Uso: /add?title=El%20Quijote&author=Cervantes&year=1605";
        }
        Book b = add(title, author, year);
        return "OK Añadido: " + b;
    }

    private static String borrow(Map<String, String> q) {
        Integer id = parseIntOpt(q.get("id"));
        if (id == null) return "ERR falta id";
        Book b = books.get(id);
        if (b == null) return "ERR No existe ID=" + id;
        return b.borrow() ? "OK Prestado: " + b : "ERR Ya estaba prestado";
    }

    private static String giveBack(Map<String, String> q) {
        Integer id = parseIntOpt(q.get("id"));
        if (id == null) return "ERR falta id";
        Book b = books.get(id);
        if (b == null) return "ERR No existe ID=" + id;
        return b.giveBack() ? "OK Devuelto: " + b : "ERR Ya estaba disponible";
    }

    private static Integer parseIntOpt(String s) {
        if (s == null) return null;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        if (query == null || query.isBlank()) return map;

        for (String pair : query.split("&")) {
            int idx = pair.indexOf('=');
            if (idx < 0) continue;
            String k = urlDecode(pair.substring(0, idx));
            String v = urlDecode(pair.substring(idx + 1));
            map.put(k, v);
        }
        return map;
    }

    private static String urlDecode(String s) {
        try { return URLDecoder.decode(s, StandardCharsets.UTF_8); }
        catch (Exception e) { return s; }
    }

    private static void writeResponse(OutputStream out, int status, String contentType, String body) throws IOException {
        byte[] data = body.getBytes(StandardCharsets.UTF_8);
        String reason = switch (status) {
            case 200 -> "OK";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            default -> "OK";
        };

        String headers =
                "HTTP/1.1 " + status + " " + reason + "\r\n" +
                        "Content-Type: " + contentType + "\r\n" +
                        "Content-Length: " + data.length + "\r\n" +
                        "Connection: close\r\n" +
                        "\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(data);
        out.flush();
    }

    private static String homeHtml() {
        return """
            <html><head><meta charset="utf-8"><title>Biblioteca</title></head>
            <body>
              <h1>Biblioteca HTTP</h1>
              <ul>
                <li><code>/books</code></li>
                <li><code>/book?id=1</code></li>
                <li><code>/add?title=El%20Quijote&author=Cervantes&year=1605</code></li>
                <li><code>/borrow?id=1</code></li>
                <li><code>/return?id=1</code></li>
              </ul>
            </body></html>
            """;
    }
}
