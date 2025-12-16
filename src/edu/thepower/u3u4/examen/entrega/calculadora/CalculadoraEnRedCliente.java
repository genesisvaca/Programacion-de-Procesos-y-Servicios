package edu.thepower.u3u4.examen.entrega.calculadora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class CalculadoraEnRedCliente {

    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            // Lee bienvenida (2 líneas)
            System.out.println(br.readLine());
            System.out.println(br.readLine());

            while (true) {
                System.out.print("> ");
                String cmd = sc.nextLine();
                pw.println(cmd);

                String resp = br.readLine(); // el servidor responde una línea por comando
                System.out.println(resp);

                if (resp != null && resp.startsWith("BYE")) break;
            }

        } catch (IOException e) {
            System.err.println("❌ Error cliente: " + e.getMessage());
        }
    }
}
