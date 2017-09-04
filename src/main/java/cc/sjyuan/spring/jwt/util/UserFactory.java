package cc.sjyuan.spring.jwt.util;


import cc.sjyuan.spring.jwt.configuration.security.JWTUser;
import cc.sjyuan.spring.jwt.entity.User;

public class UserFactory {
    public static User fromJWTUser(JWTUser jwtUser){
        User user = new User();
        user.setName(jwtUser.getUsername());
        user.setRoles(jwtUser.getRoles());
        user.setPassword(jwtUser.getPassword());
        return user;
    }

    public static JWTUser fromUser(User user){
        JWTUser jwtUser = new JWTUser();
        jwtUser.setUsername(user.getName());
        jwtUser.setRoles(user.getRoles());
        return jwtUser;
    }
}
