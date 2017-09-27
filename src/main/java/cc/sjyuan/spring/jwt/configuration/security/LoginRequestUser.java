package cc.sjyuan.spring.jwt.configuration.security;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestUser {
    private String username;
    private String password;
}
