package client;

import masterserver.MasterServerClientInterface;

public interface Action {
    void executeAction(MasterServerClientInterface masterServerClientInterface);
}
