package cc.sjyuan.spring.jwt.service;

import cc.sjyuan.spring.jwt.entity.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    User create(User user) throws Exception;

    User findByName(String username);
}
