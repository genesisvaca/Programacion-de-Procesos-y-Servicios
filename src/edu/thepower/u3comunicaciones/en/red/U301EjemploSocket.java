package edu.thepower.u3comunicaciones.en.red;

import java.io.*;
import java.net.Socket;

public class U301EjemploSocket {

    public static void main(String[] args) {

        try (Socket socket = new Socket("whois.internic.net",43 )){

            // Defirnir aquí el Stream de salida hacia el whoIs
            OutputStream os = socket.getOutputStream();

            PrintWriter pw = new PrintWriter(os,true);

            InputStream is = socket.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            pw.println("google.com");

            String line = null;
            while ((line = br.readLine()) != null){
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error al entrar al servidor");
        }

    }
}