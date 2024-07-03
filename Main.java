import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    public static class Barbearia{
        private boolean dormindo;
        private final Semaphore ocupacao = new Semaphore(10);
        private int cadeiras;
        private final Lock lock = new ReentrantLock();


        public Barbearia(){
            this.dormindo = true;
            this.cadeiras = 5;
        }
        public void cortarCabelo(String nome) throws InterruptedException{
            boolean espaco = ocupacao.tryAcquire();
            if (!espaco){
                Thread.sleep(1000);
                System.out.println(nome + " não entrou na barbearia porque estava cheio.");
            }else{
                ocupacao.acquire();
                lock.lock();
                try{
                    cadeiras--;
                    System.out.println("O cliente " + nome + " entrou na barbearia.");
                    if (dormindo){
                        System.out.println("... O barbeiro foi acordado pelo cliente " + nome + " ...");
                        dormindo = false;
                    }
                    if (cadeiras == 0){
                        System.out.println("... Barbearia cheia ...");
                    }
                }finally{
                    lock.unlock();
                    Thread.sleep(1000);
                    lock.lock();
                    try{
                        cadeiras ++;
                        System.out.println(nome + " saiu da barbearia após do corte.");
                        if (cadeiras == 5){
                            dormindo = true;
                            System.out.println("... O barbeiro voltou a dormir ...");
                        }
                    }finally{
                        lock.unlock();
                    }
                }
            }
        }
    }

    public static class Clientes implements Runnable{
        private Barbearia barbearia;
        private String nome;

        public Clientes(Barbearia barbearia, String nome){
            this.nome = nome;
            this.barbearia = barbearia;
        }

        public void run(){
            try{
                barbearia.cortarCabelo(nome);
            }catch(InterruptedException e){
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) throws InterruptedException{
        Barbearia pega_barbeiro = new Barbearia();

        Clientes joao = new Clientes(pega_barbeiro, "Joao");
        Thread joao_thread = new Thread(joao);
        joao_thread.start();

        Clientes maria = new Clientes(pega_barbeiro, "Maria");
        Thread maria_thread = new Thread(maria);
        maria_thread.start();

        Clientes pedro = new Clientes(pega_barbeiro, "Pedro");
        Thread pedro_thread = new Thread(pedro);
        pedro_thread.start();

        Clientes luana = new Clientes(pega_barbeiro, "Luana");
        Thread luana_thread = new Thread(luana);
        luana_thread.start();

        Clientes juliana = new Clientes(pega_barbeiro, "Juliana");
        Thread juliana_thread = new Thread(juliana);
        juliana_thread.start();

        Clientes thomaz = new Clientes(pega_barbeiro, "Thomaz");
        Thread thomaz_thread = new Thread(thomaz);
        thomaz_thread.start();

        Clientes welton = new Clientes(pega_barbeiro, "welton");
        Thread welton_thread = new Thread(welton);
        welton_thread.start();

    }
}