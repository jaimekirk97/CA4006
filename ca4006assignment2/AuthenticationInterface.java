import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AuthenticationInterface extends Remote {

	public boolean authentication(String username, String password) throws RemoteException;

}