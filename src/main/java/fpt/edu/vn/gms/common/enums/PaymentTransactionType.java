package fpt.edu.vn.gms.common.enums;

import java.util.stream.Stream;

import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentTransactionType {
    DEPOSIT("DEPOSIT"),
    PAYMENT("PAYMENT");

    private final String value;

    public static PaymentTransactionType fromValue(String value) {
        return Stream.of(PaymentTransactionType.values())
                .filter(type -> type.getValue().equals(value))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(
                        "PaymentTransactionValue '%s' is not found".formatted(value)));
    }
}
