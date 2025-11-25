package edu.thepower.u3comunicaciones.en.red;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

class GestorCliente implements Runnable {
    private Socket socket;

    public GestorCliente(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        String nombre = "[" + Thread.currentThread().getName() + "]";
        System.out.println(nombre + "Cliente conectado: " + socket.getInetAddress() + ":" + socket.getPort());
        try {
            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedReader bf = new BufferedReader(new InputStreamReader(in));
            PrintWriter pw = new PrintWriter(out, true);

            // Mensaje de bienvenida para desbloquear el cliente
            pw.println("Bienvenido al servidor Echo");

            String entrada;
            while ((entrada = bf.readLine()) != null) {
                System.out.println(nombre + " Recibido de cliente: " + entrada);
                pw.println(entrada.toLowerCase());
            }
        } catch (IOException e) {
            System.err.println(nombre + "Error en la conexión: " + e.getMessage());
        }
        System.out.println(nombre  + " El cliente se ha desconectado");
    }
}

public class U3P04EchoServerMultiCliente {
    public static void main(String[] args) {
        int puerto = 8000; // Valor por defecto. Cambia esto si usas Validacion.
        /*
        try {
            puerto = Validacion.validarPuerto(args);
        } catch (Exception e) {
            System.err.println("Error en el puerto: " + e.getMessage());
            System.exit(1);
        }
        */
        try (ServerSocket servidor = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciado: esperando conexiones en el puerto: " + puerto);
            while (true) {
                Socket socket = servidor.accept();
                System.out.println("Cliente conectado: " + socket.getInetAddress() + ":" + socket.getPort());
                Thread t = new Thread(new GestorCliente(socket));
                t.start();
            }
        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: " + e.getMessage());
        }
    }
}