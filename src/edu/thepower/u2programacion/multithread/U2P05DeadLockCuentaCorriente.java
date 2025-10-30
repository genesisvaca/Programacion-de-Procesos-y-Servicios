package edu.thepower.u2programacion.multithread;

// ────────────────────────────────────────────────────────────────
// CLASE DE DOMINIO: CuentaCorriente
// Representa una cuenta simple con un saldo sobre el que se pueden
// hacer ingresos y retiradas. Aquí NO está sincronizada, porque
// la sincronización la vamos a controlar fuera (en las transferencias).
// ────────────────────────────────────────────────────────────────
class CuentaCorriente {
    // Saldo actual de la cuenta.
    // Tipo/alcance: privado, propio de cada instancia.
    // Dominio: representa el dinero disponible de esa cuenta.
    private float saldo;

    // Constructor: inicializa la cuenta con un saldo concreto.
    // Dominio: permite crear distintas cuentas con distintos saldos iniciales.
    public CuentaCorriente(float saldo) {
        this.saldo = saldo;
    }

    // Getter simple para consultar el saldo actual.
    // No está sincronizado porque se asume que las operaciones externas
    // ya han protegido la coherencia cuando haga falta.
    public float getSaldo() {
        return saldo;
    }

    // Método para retirar dinero de la cuenta.
    // Aquí se hace una comprobación muy sencilla de fondos.
    // IMPORTANTE: este método por sí solo NO es thread-safe; por eso
    // la clase que transfiere hace el synchronized externo.
    public void retirarSaldo(float importe) {
        if (saldo > importe) {   // Solo retira si hay suficiente saldo.
            saldo -= importe;    // Actualiza el saldo restando el importe.
        }
    }

    // Método para ingresar dinero en la cuenta.
    // Igual que arriba: no está sincronizado, se supone que quien lo llama
    // ya ha garantizado el acceso exclusivo.
    public void ingresarSaldo(float importe) {
        saldo += importe;  // Suma el importe al saldo actual.
    }
}


// ────────────────────────────────────────────────────────────────
// CLASE PRINCIPAL: U2P05DeadLockCuentaCorriente
// Objetivo del programa:
//   - Tener dos cuentas con dinero.
//   - Lanzar dos hilos que transfieren dinero en sentidos opuestos.
//   - Hacer la transferencia de forma SINCRONIZADA sobre las dos cuentas
//     pero en un ORDEN FIJO (usando hashCode) para EVITAR DEADLOCK.
//
// Idea clave de teoría para el examen:
//   Si dos hilos necesitan más de un candado (dos cuentas), TODOS los hilos
//   deben adquirir los candados en el MISMO ORDEN. Si no, es cuando aparece
//   el deadlock: hilo A tiene cuenta1 y espera cuenta2, hilo B tiene cuenta2
//   y espera cuenta1 → ninguno suelta → bloqueo.
// ────────────────────────────────────────────────────────────────
public class U2P05DeadLockCuentaCorriente {

    // ────────────────────────────────────────────────────────────
    // MÉTODO DE NEGOCIO: transferir
    // Transfiere 'importe' desde la cuenta 'origen' a la cuenta 'destino'.
    // Problema que resuelve:
    //   - Dos cuentas son DOS recursos diferentes.
    //   - Para que la transferencia sea consistente necesitamos bloquear
    //     las dos a la vez (leer saldo de una y escribir en la otra).
    //   - Si cada hilo las bloquea en orden distinto, puede haber deadlock.
    // Solución usada:
    //   - Ordenamos las dos cuentas por su hashCode.
    //   - Siempre bloqueamos primero la cuenta con hashCode más pequeño
    //     y luego la otra.
    //   - Así TODOS los hilos bloquean en el mismo orden → no hay ciclo.
    // ────────────────────────────────────────────────────────────
    public static void transferir(CuentaCorriente origen,
                                  CuentaCorriente destino,
                                  float importe ){

        // Primero decidimos en qué orden vamos a bloquear los objetos.
        // aux1 será SIEMPRE la cuenta con hashCode más pequeño.
        // aux2 será SIEMPRE la cuenta con hashCode más grande.
        // Dominio: esto impone un ORDEN GLOBAL de bloqueo para evitar deadlock.
        CuentaCorriente aux1 = origen.hashCode() < destino.hashCode() ? origen : destino;
        CuentaCorriente aux2 = origen.hashCode() < destino.hashCode() ? destino : origen;

        // Bloqueamos primero el objeto "menor"
        synchronized (aux1){
            // y dentro, bloqueamos el otro objeto.
            // Como todos los hilos hacen esto en el mismo orden, no hay interbloqueo.
            synchronized (aux2){
                // Ya tenemos ambos "candados", ahora sí es seguro tocar las cuentas.

                // Retiramos dinero de la cuenta 'origen'.
                // OJO: aunque dentro se comprueba el saldo, la protección real
                // viene de estos synchronized de fuera.
                origen.retirarSaldo(importe);

                // Ingresamos ese mismo dinero en la cuenta 'destino'.
                destino.ingresarSaldo(importe);
                // Con estas dos operaciones hemos mantenido la INVARIANTE:
                // total del sistema = igual que antes de empezar la transferencia.
            }
        }

    }

    public static void main(String[] args) {

        // Creamos dos cuentas con el mismo saldo inicial.
        // Dominio: así podemos comprobar al final que la suma de las dos
        // sigue siendo la misma (no hemos perdido dinero).
        CuentaCorriente cc1 = new CuentaCorriente(100_000);
        CuentaCorriente cc2 = new CuentaCorriente(100_000);


        // ────────────────────────────────────────────────────────
        // HILO 1: transfiere de CC1 → CC2 en importes de 10
        // Objetivo: simular movimiento de dinero en un sentido.
        // ────────────────────────────────────────────────────────
        Thread t1 = new Thread(()->{
            for(int i = 0; i < 1000; i++){
                // Cada iteración transfiere 10 de cc1 a cc2.
                // Como transferir está sincronizado sobre las cuentas,
                // este hilo puede cruzarse con el otro sin corromper datos.
                transferir(cc1, cc2, 10);
            }
        });


        // ────────────────────────────────────────────────────────
        // HILO 2: transfiere de CC2 → CC1 en importes de 20
        // Objetivo: simular el caso clásico de deadlock:
        //   - un hilo quiere A→B
        //   - otro hilo quiere B→A
        // PERO como el orden de bloqueo está unificado, NO se bloquean.
        // ────────────────────────────────────────────────────────
        Thread t2 = new Thread(()->{
            for (int i = 0; i < 1000; i++){
                transferir(cc2, cc1, 20);
            }
        });

        // Ponemos a ejecutar los dos hilos en paralelo.
        t1.start();
        t2.start();

        try {
            // Esperamos a que terminen los dos hilos para poder imprimir
            // el resultado final sin que sigan moviendo dinero en segundo plano.
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        // Imprimimos el saldo de cada cuenta por separado.
        System.out.println("Saldo final CC1: " + cc1.getSaldo());
        System.out.println("Saldo final CC2: " + cc2.getSaldo());

        // Y muy importante: comprobamos el saldo TOTAL del sistema bancario simulado.
        // Si todo está bien, debería ser: 100_000 + 100_000 = 200_000
        // (salvo que alguna transferencia no se hiciera por falta de saldo en origen).
        System.out.println("Saldo Total: " + (cc1.getSaldo() + cc2.getSaldo()));

    }

}

// Ejercicio 2. : Similar a esto, como evitar el Deadlock
// → Resumen para examen:
//   1. Siempre adquirir los locks en el mismo orden (como aquí con hashCode).
//   2. O usar tryLock con timeout (en ReentrantLock) y reintentar.
//   3. O reducir la sección crítica para no tener dos recursos bloqueados a la vez.
//   4. Pero la idea más fácil de explicar en el examen es: "ORDEN FIJO DE BLOQUEO".
