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
    private static final String REPLICA_SERVERS_DATA_FILE = "repServers.txt";
    private List<ReplicaMetadata> replicasMetadata;

    private String lookup;
    private Registry registry;


    public static void main(String[] args) throws InterruptedException {

        if(args.length == 0)
            throw new RuntimeException("Not enough args");

        int port = Integer.valueOf(args[0]);

        startReplicaServers();
        List<Thread> replicasThread = new ArrayList<>();

        RemoteServer remoteServer = (RemoteServer) UnicastRemoteObject.exportObject(new MasterServerClient(port, lookup), port);
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


    public void parseReplicaMetaData() throws IOException {
        replicas = new ArrayList<>();
        FileReader fr = new FileReader(new File(REPLICA_SERVERS_DATA_FILE));
        BufferedReader br = new BufferedReader(fr);

        // 192.168.1.2:4040, /home/user/workplace/ReplicaApp

        for (String line = br.readLine(); line != null; line = br.readLine()) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            String[] data = line.split(",");

            assert data.length < 3 && data.length > 0 : String.format("line \"%s\" must be on the form\"ip:port(, appDirectory)\"", line);

            String[] address = data[0].split(":");

            assert address.length == 2 : String.format("Address \"%s\" must be on the form \"192.168.1.1:4040\"", data[0]);

            String ip = address[0];
            String port = address[1];

            String dir = "~";
            if (data.length == 2) {
                dir = data[1];
            }

            replicasMetadata.add(new ReplicaMetadata(ip, dir, Integer.valueOf(port)));
        }
        br.close();
        fr.close();
    }
}