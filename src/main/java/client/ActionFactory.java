package client;

public class ActionFactory {
    private static ActionFactory instance;

    private static final String READ_ACTION = "read";
    private static final String WRITE_ACTION = "write";

    private static final String TOKEN_SEPARATOR = ";";

    private ActionFactory() {
    }

    public static ActionFactory getInstance() {
        if(instance == null) {
            instance = new ActionFactory();
        }
        return instance;
    }

    public Action buildAction(String str) {
        String[] tokens = str.split(TOKEN_SEPARATOR);
        if(tokens.length == 2 && tokens[0].equals(READ_ACTION)) {
            return new ReadAction(tokens[1]);
        } else if(tokens.length == 3 && tokens[0].equals(WRITE_ACTION)) {
            return new WriteAction(tokens[1], tokens[2]);
        } else {
            throw new IllegalArgumentException("Invalid line specified in the sample input");
        }
    }
}
