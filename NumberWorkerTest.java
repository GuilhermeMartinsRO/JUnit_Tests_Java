package Teste;
import Main.NumberList;
import Main.NumberWorker;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class NumberWorkerTest {

    @Test
    void testWorkerInterruptStops() throws InterruptedException {

        NumberList <Number> lista = new NumberList<>();//crio a lista
        NumberWorker <Number> worker = new NumberWorker(lista,"thread worker");//crio a thread

        worker.start();//faço ela dar start no run e, supostamente, entrar no wait pois a lista estará vazia

        Thread.sleep(100);//faz a thread principal esperar para termos certeza que a worker está em wait
        worker.interrupt();//dou o interrupt para cair na exceção do run
        worker.join(1000);//faz a thread principal esperar a worker terminar de executar, pois
        //quando da o interrupt ela só pausa, até entrar na exceção há um tempo, e se eu não parar a principal,
        //ela será mais rápida e pode ser que a worker ainda não matou a thread

        assertFalse(worker.isAlive());

    }

    @Test
    void testWorkerProcessNumbers() throws InterruptedException {

        NumberList <Number> lista = new NumberList<>();
        NumberWorker <Number> worker = new NumberWorker (lista,"thread worker");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream oldOut = System.out;
        System.setOut(new PrintStream(baos));

        try {

            worker.start();

            Thread.sleep(100);

            lista.add(100);

            Thread.sleep(100);

            String out = baos.toString();
            assertTrue(out.contains("Adicionado: 100"));
            assertTrue(out.contains("Removido: 100"));
            assertTrue(out.contains("thread worker processando numero: 100"));

        } catch (InterruptedException e) {

            throw new RuntimeException(e);

        } finally {

            System.setOut(oldOut);
            worker.interrupt();
            worker.join(1000);

        }

    }

    @Test
    void testWorkerConcurrency() throws InterruptedException {

        NumberList <Number> lista = new NumberList<>();
        CountDownLatch latch = new CountDownLatch(50);

        Runnable consumer = () ->{
            try {

                while(!Thread.currentThread().isInterrupted()){

                    Number n = lista.remove();
                    latch.countDown();
                    Thread.sleep(10);

                }

            } catch (InterruptedException _) {}
        };

        Thread w1 = new Thread(consumer, "W1");
        Thread w2 = new Thread(consumer, "W2");

        w1.start();
        w2.start();

        for (int i = 0; i < 50; i++) lista.add(i);

        // aguardar até os 50 serem processados (timeout seguro)
        boolean allProcessed = latch.await(5, TimeUnit.SECONDS);

        // interromper e aguardar término das threads
        w1.interrupt();
        w2.interrupt();
        w1.join(500);
        w2.join(500);

        assertTrue(allProcessed, "Timeout: nem todos os 50 itens foram processados");

    }


}
