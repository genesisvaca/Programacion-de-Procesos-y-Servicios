package edu.thepower.u3u4.examen.ejemplos.calculadora;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class HttpCalcServer {

    private static final int PORT = 8080;

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("🌐 HttpCalcServer en http://localhost:" + PORT);

            while (true) {
                Socket socket = server.accept();
                // Para examen puedes: (A) hilo por cliente o (B) pool
                new Thread(() -> handleClient(socket)).start();
            }

        } catch (IOException e) {
            System.err.println("❌ Error servidor HTTP: " + e.getMessage());
        }
    }

    private static void handleClient(Socket socket) {
        try (socket;
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
             OutputStream out = socket.getOutputStream()) {

            // 1) Leer primera línea: "GET /ruta?query HTTP/1.1"
            String requestLine = br.readLine();
            if (requestLine == null || requestLine.isBlank()) return;

            // 2) Consumir headers hasta línea vacía (HTTP)
            String line;
            while ((line = br.readLine()) != null && !line.isEmpty()) { /* ignoramos headers */ }

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
                writeResponse(out, 200, "text/html; charset=utf-8", htmlHome());
                return;
            }

            if (path.equals("/calc")) {
                Map<String, String> q = parseQuery(query);
                String body = handleCalc(q);
                int status = body.startsWith("ERR") ? 400 : 200;
                writeResponse(out, status, "text/plain; charset=utf-8", body);
                return;
            }

            writeResponse(out, 404, "text/plain; charset=utf-8", "Not Found");

        } catch (IOException e) {
            // En examen: basta con loguear
            System.err.println("⚠️ Cliente HTTP error: " + e.getMessage());
        }
    }

    private static String handleCalc(Map<String, String> q) {
        String op = q.getOrDefault("op", "").toLowerCase(Locale.ROOT);

        try {
            switch (op) {
                case "add": {
                    double a = requireDouble(q, "a");
                    double b = requireDouble(q, "b");
                    return "OK " + (a + b);
                }
                case "sub": {
                    double a = requireDouble(q, "a");
                    double b = requireDouble(q, "b");
                    return "OK " + (a - b);
                }
                case "mul": {
                    double a = requireDouble(q, "a");
                    double b = requireDouble(q, "b");
                    return "OK " + (a * b);
                }
                case "div": {
                    double a = requireDouble(q, "a");
                    double b = requireDouble(q, "b");
                    if (b == 0) return "ERR division por cero";
                    return "OK " + (a / b);
                }
                case "pow": {
                    double a = requireDouble(q, "a");
                    double b = requireDouble(q, "b");
                    return "OK " + Math.pow(a, b);
                }
                case "sqrt": {
                    double a = requireDouble(q, "a");
                    if (a < 0) return "ERR sqrt de negativo";
                    return "OK " + Math.sqrt(a);
                }
                default:
                    return "ERR op inválida. Usa add|sub|mul|div|pow|sqrt";
            }
        } catch (IllegalArgumentException ex) {
            return "ERR " + ex.getMessage();
        }
    }

    private static double requireDouble(Map<String, String> q, String key) {
        String v = q.get(key);
        if (v == null) throw new IllegalArgumentException("falta parámetro: " + key);
        try {
            return Double.parseDouble(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("número inválido en " + key + ": " + v);
        }
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
        try {
            return URLDecoder.decode(s, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return s;
        }
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
            <html>
              <head><meta charset="utf-8"><title>HTTP Calc</title></head>
              <body>
                <h1>HTTP Calc Server</h1>
                <p>Usa el endpoint:</p>
                <pre>/calc?op=add&a=2&b=3</pre>
                <p>Operaciones: add, sub, mul, div, pow, sqrt</p>
                <p>Ejemplo sqrt:</p>
                <pre>/calc?op=sqrt&a=16</pre>
              </body>
            </html>
            """;
    }
}
