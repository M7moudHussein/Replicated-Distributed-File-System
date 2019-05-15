package client;

import client.actions.Action;
import client.response.Response;
import client.transaction.Transaction;
import masterserver.MasterServer;
import masterserver.MasterServerClientInterface;
import replicaserver.MessageNotFoundException;
import replicaserver.ReplicaMetadata;
import replicaserver.ReplicaServer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.*;


public class ClientMain {

    public final static String DOMAIN_NAME = "rmi://localhost:1900/master_server";
    public static String SAMPLE_ACTIONS_FILE = "sample_actions.txt";

    private final static  String TRANSACTIONS_DELIMITER = "";

    public static void main(String[] args) throws IOException, NotBoundException {
        MasterServerClientInterface masterServer = (MasterServerClientInterface) Naming.lookup(MasterServer.DOMAIN_NAME);
        List<Transaction> transactions = parseActionsFile(SAMPLE_ACTIONS_FILE, masterServer);
        System.out.println("transactions length" + transactions.size());
        for(Transaction transaction: transactions) {
            long msgSeqNum = 1;
            List<ReplicaMetadata> primaryReplicas = new ArrayList<>();
            for (Action action : transaction.getActions()) {
                Response response = action.executeAction(masterServer, transaction.getTxnID(), msgSeqNum++);
                System.out.println(action);
                System.out.println(response);
                if(response.getWriteMessage() != null)
                    primaryReplicas.add(response.getWriteMessage().getLoc());
            }
            for(ReplicaMetadata replica: primaryReplicas) {
                try {
                    replica.getReplicaInterface().commit(transaction.getTxnID(), msgSeqNum);
                } catch (MessageNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static List<Transaction> parseActionsFile(final String fileName, MasterServerClientInterface masterServer) {
        final List<Transaction> transactions = new ArrayList<>();
        final ActionFactory actionFactory = ActionFactory.getInstance();
        List<Action> actions = new ArrayList<>();
        Scanner sc = null;
        try {
            System.out.println("read from file");
            System.out.println(new File(fileName).getAbsolutePath());
            sc = new Scanner(new File(fileName));

            while(sc.hasNextLine()) {
                String line = sc.nextLine();
                if(!line.equals(TRANSACTIONS_DELIMITER))
                    actions.add(actionFactory.buildAction(line));
                else if(!actions.isEmpty()){
                    transactions.add(new Transaction(masterServer.getNewTransactionID(), actions));
                    actions = new ArrayList<>();
                }
            }
            if(!actions.isEmpty())
                transactions.add(new Transaction(masterServer.getNewTransactionID(), actions));

        } catch (FileNotFoundException e) {
            System.out.println("Client input in file /" + fileName + "/ wasn't found");
            e.printStackTrace();
        } catch (RemoteException e) {
            System.out.println("Failed to connect to master server to get new transaction id");
            e.printStackTrace();
        }

        return transactions;
    }
}
