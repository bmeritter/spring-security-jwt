package cc.sjyuan.spring.jwt.exception;

public class UserNotExistException extends RuntimeException {
    public UserNotExistException(String username) {
        super(username + " does not exist.");
    }
}
