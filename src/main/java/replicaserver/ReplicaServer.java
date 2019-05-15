package replicaserver;

import java.io.File;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class ReplicaServer {

//    private final ReplicaMetadata replicaMetadata;

//    public void runServer() {
//        File serverDirectory = new File(replicaMetadata.getDir());
//        if(!serverDirectory.exists()) {
//            serverDirectory.mkdir();
//        }
//        try {
//            ReplicaServerClientInterface obj = new ReplicaServerClient(replicaMetadata);
//            Naming.rebind(replicaMetadata.getDomainName(), obj);
//            System.out.println("Replica Server {" + replicaMetadata.getIdentifier() + "} is running");
//        } catch (RemoteException e) {
//            System.out.println("[Replication ID:" + replicaMetadata.getIdentifier() + "] Remote exception during binding the object");
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            System.out.println("[Replication ID:" + replicaMetadata.getIdentifier() + "] Malformed URL exception during binding the object");
//            e.printStackTrace();
//        }
//    }

    public static void main(String[] args) {
        try {
            LocateRegistry.createRegistry(1900);
        } catch (Exception ignored) {
        }

        if (args.length != 4) {
            throw new RuntimeException("Expected 4 args [ip, port, identifier, dir] but found " + args.length);
        }

        ReplicaMetadata replicaMetadata =
                new ReplicaMetadata(args[0], Integer.valueOf(args[1]), Integer.valueOf(args[2]), args[3]);

        File serverDirectory = new File(replicaMetadata.getDir());
        if (!serverDirectory.exists()) {
            serverDirectory.mkdir();
        }
        try {
            ReplicaServerClientInterface obj = new ReplicaServerClient(replicaMetadata);
            Naming.rebind(replicaMetadata.getDomainName(), obj);
            System.out.println("Replica Server {" + replicaMetadata.getIdentifier() + "} is running");
        } catch (RemoteException e) {
            System.out.println("[Replication ID:" + replicaMetadata.getIdentifier() + "] Remote exception during binding the object");
            e.printStackTrace();
        } catch (MalformedURLException e) {
            System.out.println("[Replication ID:" + replicaMetadata.getIdentifier() + "] Malformed URL exception during binding the object");
            e.printStackTrace();
        }
    }
}
