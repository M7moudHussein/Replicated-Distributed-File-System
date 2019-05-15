package replicaserver;

import client.transaction.Transaction;
import masterserver.FileData;
import masterserver.FileDistribution;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReplicaServerClient extends UnicastRemoteObject implements ReplicaServerClientInterface {

    private final Map<String, Lock> fileLocks;
    private final String workingDirectory;
    private ReplicaMetadata loc;
    private Map<Long, List<TransactionOperation> > transactions;

    protected ReplicaServerClient(ReplicaMetadata loc) throws RemoteException {
        this.fileLocks = Collections.synchronizedMap(new HashMap<>());
        this.transactions = new HashMap<>();
        this.workingDirectory = loc.getDir();
        this.loc = loc;
    }


    @Override
    public WriteMessage write(long txnID, long msgSeqNum, FileData data, FileDistribution distribution) throws IOException, NotBoundException {
        if(!transactions.containsKey(txnID))
            transactions.put(txnID, new ArrayList<>());

        transactions.get(txnID).add(new TransactionOperation(msgSeqNum, data, distribution));


        if(distribution != null && loc.getDomainName().equals(distribution.getPrimaryRep().getDomainName())){
            // This is a primary replica for this file, write to non-primary replicas
            for(ReplicaMetadata replica : distribution.getReplicas())
                if(replica.getIdentifer() != loc.getIdentifer())
                    replica.getReplicaInterface().write(txnID, msgSeqNum, data, distribution);
        }


        return new WriteMessage(txnID, msgSeqNum, loc, null);
    }

    @Override
    public FileData read(String fileName) throws FileNotFoundException, RemoteException, IOException {
        fileLocks.putIfAbsent(fileName, new ReentrantLock());
        Lock fileLock = fileLocks.get(fileName);
        fileLock.lock();
        String fileContent = null;
        try {
            fileContent = readFileContent(fileName);
        } catch (Exception e) {
            throw e;
        } finally {
            fileLock.unlock();
        }

        return new FileData(fileName, fileContent);
    }

    @Override
    public boolean commit(long txnID, long numOfMsgs) throws MessageNotFoundException,
                RemoteException, IOException, NotBoundException {

        if(!transactions.containsKey(txnID))
            return false;

        if(numOfMsgs == 0)
            return true;

        if(numOfMsgs > transactions.get(txnID).size())
            numOfMsgs = transactions.get(txnID).size();

//        Collections.sort(transactions.get(txnID));
        int cntWrittenMsgs = 0;
        transactions.get(txnID).sort(Comparator.comparing(TransactionOperation::getMsgSeqNo));
        List<TransactionOperation> operations = transactions.get(txnID);

        for(TransactionOperation operation: operations) {
            if(cntWrittenMsgs++ == numOfMsgs) break;
            fileLocks.putIfAbsent(operation.getFileData().getFileName(), new ReentrantLock());
            Lock fileLock = fileLocks.get(operation.getFileData().getFileName());

            fileLock.lock();
            FileWriter fw = new FileWriter(getFilePath(operation.getFileData().getFileName()).toString(), true);
            fw.write(operation.getFileData().getFileContent() + "\n");
            fw.close();

            fileLock.unlock();
        }

        for(TransactionOperation operation: operations) {
            if(operation.getFileDistribution() != null &&
                    operation.getFileDistribution().getPrimaryRep().getIdentifer() == loc.getIdentifer()) {
                ReplicaMetadata[] repilcas = operation.getFileDistribution().getReplicas();
                for(ReplicaMetadata replica: repilcas) if(replica.getIdentifer() != loc.getIdentifer())
                    replica.getReplicaInterface().commit(txnID, numOfMsgs);
            }
        }

        transactions.put(txnID, operations.subList((int)numOfMsgs, operations.size()));

        return true;
    }

    @Override
    public boolean abort(long txnID) throws RemoteException, NotBoundException {
        if(!transactions.containsKey(txnID))
            return false;

        List<TransactionOperation> operations = transactions.get(txnID);
        for(TransactionOperation operation: operations) {
            if(operation.getFileDistribution() != null &&
                    operation.getFileDistribution().getPrimaryRep().getIdentifer() == loc.getIdentifer()) {
                ReplicaMetadata[] repilcas = operation.getFileDistribution().getReplicas();
                for(ReplicaMetadata replica: repilcas) if(replica.getIdentifer() != loc.getIdentifer())
                    replica.getReplicaInterface().abort(txnID);
            }
        }

        transactions.remove(txnID);
        return true;
    }

    @Override
    public boolean checkLiveness() {
        return true;
    }

    private String readFileContent(String fileName) throws IOException {
        Path filePath = getFilePath(fileName);

        if(!Files.exists(filePath)) {
            throw new FileNotFoundException();
        }
        return new String(Files.readAllBytes(filePath));
    }

    private Path getFilePath(String fileName) {
        return Paths.get(workingDirectory, fileName);
    }
}
