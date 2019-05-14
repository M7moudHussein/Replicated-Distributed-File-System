package masterserver;

import replicaserver.ReplicaMetadata;
import replicaserver.WriteMessage;

import java.io.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MasterServerClient extends UnicastRemoteObject implements MasterServerClientInterface {

    private static final int NUMBER_OF_FILE_REPLICAS = 3;
    private Map<String, FileDistribution> fileDistributionMap;
    private boolean isTerminated = false;
    private List<ReplicaMetadata> replicas;

    private AtomicLong timeStamp;
    private AtomicLong transactionId;

    public MasterServerClient(List<ReplicaMetadata> replicas) throws InterruptedException, RemoteException {
        super();
        fileDistributionMap = new HashMap<>();
        this.replicas = replicas;
        this.timeStamp = new AtomicLong();
        transactionId = new AtomicLong();
        if (replicas.size() < NUMBER_OF_FILE_REPLICAS)
            throw new RuntimeException("Not Sufficient number of replicas");
        Thread heartbeatThread = new Thread(this::heartBeatChecking);
        heartbeatThread.join();
    }


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

    @Override
    public ReplicaMetadata read(String fileName) throws FileNotFoundException, RemoteException {
        if (!fileDistributionMap.containsKey(fileName)) {
            throw new FileNotFoundException(fileName);
        }

        timeStamp.getAndIncrement();
        return fileDistributionMap.get(fileName).getPrimaryRep();
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
    public WriteMessage write(String fileName, FileData data) {

        timeStamp.getAndIncrement();

        if (!fileDistributionMap.containsKey(fileName)) {
            createFile(fileName);
        }


        return new WriteMessage(transactionId.incrementAndGet(), timeStamp.get(), fileDistributionMap.get(fileName).getPrimaryRep());
    }


    public void exit() {
        isTerminated = true;
    }

}