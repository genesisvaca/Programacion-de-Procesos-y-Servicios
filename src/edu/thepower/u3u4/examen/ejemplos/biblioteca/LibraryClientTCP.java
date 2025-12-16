package edu.thepower.u3u4.examen.ejemplos.biblioteca;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class LibraryClientTCP {

    private static final String HOST = "localhost";
    private static final int PORT = 7070;

    public static void main(String[] args) {
        try (Socket socket = new Socket(HOST, PORT);
             BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);
             Scanner sc = new Scanner(System.in)) {

            System.out.println(br.readLine());
            System.out.println(br.readLine());

            while (true) {
                System.out.print("> ");
                String cmd = sc.nextLine();
                pw.println(cmd);

                String resp = br.readLine();
                if (resp == null) break;

                System.out.println(resp);

                // Para respuestas multilínea (LIST)
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
