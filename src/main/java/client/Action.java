package client;

import masterserver.MasterServerClientInterface;

import java.io.IOException;

public interface Action {
    void executeAction(MasterServerClientInterface masterServerClientInterface) throws IOException;
}
