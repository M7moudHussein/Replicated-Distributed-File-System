package masterserver;


import replicaserver.ReplicaMetadata;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;

public class MasterServer {
    public final static String DOMAIN_NAME = "rmi://localhost:1900/master_server";
    private final List<ReplicaMetadata> replicasMetadata;


    public MasterServer(final List<ReplicaMetadata> replicasMetadata) {
        this.replicasMetadata = replicasMetadata;
    }

    public void runMaster() {
        try {
            LocateRegistry.createRegistry(1900);
            Naming.rebind(MasterServer.DOMAIN_NAME, new MasterServerClient(replicasMetadata));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
