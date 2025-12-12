package fpt.edu.vn.gms.common.enums;

import lombok.Getter;

@Getter
public enum DeductionType {

    DAMAGE("Bồi thường hỏng hóc"),
    PENALTY("Phạt"),
    OTHER("Khác");

    private final String vietnamese;

    DeductionType(String vietnamese) {
        this.vietnamese = vietnamese;
    }
}
