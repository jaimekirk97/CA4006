import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
        
public class Server implements Runnable{

    Calendar c = Calendar.getInstance();
    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
    String oldDate = "12-04-2021";
    String newDate;
        
    public Server() {

    }

    public void run(){

        try{
            c.setTime(formatter.parse(oldDate));
            while(true){
                TimeUnit.SECONDS.sleep(1);
                c.add(Calendar.DAY_OF_MONTH, 1);
                newDate = formatter.format(c.getTime()); 
                //System.out.println(newDate);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static int SubtractOrders(int currentStock, int quantity){
        return(currentStock - quantity);
    }

    public static int calculateStock(Calendar start, Calendar end, String[] data){
    int stock = Integer.parseInt(data[1].trim());
    Boolean restocked = false;
    int currentMonth = start.get(Calendar.MONTH) + 1;

    for (Date date = start.getTime(); start.before(end); start.add(Calendar.DATE, 1), date = start.getTime()) {
        if (restocked == false && currentMonth == start.get(Calendar.MONTH)+1){
            if (start.get(Calendar.DATE) == Integer.parseInt(data[2].trim()) && start.get(Calendar.DATE)!=1)
            {
                stock = stock + Integer.parseInt(data[3].trim());
                restocked = true;
            }
        }
        else if (start.get(Calendar.DATE)==Integer.parseInt(data[2].trim()) && restocked == false && currentMonth != start.get(Calendar.MONTH)+1){
            stock = stock + Integer.parseInt(data[3].trim());
            restocked = false;
            currentMonth += 1;
        }
        else if(start.get(Calendar.DATE)==1 && currentMonth <= 12){
            restocked = false;
            currentMonth += 1;
        } 
        else if (currentMonth == 13){
            currentMonth = 1;
        }
    }
    return stock;
    }

    public static void writeCSV(List<List<String>> order, String file){
        try {
            PrintWriter csvWriter = new PrintWriter(new FileWriter(file,true));
            for (List<String> item : order){
                String newItem = String.join(",",item);
                csvWriter.append(newItem);
            }
            csvWriter.append("\n");
            csvWriter.close();
            System.out.println("Order complete");
        }
        catch (Exception e){

        }
    }

    public static void cancelOrder(List<List<String>> order, String file){
        try {
            FileWriter csvWriter = new FileWriter(file);
            for (List<String> item : order){
                String newItem = String.join(",",item)+"\n";
                csvWriter.append(newItem);
            }
            csvWriter.close();
        }
        catch (Exception e){

        }
    }

    private static final int PORT = 5000;

    public static void main(String args[]) {
        
        try {

            AuthenticationInterface authenticationInterface = new Authentication();
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.bind("authent", authenticationInterface);

            System.out.println("Authentication Service running!");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}