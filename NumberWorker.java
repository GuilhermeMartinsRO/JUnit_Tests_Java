package Main;

public class NumberWorker<T extends Number> extends Thread {

    private final NumberList<T> numberList;
    private final String threadName;

    public NumberWorker(NumberList<T> numberList, String threadName) {
        super(threadName);
        this.numberList = numberList;
        this.threadName = threadName;
    }

    @Override
    public void run() {
        try {
            while (true) {
                T number = numberList.remove();
                System.out.println(threadName + " processando numero: " + number);

                // Simula algum trabalho
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            System.out.println(threadName + " finalizada.");
        }
    }
}
