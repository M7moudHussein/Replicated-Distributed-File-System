package master_server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterServerClientInterface extends Remote {
	/**
	 * Read file from server
	 * 
	 * @param file_name
	 * @return the addresses of  of its different replicas
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws RemoteException
	 */
	public ReplicaLoc[] read(String file_name) throws FileNotFoundException,
			IOException, RemoteException;

	/**
	 * Start a new write transaction
	 * 
	 * @param file_name
	 * @return the requiref info
	 * @throws RemoteException
	 * @throws IOException
	 */
	public WriteMsg write(String file_name, FileContent data) throws RemoteException, IOException;

}
