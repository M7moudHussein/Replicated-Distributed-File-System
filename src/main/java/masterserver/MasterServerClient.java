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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MasterServerClient extends UnicastRemoteObject implements MasterServerClientInterface {

    private static final int NUMBER_OF_FILE_REPLICAS = 3;
    private static final int HEART_BEAT_SLEEP_TIME = 20000;

    private Map<String, FileDistribution> fileDistributionMap;
    private boolean isTerminated = false;
    private List<ReplicaMetadata> replicas;
    private List<ReplicaMetadata> offlineReplicas;

    private AtomicLong timeStamp;
    private AtomicLong transactionId;

    private Lock distributionInfoLock;

    public MasterServerClient(List<ReplicaMetadata> replicas) throws InterruptedException, RemoteException {
        super();
        fileDistributionMap = Collections.synchronizedMap(new HashMap<>());
        this.replicas = replicas;
        this.offlineReplicas = new ArrayList<>();
        this.timeStamp = new AtomicLong();
        this.distributionInfoLock = new ReentrantLock();
        this.transactionId = new AtomicLong();
        if (replicas.size() < NUMBER_OF_FILE_REPLICAS)
            throw new RuntimeException("Not Sufficient number of replicas");
        Thread heartbeatThread = new Thread(this::heartBeatChecking);
        heartbeatThread.start();
    }


    public void heartBeatChecking() {
        while (!isTerminated) {
            System.out.println("Running Heartbeat over " + replicas.size() + " replica servers");
            List<ReplicaMetadata> newOnlineReplicas = getNewOnlineReplicas();
            List<ReplicaMetadata> newOfflineReplicas = getNewOfflineReplicas();
            distributionInfoLock.lock();
            removeReplicas(newOfflineReplicas);
            replicas.addAll(newOnlineReplicas);
            offlineReplicas.addAll(newOfflineReplicas);
            distributionInfoLock.unlock();
            try {
                System.out.println("Heart beat thread is sleeping");
                Thread.sleep(HEART_BEAT_SLEEP_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void removeReplicas(List<ReplicaMetadata> failedReplicas) {
        replicas.removeAll(failedReplicas);
        for(Map.Entry<String, FileDistribution> entry : fileDistributionMap.entrySet()) {
            entry.getValue().removeReplicas(failedReplicas);
        }
    }

    private List<ReplicaMetadata> getNewOfflineReplicas() {
        List<ReplicaMetadata> newOfflineReplicas = new ArrayList<>();
        for(ReplicaMetadata replicaMetadata : replicas) {
            boolean replicaAlive = false;
            try {
                ReplicaServerClientInterface replicaServer = replicaMetadata.getReplicaInterface();
                replicaAlive = replicaServer.checkLiveness();
            } catch (RemoteException|NotBoundException e) {
            }
            if(replicaAlive) {
                System.out.println("Replica Server {" + replicaMetadata.getIdentifer() + "} is still alive");
            } else {
                System.out.println("Replica Server {" + replicaMetadata.getIdentifer() + "} is currently dead");
                newOfflineReplicas.add(replicaMetadata);
            }
        }
        return newOfflineReplicas;
    }

    private List<ReplicaMetadata> getNewOnlineReplicas() {
        List<ReplicaMetadata> newOnlineReplicas = new ArrayList<>();
        for(ReplicaMetadata replicaMetadata : offlineReplicas) {
            boolean replicaAlive = false;
            try {
                ReplicaServerClientInterface replicaServer = replicaMetadata.getReplicaInterface();
                replicaAlive = replicaServer.checkLiveness();
            } catch (RemoteException|NotBoundException e) {
            }
            if(replicaAlive) {
                System.out.println("Replica Server {" + replicaMetadata.getIdentifer() + "} is back online");
                newOnlineReplicas.add(replicaMetadata);
            } else {
                System.out.println("Replica Server {" + replicaMetadata.getIdentifer() + "} is still dead");
            }
        }
        return newOnlineReplicas;
    }

    @Override
    public ReplicaMetadata[] read(String fileName) throws FileNotFoundException, RemoteException {
        distributionInfoLock.lock();
        if (!fileDistributionMap.containsKey(fileName)) {
            distributionInfoLock.unlock();
            throw new FileNotFoundException(fileName);
        }

        timeStamp.getAndIncrement();
        ReplicaMetadata[] result = fileDistributionMap.get(fileName).getReplicas();
        distributionInfoLock.unlock();
        return result;
    }

    @Override
    public long getNewTransactionID() throws RemoteException {
        return transactionId.getAndIncrement();
    }

    private FileDistribution createFile(String fileName) {
        Random random = new Random();
        ReplicaMetadata[] fileReplicas = new ReplicaMetadata[NUMBER_OF_FILE_REPLICAS];
        Set<Integer> takenReplica = new HashSet<>();
        distributionInfoLock.lock();
        for (int i = 0; i < NUMBER_OF_FILE_REPLICAS; ++i) {
            int curIdx = random.nextInt(replicas.size());
            while (takenReplica.contains(curIdx))
                curIdx = random.nextInt(replicas.size());

            fileReplicas[i] = this.replicas.get(curIdx);
            takenReplica.add(curIdx);
        }

        FileDistribution fileDistribution = new FileDistribution(fileReplicas[0], fileReplicas);
        fileDistributionMap.put(fileName, fileDistribution);
        distributionInfoLock.unlock();
        return fileDistribution;
    }

    @Override
    public WriteMessage write(String fileName, FileData data, long txnID) {
        timeStamp.getAndIncrement();
        distributionInfoLock.lock();
        if (!fileDistributionMap.containsKey(fileName))
            createFile(fileName);


        WriteMessage result = new WriteMessage(txnID,
                                timeStamp.get(),
                                fileDistributionMap.get(fileName).getPrimaryRep(),
                                fileDistributionMap.get(fileName));
        distributionInfoLock.unlock();
        return result;
    }


    public void exit() {
        isTerminated = true;
    }

}