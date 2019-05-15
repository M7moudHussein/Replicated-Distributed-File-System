package masterserver;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import replicaserver.ReplicaMetadata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
public class FileDistribution implements Serializable {
    private ReplicaMetadata primaryRep;
    private ReplicaMetadata[] replicas;

    public void removeReplicas(List<ReplicaMetadata> failedReplicas) {
        List<ReplicaMetadata> replicasList = new ArrayList<>(Arrays.asList(replicas));
        replicasList.remove(failedReplicas);
        replicas = replicasList.toArray(new ReplicaMetadata[0]);
        primaryRep = replicas.length > 0 ? replicas[0] : null;
    }
}
