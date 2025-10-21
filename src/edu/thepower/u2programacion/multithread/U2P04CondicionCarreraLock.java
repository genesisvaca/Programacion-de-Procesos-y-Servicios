package edu.thepower.u2programacion.multithread;

import java.util.concurrent.locks.ReentrantLock;

/**
 *  Controlamos el bloqueo, solo bloques de código que nos interese
 */

public class U2P04CondicionCarreraLock {

    private static final int ITERACIONES = 1_000_000;
    private static final int VALOR = 10;
    private static int contador = 0;
    private static ReentrantLock lock = new ReentrantLock();

    // Los objetos de tipo ReentrantLock permiten bloquear un trozo de código hasta que se ejecute
    // El bloqueo se hace con lock y el desbloqueo con unlock y nos garantiza exclusión mutua
    private static  void incrementarContador ( int num){

        System.out.println("Entrando en incrementarContador");
        // .lock (Tdo lo que hay contenido a partir de ese momento se queda bloqueado)
        lock.lock();
        try {

            contador += num;

            // finally (obligatoriamente hacemos un .unLock() del lock, aunque haya error
        }finally {

            lock.unlock();

        }

        System.out.println("Saliendo de incrementarContador");
    }

    public static  int getContador(){

        System.out.println("Entrando en getContador");
        System.out.println("Saliendo de getContador");
        return contador;

    }

    public static void main(String[] args) {

        Thread threadIncrementar = new Thread( ()-> {

            System.out.println("Iniciando ejecución incrementar");
            for (int i = 0; i < ITERACIONES; i++) {
                incrementarContador(VALOR);
            }
            System.out.println("Finalizando ejecución incrementar");
        });

        // Thread para decrementar el valor de la variable contador en "Iteraciones" veces
        Thread threadDecrementar = new Thread( ()-> {
            System.out.println("Iniciando ejecución decrementar");
            for (int i = 0; i < ITERACIONES; i++) {
                incrementarContador(-VALOR);
            }

            System.out.println("Finalizando ejecución decrementar");

        });

        threadIncrementar.start();
        threadDecrementar.start();

        try{
            // .join (espera a que terminen los threads)
            threadIncrementar.join();
            threadDecrementar.join();
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        System.out.println("El valor final de contador es: " + getContador());
    }

}
