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
                // esse remove() tem que bloquear até alguém chamar lista.add()
                result[0] = lista.remove();
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
            }
        });

        t.start();

        // coloquei a thread principal para dormir para que de tempo
        //da threat t ser bloqueada pelo remove() (se não funcionaria incorretamente)
        //eu praticamente forço t entrar no wait()
        Thread.sleep(100);

        // aqui eu dou o notify e acordo a thread t
        lista.add(42);

        // isso faz com que a thread principal espere (num tempo máximo de 1000 milisegundos)
        // a thread t executar, pois do contrário a thread principal seguiria com o código
        // e poderia ser que a thread t não teria tido tempo de completar a remoção e definir
        //result[0]
        t.join(1000);
        //se o código der errado esse tempo estoura e result[0] se torna um valor nulo


        // se for nulo vai mostrar a mensagem, se não for nulo, o result[0] será
        // atualizado com o valor adicionado
        assertNotNull(result[0], "O remove() não retornou valor (thread pode não ter sido acordada)");
        assertEquals(42, result[0].intValue());
    }


}
