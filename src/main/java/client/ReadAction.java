package client;

import lombok.AllArgsConstructor;
import masterserver.MasterServerClientInterface;

import java.io.IOException;

@AllArgsConstructor
public class ReadAction implements Action {
    private final String fileName;

    @Override
    public void executeAction(MasterServerClientInterface masterServerClientInterface) throws IOException {
        System.out.println("tried to read" + fileName);
        masterServerClientInterface.read(fileName);
    }

    @Override
    public String toString() {
        return "[Read Action] FileName:{" + fileName + "}";
    }
}
