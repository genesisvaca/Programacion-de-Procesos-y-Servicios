package edu.thepower.u3comunicaciones.en.red;

import java.io.IOException;
import java.io.StringReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;

public class U306UDPCliente {

   private static final String MENSAJE = "Hola, mañana hará un día soleado";
   private static final String ACK = "ACK- "+ MENSAJE;
   private static final String IP = "localhost";

   public static void main(String[] args) {
       int puerto = 7777;
        try (DatagramSocket ds = new DatagramSocket()){


            byte[] data = MENSAJE.getBytes();

            InetAddress host = InetAddress.getByName(IP);

            DatagramPacket dp = new DatagramPacket(data, data.length, host, puerto);
            ds.send(dp);

            // Recogemos al ACK del servidor

            byte[] data1 = ACK.getBytes();

            DatagramPacket dp1 = new DatagramPacket(data1, data.length, host, puerto);

            ds.receive(dp1);

            String mensaje = new String(dp1.getData(),0, dp1.getLength());
            System.out.println("Mensaje recibido: " + mensaje);


        } catch (IOException e){
            System.err.println("Error en el servidor: " + e.getMessage());
        }
    }
}
