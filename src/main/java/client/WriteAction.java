package client;

import lombok.AllArgsConstructor;
import masterserver.MasterServerClientInterface;

@AllArgsConstructor
public class WriteAction implements Action {
    private final String fileName;
    private final String content;

    @Override
    public void executeAction(MasterServerClientInterface masterServerClientInterface) {

    }

    @Override
    public String toString() {
        return "[Write Action] FileName:{" + fileName + "}, Content:{" + content + "}";
    }
}
