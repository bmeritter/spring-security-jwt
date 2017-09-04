package cc.sjyuan.spring.jwt.repository;


import cc.sjyuan.spring.jwt.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrivilegeRepository extends JpaRepository<Privilege, String> {
}
