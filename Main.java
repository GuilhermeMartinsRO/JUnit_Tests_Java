package Main;

public class Main {

	public static void main(String[] args) {
        NumberList<Number> numberList = new NumberList<>();

        NumberWorker<Number> worker = new NumberWorker<Number>(numberList, "Worker-1");
        worker.start();

        numberList.add(10);
        numberList.add(3.14);
        numberList.add(25);
        numberList.add(7.5);

        try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {

			e.printStackTrace();
		}

        worker.interrupt();
	}
}

