package edu.thepower.u2programacion.multithread;

// En UX conocidos como "Demonios"
// 3 Threads, los dos primeros sacan una traza cada x segundos, y el 3 va a ser el que haga
// el demonio, un latido que diga que el thread sigue vivo hasta que los otros dos paren
public class U2P06ServiciosDemon {

final static long TIEMPO_MAXIMO = System.currentTimeMillis() + 10000;

    public static void main(String[] args) {
        Thread t1 = new Thread(()->{
            while(TIEMPO_MAXIMO>System.currentTimeMillis()){
                System.out.println("Iniciando t1");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        Thread t2 = new Thread(()->{
            while(TIEMPO_MAXIMO>System.currentTimeMillis()){
                System.out.println("Iniciando t2");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {}
            }
        });

        // proceso que estan ejecutandose en un segundo plano,
        // en este caso muestra una prueba de vida, esto serviria por
        // ejemplo para servidores o satelites que queremos comprobar que siga vivo
        Thread heartBeat = new Thread(()->{
            while(true){
                System.out.println("Pum Pum ··> heartBeat");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        heartBeat.setDaemon(true);
        System.out.println("Inicio ejecucion Threads");
        t1.start();
        // Ejemplo de como manejar la prioridad de threads pero no se va a usar por ahora
        // t1.setPriority(Thread.MIN_PRIORITY);
        t2.start();
        heartBeat.start();
        System.out.println("Threads ejecutandose");
    }
}
