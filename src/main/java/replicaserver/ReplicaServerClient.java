package replicaserver;

import masterserver.FileData;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ReplicaServerClient extends UnicastRemoteObject implements ReplicaServerClientInterface {

    private final Map<String, Lock> fileLocks;
    private final String workingDirectory;

    protected ReplicaServerClient(String directory) throws RemoteException {
        this.fileLocks = Collections.synchronizedMap(new HashMap<>());
        this.workingDirectory = directory;
    }

    private void appendToFile(FileData data) throws IOException {
        FileWriter fw = new FileWriter(data.getFileName(), true);
        fw.write(data.getFileContent());
        fw.close();
    }

    @Override
    public WriteMessage write(long txnID, long msgSeqNum, FileData data) throws RemoteException {
        // initial simple implementation for testing
        fileLocks.putIfAbsent(data.getFileName(), new ReentrantLock());
        Lock fileLock = fileLocks.get(data.getFileName());
        fileLock.lock();
        try {
            appendToFile(data);
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: throw e but do not forget to unlock
        }
        fileLock.unlock();

        // TODO: there is no loc variable here to send in constructor
        return new WriteMessage(txnID, msgSeqNum, null);
    }

    @Override
    public FileData read(String fileName) throws FileNotFoundException, RemoteException, IOException {
        fileLocks.putIfAbsent(fileName, new ReentrantLock());
        Lock fileLock = fileLocks.get(fileName);
        fileLock.lock();
        String fileContent = readFileContent(fileName);
        fileLock.unlock();
        return new FileData(fileName, fileContent);
    }

    @Override
    public boolean commit(long txnID, long numOfMsgs) throws MessageNotFoundException, RemoteException {
        return false;
    }

    @Override
    public boolean abort(long txnID) throws RemoteException {
        return false;
    }

    private String readFileContent(String fileName) throws IOException {
        Path filePath = Paths.get(workingDirectory, fileName);
        System.out.println(filePath);

        if(!Files.exists(filePath)) {
            throw new FileNotFoundException();
        }
        return new String(Files.readAllBytes(filePath));
    }

}
