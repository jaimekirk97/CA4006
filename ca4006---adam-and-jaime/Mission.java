import java.lang.Thread;
import java.util.*;


public class Mission implements Runnable{

	Random rand = new Random();

	public static float startTime = 0;

	static String uniqueID = UUID.randomUUID().toString();

	static Map<Integer, String> destinationMap = Map.of(1000,"Moon",2000, "Venus",3000, "Mars", 4000, "Mercury", 5000,"Jupiter",6000,"Saturn",7000,"Uranus", 8000,"Neptune");
	static String destination;

	public void run() {
		Controller.createMissions();
	}


	public static String getID(){
		return uniqueID;
	}

	public static float getStartTime(){
		return startTime;
	}

	public static void setDestination(int fuel){
		destination = destinationMap.get(fuel);
	}

	public static void setTime(long current, float total){
		startTime = Controller.calculateTime(current, total);
	}
}