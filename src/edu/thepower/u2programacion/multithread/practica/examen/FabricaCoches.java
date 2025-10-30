package edu.thepower.u2programacion.multithread.practica.examen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Coche {
    int id; String modelo;
    Coche(int id, String modelo){ this.id=id; this.modelo=modelo; }
}

public class FabricaCoches {
    static List<Coche> lista = Collections.synchronizedList(new ArrayList<>());
    static int contador = 0;
    static synchronized int genId(){ return ++contador; }

    public static void main(String[] args) throws InterruptedException {
        int empleados=10, cochesPorEmpleado=100;
        List<Thread> hilos = new ArrayList<>();

        for(int i=0;i<empleados;i++){
            Thread t = new Thread(() -> {
                for(int j=0;j<cochesPorEmpleado;j++)
                    lista.add(new Coche(genId(),"Modelo-X"));
            });
            hilos.add(t); t.start();
        }

        for(Thread t:hilos) t.join();
        System.out.println("Total coches: "+lista.size());
    }
}
