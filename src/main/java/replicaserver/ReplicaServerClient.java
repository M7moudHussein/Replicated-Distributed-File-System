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
    private ReplicaMetadata loc;

    protected ReplicaServerClient(ReplicaMetadata loc) throws RemoteException {
        this.fileLocks = Collections.synchronizedMap(new HashMap<>());
        this.workingDirectory = loc.getDir();
        this.loc = loc;
    }

    private void appendToFile(FileData data) throws IOException {
        FileWriter fw = new FileWriter(getFilePath(data.getFileName()).toString(), true);
        fw.write(data.getFileContent());
        fw.close();
    }

    @Override
    public WriteMessage write(long txnID, long msgSeqNum, FileData data) throws IOException {
        // initial simple implementation for testing
        fileLocks.putIfAbsent(data.getFileName(), new ReentrantLock());
        Lock fileLock = fileLocks.get(data.getFileName());
        fileLock.lock();
        try {
            appendToFile(data);
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
        finally{
            fileLock.unlock();
        }
        return new WriteMessage(txnID, msgSeqNum, loc);
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
            fileLock.unlock();
            throw e;
        }
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
