package replicaserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class ReplicaMetadata implements Serializable {
    private String ip, dir;
    private int port, identifer;
}
