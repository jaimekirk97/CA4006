import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.*;

public class Authentication extends UnicastRemoteObject implements AuthenticationInterface{

	protected Authentication() throws RemoteException{

	}

    public boolean authentication(String username, String password) throws RemoteException{
        String row;
        try{
            BufferedReader csvReader = new BufferedReader(new FileReader("login.csv"));
            while ((row = csvReader.readLine()) != null) {
                String[] data = row.split(",");
                if ((username != null && !username.isEmpty()) && (password != null && !password.isEmpty())){
                    if((username.equalsIgnoreCase(data[0])) && (password.equalsIgnoreCase(data[1]))) {
                        return true;
                    }
                }
            }
        }
        catch(Exception e){

        }
        return false;
    }
}