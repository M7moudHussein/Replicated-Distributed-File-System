package masterserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ReplicaMetadata {
    private String ip, dir;
    private int port;
}
