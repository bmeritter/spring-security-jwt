Usage of Spring security with JWT.


## Prerequisite and Skill stacks

- [JDK8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Gradle](https://gradle.org/)
- [Flyway](https://flywaydb.org/)
- [Spring boot](https://projects.spring.io/spring-boot/)
- [Docker](https://www.docker.com/)
- [Docker-compose](https://docs.docker.com/compose/gettingstarted/)
- [Redis](https://redis.io/)
- [Nginx](https://nginx.org/en/)
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

**Provide JWTAuthRepository**

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

Sever receives `username` and `password` from login request body, then do verification. Once it's passed, jwt token should be generated and attached to the response header.

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

#### Handle user logout behaivor
While user requests logout, jwt token should be put into a black list with the same expiration as itself.

So a redis server is used to cache the logout token.

```java
@Override
public void logout(HttpServletRequest request) {
    String token = extractToken(request);
    String key = PREFIX_BLACK_LIST + token;
    redisTemplate.opsForValue().set(key, token);
    redisTemplate.expire(key, expirationInSeconds, TimeUnit.SECONDS);
}
```


#### Verify follow-up requests
Using a `OncePerRequestFilter` to filter each user request after login. Verification goes as following steps:

1. First check if the header `Authorization` exists. Absense of header means request is the login request or any other permited request, just let it go.
2. header `Authorization` exists, then check if the token is in black list. Existence means the token has been invalid. Refuse request immediately.
3. Token is not in black list, verify token and parse out the user info if valid.
4. User info contains privileges which will determine which apis can be accessed.

```java
@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (!authService.hasJWTToken(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        handlerRequestAttachedJWTToken(request, response, filterChain);
    }

    private void handlerRequestAttachedJWTToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        try {
            if (authService.isTokenInBlackList(request)) {
                logger.error("Black list token.");
                throw new InvalidCredentialException();
            }

            JWTUser jwtUser = authService.getAuthorizedJWTUser(request);
            UsernamePasswordAuthenticationToken token =
                    new UsernamePasswordAuthenticationToken(
                            jwtUser, null, jwtUser.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(token);

            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException | SignatureException | InvalidCredentialException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired.");
        }
    }
}
```

## Start up 

#### One click start up server with docker

Make sure the corresponing ports, such as`3307`,`8888`,`9000`,`6378` defined in `docker-compose.yml` file, are available. Of course, you can configure them.

Make sure `Docker`and `Docker-compose` are installed in your machine.

Run command to start up the Server:

```sh
$ docker-compose up -d
```

#### Start up with gradle task

Firstly, start up the dependency servers.

```sh
$ docker-compose up -d mysql
$ docker-compose up -d redis
$ docker-compose up -d nginx
```

Start server with gradle task.

```sh
$./gradlew bootRun
```

## Login with admin/123
待续

