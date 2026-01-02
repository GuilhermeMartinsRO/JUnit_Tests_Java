package Teste;
import Main.NumberList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NumberListTest{

    private final NumberList <Number> lista = new NumberList();

    @Test
    void TestAddDiffentNumberTypes(){

        lista.add(10);
        lista.add(2.5);
        lista.add(2.6f);

        try{

            Number n1 = lista.remove();
            Number n2 = lista.remove();
            Number n3 = lista.remove();

            assertEquals(10,n1.intValue());
            assertEquals(2.5,n2.doubleValue());
            assertEquals(2.6,n3.floatValue(),1e-6);


        }catch(Exception e){
            System.out.println(e.getMessage());
        }

    }

    @Test
    void TestRemove() throws InterruptedException {

        for (int i = 0; i < 5; i++) lista.add(i);
        for (int i = 0; i < 5; i++) {
            Number n = lista.remove();
            assertEquals(i, n.intValue());
        }

    }

    @Test
    void testGenericsAcceptDifferentNumberTypes() throws InterruptedException {
        NumberList<Integer> ints = new NumberList<Integer>();
        NumberList<Double> doubles = new NumberList<Double>();
        NumberList<Float> floats = new NumberList<Float>();

        ints.add(1);
        doubles.add(1.5);
        floats.add(2.5f);

        assertEquals(1, ints.remove().intValue());
        assertEquals(1.5, doubles.remove().doubleValue(), 1e-9);
        assertEquals(2.5f, floats.remove().floatValue(), 1e-6);
    }

    @Test
    void testRemoveWaitsWhenEmpty() throws Exception {

        final Number[] result = new Number[1];

        Thread t = new Thread(() -> {
            try {
                result[0] = lista.remove();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        t.start();

        Thread.sleep(100);

        lista.add(42);
        
        t.join(1000);
        
        assertNotNull(result[0], "O remove() não retornou valor (thread pode não ter sido acordada)");
        assertEquals(42, result[0].intValue());
    }


}

