package cc.sjyuan.spring.jwt.entity;

import cc.sjyuan.spring.jwt.util.StringUtils;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
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

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "role")
    private Role role;

    public User() {
        id = StringUtils.uuid();
    }
}
