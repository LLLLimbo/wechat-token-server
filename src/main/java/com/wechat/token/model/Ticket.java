package com.wechat.token.model;

import com.wechat.token.repo.CryptoConverter;
import lombok.Data;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "wechat_access_token",indexes = {
        @Index(name = "sys_config_id_index", columnList = "id", unique = true),
        @Index(name = "sys_config_app_id_index", columnList = "app_id", unique = true)
}
)
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer pid;

    @Column(name = "id", length = 32)
    private String id;

    @Column(name = "token", length = 512)
    @Convert(converter = CryptoConverter.class)
    private String token;

    @Column(name = "app_id", length = 18)
    private String appId;

    @Column(name = "secret", length = 32)
    private String secret;

    @Column(name = "grant_type", columnDefinition = "varchar(20) default 'client_credential'")
    private String grantType;

    @Column(name = "refresh_interval", columnDefinition = "integer(11) default 7000")
    private Long refreshInterval;

    @Column(name = "last_refreshed_at")
    private Date lastRefreshedAt;

    @Column(name = "expired_at")
    private Date expiredAt;

    @Column(name = "create_at")
    private Date createdAt;

}
