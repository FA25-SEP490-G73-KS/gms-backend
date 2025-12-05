package fpt.edu.vn.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AllowanceType {
    MEAL("Phụ cấp ăn trưa"),
    OVERTIME("Phụ cấp tăng ca"),
    BONUS("Thưởng"),
    OTHER("Khác");

    private final String vietnamese;

    public String getVietnamese() {
        return this.vietnamese;
    }
}
