package edu.thepower.u2programacion.multithread;

public class U2P04CondicionDeCarreraMonitor {

    private static final int ITERACIONES = 1_000_000;
    private static final int VALOR = 10;
    private static int contador = 0;

    // Bloquear la clase con synchronized o monitores
    private static synchronized void incrementarContador ( int num){

        System.out.println("Entrando en incrementarContador");
        try {
            Thread.sleep(ITERACIONES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        contador += num;
        System.out.println("Saliendo de incrementarContador");
    }

    public static synchronized int getContador(){

        System.out.println("Entrando en getContador");
        try {
            Thread.sleep(ITERACIONES);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Saliendo de getContador");
        return contador;

    }

    public static void main(String[] args) {
/*
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
*/


        Thread accesoIncrementarContador = new Thread(()-> {
            incrementarContador(VALOR);
        });

        Thread accesoGetContador = new Thread(()->{
            getContador();
        });
    }

}
