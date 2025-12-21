package fpt.edu.vn.gms.entity;

import com.google.gson.annotations.SerializedName;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "one_time_token")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class OneTimeToken {
    @Id
    @Column(length = 36)
    private String id;

    @Column(name = "token", columnDefinition = "TEXT")
    @SerializedName("token")
    private String token;

    @Column(name = "expires_at")
    @SerializedName("expires_at")
    public String expiresAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}