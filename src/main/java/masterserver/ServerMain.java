package masterserver;

import replicaserver.ReplicaMetadata;
import replicaserver.ReplicaServer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class ServerMain {
    private static final int WAIT_INTERVAL = 5000;

    public static void main(String[] args) throws InterruptedException {
        List<ReplicaMetadata> replicasMetadata = parseReplicaMetaData();
        List<Thread> replicasThread = new ArrayList<>();

        RemoteServer remoteServer = (RemoteServer) UnicastRemoteObject.exportObject(this, port);
        Registry registry = LocateRegistry.createRegistry(port);
        registry.bind(lookup, remoteServer);

        for (ReplicaMetadata replicaMetadata : replicasMetadata) {
            ReplicaServer replica = new ReplicaServer(replicaMetadata);
            Thread replicaThread = new Thread(replica::runServer);
            replicasThread.add(replicaThread);
            replicaThread.start();
        }

        Thread.sleep(WAIT_INTERVAL);

        MasterServer master = new MasterServer(replicasMetadata);
        master.runMaster();
    }

    private static List<ReplicaMetadata> parseReplicaMetaData() {

        return null;
    }
}
