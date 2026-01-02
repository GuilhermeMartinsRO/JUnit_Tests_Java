package Main;

import java.util.ArrayList;
import java.util.List;

public class NumberList<T extends Number> {

    private final List<T> list = new ArrayList<>();

    public synchronized void add(T value) {
        list.add(value);
        System.out.println("Adicionado: " + value);
        notify();
    }

    public synchronized T remove() throws InterruptedException {
        while (list.isEmpty()) {
            wait(); 
        }
        T value = list.remove(0);
        System.out.println("Removido: " + value);
        return value;
    }
}
