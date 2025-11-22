package fpt.edu.vn.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DebtStatus {

    CON_NO("CÒN_NỢ"),
    DA_TAT_TOAN("ĐÃ_TẤT_TOÁN");

    private final String label;
}
