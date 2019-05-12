package replicaserver;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ReplicaServer {

    public int REGISTRY_PORT = 1900;

    private final ReplicaMetadata replicaMetadata;

    public ReplicaServer(final ReplicaMetadata replicaMetadata) {
        this.replicaMetadata = replicaMetadata;
    }

    public void runServer() {
        try {
            ReplicaServerClientInterface obj = new ReplicaServerClient();
            LocateRegistry.createRegistry(REGISTRY_PORT);
            Naming.rebind(getDomainName(), obj);
        } catch (RemoteException e) {
            System.out.println("[Replication ID:" + replicaMetadata.getIdentifer() + "] Remote exception during binding the object");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("[Replication ID:" + replicaMetadata.getIdentifer() + "] Malformed URL exception during binding the object");
            e.printStackTrace();
        }
    }

    private String getDomainName() {
        return "rmi://" + replicaMetadata.getIp() + ":" + replicaMetadata.getPort() + "/replica_" + replicaMetadata.getIdentifer();
    }
}
