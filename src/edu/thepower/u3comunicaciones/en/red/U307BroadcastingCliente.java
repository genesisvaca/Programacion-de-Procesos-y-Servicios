package edu.thepower.u3comunicaciones.en.red;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class U307BroadcastingCliente {

    private static final String MENSAJE = "Mensaje de Broadcast";
    private static final String ACK = "ACK- "+ MENSAJE;
    public static void main(String[] args) {
        int puerto = 7777;
        try (DatagramSocket ds = new DatagramSocket()){


            byte[] data = MENSAJE.getBytes();

            InetAddress host = InetAddress.getByName( "10.255.255.255");

            DatagramPacket dp = new DatagramPacket(data, data.length, host, puerto);
            ds.setBroadcast(true);
            ds.send(dp);

        } catch (IOException e){
            System.err.println("Error en el servidor: " + e.getMessage());
        }

    }

}

