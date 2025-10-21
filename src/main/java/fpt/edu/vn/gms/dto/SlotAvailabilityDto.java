package fpt.edu.vn.gms.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SlotAvailabilityDto {

    private Long timeSlotId;
    private LocalTime startTime;
    private LocalTime endTime;
    private int max;
    private int bookedCount;
    private boolean available;
}