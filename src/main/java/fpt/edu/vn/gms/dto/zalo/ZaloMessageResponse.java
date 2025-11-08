package fpt.edu.vn.gms.dto.zalo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZaloMessageResponse<T> {
    private int error;
    private String message;
    private T data;
}