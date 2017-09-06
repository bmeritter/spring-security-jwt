package cc.sjyuan.spring.jwt.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalControllerAdvice {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidCredentialException.class)
    public void handleHackerException() {
        log.error("Invalid credential.");
    }

    @ExceptionHandler(UserExistedException.class)
    public ResponseEntity<?> handleUserExistedException(UserExistedException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UserNotExistException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleUserNotExistException(UserNotExistException e) {
        log.error(e.getMessage());
    }
}

