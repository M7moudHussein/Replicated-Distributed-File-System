package client.response;

import lombok.Getter;

public enum ResponseError {

    FILE_NOT_FOUND("Requested file wasn't found"), MASTER_NOT_FOUND("Master server isn't found in the registry"),
    MASTER_NO_RESPONSE("No response received from master server"), REPLICA_REMOTE_ERROR("Remote error when contacting replica server"),
    REPLICA_NOT_BOUND("Replica interface is not bound"), REPLICA_IO_EXCEPTION("IO exception triggered inside Replica server");

    @Getter private final String errorMessage;

    private ResponseError(final String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return this.errorMessage;
    }
}
