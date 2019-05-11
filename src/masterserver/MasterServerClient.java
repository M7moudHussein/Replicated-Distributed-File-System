package masterserver;

import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MasterServerClient implements MasterServerClientInterface {

    public static final String REPLICA_SERVERS_DATA_FILE = "repServers.txt";
    public static final int NUMBER_OF_FILE_REPLICAS = 3;
    private List<ReplicaLoc> replicas;
    private Map<String, FileDistribution> fileDistributionMap;
    Thread heartbeatThread;
    private boolean isTerminated = false;


    private Registry registry;


    long timeStamp = 0;
    long transactionId = 0;


    public void startReplicaServers() throws IOException {
        replicas = new ArrayList<>();
        FileReader fr = new FileReader(new File(REPLICA_SERVERS_DATA_FILE));
        @SuppressWarnings("resource")
        BufferedReader br = new BufferedReader(fr);

        String line = br.readLine();
        for (; line != null; line = br.readLine()) {
            line = line.trim();
            if (line.isEmpty())
                continue;

            String[] data = line.split(",");

            if (data.length > 3)
                throw new RuntimeException("There is line has more than one comma");

            String ip = data[0];
            String dir = data.length == 2 ? data[1] : "~";
            int port = data.length == 3 ? Integer.valueOf(data[1]) : 8080;

            // starting each replica (call start in replica interface or using command)
            replicas.add(new ReplicaLoc(ip, dir, port));
        }
        br.close();
        fr.close();
    }

    public void heartBeatChecking() {
        while (!isTerminated) {
//			for(ReplicaLoc rep: replicas) {
//				// ssh or rmi 
//			}

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public MasterServerClient(int port, String lookup) throws IOException,
            AlreadyBoundException, InterruptedException {
        fileDistributionMap = new HashMap<String, FileDistribution>();
        startReplicaServers();
        if (replicas.size() < NUMBER_OF_FILE_REPLICAS)
            throw new RuntimeException("Not Sufficient number of replicas");

        RemoteServer remoteServer = (RemoteServer) UnicastRemoteObject.exportObject(this, port);
        registry = LocateRegistry.createRegistry(port);
        registry.bind(lookup, remoteServer);

        heartbeatThread = new Thread(this::heartBeatChecking);


        heartbeatThread.join();
    }


    @Override
    public ReplicaLoc[] read(String fileName) throws IOException {
        if (!fileDistributionMap.containsKey(fileName))
            throw new FileNotFoundException();

        ++timeStamp;
        return fileDistributionMap.get(fileName).getReplicas();
    }

    private void createFile(String fileName) {
        Random random = new Random();
        ReplicaLoc[] fileReplicas = new ReplicaLoc[NUMBER_OF_FILE_REPLICAS];
        Set<Integer> takenReplica = new HashSet<>();
        for (int i = 0; i < NUMBER_OF_FILE_REPLICAS; ++i) {
            int curIdx = random.nextInt(replicas.size());
            while (takenReplica.contains(curIdx))
                curIdx = random.nextInt(replicas.size());

            fileReplicas[i] = this.replicas.get(curIdx);
            takenReplica.add(curIdx);
        }

        fileDistributionMap.put(fileName, new FileDistribution(fileReplicas[0], fileReplicas));
    }

    @Override
    public WriteMsg write(String fileName, FileContent data) {

        ++timeStamp;

        if (!fileDistributionMap.containsKey(fileName)) {
            createFile(fileName);
        }


        return new WriteMsg(++transactionId, timeStamp, fileDistributionMap.get(fileName).getPrimaryRep());
    }


    public void exit() {
        isTerminated = true;
    }

}
