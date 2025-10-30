package edu.thepower.u2programacion.multithread.practica.examen;

class Almacen {
    int stock;
    Almacen(int s){stock=s;}
    void sacar(int n){if(stock>=n)stock-=n;}
    void meter(int n){stock+=n;}
    int get(){return stock;}
}

public class Concesionario {
    static void transferir(Almacen o,Almacen d,int n){
        Almacen a=o.hashCode()<d.hashCode()?o:d;
        Almacen b=o.hashCode()<d.hashCode()?d:o;
        synchronized(a){ synchronized(b){ o.sacar(n); d.meter(n); } }
    }

    public static void main(String[] a)throws InterruptedException{
        Almacen a1=new Almacen(500),a2=new Almacen(500);
        Thread t1=new Thread(()->{for(int i=0;i<1000;i++)transferir(a1,a2,1);});
        Thread t2=new Thread(()->{for(int i=0;i<1000;i++)transferir(a2,a1,2);});
        t1.start();t2.start();t1.join();t2.join();
        System.out.println("Total:"+ (a1.get()+a2.get()));
    }
}
