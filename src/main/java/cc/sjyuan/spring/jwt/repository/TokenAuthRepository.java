package cc.sjyuan.spring.jwt.repository;

public interface TokenAuthRepository {

    String generateToken(String subject);
    String extractAuthorizedSubject(String token);

}