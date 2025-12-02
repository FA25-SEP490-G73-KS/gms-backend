package fpt.edu.vn.gms.entity;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "zalo_access_token")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_token", length = 1000)
    @SerializedName("access_token")
    private String accessToken;

    @Column(name = "refresh_token", length = 1000)
    @SerializedName("refresh_token")
    private String refreshToken;

    @Transient
    @SerializedName("expires_in")
    public String expiresIn;

    @Column(name = "created_at")
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateAt;
}
