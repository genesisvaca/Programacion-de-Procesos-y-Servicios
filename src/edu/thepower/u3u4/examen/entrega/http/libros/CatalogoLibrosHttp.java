package edu.thepower.u3u4.examen.entrega.http.libros;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class CatalogoLibrosHttp {

    private static final int PORT = 7070;

    private static final Map<Integer, Libro> libros = new ConcurrentHashMap<>();
    private static final AtomicInteger idGen = new AtomicInteger(0);
    private static final String DOCTYPE1 = """
            <!DOCTYPE html>
            <html lang="es">
            <head>
             <meta charset="UTF-8">
             <title>Lista de libros</title>
            </head>
            <body>
             <h1>Lista de libros</h1>
             <ul>
            """;
    private static final String DOCTYPE2 = """
            </ul>
             <p><a href="/">Volver al inicio</a></p>
            </body>
            </html>
            """;

    static class Libro {
        final int id;
        final String title;
        final String author;
        private boolean available = true;

        Libro(int id, String title, String author) {
            this.id = id;
            this.title = title;
            this.author = author;
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
            return "ID=" + id + " | \"" + title + "\" | " + author + " | "
                    + (isAvailable() ? "DISPONIBLE" : "PRESTADO");
        }
    }

    public static void main(String[] args) {
        seed();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("🌐 CatalogoLibrosHttp en http://localhost:" + PORT);

            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handle(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Error HTTP: " + e.getMessage());
        }
    }

    private static void seed() {
        add("El Quijote", "Miguel de Cervantes",0);
        add("Cien años de soledad", "Gabriel García Márquez",0);
        add("1984", "George Orwell", 0);
        add("Pantaleón y las visitadoras", " Mario Vargas Llosa", 0);
        add("Dune", "Frank Herbert", 0);
    }

    private static Libro add(String title, String author, int year) {
        int id = idGen.incrementAndGet();
        Libro b = new Libro(id, title, author);
        libros.put(id, b);
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

            if (path.equals("/libros")) {
                writeResponse(out, 200, "text/plain; charset=utf-8", listaLibros());
                return;
            }

            if (path.equals("/libros_total")) {
                writeResponse(out, 200, "text/plain; charset=utf-8", listaLibros());
                return;
            }

            if (path.equals("/libro")) {
                Map<String, String> q = parseQuery(query);
                writeResponse(out, 200, "text/plain; charset=utf-8", getLibro(q));
                return;
            }

            if (path.equals("/add")) {
                Map<String, String> q = parseQuery(query);
                String body = addLibroFromQuery(q);
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

    private static String listaLibros() {
        if (libros.isEmpty()) return "Inventario vacío";
        StringBuilder sb = new StringBuilder();
        libros.keySet().stream().sorted().forEach(id -> sb.append(libros.get(id)).append("\n"));
        return DOCTYPE1 + sb.toString().trim() + DOCTYPE2;
    }

    private static String getLibro(Map<String, String> q) {
        Integer id = parseIntOpt(q.get("id"));
        if (id == null) return "ERR falta id";
        Libro b = libros.get(id);
        return (b == null) ? "ERR No existe ID=" + id : b.toString();
    }

    private static String addLibroFromQuery(Map<String, String> q) {
        String title = q.get("title");
        String author = q.get("author");
        Integer year = parseIntOpt(q.get("year"));
        if (title == null || author == null || year == null) {
            return "ERR Uso: /add?title=El%20Quijote&author=Cervantes&year=1605";
        }
        Libro b = add(title, author, year);
        return "OK Añadido: " + b;
    }

    private static String borrow(Map<String, String> q) {
        Integer id = parseIntOpt(q.get("id"));
        if (id == null) return "ERR falta id";
        Libro b = libros.get(id);
        if (b == null) return "ERR No existe ID=" + id;
        return b.borrow() ? "OK Prestado: " + b : "ERR Ya estaba prestado";
    }

    private static String giveBack(Map<String, String> q) {
        Integer id = parseIntOpt(q.get("id"));
        if (id == null) return "ERR falta id";
        Libro b = libros.get(id);
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
                <!DOCTYPE html>
                              <html lang="es">
                              <head>
                               <title>Catálogo de libros</title>
                              </head>
                              <body>
                               <h1>Catálogo de libros</h1>
                               <p>Opciones disponibles:</p>
                               <ul>
                               <li><a href="/libros">Ver lista completa de libros</a></li>
                               <li><a href="/libros_total">Número total de libros</a></li>
                               </ul>
                              </body>
                              </html>
            """;
    }
}
