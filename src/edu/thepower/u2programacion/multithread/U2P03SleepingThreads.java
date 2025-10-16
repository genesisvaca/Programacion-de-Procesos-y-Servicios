package edu.thepower.u2programacion.multithread;

public class U2P03SleepingThreads implements Runnable{
    
    private static final String NOMBRETHREAD = "["+ Thread.currentThread().getName()+ "]";;
    
    // Sleeping Thread 1
    @Override
    public void run(){
        
        System.out.println(NOMBRETHREAD + "Iniciando ejecución");

        try {

            Thread.sleep(Long.MAX_VALUE);

        } catch (InterruptedException e){

            System.out.println(NOMBRETHREAD + " Despertando 1");

        }

        // Otra forma de Sleeping Thread 2 -> Crear un Bucle infinito
        while (!Thread.interrupted()){
            
        }
        System.out.println(NOMBRETHREAD + " Despertando 2");

        
    }

    public static void main(String[] args) {

        Thread t1 = new Thread(new U2P03SleepingThreads(), "Sleeping Thread");
        t1.start();


        System.out.println(NOMBRETHREAD + "Iniciando ejecución");

        for (int i = 3; i < 5 ; i++) {

            System.out.println(NOMBRETHREAD + " - Sleeping Thread 5 sec");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println(NOMBRETHREAD + " - Despertando al thread durmiente");
            t1.interrupt();
        }
    }
}

