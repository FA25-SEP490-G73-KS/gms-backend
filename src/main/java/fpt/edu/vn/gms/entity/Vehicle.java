package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehicle")
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vehicle_id")
    private Long vehicleId;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "license_plate", length = 20, unique = true)
    private String licensePlate; // Biển số xe

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_model_id", referencedColumnName = "vehicle_model_id")
    private VehicleModel vehicleModel;

    @Column(name = "year")
    private Integer year; // Năm sản xuất

    @Column(name = "vin", length = 50)
    private String vin; // Số khung
}
