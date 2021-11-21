import java.util.concurrent.*;

public class Main implements Runnable{

	static ExecutorService executorService = Executors.newFixedThreadPool(100);

    public void run() {
    	
    }

	public static void main(String args[]){

        executorService.execute(new Client());
        executorService.shutdown();
	}
}