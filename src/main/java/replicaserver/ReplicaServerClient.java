package replicaserver;

import masterserver.FileContent;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ReplicaServerClient extends UnicastRemoteObject implements ReplicaServerClientInterface {
    protected ReplicaServerClient() throws RemoteException {
    }

    @Override
    public WriteMessage write(long txnID, long msgSeqNum, FileContent data) throws RemoteException, IOException {
        return null;
    }

    @Override
    public FileContent read(String fileName) throws FileNotFoundException, IOException, RemoteException {
        return null;
    }

    @Override
    public boolean commit(long txnID, long numOfMsgs) throws MessageNotFoundException, RemoteException {
        return false;
    }

    @Override
    public boolean abort(long txnID) throws RemoteException {
        return false;
    }
}
