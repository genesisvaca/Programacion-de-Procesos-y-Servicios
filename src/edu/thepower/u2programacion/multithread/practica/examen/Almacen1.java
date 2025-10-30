package edu.thepower.u2programacion.multithread.practica.examen;

class Stock {
    int unidades;
    Stock(int u){unidades=u;}
    void quitar(int n){if(unidades>=n)unidades-=n;}
    void poner(int n){unidades+=n;}
    int get(){return unidades;}
}

public class Almacen1 {
    static void mover(Stock o,Stock d,int n){
        Stock p=o.hashCode()<d.hashCode()?o:d;
        Stock s=o.hashCode()<d.hashCode()?d:o;
        synchronized(p){ synchronized(s){ o.quitar(n); d.poner(n); } }
    }

    public static void main(String[] a)throws InterruptedException{
        Stock s1=new Stock(300),s2=new Stock(300);
        Thread t1=new Thread(()->{for(int i=0;i<500;i++)mover(s1,s2,1);});
        Thread t2=new Thread(()->{for(int i=0;i<500;i++)mover(s2,s1,2);});
        t1.start();t2.start();t1.join();t2.join();
        System.out.println("Total: "+(s1.get()+s2.get()));
    }
}
