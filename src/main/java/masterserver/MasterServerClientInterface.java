package masterserver;

import replicaserver.ReplicaMetadata;
import replicaserver.WriteMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterServerClientInterface extends Remote {
    /**
     * Read file from server
     *
     * @param fileName
     * @return the addresses of  of its different replicas
     * @throws FileNotFoundException
     * @throws IOException
     * @throws RemoteException
     */
    public ReplicaMetadata[] read(String fileName) throws FileNotFoundException, RemoteException;

    /**
     * Start a new write transaction
     *
     * @param fileName
     * @return the required info
     * @throws RemoteException
     * @throws IOException
     */
    public WriteMessage write(String fileName, FileData data, long txnID) throws RemoteException, IOException;


    public long getNewTransactionID() throws  RemoteException;
}
