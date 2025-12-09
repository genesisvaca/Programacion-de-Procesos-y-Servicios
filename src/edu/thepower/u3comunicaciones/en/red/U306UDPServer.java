package edu.thepower.u3comunicaciones.en.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U306UDPServer {



    public static void main(String[] args) {

        int puerto = 7777;
        try (DatagramSocket ds = new DatagramSocket(puerto)) {
            System.out.println("Servidor escuchando en puerto " + puerto);
            byte[] data = new byte[1024];
            DatagramPacket dp = new DatagramPacket(data, data.length);
            ds.receive(dp);

            String mensaje = new String(dp.getData(), 0, dp.getLength());

            System.out.println("Mensage recibido: " + mensaje);

            // Respuesta al mensaje recibido con un mensaje de ACK
            String ack = "ACK - " + mensaje;

            byte[] data1 = ack.getBytes();
            InetAddress host = dp.getAddress();
            int puerto2 = dp.getPort();
            DatagramPacket dp1 = new DatagramPacket(data1, data.length, host, puerto2);

            ds.send(dp1);


        } catch (IOException e) {
            System.err.println("Error en el servidor: " + e.getMessage());
        }

    }
}
