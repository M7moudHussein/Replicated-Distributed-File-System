package masterserver;


import java.util.List;

public class MasterServer {
    private final List<ReplicaMetadata> replicasMetadata;

    public MasterServer(final List<ReplicaMetadata> replicasMetadata) {
        this.replicasMetadata = replicasMetadata;
    }

    public void runMaster() {
        
    }
}
