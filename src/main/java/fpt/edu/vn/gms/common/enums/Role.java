package fpt.edu.vn.gms.common.enums;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Role {
    MANAGER("MANAGER"),
    SERVICE_ADVISOR("SERVICE_ADVISOR"),
    ACCOUNTANT("ACCOUNTANT"),
    WAREHOUSE("WAREHOUSE");

    String value;
}
