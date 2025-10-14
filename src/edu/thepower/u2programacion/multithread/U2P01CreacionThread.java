package edu.thepower.u2programacion.multithread;

// Implementación de thread mediante implementación de interface Runnable
class ThreadImplements implements Runnable{

    @Override
    public void run() {
        System.out.println("El nombre del thread es: " + Thread.currentThread().getName());
        System.out.println("El ID del thread es: " + Thread.currentThread().threadId());
    }
}

// Declaración de clase thread mediante herencia de la clase padre Thread
public class U2P01CreacionThread extends Thread{

    // Este es el código que se ejecuta cuando lanzo el thread
    @Override
    public void run(){
        System.out.println("El nombre del thread es: " + Thread.currentThread().getName());
        System.out.println("El ID del thread es: " + Thread.currentThread().threadId());
    }


    public static void  main (String[] args){
        // Instanciación de Thread mediante expresión lambda
        Thread t1 = new Thread( () -> {
            System.out.println("El nombre del thread es: " + Thread.currentThread().getName());
            System.out.println("El ID del thread es: " + Thread.currentThread().threadId());
        }, "Thread Lambda" );

        Thread t2 = new Thread(new U2P01CreacionThread(), "Thread Herencia");



        for (int i = 0 ; i <= 5;i++){

            Thread t3 = new Thread(new ThreadImplements(),"Thread Implements" + i);
            t3.start();
        }
        // Iniciar los Threads que se ejecutan en paralelo
        t1.start();
        t2.start();


    }

}
