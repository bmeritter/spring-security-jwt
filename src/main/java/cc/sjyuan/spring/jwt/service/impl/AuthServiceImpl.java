package cc.sjyuan.spring.jwt.service.impl;

import cc.sjyuan.spring.jwt.configuration.security.JWTUser;
import cc.sjyuan.spring.jwt.configuration.security.LoginRequestUser;
import cc.sjyuan.spring.jwt.entity.Privilege;
import cc.sjyuan.spring.jwt.entity.User;
import cc.sjyuan.spring.jwt.exception.InvalidCredentialException;
import cc.sjyuan.spring.jwt.repository.TokenAuthRepository;
import cc.sjyuan.spring.jwt.repository.UserRepository;
import cc.sjyuan.spring.jwt.service.AuthService;
import cc.sjyuan.spring.jwt.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {
    private static final String PREFIX_BLACK_LIST = "SSJ-BLACKLIST-";

    @Value("${security.jwt.token-prefix:Bearer}")
    private String tokenPrefix;

    @Value("${security.jwt.header:Authorization}")
    private String header;

    @Value("${security.jwt.expiration-in-seconds}")
    private long expirationInSeconds;

    @Autowired
    private TokenAuthRepository authRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public JWTUser login(HttpServletResponse response, LoginRequestUser loginRequestUser) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestUser.getUsername(), loginRequestUser.getPassword()));
        JWTUser principal = (JWTUser) authenticate.getPrincipal();
        Map<String, Object> payload = new HashMap<String, Object>() {{
            put("privileges", principal.getPrivileges());
            put("username", principal.getUsername());
            put("role", principal.getRole());
        }};
        response.addHeader(header, String.join(" ", tokenPrefix,
                authRepository.generateToken(payload)));
        return principal;
    }

    @Override
    public void logout(HttpServletRequest request) {
        String token = extractToken(request);
        String key = PREFIX_BLACK_LIST + token;
        redisTemplate.opsForValue().set(key, token);
        redisTemplate.expire(key, expirationInSeconds, TimeUnit.SECONDS);
    }

    @Override
    public JWTUser getAuthorizedJWTUser(HttpServletRequest request) {
        String payload = authRepository.extractAuthorizedPayload(extractToken(request));
        return StringUtils.readJsonStringAsObject(payload, JWTUser.class);
    }

    @Override
    public boolean hasJWTToken(HttpServletRequest request) {
        String authorizationHeader = request.getHeader(header);
        return StringUtils.hasText(authorizationHeader) && authorizationHeader.startsWith(tokenPrefix);
    }

    @Override
    public boolean isTokenInBlackList(HttpServletRequest request) {
        String token = extractToken(request);
        return StringUtils.hasText(redisTemplate.opsForValue().get(PREFIX_BLACK_LIST + token));
    }

    private String extractToken(HttpServletRequest request) {
        if (!hasJWTToken(request)) {
            throw new InvalidCredentialException();
        }
        return request.getHeader(header).substring(tokenPrefix.length() + 1);
    }
}
