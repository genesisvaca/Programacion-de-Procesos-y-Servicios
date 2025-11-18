package edu.thepower.u3comunicaciones.en.red;

import java.io.*;
import java.net.Socket;

public class U3P00EchoClientGood {
    public static void main(String[] args) {
        try (Socket socket = new Socket("localhost", 7777)){
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            PrintWriter pw = new PrintWriter(out, true);

            pw.println("Mesanje que devuelve");
            System.out.println("Lo que recibe el servidor" + br.readLine());
        } catch (Exception e) {
            System.err.println("Error en la conexion: " + e.getMessage());
        }

        System.out.println("Comunicación finalizada");
    }
}
