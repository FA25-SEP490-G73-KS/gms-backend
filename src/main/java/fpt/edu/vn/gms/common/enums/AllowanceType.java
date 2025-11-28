package fpt.edu.vn.gms.common.enums;

import lombok.Getter;

@Getter
public enum AllowanceType {
    MEAL("Phụ cấp ăn trưa"),
    OVERTIME("Phụ cấp tăng ca"),
    BONUS("Thưởng"),
    OTHER("Khác");

    private final String vietnamese;

    AllowanceType(String vietnamese) {
        this.vietnamese = vietnamese;
    }
}
