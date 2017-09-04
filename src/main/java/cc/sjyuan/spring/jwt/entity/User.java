package cc.sjyuan.spring.jwt.entity;

import cc.sjyuan.spring.jwt.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User implements Serializable {

    @Id
    private String id;

    @Column(unique = true)
    private String name;

    private String password;

    private String realName;

    @CreatedDate
    private long createdDate;

    @LastModifiedDate
    private long updatedDate;

    @ManyToMany(cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    @JoinTable(name = "t_user_role", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_symbol", referencedColumnName = "symbol"))
    private List<Role> roles = new ArrayList<>();

    public User() {
        id = StringUtils.uuid();
    }
}
