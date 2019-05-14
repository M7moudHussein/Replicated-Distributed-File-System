package masterserver;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import replicaserver.ReplicaMetadata;

import java.io.Serializable;

@Setter
@Getter
@AllArgsConstructor
public class FileDistribution implements Serializable {
    private ReplicaMetadata primaryRep;
    private ReplicaMetadata[] replicas;
}
