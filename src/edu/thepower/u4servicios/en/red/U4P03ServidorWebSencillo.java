package edu.thepower.u4servicios.en.red;

import javax.swing.text.AbstractDocument;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class U4P03ServidorWebSencillo {
    private static final String HTML = """
            <html>
            
                <head>
                    <title>Servidor Web Simple</title>
                </head>
                <body>
                    <h1>Hola Server</h1>
                    <p>Lorem Ipsum</p>
                
                
                </body>
            
            </html>
            
            """;
    private static final int PUERTO = 8080;

    public static void main(String[] args) {
        try (ServerSocket socket = new ServerSocket(PUERTO)){
            System.out.println("Escuchando en el puerto " + PUERTO);

            while (true){
                Socket sc = socket.accept();

                Thread t = new Thread(()-> atenderSolicitud(sc));

            }
        }catch (IOException e){

        }
    }

    private static void atenderSolicitud(Socket sc){

        try(BufferedReader br = new BufferedReader(new InputStreamReader(sc.getInputStream()));
            PrintWriter pw  = new PrintWriter(sc.getOutputStream())){
            String linea;

            while((linea = br.readLine()) != null){
                System.out.println(linea);
            }

            System.out.println("Devolviendo html");
            pw.println("HTTP/1.1 200 Ok");
            pw.println("Content-Type: text/html;charset=UTF-8");
            pw.println("Content-Length: " + HTML.getBytes().length);
            pw.println(HTML);
            pw.flush();

        }catch (IOException e){
            System.err.println("Error: " + e.getMessage());
        }
    }
}
