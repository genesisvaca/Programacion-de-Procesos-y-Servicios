package edu.thepower.u3u4.examen.ejemplos.restaurante;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantHttpServer {

    private static final int PORT = 8080;

    private static final Map<String, Integer> MENU = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        seedMenu();
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("🌐 RestaurantHttpServer en http://localhost:" + PORT);

            while (true) {
                Socket socket = server.accept();
                new Thread(() -> handle(socket)).start();
            }
        } catch (IOException e) {
            System.err.println("❌ Error HTTP: " + e.getMessage());
        }
    }

    private static void seedMenu() {
        MENU.put("pizza", 12);
        MENU.put("pasta", 10);
        MENU.put("ensalada", 8);
        MENU.put("hamburguesa", 11);
        MENU.put("tarta", 6);
        MENU.put("agua", 2);
        MENU.put("refresco", 3);
    }

    private static void handle(Socket socket) {
        try (socket;
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = socket.getOutputStream()) {

            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            // Consumir headers
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
                writeResponse(out, 200, "text/html; charset=utf-8", homeHtml());
                return;
            }

            if (path.equals("/menu")) {
                writeResponse(out, 200, "text/plain; charset=utf-8", menuText());
                return;
            }

            if (path.equals("/order")) {
                Map<String, String> q = parseQuery(query);
                String body = handleOrder(q);
                int status = body.startsWith("ERR") ? 400 : 200;
                writeResponse(out, status, "text/plain; charset=utf-8", body);
                return;
            }

            if (path.equals("/bill")) {
                Map<String, String> q = parseQuery(query);
                String body = handleBill(q);
                int status = body.startsWith("ERR") ? 400 : 200;
                writeResponse(out, status, "text/plain; charset=utf-8", body);
                return;
            }

            writeResponse(out, 404, "text/plain; charset=utf-8", "Not Found");

        } catch (IOException e) {
            System.err.println("⚠️ Cliente HTTP error: " + e.getMessage());
        }
    }

    private static String handleOrder(Map<String, String> q) {
        String item = q.get("item");
        Integer qty = parseIntOpt(q.get("qty"));

        if (item == null || qty == null) return "ERR Uso: /order?item=pizza&qty=2";
        item = item.toLowerCase(Locale.ROOT);

        Integer price = MENU.get(item);
        if (price == null) return "ERR item no existe: " + item;
        if (qty <= 0) return "ERR qty debe ser > 0";

        int total = price * qty;
        return "OK " + item + " x" + qty + " = " + total + "€";
    }

    // /bill?pizza=2&agua=1
    private static String handleBill(Map<String, String> q) {
        if (q.isEmpty()) return "ERR Uso: /bill?pizza=2&agua=1";

        int total = 0;
        StringBuilder sb = new StringBuilder("OK FACTURA\n");

        for (Map.Entry<String, String> e : q.entrySet()) {
            String item = e.getKey().toLowerCase(Locale.ROOT);
            Integer qty = parseIntOpt(e.getValue());
            if (qty == null || qty <= 0) return "ERR cantidad inválida para " + item;

            Integer price = MENU.get(item);
            if (price == null) return "ERR item no existe: " + item;

            int subtotal = price * qty;
            total += subtotal;
            sb.append("- ").append(item).append(" x").append(qty).append(" = ").append(subtotal).append("€\n");
        }

        sb.append("TOTAL = ").append(total).append("€");
        return sb.toString();
    }

    private static String menuText() {
        StringBuilder sb = new StringBuilder();
        MENU.keySet().stream().sorted().forEach(k -> sb.append(k).append(" : ").append(MENU.get(k)).append("€\n"));
        return sb.toString().trim();
    }

    private static String homeHtml() {
        return """
            <html><head><meta charset="utf-8"><title>Restaurante</title></head>
            <body>
              <h1>Restaurante HTTP</h1>
              <p>Endpoints:</p>
              <ul>
                <li><code>/menu</code></li>
                <li><code>/order?item=pizza&qty=2</code></li>
                <li><code>/bill?pizza=2&agua=1</code></li>
              </ul>
            </body></html>
            """;
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

    private static Integer parseIntOpt(String s) {
        if (s == null) return null;
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
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
}
