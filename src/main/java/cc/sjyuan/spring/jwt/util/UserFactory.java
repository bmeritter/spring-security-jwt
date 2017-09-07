package cc.sjyuan.spring.jwt.util;


import cc.sjyuan.spring.jwt.configuration.security.JWTUser;
import cc.sjyuan.spring.jwt.entity.Privilege;
import cc.sjyuan.spring.jwt.entity.User;

import java.util.stream.Collectors;

public class UserFactory {
    public static JWTUser fromUser(User user) {
        JWTUser jwtUser = new JWTUser();
        jwtUser.setUsername(user.getName());
        jwtUser.setRole(user.getRole().getSymbol().name());
        jwtUser.setPrivileges(user.getRole().getPrivileges().stream().map(Privilege::getSymbol).collect(Collectors.toList()));
        jwtUser.setCreatedDateTime(user.getCreatedDateTime());
        return jwtUser;
    }


}
