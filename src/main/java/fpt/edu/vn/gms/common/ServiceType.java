package fpt.edu.vn.gms.common;

import lombok.Getter;

@Getter
public enum ServiceType {
    SUA_CHUA("Sửa chữa"),
    BAO_HANH("Bảo hành"),
    SON("Sơn");
    
    private final String displayName;
    
    ServiceType(String displayName) {
        this.displayName = displayName;
    }
}
