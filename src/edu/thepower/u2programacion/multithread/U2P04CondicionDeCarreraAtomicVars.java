package edu.thepower.u2programacion.multithread;

import java.util.concurrent.atomic.AtomicInteger;

public class U2P04CondicionDeCarreraAtomicVars {

    private static final int ITERACIONES = 1_000_000;
    private static final int VALOR = 10;
    private static AtomicInteger contador = new AtomicInteger(0);


    private static void incrementarContador ( int num){

        contador.addAndGet(num);

    }

    public static  int getContador(){

        return contador.get();

    }

    public static void main(String[] args) {

        Thread threadIncrementar = new Thread( ()-> {

            System.out.println("Iniciando ejecución incrementar");
            for (int i = 0; i < ITERACIONES; i++) {
                incrementarContador(VALOR);
            }
            System.out.println("Finalizando ejecución incrementar");
        });

        // Thread para decrementar el valor de la variable  contador en "Iteraciones" veces
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
            threadIncrementar.join();
            threadDecrementar.join();
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }

        System.out.println("El valor final de contador es: " + getContador());


    }

}
