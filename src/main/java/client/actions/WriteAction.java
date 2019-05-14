package client.actions;

import client.response.Response;
import client.response.ResponseError;
import lombok.AllArgsConstructor;
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
public class WriteAction implements Action {
    private final String fileName;
    private final String content;

    @Override
    public Response executeAction(MasterServerClientInterface masterServerClientInterface) {
        WriteMessage writeMessage;
        FileData data = new FileData(fileName, content);
        try {
            writeMessage = masterServerClientInterface.write(fileName, data);
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
            result = primaryReplicaInterface.write(writeMessage.getTransactionId(),
                    1, data, writeMessage.getFileDistribution());
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
        return new Response(result.toString());
    }

    @Override
    public String toString() {
        return "[Write Action] FileName:{" + fileName + "}, Content:{" + content + "}";
    }
}
