package fpt.edu.vn.gms.dto;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.CustomerType;
import lombok.*;

public record CustomerDto(Long id, String name, String phoneNumber) {}