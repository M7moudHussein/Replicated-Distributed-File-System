package masterserver;


public class FileDistribution {
    private ReplicaLoc primaryRep;
    private ReplicaLoc[] replicas;


    public FileDistribution(ReplicaLoc primaryRep, ReplicaLoc[] replicas) {
        super();
        this.primaryRep = primaryRep;
        this.replicas = replicas;
    }


    public ReplicaLoc getPrimaryRep() {
        return primaryRep;
    }

    public void setPrimaryRep(ReplicaLoc primaryRep) {
        this.primaryRep = primaryRep;
    }

    public ReplicaLoc[] getReplicas() {
        return replicas;
    }

    public void setReplicas(ReplicaLoc[] replicas) {
        this.replicas = replicas;
    }


}
