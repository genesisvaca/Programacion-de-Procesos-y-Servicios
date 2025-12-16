package edu.thepower.u3u4.examen.ejemplos.concesionario;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class DealerServerTCP {

    private static final int PORT = 5050;

    // "Base de datos" en memoria: id -> coche
    private static final Map<Integer, Car> inventory = new ConcurrentHashMap<>();
    private static final AtomicInteger idGen = new AtomicInteger(0);

    // Modelo simple
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
        // Datos iniciales (te ayuda en examen para probar rápido)
        seed();

        System.out.println("🚗 DealerServerTCP escuchando en puerto " + PORT);

        try (ServerSocket server = new ServerSocket(PORT)) {
            while (true) {
                Socket client = server.accept();
                new Thread(new ClientHandler(client)).start(); // multicliente (hilo por cliente)
            }
        } catch (IOException e) {
            System.err.println("❌ Error servidor: " + e.getMessage());
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

                pw.println("Bienvenido al Concesionario TCP.");
                pw.println("Comandos: LIST | ADD marca modelo precio | GET id | BUY id | HELP | BYE");

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String response = handleCommand(line);
                    pw.println(response);

                    if (response.startsWith("BYE")) break;
                }

            } catch (IOException e) {
                System.err.println("⚠️ Cliente " + who + " error: " + e.getMessage());
            } finally {
                System.out.println("🔚 Fin sesión: " + who);
            }
        }

        private String handleCommand(String line) {
            String[] parts = line.split("\\s+");
            String cmd = parts[0].toUpperCase(Locale.ROOT);

            try {
                switch (cmd) {
                    case "HELP":
                        return "LIST | ADD marca modelo precio | GET id | BUY id | BYE";

                    case "LIST": {
                        if (inventory.isEmpty()) return "OK Inventario vacío";
                        StringBuilder sb = new StringBuilder("OK INVENTARIO\n");
                        // Ordenado por id para que se vea “bonito”
                        inventory.keySet().stream().sorted().forEach(id ->
                                sb.append(inventory.get(id)).append("\n"));
                        return sb.toString().trim();
                    }

                    case "ADD": {
                        // ADD marca modelo precio
                        if (parts.length != 4) return "ERR Uso: ADD marca modelo precio";
                        String brand = parts[1];
                        String model = parts[2];
                        int price = parseInt(parts[3], "precio");
                        if (price <= 0) return "ERR precio debe ser > 0";
                        Car c = addCar(brand, model, price);
                        return "OK Añadido: " + c;
                    }

                    case "GET": {
                        if (parts.length != 2) return "ERR Uso: GET id";
                        int id = parseInt(parts[1], "id");
                        Car c = inventory.get(id);
                        return (c == null) ? "ERR No existe ID=" + id : "OK " + c;
                    }

                    case "BUY": {
                        if (parts.length != 2) return "ERR Uso: BUY id";
                        int id = parseInt(parts[1], "id");
                        Car removed = inventory.remove(id);
                        return (removed == null) ? "ERR No existe ID=" + id : "OK Comprado: " + removed;
                    }

                    case "BYE":
                        return "BYE ¡Hasta luego!";

                    default:
                        return "ERR Comando no reconocido (usa HELP)";
                }
            } catch (IllegalArgumentException ex) {
                return "ERR " + ex.getMessage();
            }
        }

        private int parseInt(String s, String field) {
            try {
                return Integer.parseInt(s);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(field + " inválido: " + s);
            }
        }
    }
}
