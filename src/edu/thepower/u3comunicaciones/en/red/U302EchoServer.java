package edu.thepower.u3comunicaciones.en.red;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class U302EchoServer {

    public static DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public static String getFecha() {
        return dateFormat.format(System.currentTimeMillis());

    }

    public static void main(String[] args) {

        Thread heartBeat = new Thread(()->{
            while(true){
                System.out.println("| " + getFecha() + " | Servidor activo |");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        heartBeat.setDaemon(true);
        heartBeat.start();

        try (ServerSocket server = new ServerSocket(1027)){

            System.out.println("| " + getFecha() + " | Servidor Iniciado puerto 1027 |");
            Socket socket = server.accept();
            System.out.println("| " + getFecha() + " | Conectando con el servidor, aceptada solicitud |");
            ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());
            PrintWriter pw = new PrintWriter(os, true);
            InputStream in = socket.getInputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(in));

            String line= null;
            while ((line = input.readLine()) != null) {
                System.out.println("| " + getFecha() + " | Recibido del cliente: " + line + " |");
                pw.println(line.toLowerCase());

            }

        } catch (IOException e) {
            System.err.println("|" + getFecha() + " | Error al arracar el servidor: " + e.getMessage());
        }
    }
}
