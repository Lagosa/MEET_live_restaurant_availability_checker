package blank.meet.server.ws;

public class UserNotActiveException extends ClientException {
    public UserNotActiveException(String message) {
        super(message);
    }
}
