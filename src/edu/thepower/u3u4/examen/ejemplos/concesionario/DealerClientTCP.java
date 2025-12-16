package edu.thepower.u3u4.examen.ejemplos.concesionario;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class DealerClientTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 5050;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            // Bienvenida (2 líneas)
            System.out.println(br.readLine());
            System.out.println(br.readLine());

            while (true) {
                System.out.print("> ");
                String cmd = sc.nextLine();
                pw.println(cmd);

                String resp = br.readLine();
                if (resp == null) break;

                // Si LIST devuelve varias líneas, las leemos hasta que no queden disponibles
                System.out.println(resp);
                while (br.ready()) {
                    System.out.println(br.readLine());
                }

                if (resp.startsWith("BYE")) break;
            }

        } catch (IOException e) {
            System.err.println("❌ Error cliente: " + e.getMessage());
        }
    }
}

