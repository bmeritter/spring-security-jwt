package cc.sjyuan.spring.jwt.repository;

import cc.sjyuan.spring.jwt.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}
