package edu.thepower.u4servicios.en.red;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class U4T02SelfHealing {
    public static void main(String[] args) {
        while(true){
            try{
               arrancarServidor();
            } catch (Exception e) {
                System.err.println("Servidor fuera de servicio");
                System.err.println("Arrancando en 2 segundos");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {

                }
            }
        }
    }
    private static void arrancarServidor(){

        int puerto = 2400;
        try (ServerSocket socket = new ServerSocket(puerto)){

            System.out.println("Escuchando al servidor " + socket.getLocalPort());

            // thread, a los 5 segundos cierra el servidor
            Thread killer = new Thread(()->{
                System.out.println("Thread Killer iniciándose");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {

                }
                try {
                    socket.close();
                } catch (IOException e) {

                }
            });
            killer.start();

            while (true){
                Socket sc = socket.accept();
                 new Thread(()->{
                    try(BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
                        PrintWriter pw  = new PrintWriter(sc.getOutputStream(),true)){
                        String linea;

                        while((linea = br.readLine()) != null){
                            System.out.println("Recibido de cliente: " + linea);

                            pw.println(linea.toLowerCase());
                        }


                    }catch (IOException e){
                        System.err.println("Error: " + e.getMessage());
                    }
                }).start();
            }

        }catch(IOException e){
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
