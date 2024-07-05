import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class problema4{

    //   Criação da classe do restaurante com o semáforo da quantidade de cadeiras
    // uma variável de cadeiras para contagem, uma booleana que determina se existe
    // um grupo de 5 amigos e um lock para manipular variáveis.
    //
    //  O "acquire()" garante que exista uma fila de espera e que todas as Threads
    // entrem no restaurante alguma hora. O Lock auxilia na manipulação das variaveis
    // numericas e booleanas. Uma vez que um cliente entra no restaurante e come (sleep)
    // ele sai e libera espaço. Caso o restaurante esteja cheio (5 pessoas), As Threads
    // que terminarem primeiro saem sem dar release e a ultima Thread libera todos os
    // espaços.

    public static class Restaurante {
        private static final Semaphore semaforo = new Semaphore(5);
        private int cadeiras = 5;
        private boolean amigos = false;
        private final Lock lock = new ReentrantLock();

        public void comer(String nome, int numero, int tempo) throws InterruptedException {
            System.out.println(nome + numero + ": chegou ao restaurante.");
            semaforo.acquire();
            System.out.println(nome + numero + ": sentou-se em uma cadeira.");

            // mMnipulação das variáveis por mutex.
            lock.lock();
            try {
                cadeiras--;
                if (cadeiras==0){
                    System.out.println("... Um grupo de 5 amigos estão comendos juntos ...");
                    amigos = true;
                }
            } finally {
                lock.unlock();
            }

            //   Simulação do tempo da refeição, o tempo pode ser manipulado
            // na criação das Threads, variando de pessoa em pessoa.
            Thread.sleep(tempo);

            // outra manipulação de variável para saída.
            lock.lock();
            try{
                //   Caso em que temos 5 amigos. Note que as primeiras Threads a terminarem
                // saem sem dar release a a ultima libera todos os espaços.
                if(amigos){
                    cadeiras++;
                    if(cadeiras==5){
                        System.out.println("... Os 5 amigos terminaram de comer ...");
                        amigos = false;
                        semaforo.release(5);
                    }

                // Caso de saída normal sem grupo de amigos.
                }else{
                    cadeiras++;
                    System.out.println(nome + numero + ": terminou de comer e saiu.");
                    semaforo.release();
                }
            }finally{
                lock.unlock();
            }
        }
    }

    // Criação da classe de clientes, jogando-as na função do restaurante.
    public static class Clientes implements Runnable {
        private final Restaurante restaurante;
        private final String nome;
        private final int numero;
        private final int tempo;

        public Clientes(Restaurante restaurante, String nome, int numero, int tempo) {
            this.nome = nome;
            this.restaurante = restaurante;
            this.numero = numero;
            this.tempo = tempo;
        }

        public void run() {
            try {
                restaurante.comer(nome, numero, tempo);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    // Main onde são criadas as Threads e o Restaurante.
    public static void main(String[] args) throws InterruptedException {

        Restaurante pega_restaurante = new Restaurante();

        for (int i = 0; i < 100; i++) {
            Clientes cliente = new Clientes(pega_restaurante, "Cliente", i + 1, 5000);
            Thread cliente_thread = new Thread(cliente);
            cliente_thread.start();
            Thread.sleep(1000);
        }
    }
}
