package cc.sjyuan.spring.jwt.configuration.security;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LoginRequestUser {
    private String username;
    private String password;
}
