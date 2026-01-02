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

        NumberList <Number> lista = new NumberList<>();
        NumberWorker <Number> worker = new NumberWorker(lista,"thread worker");

        worker.start();

        Thread.sleep(100);
        worker.interrupt();
        worker.join(1000);

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

        boolean allProcessed = latch.await(5, TimeUnit.SECONDS);

        w1.interrupt();
        w2.interrupt();
        w1.join(500);
        w2.join(500);

        assertTrue(allProcessed, "Timeout: nem todos os 50 itens foram processados");

    }


}

