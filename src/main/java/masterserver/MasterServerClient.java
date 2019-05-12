package masterserver;

import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MasterServerClient implements MasterServerClientInterface {

    private static final String REPLICA_SERVERS_DATA_FILE = "repServers.txt";
    private static final int NUMBER_OF_FILE_REPLICAS = 3;
    private List<ReplicaLoc> replicas;
    private Map<String, FileDistribution> fileDistributionMap;
    private boolean isTerminated = false;


    private long timeStamp = 0;
    private long transactionId = 0;


    public void startReplicaServers() throws IOException {
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

            replicas.add(new ReplicaLoc(ip, dir, Integer.valueOf(port)));
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
        Registry registry = LocateRegistry.createRegistry(port);
        registry.bind(lookup, remoteServer);

        Thread heartbeatThread = new Thread(this::heartBeatChecking);


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