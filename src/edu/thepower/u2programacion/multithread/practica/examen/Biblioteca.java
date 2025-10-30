package edu.thepower.u2programacion.multithread.practica.examen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Prestamo1 { String u,l; Prestamo1(String u,String l){this.u=u;this.l=l;} }

public class Biblioteca {
    static List<Prestamo1> lista = Collections.synchronizedList(new ArrayList<>());
    static String[] libros = {"Java","Redes","BBDD"};

    public static void main(String[] a) throws InterruptedException {
        List<Thread> hilos=new ArrayList<>();
        for(int i=0;i<8;i++){
            String usuario="U"+i;
            Thread t=new Thread(()->{
                for(int j=0;j<50;j++)
                    lista.add(new Prestamo1(usuario, libros[j%libros.length]));
            });
            hilos.add(t); t.start();
        }
        for(Thread t:hilos)t.join();
        System.out.println("Total préstamos: "+lista.size());
    }
}
