package edu.thepower.u3comunicaciones.en.red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public class U3P05ServerDiccionario {

    private static Map<String, String> diccionario = Collections.synchronizedMap(new TreeMap<>());

    static {
        String[] claves = {"house", "happy", "red", "monkey", "hello"};
        String[] valores = {"casa", "feliz", "rojo", "mono", "hola"};
        for (int i = 0; i < claves.length; i++) {
            diccionario.put(claves[i], valores[i]);
        }
    }

    private static void gestionarCliente(Socket socket) {

        try (
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true)
        ) {

            String comando;
            String respuesta;

            pw.println("Conexión con el Servidor Diccionario establecida. Bienvenido.");
            while ((comando = br.readLine()) != null) {

                String[] partes = comando.split("\\s+", 3);

                respuesta = switch (partes[0].trim().toLowerCase()) {

                    case "trd" -> {
                        if (partes.length > 1) {
                            yield diccionario.getOrDefault(partes[1], "No existe la palabra en el diccionario");
                        } else {
                            yield "Uso: TRD <palabra>";
                        }
                    }

                    case "inc" -> {
                        if (partes.length > 2) {
                            diccionario.put(partes[1], partes[2]);
                            yield "Palabra insertada.";
                        } else {
                            yield "Uso: INC <palabra> <traducción>";
                        }
                    }

                    case "lis" -> {
                        StringBuffer sb = new StringBuffer();

                        for (Map.Entry<String, String> entrada : diccionario.entrySet()) {
                            sb.append(entrada.getKey())
                                    .append(": ")
                                    .append(entrada.getValue())
                                    .append(",");
                        }

                        if (sb.length() > 0) {
                            sb.setLength(sb.length() - 1); // quitar última coma
                        }

                        yield sb.toString();
                    }

                    case "sal", "bye" -> "Hasta la vista.";

                    default -> "El comando no existe.";
                };

                pw.println(respuesta);
            }

        } catch (IOException e) {
            System.err.println("Error con el cliente: " + e.getMessage());
        }

        System.out.println("El cliente ha salido");
    }

    public static void main(String[] args) {

        try (ServerSocket server = new ServerSocket(4000)) {

            System.out.println("Servidor iniciado en el puerto 4000");

            while (true) {
                Socket socket = server.accept();
                new Thread(() -> gestionarCliente(socket)).start();
            }

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}