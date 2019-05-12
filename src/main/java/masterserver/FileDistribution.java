package masterserver;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import replicaserver.ReplicaMetadata;

@Setter
@Getter
@AllArgsConstructor
public class FileDistribution {
    private ReplicaMetadata primaryRep;
    private ReplicaMetadata[] replicas;
}
