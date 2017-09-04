package cc.sjyuan.spring.jwt.exception;

public class UserExistedException extends RuntimeException {

    public UserExistedException(String s) {
        super(s);
    }
}
