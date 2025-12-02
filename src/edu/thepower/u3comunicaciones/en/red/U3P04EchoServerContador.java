package edu.thepower.u3comunicaciones.en.red;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicInteger;

public class U3P04EchoServerContador {


    private static AtomicInteger contador = new AtomicInteger(0);

    private static final String CONTADOR_ACTUALIZADO = "Contador actualizado. ";
    private static final String VALOR_CONTADOR = " Valor: ";

    static class GestorClienteContador implements Runnable{

        private Socket socket;
        private String cliente;

        public GestorClienteContador (Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {


            cliente = socket.getInetAddress() + ":" + socket.getPort();;
            System.out.println("[" + Thread.currentThread().getName() + "] IP: " + cliente);

            try(BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);){

                String comando;
                String respuesta;
                while ((comando = br.readLine() )!= null) {

                    respuesta = switch (comando.trim().toLowerCase()) {

                        case "inc" -> CONTADOR_ACTUALIZADO + VALOR_CONTADOR + incrementarContador();

                        case "dec" ->  CONTADOR_ACTUALIZADO + VALOR_CONTADOR + decrementarContador();

                        case "get" -> VALOR_CONTADOR + getContador();

                        case "bye" -> "Bey";

                        default -> "Comando no valido";
                    };
                    pw.println(respuesta);

                }



            }catch (IOException e){

                System.err.println("Error en la conexion: " + e.getMessage());
            }
            System.out.println("Conexion finalizada de " + cliente);

        }
    }

    public static int getContador() {
        return contador.get();
    }

    public static int incrementarContador() {
        return contador.incrementAndGet();
    }

    public static int decrementarContador() {
        return contador.decrementAndGet();
    }

    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(7777)){


            System.out.println("Servidor escuchando peticiones en el servidor: 7777");

            // Iniciamos bucle para atender todas las solicitudes y asignarlas a un thread
            while(true){
            Socket socket = serverSocket.accept();
            // Instanciamos nuevo thread para atender solicitud de cliente con la clase
                // GestorClienteContador que recibe un socket como argumento de entrada
            Thread t = new Thread(new GestorClienteContador(socket));
            t.start();
            }

        } catch (IOException e){
            System.err.println("Error el el servidor: " + e.getMessage());
        }

    }
}
