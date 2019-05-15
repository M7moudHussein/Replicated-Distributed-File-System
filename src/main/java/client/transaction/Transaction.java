package client.transaction;

import client.actions.Action;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
@AllArgsConstructor

public class Transaction {
    private  final long txnID;
    private  final List<Action> actions;
    private  final String fileName;

    public Transaction(final long txnID, final List<Action> actions) {
        this.txnID = txnID;
        this.actions = actions;

        fileName = !actions.isEmpty()? actions.get(0).getFileName():null;

        for(Action action: actions) {
            if(!fileName.equals(action.getFileName()))
                throw new IllegalArgumentException("Invalid multiple files at the same transaction");
        }
    }


}
