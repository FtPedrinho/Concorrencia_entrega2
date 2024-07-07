import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    
    //Classe da barbearia
    public static class Barbearia {
        private static final Semaphore assento = new Semaphore(1);
        private static final Semaphore filaEspera = new Semaphore(6);
        private int pessoas = 0;
        private final Lock lock = new ReentrantLock();
        
        //    A variavel pessoas será usada para determinar a posição na fila de espera e se o barbeiro estará dormindo 
        // ou acordado. Os dois semáforos serão utilizados para sinalizar a quantidade de pessoas na barbearia (contando 
        // com quem está cortando. O Lock foi usado para manipular a variavel "pessoas".
        
        public void cortarCabelo(String nome, int numero) throws InterruptedException {
            System.out.println(nome + numero + ": chegou na barbearia...");

            // Verificar se a barbearia está lotada.
            if (!filaEspera.tryAcquire()) {
                Thread.sleep(500);
                System.out.println(nome + numero + ": foi embora porque estava cheio.");
                return;
            }
            
            // Manipulação da variável "pessoas".
            lock.lock();
            try {
                if(pessoas==0){
                    System.out.println("... Barbeiro acordou ...");
                }else{
                    System.out.println(nome + numero + " está aguardando na fila na posição: " + pessoas);
                }
                pessoas++;
            }finally{
                lock.unlock();
            }
            
            // Cliente ocupa a cadeira do barbeiro.
            assento.acquire();
            System.out.println("Chegou a vez de " + nome + numero + " cortar o cabelo.");
            
            // Simulando tempo de corte.
            Thread.sleep(2000);
            System.out.println(nome + numero + ": Saiu da barbearia.");

            // Comandos de saída do cliente e verificação da quantidade de pessoas.
            assento.release();
            filaEspera.release();
            pessoas--;
            if(pessoas==0){
                System.out.println("... Barbeiro voltou a dormir ...");
            }
        }
    }

    // Classe dos clientes e função de jogar na barbearia.
    public static class Cliente implements Runnable {
        private final Barbearia barbearia;
        private final String nome;
        private final int numero;

        public Cliente(Barbearia barbearia, String nome, int numero) {
            this.barbearia = barbearia;
            this.nome = nome;
            this.numero = numero;
        }

        public void run() {
            try {
                barbearia.cortarCabelo(nome, numero);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Barbearia barbearia = new Barbearia();

        // Criando os clientes da barbearia (10 clientes a cada 15 segs).
        for (int i = 0; i < 100; i++) {
            Cliente cliente = new Cliente(barbearia, "Cliente", i + 1);
            Thread clienteThread = new Thread(cliente);
            clienteThread.start();
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
