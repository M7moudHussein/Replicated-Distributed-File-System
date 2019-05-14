package replicaserver;


import java.io.Serializable;

public class WriteMessage implements Serializable {

	private long transactionId;
	private  long timeStamp;
	private ReplicaMetadata loc;

	
	public WriteMessage(long transactionId, long timeStamp, ReplicaMetadata loc) {
		this.transactionId = transactionId;
		this.timeStamp = timeStamp;
		this.loc = loc;
	}


	public long getTransactionId() {
		return transactionId;
	}


	public void setTransactionId(long transactionId) {
		this.transactionId = transactionId;
	}


	public long getTimeStamp() {
		return timeStamp;
	}


	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}


	public ReplicaMetadata getLoc() {
		return loc;
	}


	public void setLoc(ReplicaMetadata loc) {
		this.loc = loc;
	}

	@Override
	public String toString() {
		return "TID: " + transactionId + ", timeStamp: " + timeStamp + ", loc: TODO"; // + loc.getDomainName();
	}

}
