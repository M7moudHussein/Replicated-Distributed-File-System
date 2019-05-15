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
    long txnID;
    List<Action> actions;


}
