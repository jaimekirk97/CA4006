import java.lang.Thread;
import java.util.*;

public class Network implements Runnable{

	List<Integer> speedList = Arrays.asList(2000000,2000,2);
    Random rand = new Random();
    int randNum = rand.nextInt(3);
    public double networkSpeed = speedList.get(randNum);

    List<Double> availList = Arrays.asList(80.0,90.0,99.9);
	public double availability = availList.get(randNum);

	public void run() {
		 
	}

	public double getSpeed(){
		return networkSpeed;
	}

	public double getAvailability(){
		return availability;
	}
}