Usage of Spring security with JWT.


## Prerequisite and Skill stacks

- JDK8
- Gradle
- Spring boot
- [Docker](https://www.docker.com/)
- [Docker-compose](https://docs.docker.com/compose/gettingstarted/)
- [Redis](https://redis.io/)
- [MySQL](https://www.mysql.com/cn/)


## Setup Spring Security

#### Resolve dependency:

```groovy
dependencies {
	...
	compile('org.springframework.boot:spring-boot-starter-security')
	...
}
```

#### Configure security 

```java
@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/login").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(passwordEncoder());
    }

    ...
}
```

#### Provide UserDetailService Implementation

```java
import org.springframework.security.core.userdetails.UserDetailsService;
public interface UserService extends UserDetailsService {}
```

```java
@Service
public class UserServiceImpl implements UserService {
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
	...
}
```

## Integrate JWT with Spring Scurity

Provide JWTAuthRepository

```java
@Repository
public class JWTAuthRepositoryImpl implements TokenAuthRepository {
    @Value("${security.jwt.secret:_SEMS_JWT_SECRET_201708240805987}")
    private String jwtSecret;
    @Value("${security.jwt.expiration-in-seconds}")
    private long expirationInSeconds;

    @Override
    public String generateToken(Map<String, Object> payload) {
        return Jwts.builder()
                .setClaims(payload)
                .setExpiration(new Date(System.currentTimeMillis() + expirationInSeconds * 1000))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
    @Override
    public String extractAuthorizedPayload(String jwtToken) {
        return StringUtils.writeObjectAsJsonString(Jwts.parser().setSigningKey(jwtSecret)
                .parseClaimsJws(jwtToken)
                .getBody());
    }
}
```

#### Implement auth service

```java
@Service
public class AuthServiceImpl implements AuthService {
    @Override
    public JWTUser login(HttpServletResponse response, LoginRequestUser loginRequestUser) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequestUser.getUsername(), loginRequestUser.getPassword()));
        JWTUser principal = (JWTUser) authenticate.getPrincipal();

        Map payload = StringUtils.readJsonStringAsObject(StringUtils.writeObjectAsJsonString(principal), Map.class);

        response.addHeader(header, String.join(" ", tokenPrefix,
                authRepository.generateToken(payload)));
        return principal;
    }
```
