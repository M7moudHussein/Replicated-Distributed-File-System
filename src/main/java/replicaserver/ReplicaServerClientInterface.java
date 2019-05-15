package replicaserver;

import masterserver.FileData;
import masterserver.FileDistribution;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReplicaServerClientInterface extends Remote {
    /**
     *
     * @param txnID
     *            : the ID of the transaction to which this message relates
     * @param msgSeqNum
     *            : the message sequence number. Each transaction starts with
     *            message sequence number 1.
     * @param data
     *            : data to write in the file
     * @param distribution
     *            : contains the meta data of the replicas that has this file
     * @return message with required info
     * @throws IOException
     * @throws RemoteException
     */
    public WriteMessage write(long txnID, long msgSeqNum, FileData data, FileDistribution distribution) throws RemoteException, IOException, NotBoundException;

    public FileData read(String fileName) throws FileNotFoundException, RemoteException, IOException;

    /**
     *
     * @param txnID
     *            : the ID of the transaction to which this message relates
     * @param numOfMsgs
     *            : Number of messages sent to the server
     * @return true for acknowledgment
     * @throws MessageNotFoundException
     * @throws RemoteException
     */
    public boolean commit(long txnID, long numOfMsgs) throws MessageNotFoundException,
            RemoteException, IOException, NotBoundException;

    /**
     * * @param txnID: the ID of the transaction to which this message relates
     *
     * @return true for acknowledgment
     * @throws RemoteException
     */
    public boolean abort(long txnID) throws RemoteException, NotBoundException;
}
