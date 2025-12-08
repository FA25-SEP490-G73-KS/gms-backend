package fpt.edu.vn.gms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZnsAppointmentInfo {
    private String address;
    private String appointmentCode;
    private String appointmentDate;
    private String fullName;
}
