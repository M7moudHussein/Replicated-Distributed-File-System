package masterserver;

import replicaserver.ReplicaMetadata;
import replicaserver.WriteMessage;

import java.io.*;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.RemoteServer;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class MasterServerClient implements MasterServerClientInterface {

    private static final int NUMBER_OF_FILE_REPLICAS = 3;
    private Map<String, FileDistribution> fileDistributionMap;
    private boolean isTerminated = false;
    private List<ReplicaMetadata> replicas;

    private long timeStamp = 0;
    private long transactionId = 0;

    public void heartBeatChecking() {
        while (!isTerminated) {
//			for(ReplicaMetadata rep: replicas) {
//				// ssh or rmi
//			}
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    public MasterServerClient(List<ReplicaMetadata> replicas) throws InterruptedException {
        fileDistributionMap = new HashMap<String, FileDistribution>();
        if (replicas.size() < NUMBER_OF_FILE_REPLICAS)
            throw new RuntimeException("Not Sufficient number of replicas");
        this.replicas = replicas;
        Thread heartbeatThread = new Thread(this::heartBeatChecking);
        heartbeatThread.join();
    }


    @Override
    public ReplicaMetadata[] read(String fileName) throws IOException {
        if (!fileDistributionMap.containsKey(fileName))
            throw new FileNotFoundException();

        ++timeStamp;
        return fileDistributionMap.get(fileName).getReplicas();
    }

    private void createFile(String fileName) {
        Random random = new Random();
        ReplicaMetadata[] fileReplicas = new ReplicaMetadata[NUMBER_OF_FILE_REPLICAS];
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
    public WriteMessage write(String fileName, FileContent data) {

        ++timeStamp;

        if (!fileDistributionMap.containsKey(fileName)) {
            createFile(fileName);
        }


        return new WriteMessage(++transactionId, timeStamp, fileDistributionMap.get(fileName).getPrimaryRep());
    }


    public void exit() {
        isTerminated = true;
    }

}