package cc.sjyuan.spring.jwt.repository;


import cc.sjyuan.spring.jwt.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    User findByName(String name);
}
