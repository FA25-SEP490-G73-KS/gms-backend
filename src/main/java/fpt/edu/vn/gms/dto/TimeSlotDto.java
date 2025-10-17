package fpt.edu.vn.gms.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotDto {
    private Long timeSlotId;
    private String label;
    private int booked;
    private int maxCapacity;
    private boolean available;
}
