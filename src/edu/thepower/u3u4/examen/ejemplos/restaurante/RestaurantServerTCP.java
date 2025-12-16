package edu.thepower.u3u4.examen.ejemplos.restaurante;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RestaurantServerTCP {

    private static final int PORT = 6060;

    // "Base de datos" del menú (compartida por todos)
    private static final Map<String, Integer> MENU = new ConcurrentHashMap<>();

    // Estadística global del restaurante (útil para justificar concurrencia)
    private static final AtomicInteger totalPedidosCerrados = new AtomicInteger(0);

    public static void main(String[] args) {
        seedMenu();
        System.out.println("🍽️ RestaurantServerTCP escuchando en puerto " + PORT);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket client = server.accept();
                new Thread(new ClientHandler(client)).start(); // multicliente
            }
        } catch (IOException e) {
            System.err.println("❌ Error servidor: " + e.getMessage());
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

    static class ClientHandler implements Runnable {
        private final Socket socket;

        // Pedido del cliente (estado por conexión/hilo)
        private final Map<String, Integer> pedido = new HashMap<>();

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

                pw.println("Bienvenido al Restaurante TCP.");
                pw.println("Comandos: MENU | ORDER plato cant | TOTAL | CLOSE | HELP | BYE");

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String response = handleCommand(line);
                    pw.println(response);

                    if (response.startsWith("BYE")) break;
                }

            } catch (IOException e) {
                System.err.println("⚠️ Error con cliente " + who + ": " + e.getMessage());
            } finally {
                System.out.println("🔚 Fin sesión: " + who);
            }
        }

        private String handleCommand(String line) {
            String[] parts = line.split("\\s+");
            String cmd = parts[0].toUpperCase(Locale.ROOT);

            switch (cmd) {
                case "HELP":
                    return "MENU | ORDER plato cant | TOTAL | CLOSE | BYE";

                case "MENU":
                    return menuAsText();

                case "ORDER":
                    // ORDER pizza 2
                    if (parts.length != 3) return "ERR Uso: ORDER plato cantidad";
                    String plato = parts[1].toLowerCase(Locale.ROOT);
                    Integer precio = MENU.get(plato);
                    if (precio == null) return "ERR Plato no existe: " + plato;

                    Integer cant = parseIntOpt(parts[2]);
                    if (cant == null || cant <= 0) return "ERR cantidad inválida";

                    // sumamos a pedido local
                    pedido.put(plato, pedido.getOrDefault(plato, 0) + cant);
                    return "OK Añadido: " + plato + " x" + cant;

                case "TOTAL":
                    return "OK Total = " + calcularTotal() + "€";

                case "CLOSE":
                    int total = calcularTotal();
                    pedido.clear();
                    int n = totalPedidosCerrados.incrementAndGet();
                    return "OK Pedido cerrado. Total=" + total + "€ | Pedidos cerrados hoy=" + n;

                case "BYE":
                    return "BYE ¡Gracias!";

                default:
                    return "ERR comando no reconocido (usa HELP)";
            }
        }

        private String menuAsText() {
            StringBuilder sb = new StringBuilder("OK MENU\n");
            MENU.keySet().stream().sorted().forEach(k ->
                    sb.append("- ").append(k).append(" : ").append(MENU.get(k)).append("€\n"));
            return sb.toString().trim();
        }

        private int calcularTotal() {
            int total = 0;
            for (Map.Entry<String, Integer> e : pedido.entrySet()) {
                int precio = MENU.get(e.getKey());
                total += precio * e.getValue();
            }
            return total;
        }

        private Integer parseIntOpt(String s) {
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e) { return null; }
        }
    }
}

