package cc.sjyuan.spring.jwt.service;


import cc.sjyuan.spring.jwt.configuration.security.JWTUser;
import cc.sjyuan.spring.jwt.configuration.security.LoginRequestUser;
import cc.sjyuan.spring.jwt.entity.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService {
    void logout(HttpServletRequest authorization);

    JWTUser getAuthorizedJWTUser(HttpServletRequest request);

    boolean hasJWTToken(HttpServletRequest request);

    boolean isTokenInBlackList(HttpServletRequest request);

    JWTUser login(HttpServletResponse response, LoginRequestUser loginRequestUser);
}
