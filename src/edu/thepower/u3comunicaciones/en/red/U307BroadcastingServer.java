package edu.thepower.u3comunicaciones.en.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U307BroadcastingServer {
    public static void main(String[] args) {

        int puerto = 7777;
        try (DatagramSocket ds = new DatagramSocket(puerto)) {
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.receive(dp);

            String mensaje = new String(dp.getData(), 0, dp.getLength());
            System.out.println("Mensage recibido: " + mensaje);

        } catch (
                IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }

}
