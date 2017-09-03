package cc.sjyuan.spring.springsecurity;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;


@RestController
public class UserController {

    @RequestMapping(value = "users/{username}",
            method = RequestMethod.GET)
    public ResponseEntity<?> get(@PathVariable String username) throws SQLException {
        return ResponseEntity.ok(username);
    }

    @PostMapping(value = "users")
    public ResponseEntity<?> create() throws SQLException {
        return ResponseEntity.ok("post");
    }

    @PutMapping(value = "users")
    public ResponseEntity<?> put() throws SQLException {
        return ResponseEntity.ok("put");
    }

    @PatchMapping(value = "users")
    public ResponseEntity<?> patch() throws SQLException {
        return ResponseEntity.ok("patch");
    }

}