package fpt.edu.vn.gms.dto;

import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;
import lombok.*;

public record CustomerDto(Long id, String name, String phoneNumber) {}