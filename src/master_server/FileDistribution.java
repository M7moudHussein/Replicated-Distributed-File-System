package master_server;


public class FileDistribution {
	private ReplicaLoc primary_rep;
	private ReplicaLoc []replicas;
	
	
	public FileDistribution(ReplicaLoc primary_rep, ReplicaLoc[] replicas) {
		super();
		this.primary_rep = primary_rep;
		this.replicas = replicas;
	}
	
	
	public ReplicaLoc getPrimaryRep() {
		return primary_rep;
	}
	
	public void setPrimaryRep(ReplicaLoc primary_rep) {
		this.primary_rep = primary_rep;
	}
	
	public ReplicaLoc[] getReplicas() {
		return replicas;
	}
	
	public void setReplicas(ReplicaLoc []replicas) {
		this.replicas = replicas;
	}
	
	
}
