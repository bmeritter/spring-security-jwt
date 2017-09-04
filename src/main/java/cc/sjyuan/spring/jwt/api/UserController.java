package cc.sjyuan.spring.jwt.api;

import cc.sjyuan.spring.jwt.configuration.security.JWTUser;
import cc.sjyuan.spring.jwt.entity.User;
import cc.sjyuan.spring.jwt.service.UserService;
import cc.sjyuan.spring.jwt.util.UserFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import static cc.sjyuan.spring.jwt.configuration.security.APISecureRolePrivilege.CREATE_USER;
import static cc.sjyuan.spring.jwt.configuration.security.APISecureRolePrivilege.RETRIVE_USER;

@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Secured(CREATE_USER)
    public JWTUser create(@RequestBody User user) throws Exception {
        return UserFactory.fromUser(userService.create(user));
    }

    @GetMapping("{username}")
    @ResponseStatus(HttpStatus.OK)
    @Secured(RETRIVE_USER)
    public JWTUser find(@PathVariable String username) throws Exception {
        return UserFactory.fromUser(userService.findByName(username));
    }
}