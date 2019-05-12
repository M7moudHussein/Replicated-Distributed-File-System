package client;

import masterserver.MasterServer;
import masterserver.MasterServerClientInterface;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class ClientMain {

    public final static String DOMAIN_NAME = "rmi://localhost:1900/master_server";
    public static String SAMPLE_ACTIONS_FILE = "sample_actions.txt";


    public static void main(String[] args) throws IOException, NotBoundException {
        List<Action> actions = parseActionsFile(SAMPLE_ACTIONS_FILE);
        MasterServerClientInterface masterServer = (MasterServerClientInterface) Naming.lookup(MasterServer.DOMAIN_NAME);
        for(Action action : actions) {
            action.executeAction(masterServer);
            System.out.println(action.toString());
        }


    }

    static List<Action> parseActionsFile(final String fileName) {
        final List<Action> actions = new ArrayList<>();
        final ActionFactory actionFactory = ActionFactory.getInstance();
        Scanner sc = null;
        try {
            sc = new Scanner(new File(fileName));
            while(sc.hasNextLine()) {
                actions.add(actionFactory.buildAction(sc.nextLine()));
            }
        } catch (FileNotFoundException e) {
            System.out.println("Client input in file /" + fileName + "/ wasn't found");
            e.printStackTrace();
        }

        return actions;
    }
}
