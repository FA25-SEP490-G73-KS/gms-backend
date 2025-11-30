package fpt.edu.vn.gms.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TransactionResponseDto {
  private Long id;
  private String paymentLinkId;
  private String customerFullName;
  private String customerPhone;
  private String customerAddress;
  private String method;
  private String type;
  private Long amount;
  private LocalDateTime createdAt;
  private String paymentUrl;
}