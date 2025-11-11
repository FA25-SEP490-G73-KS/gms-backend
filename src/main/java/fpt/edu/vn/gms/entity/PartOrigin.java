package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "part_origin")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PartOrigin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String country; // ví dụ: Japan, USA

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;
}
