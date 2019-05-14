package replicaserver;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ReplicaServer {

    private final ReplicaMetadata replicaMetadata;

    public ReplicaServer(final ReplicaMetadata replicaMetadata) {
        this.replicaMetadata = replicaMetadata;
    }

    public void runServer() {
        try {
            ReplicaServerClientInterface obj = new ReplicaServerClient(replicaMetadata.getDir());
            Naming.rebind(replicaMetadata.getDomainName(), obj);
        } catch (RemoteException e) {
            System.out.println("[Replication ID:" + replicaMetadata.getIdentifer() + "] Remote exception during binding the object");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("[Replication ID:" + replicaMetadata.getIdentifer() + "] Malformed URL exception during binding the object");
            e.printStackTrace();
        }
    }
}
