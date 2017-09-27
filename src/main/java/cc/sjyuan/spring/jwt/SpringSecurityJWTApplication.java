package cc.sjyuan.spring.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class SpringSecurityJWTApplication {
	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJWTApplication.class, args);
	}
}
