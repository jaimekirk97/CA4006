import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.Naming;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.*;
import java.lang.Thread;

public class Client implements Runnable{


    static ExecutorService serverExecute = Executors.newFixedThreadPool(1);
    static String row;
    static BufferedReader csvReader;
    static String userName;
    static String password;
    static AuthenticationInterface authenticationInterface = null;
    static Scanner scanner = new Scanner(System.in);
    static SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");

    public static List<String> total = new ArrayList<String>();

    public void run() {

        LoginSession();
    }

    public static void testMode(){
        int totalOrders = calTotalOrders();
        Random random = new Random();
        try{
            BufferedReader userReader = new BufferedReader(new FileReader("login.csv"));
            while((row = userReader.readLine()) != null){
                String[] data = row.split(",");
                userName = data[0];
                System.out.println(data[0]);
                String product = total.get(random.nextInt(total.size()));
                String[] productInfo = product.split(",");
                product = productInfo[0];

                System.out.println("Checking stock for products");
                Calendar start = Calendar.getInstance();
                String current = Integer.toString(start.get(Calendar.DATE)) + "-" + Integer.toString(start.get(Calendar.MONTH)+1) + "-" + Integer.toString(start.get(Calendar.YEAR));
                Date startDate = formatter.parse(current);
                start.setTime(startDate);
                String endTime = Integer.toString(random.nextInt(31)) + "-" + Integer.toString(random.nextInt(12)) + "-" + "2022";
                Date endDate = formatter.parse(endTime);
                Calendar end = Calendar.getInstance();
                end.setTime(endDate);
                int stock = Server.calculateStock(start, end, productInfo);
                System.out.println("Predicted stock for " + product + " on " + endDate + ": "  + stock);
                Thread.currentThread().sleep(random.nextInt(5000));

                System.out.println("Performing test orders for products");
                String testDate = Integer.toString(random.nextInt(31)) + "-" + Integer.toString(random.nextInt(12)) + "-" + "2022";
                makeOrder(product,random.nextInt(10),testDate);
                Thread.currentThread().sleep(random.nextInt(5000));

                System.out.println("Cancelling order");
                cancelOrder(random.nextInt(totalOrders));
                Thread.currentThread().sleep(random.nextInt(5000));

                System.out.println("Viewing orders for " + userName);
                viewOrders();
            }
        }
        catch (Exception e)
        {

        }
    }

    public synchronized static void cancelOrder(int orderNumber){
        try {
            Boolean cancelled = false;
            csvReader = new BufferedReader(new FileReader("orders.csv"));
            List<List<String>> order = new ArrayList<List<String>>();
            while ((row = csvReader.readLine()) != null){
                String[] data = row.split(",");
                if (orderNumber == Integer.parseInt(data[0].trim()) && !data[5].equals("cancelled") && userName.equals(data[1].trim()))
                {
                    List<String> info = Arrays.asList(data[0], data[1],data[2],data[3],data[4],"cancelled");
                    List<List<String>> productLst = new ArrayList<List<String>>();
                    order.add(info);
                    BufferedReader prodReader = new BufferedReader(new FileReader("products.csv"));
                    while ((row = prodReader.readLine()) != null){
                        String[] products = row.split(",");
                        if (products[0].trim().equals(data[2].trim())){
                            int newTotal = Integer.parseInt(data[3].trim()) + Integer.parseInt(products[1].trim());
                            List<String> prod = Arrays.asList(products[0], Integer.toString(newTotal) ,products[2],products[3]);
                            productLst.add(prod);
                            cancelled = true;
                        }
                        else{
                            List<String> prod = Arrays.asList(products[0], products[1] ,products[2],products[3]);
                            productLst.add(prod);
                        }
                    }
                    Server.cancelOrder(productLst,"products.csv");
                }
                else {
                    List<String> info = Arrays.asList(data[0], data[1],data[2],data[3],data[4],data[5]);
                    order.add(info);
                }
            }
            if (cancelled == true){
                Server.cancelOrder(order,"orders.csv");
                System.out.println("Order Cancelled");
            }
            else{
                System.out.println("Order couldn't be cancelled");
            }
        }
        catch (Exception e)
        {

        }  
    }

    public synchronized static void makeOrder(String product, int quantity, String delDate){
        int totalOrders = calTotalOrders();
        for(int i = 0; i < total.size(); i++){
            String[] currentProduct = total.get(i).split(",");
            List<String> checker = new ArrayList<>(Arrays.asList(currentProduct));
            if(checker.contains(product))
            {
                int currentStock = Integer.parseInt(checker.get(1).replaceAll("\\s+",""));
                String newStock = String.valueOf(Server.SubtractOrders(currentStock, quantity));
                if (quantity <= Integer.valueOf(newStock)){
                    // Remove old stock number
                    checker.remove(1);
                    // Remove old row from products
                    total.remove(i);
                    // Replace old stock number with new one
                    checker.add(1, newStock);
                    // Add new row to products
                    total.add(i, String.join(",",checker));
                    UpdateStock(total);
                    System.out.println("Processing Order");
                    totalOrders += 1;
                    List<String> info = Arrays.asList(Integer.toString(totalOrders), userName,product,Integer.toString(quantity),delDate,"active");
                    List<List<String>> order = new ArrayList<List<String>>();
                    order.add(info);
                    Server.writeCSV(order,"orders.csv");
                    break;
                }
            }
        }
    }

    public static int calTotalOrders(){
        int total = 0;
        try
        {
            csvReader = new BufferedReader(new FileReader("orders.csv"));
            while ((row = csvReader.readLine()) != null){
                total += 1;
            }
        }
        catch (Exception e){
        }
        return total;
    }

    public synchronized static void UpdateStock(List<String> product){

        try {
            FileWriter csvWriter = new FileWriter("products.csv");
            for (int i = 0; i < product.size(); i++){
                //System.out.println(product.get(i));
                csvWriter.append(product.get(i));
                csvWriter.append("\n");
            }
            csvWriter.close();
        }
        catch (Exception e){

        }
    }

    public static void viewOrders(){
        try{
            csvReader = new BufferedReader(new FileReader("orders.csv"));
            while ((row = csvReader.readLine()) != null){
                String[] data = row.split(",");
                if (data[1].equalsIgnoreCase(userName)){
                    System.out.println(row);
                }
            }
        }
        catch (Exception e)
        {

        }
    }

    public static void readProducts(){
        try{
            csvReader = new BufferedReader(new FileReader("products.csv"));
            while((row = csvReader.readLine()) != null){
                total.add(row);
            }           
        }   
        catch (Exception e){
        }
    }

    public static void LoginSession(){

        readProducts();
        //System.out.println(total);

        serverExecute.execute(new Server());

        try {
            Boolean status = false;
            System.out.println("Would you like to 'Login' or run 'Test mode'");
            String command = scanner.nextLine();
            authenticationInterface = (AuthenticationInterface) Naming.lookup("rmi://localhost:5000/authent");

            if (command.equalsIgnoreCase("Login"))
            {
                while(!status){

                    System.out.println("Enter the username: ");
                    userName = scanner.nextLine();

                    System.out.println("Enter the password: ");
                    password = scanner.nextLine();

                    status = authenticationInterface.authentication(userName, password);
                    if(status) {
                        System.out.println("You are an authorized user...");
                        int totalOrders = calTotalOrders();
                        System.out.println("Please enter a command or enter 'log out' to end session:");
                        command = scanner.nextLine();
                        while(!command.equalsIgnoreCase("log out")){
                            csvReader = new BufferedReader(new FileReader("products.csv"));
                            if(command.equalsIgnoreCase("product stock")){
                                System.out.println("Would you like stock for 'Individual' stock or 'all' stock");
                                command = scanner.nextLine();
                                if (command.equalsIgnoreCase("Individual")){
                                    System.out.println("Please enter the product you are want stock information for.");
                                    String product = scanner.nextLine();
                                    while ((row = csvReader.readLine()) != null){
                                        String[] data = row.split(",");
                                        if(product.equalsIgnoreCase(data[0]))
                                        {
                                            System.out.println("Please enter the date want stock for " + product + " in the format dd-MM-yyyy");
                                            Calendar start = Calendar.getInstance();
                                            String current = Integer.toString(start.get(Calendar.DATE)) + "-" + Integer.toString(start.get(Calendar.MONTH)+1) + "-" + Integer.toString(start.get(Calendar.YEAR));
                                            Date startDate = formatter.parse(current);
                                            Date endDate = formatter.parse(scanner.nextLine());
                                            start.setTime(startDate);
                                            Calendar end = Calendar.getInstance();
                                            end.setTime(endDate);
                                            int predictedStock = Server.calculateStock(start, end, data);

                                            System.out.println("Predicted stock for " + data[0] + " on " + endDate + ": "  + predictedStock);
                                            csvReader.close();
                                            break;
                                        }
                                    }
                                }
                                else if(command.equalsIgnoreCase("all")){
                                    while ((row = csvReader.readLine()) != null){
                                        String[] data = row.split(",");
                                        Calendar start = Calendar.getInstance();
                                        String current = Integer.toString(start.get(Calendar.DATE)) + "-" + Integer.toString(start.get(Calendar.MONTH)+1) + "-" + Integer.toString(start.get(Calendar.YEAR));
                                        Date startDate = formatter.parse(current);
                                        start.setTime(startDate);
                                        Date endDate = formatter.parse(current);
                                        Calendar end = Calendar.getInstance();
                                        end.setTime(endDate);
                                        end.add(Calendar.MONTH, 6);
                                        int predictedStock = Server.calculateStock(start, end, data);

                                        System.out.println("Predicted stock for " + data[0] + ": " + predictedStock);
                                    }
                                    csvReader.close();
                                }                      
                            }
                            else if (command.equalsIgnoreCase("make order")){
                                csvReader = new BufferedReader(new FileReader("products.csv"));
                                System.out.println("What product would you like to order?");
                                String product = scanner.nextLine();
                                System.out.println("How many would you like to order?");
                                int quantity = scanner.nextInt();
                                System.out.println("What date do you want your items for?");
                                String delDate = scanner.next();
                                makeOrder(product,quantity,delDate);
                                csvReader.close();
                            }
                            else if(command.equalsIgnoreCase("view orders"))
                            {
                                viewOrders();
                            }
                            else if(command.equalsIgnoreCase("cancel order"))
                            {
                                System.out.println("Please enter the order Number");
                                int orderNumber = scanner.nextInt();
                                cancelOrder(orderNumber);
                            }
                            else if(command.equalsIgnoreCase("help"))
                            {
                                System.out.println("Make order: Order any available product");
                                System.out.println("Product stock: View stock for an 'Individual' product or for 'all' products for next six months");
                                System.out.println("View orders: View all your existing orders");
                                System.out.println("Cancel order: Cancel any of your active orders");
                            }
                            System.out.println("Please enter a command or enter 'log out' to end session:");
                            command = scanner.nextLine();   
                        }
                    }
                    else{
                        System.out.println("Login details don't match an existing user");
                    } 
                }
            }
            else if (command.equalsIgnoreCase("test mode"))
            {
                System.out.println("Starting test mode: ");
                testMode();
            }
            else {
                System.out.println("Please enter valid command");
                LoginSession();
            }
        }    
        catch (Exception e) 
        {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
        serverExecute.shutdown();
        System.out.println("Logging out");
        System.exit(0);
    }
}