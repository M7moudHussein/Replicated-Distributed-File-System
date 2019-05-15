package replicaserver;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import masterserver.FileData;
import masterserver.FileDistribution;

import java.util.Comparator;

@Setter
@Getter
@AllArgsConstructor


public class TransactionOperation {

    long msgSeqNo;
    FileData fileData;
    FileDistribution fileDistribution;

}
