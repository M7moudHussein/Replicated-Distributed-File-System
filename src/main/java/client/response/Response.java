package client.response;

import lombok.Getter;

@Getter
public class Response {
    private final ResponseError responseError;
    private final String result;

    public Response(final String result) {
        this.result = result;
        this.responseError = null;
    }

    public Response(final ResponseError responseError) {
        this.result = "";
        this.responseError = responseError;
    }

    public boolean hasError() {
        return responseError != null;
    }

    @Override
    public String toString() {
        return "Response -> " + (hasError() ? "Error: " + this.responseError : "Content: " + this.result);
    }
}
