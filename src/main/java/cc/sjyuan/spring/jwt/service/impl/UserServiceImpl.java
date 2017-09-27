package cc.sjyuan.spring.jwt.service.impl;

import cc.sjyuan.spring.jwt.configuration.security.JWTUser;
import cc.sjyuan.spring.jwt.entity.Privilege;
import cc.sjyuan.spring.jwt.entity.Role;
import cc.sjyuan.spring.jwt.entity.User;
import cc.sjyuan.spring.jwt.exception.UserExistedException;
import cc.sjyuan.spring.jwt.exception.UserNotExistException;
import cc.sjyuan.spring.jwt.repository.UserRepository;
import cc.sjyuan.spring.jwt.service.UserService;
import cc.sjyuan.spring.jwt.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UsernameNotFoundException("Username does not exist.");
        }
        Role role = user.getRole();
        return JWTUser.builder()
                .username(user.getName())
                .password(user.getPassword())
                .role(user.getRole().getSymbol().name())
                .privileges(role.getPrivileges().stream().map(Privilege::getSymbol).collect(toList()))
                .build();
    }

    @Override
    @Transactional
    public User create(User user) {
        user.setId(StringUtils.uuid());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if (userRepository.findByName(user.getName()) != null) {
            throw new UserExistedException("User already exists.");
        }
        return userRepository.save(user);
    }

    @Override
    public User findByName(String username) {
        User user = userRepository.findByName(username);
        if (user == null) {
            throw new UserNotExistException(username);
        }
        return userRepository.findByName(username);
    }
}
