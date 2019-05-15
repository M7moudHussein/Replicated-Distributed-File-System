package replicaserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

@Setter
@Getter
@AllArgsConstructor
public class ReplicaMetadata implements Serializable {
    private String ip;
    private int port;
    private int identifier;
    private String dir;

    public ReplicaServerClientInterface getReplicaInterface() throws RemoteException, NotBoundException {
        try {
            return (ReplicaServerClientInterface) Naming.lookup(getDomainName());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getDomainName() {
        return "rmi://" + ip + ":" + port + "/replica_" + identifier;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReplicaMetadata)) {
            return false;
        }
        ReplicaMetadata metadata = (ReplicaMetadata) obj;
        return ip.equals(metadata.ip) && dir.equals(metadata.dir) && port == metadata.port && identifier == metadata.identifier;
    }
}
