package client;

import lombok.AllArgsConstructor;
import masterserver.MasterServerClientInterface;

@AllArgsConstructor
public class ReadAction implements Action {
    private final String fileName;

    @Override
    public void executeAction(MasterServerClientInterface masterServerClientInterface) {

    }

    @Override
    public String toString() {
        return "[Read Action] FileName:{" + fileName + "}";
    }
}
