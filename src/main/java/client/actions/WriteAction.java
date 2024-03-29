package client.actions;

import client.response.Response;
import client.response.ResponseError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import masterserver.FileData;
import masterserver.MasterServerClientInterface;
import replicaserver.ReplicaMetadata;
import replicaserver.ReplicaServerClientInterface;
import replicaserver.WriteMessage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@AllArgsConstructor
@Setter
@Getter

public class WriteAction implements Action {
    private final String fileName;
    private final String content;

    @Override
    public Response executeAction(MasterServerClientInterface masterServerClientInterface,
                                  long txnID, long msgSeqNum) {

        WriteMessage writeMessage;
        FileData data = new FileData(fileName, content);
        try {
            writeMessage = masterServerClientInterface.write(fileName, data, txnID);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new Response(ResponseError.MASTER_NO_RESPONSE);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_IO_EXCEPTION);
        }

        ReplicaServerClientInterface primaryReplicaInterface;
        try {
            primaryReplicaInterface = writeMessage.getLoc().getReplicaInterface();
        } catch (RemoteException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_REMOTE_ERROR);
        } catch (NotBoundException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_NOT_BOUND);
        }

        WriteMessage result;
        try {
            result = primaryReplicaInterface.write(writeMessage.getTransactionId(), msgSeqNum
                                    , data, writeMessage.getFileDistribution());
        } catch (RemoteException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_REMOTE_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_IO_EXCEPTION);
        } catch (NotBoundException e){
            e.printStackTrace();
            return new Response(ResponseError.REPLICA_NOT_BOUND);
        }
        return new Response(result);
    }

    @Override
    public String toString() {
        return "[Write Action] FileName:{" + fileName + "}, Content:{" + content + "}";
    }

}
