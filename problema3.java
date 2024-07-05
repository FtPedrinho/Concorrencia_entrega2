import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static class Barbearia {
        private boolean dormindo;
        private static Semaphore espera = new Semaphore(12);
        private static final Semaphore assento = new Semaphore(1);
        private int pessoas;
        private final Lock lock = new ReentrantLock();


        public Barbearia() {
            this.dormindo = true;
            this.pessoas = 0;
        }

        public void cortarCabelo(String nome, int numero) throws InterruptedException {
            System.out.println(nome + numero + ": chegou na barbearia...");
            if (!espera.tryAcquire()) {
                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println(nome + numero + ": foi embora porque estava cheio.");
            } else {
                try {
                    espera.acquire();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                lock.lock();
                try {
                    if (pessoas != 0) {
                        try {
                            Thread.sleep(0);
                        }
                        catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        System.out.println(nome + numero + ": está na fila de espera na posição - " + pessoas);
                        pessoas++;
                        if (pessoas == 6) {
                            System.out.println("... Barbearia cheia ...");
                        }
                    } else {
                        pessoas++;
                        System.out.println("... " + nome + numero + " acordou o barbeiro ...");
                    }
                }finally{
                    lock.unlock();
                }
                try {
                    assento.acquire();
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Chegou a vez de " + nome + numero + " cortar o cabelo.");
                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(nome + numero + ": Saiu da barbearia.");
                assento.release();
                pessoas--;
                if (pessoas == 0) {
                    dormindo = true;
                    System.out.println("... Barbeiro voltou a dormir ...");
                }
                espera.release();
            }
        }
    }

    public static class Clientes implements Runnable {
        private Barbearia barbearia;
        private String nome;
        private int numero;

        public Clientes(Barbearia barbearia, String nome, int numero) {
            this.nome = nome;
            this.barbearia = barbearia;
            this.numero = numero;
        }

        public void run() {
            try {
                barbearia.cortarCabelo(nome, numero);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Barbearia pega_barbeiro = new Barbearia();

        for (int i = 0; i < 19; i++) {
            Clientes cliente = new Clientes(pega_barbeiro, "Cliente", i + 1);
            Thread cliente_thread = new Thread(cliente);
            cliente_thread.start();
            if (i==9){
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
