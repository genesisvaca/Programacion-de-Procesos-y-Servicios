package edu.thepower.u3comunicaciones.en.red;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


class Validacion {

    public static int validarPuerto(String[] args){
        // Se valida que llegue  un solo argumento
        if (args.length!=1){
            System.err.println("Argumento no valido");
            throw new IllegalArgumentException("Se debe enviar un unico argumento");
        }
        int puerto = Integer.parseInt(args[0]);

        //
        if(puerto<1024 || puerto>65535){
            throw new IllegalArgumentException("Debe  ingresar un puerto entre 1024 y 65535");
        }

        return puerto;
    }
}


public class U3P02EchoServer {
    public static void main(String[] args) {

        int puerto = 0;

        try {
            puerto = Validacion.validarPuerto(args);

        } catch (Exception e) {
            System.err.println("Error en el formato del puerto: " + e.getMessage());
            System.exit(1);
        }

        // En esta linea el puerto ya está validado
        try(ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor iniciando en puerto: " + puerto);

            Socket socket = serverSocket.accept();

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            PrintWriter pw = new PrintWriter(out, true);

            String entrada;
            while((entrada = br.readLine())!= null){
                System.out.println("Recibido de cliente: " + entrada);
                pw.println(entrada.toLowerCase());
            }

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor: "  + e.getMessage());
        }
    }
}
