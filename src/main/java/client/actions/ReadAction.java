package client.actions;

import client.response.Response;
import client.response.ResponseError;
import client.transaction.Transaction;
import lombok.AllArgsConstructor;
import masterserver.FileData;
import masterserver.MasterServerClientInterface;
import replicaserver.ReplicaMetadata;
import replicaserver.ReplicaServerClientInterface;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@AllArgsConstructor
public class ReadAction implements Action {
    private final String fileName;

    @Override
    public Response executeAction(MasterServerClientInterface masterServerClientInterface,
                                  long txnID, long msgSeqNum) {
        ReplicaMetadata primaryReplicaMetadata = null;
        try {
            primaryReplicaMetadata = masterServerClientInterface.read(fileName)[0];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new Response(ResponseError.FILE_NOT_FOUND);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new Response(ResponseError.MASTER_NO_RESPONSE);
        }
        ReplicaServerClientInterface primaryReplicaInterface = null;
        try {
            primaryReplicaInterface = primaryReplicaMetadata.getReplicaInterface();
        } catch (RemoteException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_REMOTE_ERROR);
        } catch (NotBoundException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_NOT_BOUND);
        }
        FileData result = null;
        try {
            result = primaryReplicaInterface.read(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return new Response(ResponseError.FILE_NOT_FOUND);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_REMOTE_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_IO_EXCEPTION);
        }
        return new Response(result.toString());
    }

    @Override
    public String toString() {
        return "[Read Action] FileName:{" + fileName + "}";
    }
}
