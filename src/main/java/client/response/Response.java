package client.response;

import lombok.Getter;
import replicaserver.WriteMessage;

@Getter
public class Response {
    private final ResponseError responseError;
    private final String result;
    private final WriteMessage writeMessage;

    public Response(final String result) {
        this.result = result;
        this.writeMessage = null;
        this.responseError = null;
    }

    public Response(final ResponseError responseError) {
        this.result = "";
        this.writeMessage = null;
        this.responseError = responseError;
    }

    public Response(final WriteMessage writeMessage){
        this.result = writeMessage.toString();
        this.responseError = null;
        this.writeMessage = writeMessage;
    }

    public boolean hasError() {
        return responseError != null;
    }

    @Override
    public String toString() {
        return "Response -> " + (hasError() ? "Error: " + this.responseError : "Content: " + this.result);
    }
}
