package replicaserver;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import masterserver.FileDistribution;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor

public class WriteMessage implements Serializable {

	private long transactionId;
	private  long timeStamp;
	private ReplicaMetadata loc;
	private FileDistribution fileDistribution;


	@Override
	public String toString() {
		return "TID: " + transactionId + ", timeStamp: " + timeStamp + ", loc: " + loc.getDomainName();
	}

}
