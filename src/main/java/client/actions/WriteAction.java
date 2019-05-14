package client.actions;

import client.response.Response;
import lombok.AllArgsConstructor;
import masterserver.MasterServerClientInterface;

@AllArgsConstructor
public class WriteAction implements Action {
    private final String fileName;
    private final String content;

    @Override
    public Response executeAction(MasterServerClientInterface masterServerClientInterface) {
        return null;
    }

    @Override
    public String toString() {
        return "[Write Action] FileName:{" + fileName + "}, Content:{" + content + "}";
    }
}
