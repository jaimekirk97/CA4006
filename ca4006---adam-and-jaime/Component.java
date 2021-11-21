import java.lang.Thread;
import java.util.*;

public class Component implements Runnable{

	Random rand = new Random();

	// Generate random fuel load from 0-20
	int fuelList[] = {1000,2000,3000,4000,5000,6000,7000};
	int fuel = fuelList[rand.nextInt(7)];

	// Random thrusters from 0-4
	int thrusters = rand.nextInt(4);

	int powerplants = rand.nextInt(3);

	int controlSystems = rand.nextInt(3);

	String[] instruments = {"Spectrometer", "Magnetometer", "Interferometers"};

	int components[] = {fuel, thrusters, powerplants, controlSystems};

	public void run() {

	}

	public int[] buildList(){

		return components;
	}

	public int getFuel()
	{
		return fuel;
	}
}