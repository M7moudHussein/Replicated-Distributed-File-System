package masterserver;

import replicaserver.ReplicaMetadata;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.rmi.registry.LocateRegistry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ServerMain {
    //    private static final int WAIT_INTERVAL = 5000;
    private static final String REPLICA_SERVERS_DATA_FILE = "repServers.txt";

    public static void main(String[] args) throws IOException {

//        LocateRegistry.createRegistry(1900);

        List<ReplicaMetadata> replicasMetadata = parseReplicaMetaData();
//        List<Thread> replicasThread = new ArrayList<>();

//        for (ReplicaMetadata replicaMetadata : replicasMetadata) {
//            ReplicaServer replica = new ReplicaServer(replicaMetadata);
//            Thread replicaThread = new Thread(replica::runServer);
//            replicasThread.add(replicaThread);
//            replicaThread.start();
//        }

//        Thread.sleep(WAIT_INTERVAL);
        MasterServer master = new MasterServer(replicasMetadata);
        master.runMaster();
    }


    private static List<ReplicaMetadata> parseReplicaMetaData() throws IOException {
        List<ReplicaMetadata> replicasMetadata = new ArrayList<>();
        FileReader fr = new FileReader(new File(REPLICA_SERVERS_DATA_FILE));
        BufferedReader br = new BufferedReader(fr);

        int serverID = 1;
        for (String line = br.readLine(); line != null; line = br.readLine()) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            String[] data = line.split(" ");

            assert data.length < 3 && data.length > 0 : String.format("line \"%s\" must be on the form\"ip:port(, appDirectory)\"", line);

            String[] address = data[0].split(":");

            assert address.length == 2 : String.format("Address \"%s\" must be on the form \"192.168.1.1:4040\"", data[0]);

            String ip = address[0].trim();
            String port = address[1].trim();

            String dir = "";
            String defaultDirectory = Paths.get(System.getProperty("user.dir"), "replica_server_" + serverID).toString();
            if (data.length == 2) {
                dir = data[1].trim();
            }

            if (dir.isEmpty()) {
                dir = defaultDirectory;
            }

            replicasMetadata.add(new ReplicaMetadata(ip, Integer.valueOf(port), serverID++, dir));
        }
        br.close();
        fr.close();

        return replicasMetadata;
    }
}