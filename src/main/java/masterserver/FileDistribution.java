package masterserver;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class FileDistribution {
    private ReplicaMetadata primaryRep;
    private ReplicaMetadata[] replicas;
}
