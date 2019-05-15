package client.actions;

import client.response.Response;
import masterserver.MasterServerClientInterface;

import java.io.IOException;
import java.rmi.NotBoundException;


public interface Action {
    Response executeAction(MasterServerClientInterface masterServerClientInterface, long txnID, long msgSeqNum) throws IOException, NotBoundException;

    String getFileName();
}
