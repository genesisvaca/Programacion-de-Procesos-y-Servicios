package edu.thepower.u2programacion.multithread;

// en el run va el código que se encarga de contar las vocales, a tener en cuenta que
// el métdo run no recibe argumentos, declarando un constructor para la clase que
//

public class U2P02ContadorVocalesThread implements Runnable{

    private char vocal;
    private String archivo;

    public  U2P02ContadorVocalesThread (char vocal,String archivo){
        this.vocal = vocal;
        this.archivo = archivo;
    }

    @Override
    public void run() {



    }

    public static void main(String[] args) {

    }

}


