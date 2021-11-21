import java.lang.Thread;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class Controller implements Runnable{

	static ExecutorService executorService = Executors.newFixedThreadPool(100);

	static Random rand = new Random();

	public static int numOfMissions;

	public void run() {

	}

	public static void sendInstructions(){

		String commands[] = new String[] {"Mission ID: " + Thread.currentThread().getId() + " - Beware of passing debris.","Mission ID: " + Thread.currentThread().getId() + " - Check fuel levels."};

    	int randomElement = rand.nextInt(commands.length);

    	System.out.println(commands[randomElement]);

	}

	public static void sendReports(Component compo){

		int[] componentList = compo.components;

		String[] possibleReports = new String[] {"Mission Component Fuel with (Thread ID) " + Thread.currentThread().getId() + " - Fuel load is: " + componentList[0] + " Time: " + Mission.startTime,
		"Mission Component Control Systems with (Thread ID) " + Thread.currentThread().getId() + " - Control systems are functioning. Time: " + Mission.startTime,
		"Mission Component Thrusters with (Thread ID) " + Thread.currentThread().getId() + " - Thruster conditions are good. Time: " + Mission.startTime};
		
		int randomElement = rand.nextInt(possibleReports.length);
    	System.out.println(possibleReports[randomElement]);
	}

	public static void instrumentData(Component compo){

		String[] instrumentList = compo.instruments;

		String[] possibleReports = new String[] {"Mission ID: " + Thread.currentThread().getId() + " - " + instrumentList[0] + ": Light intensity is normal", "Mission ID: " + Thread.currentThread().getId() + " - " + instrumentList[1] + ": Magnetic field strength stable.", "Mission ID: " + Thread.currentThread().getId() + " - " + instrumentList[2] + ": No abnormal readings."};
		int randomElement = rand.nextInt(possibleReports.length);
    	System.out.println(possibleReports[randomElement]);
	}

	public static Boolean changeStages(Thread missionThread, Network net, Component compo){
		try{	
			String[] stages = {"Boost", "Interplanetary transit", "Entry/Landing", "Exploration(rover)"};
			for(int i=0; i<stages.length;i++){
				if(rand.nextInt(100) > 10){
					System.out.println("Mission ID: " + Thread.currentThread().getId() + " - is entering " + stages[i] + " stage");
					if(rand.nextInt(100) <= net.availability){
						sendReports(compo);
						missionThread.sleep(((rand.nextInt(10000)+100)/1000)/(long)net.networkSpeed);
						instrumentData(compo);
						missionThread.sleep(((rand.nextInt(100000000)+100000)/1000)/(long)net.networkSpeed);
						if(rand.nextInt(100) <= 30){
							sendInstructions();
						}
					}
					else{
						System.out.println("Network for mission " + Thread.currentThread().getId() + " not currently available");
					}
			}
				else if(rand.nextInt(100) > 25){
					missionThread.sleep(softwareUpdate(net));
					System.out.println("Mission ID: " + Thread.currentThread().getId() + " - " + stages[i] + " stage failed but is been recovered");
					System.out.println("Mission ID: " + Thread.currentThread().getId() + " - Software update complete");
			}
				else{
					System.out.println(Thread.currentThread().getId() + ": " + stages[i] + " stage failed");
					return false;
			}
		}
	}
	catch(InterruptedException e){
		
	}
	return true;
	}

	public static float calculateTime(long current, float total){
		long end = System.currentTimeMillis();
		float sec = (end - current) / 1000F;
		current = System.currentTimeMillis();
		total = total + sec;
		return total;
	}

	public static int softwareUpdate(Network net){
		int update = rand.nextInt(100);
		double networkSpeed = net.networkSpeed;
		double time = (update/networkSpeed);
		int updateInt = (int)time;
		return updateInt;
	}

	public static void createMissions(){
		long current = System.currentTimeMillis();
		float total = Mission.startTime;

		Lock networkLock = new ReentrantLock();
		networkLock.lock();
		Network net = new Network();
		networkLock.unlock();

		Lock compLock = new ReentrantLock();
		compLock.lock();
		Component compo = new Component();
		compLock.unlock();

		Boolean success = Controller.changeStages(Thread.currentThread(), net, compo);
		Mission.setTime(current, total);
		Mission.setDestination(compo.fuel);
		if(success == true){
			System.out.println("End Time: " + Mission.getStartTime() + " - " + Thread.currentThread().getId() + ": Mission success - " + Mission.destination + " reached");
		}
		else{
			System.out.println("End Time: " + Mission.getStartTime() + " - " + Thread.currentThread().getId() + ": Mission failed");
		}
	}

	public static void main(String[] args){

		Scanner scan = new Scanner(System.in);

		numOfMissions = scan.nextInt();

		try{
			PrintStream out = new PrintStream(new FileOutputStream("output.dat"));
			System.setOut(out);
			for(int i = 1; i <= numOfMissions; i++){

				executorService.execute(new Mission());
			}

		}catch (FileNotFoundException ex){

			ex.printStackTrace();
		}

		executorService.shutdown();
	}
}