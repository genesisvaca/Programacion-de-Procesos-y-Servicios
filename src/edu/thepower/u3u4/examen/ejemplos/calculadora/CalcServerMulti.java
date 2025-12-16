package edu.thepower.u3u4.examen.ejemplos.calculadora;


import java.io.*;
import java.net.*;
import java.util.Locale;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class CalcServerMulti {

    private static final int PORT = 5000;
    private static final int POOL_SIZE = 10;

    // Estado compartido (útil en examen para justificar concurrencia)
    private static final AtomicInteger totalOperaciones = new AtomicInteger(0);

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("✅ CalcServerMulti escuchando en puerto " + PORT);

            while (true) {
                Socket client = server.accept(); // bloquea hasta que llegue un cliente
                pool.submit(new ClientHandler(client));
            }

        } catch (IOException e) {
            System.err.println("❌ Error servidor: " + e.getMessage());
        } finally {
            pool.shutdown();
        }
    }

    static class ClientHandler implements Runnable {
        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            String clientInfo = socket.getRemoteSocketAddress().toString();
            System.out.println("🔌 Cliente conectado: " + clientInfo);

            try (socket;
                 BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)) {

                pw.println("Bienvenido a CalcServerMulti.");
                pw.println("Escribe HELP para ver comandos. BYE para salir.");

                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) continue;

                    String response = procesarComando(line);

                    pw.println(response);

                    if (response.startsWith("BYE")) break;
                }

            } catch (IOException e) {
                System.err.println("⚠️ Cliente " + clientInfo + " desconectado con error: " + e.getMessage());
            } finally {
                System.out.println("🔚 Fin sesión cliente: " + clientInfo);
            }
        }

        private String procesarComando(String line) {
            String[] parts = line.split("\\s+");
            String cmd = parts[0].toUpperCase(Locale.ROOT);

            try {
                switch (cmd) {
                    case "HELP":
                        return "Comandos: ADD a b | SUB a b | MUL a b | DIV a b | POW a b | SQRT a | BYE";
                    case "BYE":
                        return "BYE. Total ops globales=" + totalOperaciones.get();

                    case "ADD":
                        checkArity(parts, 3);
                        totalOperaciones.incrementAndGet();
                        return "OK " + (parseDouble(parts[1]) + parseDouble(parts[2]));

                    case "SUB":
                        checkArity(parts, 3);
                        totalOperaciones.incrementAndGet();
                        return "OK " + (parseDouble(parts[1]) - parseDouble(parts[2]));

                    case "MUL":
                        checkArity(parts, 3);
                        totalOperaciones.incrementAndGet();
                        return "OK " + (parseDouble(parts[1]) * parseDouble(parts[2]));

                    case "DIV":
                        checkArity(parts, 3);
                        totalOperaciones.incrementAndGet();
                        double b = parseDouble(parts[2]);
                        if (b == 0) return "ERR division por cero";
                        return "OK " + (parseDouble(parts[1]) / b);

                    case "POW":
                        checkArity(parts, 3);
                        totalOperaciones.incrementAndGet();
                        return "OK " + Math.pow(parseDouble(parts[1]), parseDouble(parts[2]));

                    case "SQRT":
                        checkArity(parts, 2);
                        totalOperaciones.incrementAndGet();
                        double x = parseDouble(parts[1]);
                        if (x < 0) return "ERR sqrt de negativo";
                        return "OK " + Math.sqrt(x);

                    default:
                        return "ERR comando no reconocido. Usa HELP.";
                }
            } catch (IllegalArgumentException ex) {
                return "ERR " + ex.getMessage();
            }
        }

        private static void checkArity(String[] parts, int expected) {
            if (parts.length != expected) {
                throw new IllegalArgumentException("nº argumentos incorrecto. Esperado: " + (expected - 1));
            }
        }

        private static double parseDouble(String s) {
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("número inválido: " + s);
            }
        }
    }
}
