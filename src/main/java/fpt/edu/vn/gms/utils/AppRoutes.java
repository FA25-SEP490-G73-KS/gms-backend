package fpt.edu.vn.gms.utils;

import java.util.List;

import org.springframework.util.AntPathMatcher;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AppRoutes {
  private static final AntPathMatcher pathMatcher = new AntPathMatcher();

  public static final String API_PREFIX = "/api";

  public static final String AUTH_PREFIX = API_PREFIX + "/auth";
  public static final String APPOINTMENTS_PREFIX = API_PREFIX + "/appointments";
  public static final String CUSTOMERS_PREFIX = API_PREFIX + "/customers";
  public static final String EMPLOYEES_PREFIX = API_PREFIX + "/employees";
  public static final String NOTIFICATIONS_PREFIX = API_PREFIX + "/notifications";
  public static final String OTP_PREFIX = API_PREFIX + "/otp";
  public static final String PARTS_PREFIX = API_PREFIX + "/parts";
  public static final String PRICE_QUOTATIONS_PREFIX = API_PREFIX + "/price-quotations";
  public static final String QUOTATION_ITEMS_PREFIX = API_PREFIX + "/quotation-items";
  public static final String SERVICE_TICKETS_PREFIX = API_PREFIX + "/service-tickets";
  public static final String STOCK_EXPORTS_PREFIX = API_PREFIX + "/stock-exports";
  public static final String VEHICLES_PREFIX = API_PREFIX + "/vehicles";
  public static final String PAYMENT_PREFIX = API_PREFIX + "/payments";
  public static final String ZNS_NOTIFICATIONS_PREFIX = API_PREFIX + "/zns-notifications";

  public static final List<String> whitelistedRoutes = List.of(
      API_PREFIX + "/docs/**",
      API_PREFIX + "/swagger-ui/**",
      "/api-docs/**",
      "/favicon.ico",
      "/index.html",
      "/swagger-ui/**",
      "/ws/**");

  public static boolean isWhitelistedRoute(String path) {
    return whitelistedRoutes.stream().anyMatch(route -> pathMatcher.match(route, path));
  }
}
