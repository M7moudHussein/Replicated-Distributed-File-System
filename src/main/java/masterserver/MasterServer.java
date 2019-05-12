package masterserver;


import replicaserver.ReplicaMetadata;

import java.util.List;

public class MasterServer {
    public final static String DOMAIN_NAME = "rmi://localhost:1900/master_server";
    private final List<ReplicaMetadata> replicasMetadata;


    public MasterServer(final List<ReplicaMetadata> replicasMetadata) {
        this.replicasMetadata = replicasMetadata;
    }

    public void runMaster() {
        
    }
}
