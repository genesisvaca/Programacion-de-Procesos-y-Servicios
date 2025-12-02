package edu.thepower.u3comunicaciones.en.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class U3P06UDServer {

    public static void main(String[] args) {

        try (DatagramSocket ds = new DatagramSocket(7777)) {
            System.out.println("Servidor escuchando en puerto 7777");
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.receive(dp);
            System.out.println("Mensage recibido");

        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }

    }
}
