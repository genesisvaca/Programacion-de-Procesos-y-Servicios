package edu.thepower.u3comunicaciones.en.red;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class U302EchoClient {

    public static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {


        // En lugar de ECHO SERVER enviar lo que escribamos por teclaso, y paramos cuando sea !0
        try (Socket socket = new Socket("localhost",1027 )){

            // Defirnir aquí el Stream de salida hacia el whoIs
            OutputStream os = socket.getOutputStream();
            PrintWriter pw = new PrintWriter(os,true);
            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String entrada;

            do {
                entrada = sc.nextLine();
                pw.println(entrada);
                System.out.println("Recibido del servidor: " + br.readLine());
            }while (!entrada.equals("0"));
        } catch (IOException e) {
            System.out.println("Error al entrar al servidor");
        }

    }

}
