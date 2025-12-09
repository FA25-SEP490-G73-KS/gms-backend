package fpt.edu.vn.gms.entity;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "one_time_token")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class OneTimeToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token", columnDefinition = "TEXT")
    @SerializedName("token")
    private String token;

    @Column(name = "expires_at")
    @SerializedName("expires_at")
    public String expiresAt;

    public OneTimeToken(String token, String expiresAt) {
        this.token = token;
        this.expiresAt = expiresAt;
    }
}
