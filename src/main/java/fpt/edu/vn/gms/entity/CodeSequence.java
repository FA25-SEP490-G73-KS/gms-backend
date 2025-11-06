package fpt.edu.vn.gms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "code_sequence")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CodeSequence {
    @Id
    @Column(name = "prefix", nullable = false, length = 10)
    private String prefix;

    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "current_value", nullable = false)
    private long currentValue;
}
