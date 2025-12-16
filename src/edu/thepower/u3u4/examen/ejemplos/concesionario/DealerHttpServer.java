package edu.thepower.u3u4.examen.ejemplos.concesionario;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DealerHttpServer {

    private static final int PORT = 8080;

    private static final Map<Integer, Car> inventory = new ConcurrentHashMap<>();
    private static final AtomicInteger idGen = new AtomicInteger(0);

    static class Car {
        final int id;
        final String brand;
        final String model;
        final int price;

        Car(int id, String brand, String model, int price) {
            this.id = id;
            this.brand = brand;
            this.model = model;
            this.price = price;
        }

        @Override public String toString() {
            return "ID=" + id + " | " + brand + " " + model + " | " + price + "€";
        }
    }

    public static void main(String[] args) {
        seed();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("🌐 DealerHttpServer en http://localhost:" + PORT);

            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Error HTTP: " + e.getMessage());
        }
    }

    private static void seed() {
        addCar("Toyota", "Corolla", 14500);
        addCar("SEAT", "Ibiza", 11900);
        addCar("BMW", "Serie1", 18900);
    }

    private static Car addCar(String brand, String model, int price) {
        int id = idGen.incrementAndGet();
        Car c = new Car(id, brand, model, price);
        inventory.put(id, c);
        return c;
    }

    private static void handleClient(Socket socket) {
        try (socket;
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = socket.getOutputStream()) {

            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            // Consumir headers HTTP hasta línea vacía
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
            int qIdx = pathAndQuery.indexOf('?');
            if (qIdx >= 0) {
                path = pathAndQuery.substring(0, qIdx);
                query = pathAndQuery.substring(qIdx + 1);
            }

            if (path.equals("/")) {
                writeResponse(out, 200, "text/html; charset=utf-8", htmlHome());
                return;
            }

            if (path.equals("/cars")) {
                writeResponse(out, 200, "text/plain; charset=utf-8", listCars());
                return;
            }

            if (path.equals("/car")) {
                Map<String, String> q = parseQuery(query);
                writeResponse(out, 200, "text/plain; charset=utf-8", getCar(q));
                return;
            }

            if (path.equals("/buy")) {
                Map<String, String> q = parseQuery(query);
                writeResponse(out, 200, "text/plain; charset=utf-8", buyCar(q));
                return;
            }

            if (path.equals("/add")) {
                Map<String, String> q = parseQuery(query);
                String body = addFromQuery(q);
                int status = body.startsWith("ERR") ? 400 : 200;
                writeResponse(out, status, "text/plain; charset=utf-8", body);
                return;
            }

            writeResponse(out, 404, "text/plain; charset=utf-8", "Not Found");

        } catch (IOException e) {
            System.err.println("⚠️ Cliente HTTP error: " + e.getMessage());
        }
    }

    private static String listCars() {
        if (inventory.isEmpty()) return "Inventario vacío";
        StringBuilder sb = new StringBuilder();
        inventory.keySet().stream().sorted().forEach(id -> sb.append(inventory.get(id)).append("\n"));
        return sb.toString().trim();
    }

    private static String getCar(Map<String, String> q) {
        Integer id = parseIntOpt(q.get("id"));
        if (id == null) return "ERR falta id";
        Car c = inventory.get(id);
        return (c == null) ? "ERR No existe ID=" + id : c.toString();
    }

    private static String buyCar(Map<String, String> q) {
        Integer id = parseIntOpt(q.get("id"));
        if (id == null) return "ERR falta id";
        Car removed = inventory.remove(id);
        return (removed == null) ? "ERR No existe ID=" + id : "OK Comprado: " + removed;
    }

    private static String addFromQuery(Map<String, String> q) {
        String brand = q.get("brand");
        String model = q.get("model");
        Integer price = parseIntOpt(q.get("price"));

        if (brand == null || model == null || price == null) {
            return "ERR Uso: /add?brand=Ford&model=Fiesta&price=9000";
        }
        if (price <= 0) return "ERR price debe ser > 0";

        Car c = addCar(brand, model, price);
        return "OK Añadido: " + c;
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

    private static String htmlHome() {
        return """
            <html><head><meta charset="utf-8"><title>Concesionario</title></head>
            <body>
              <h1>Concesionario HTTP</h1>
              <p>Endpoints:</p>
              <ul>
                <li><code>/cars</code> → lista coches</li>
                <li><code>/car?id=1</code> → detalle</li>
                <li><code>/buy?id=1</code> → comprar</li>
                <li><code>/add?brand=Ford&model=Fiesta&price=9000</code> → añadir</li>
              </ul>
            </body></html>
            """;
    }
}
