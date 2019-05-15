package masterserver;

import replicaserver.ReplicaMetadata;
import replicaserver.ReplicaServerClientInterface;
import replicaserver.WriteMessage;

import java.io.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class MasterServerClient extends UnicastRemoteObject implements MasterServerClientInterface {

    private static final int NUMBER_OF_FILE_REPLICAS = 3;
    private static final int HEART_BEAT_SLEEP_TIME = 20000;

    private Map<String, FileDistribution> fileDistributionMap;
    private boolean isTerminated = false;
    private List<ReplicaMetadata> replicas;

    private AtomicLong timeStamp;
    private AtomicLong transactionId;

    public MasterServerClient(List<ReplicaMetadata> replicas) throws InterruptedException, RemoteException {
        super();
        fileDistributionMap = Collections.synchronizedMap(new HashMap<>());
        this.replicas = replicas;
        this.timeStamp = new AtomicLong();
        transactionId = new AtomicLong();
        if (replicas.size() < NUMBER_OF_FILE_REPLICAS)
            throw new RuntimeException("Not Sufficient number of replicas");
        Thread heartbeatThread = new Thread(this::heartBeatChecking);
        heartbeatThread.start();
    }


    public void heartBeatChecking() {
        while (!isTerminated) {
            Set<Integer> failedReplicasId = new HashSet<>();
            System.out.println("Running Heartbeat over " + replicas.size() + " replica servers");
            for(ReplicaMetadata replicaMetadata : replicas) {
                boolean replicaAlive = false;
                try {
                    ReplicaServerClientInterface replicaServer = replicaMetadata.getReplicaInterface();
                    replicaAlive = replicaServer.checkLiveness();
                } catch (RemoteException|NotBoundException e) {
                }
                if(replicaAlive) {
                    System.out.println("Replica Server {" + replicaMetadata.getIdentifer() + "} is alive");
                } else {
                    System.out.println("Replica Server {" + replicaMetadata.getIdentifer() + "} is dead");
                    failedReplicasId.add(replicaMetadata.getIdentifer());
                }
            }
            try {
                System.out.println("Heart beat thread is sleeping");
                Thread.sleep(HEART_BEAT_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ReplicaMetadata[] read(String fileName) throws FileNotFoundException, RemoteException {
        if (!fileDistributionMap.containsKey(fileName)) {
            throw new FileNotFoundException(fileName);
        }

        timeStamp.getAndIncrement();
        return fileDistributionMap.get(fileName).getReplicas();
    }

    @Override
    public long getNewTransactionID() throws RemoteException {
        return transactionId.getAndIncrement();
    }

    private FileDistribution createFile(String fileName) {
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

        FileDistribution fileDistribution = new FileDistribution(fileReplicas[0], fileReplicas);
        fileDistributionMap.put(fileName, fileDistribution);
        return fileDistribution;
    }

    @Override
    public WriteMessage write(String fileName, FileData data, long txnID) {
        System.out.println(fileName + " inside write");
        timeStamp.getAndIncrement();

        if (!fileDistributionMap.containsKey(fileName))
            createFile(fileName);


        return new WriteMessage(txnID,
                                timeStamp.get(),
                                fileDistributionMap.get(fileName).getPrimaryRep(),
                                fileDistributionMap.get(fileName));
    }


    public void exit() {
        isTerminated = true;
    }

}