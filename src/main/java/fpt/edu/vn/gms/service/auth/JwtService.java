package fpt.edu.vn.gms.service.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import fpt.edu.vn.gms.entity.Employee;

import java.security.Key;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.access.secret}")
    private String ACCESS_SECRET;

    @Value("${jwt.refresh.secret}")
    private String REFRESH_SECRET;

    @Value("${jwt.access.expiration}")
    private Long ACCESS_EXPIRATION;

    @Value("${jwt.refresh.expiration}")
    private Long REFRESH_EXPIRATION;

    private final RedisService redisService;

    // -------------------- GENERATE TOKEN --------------------
    public String generateAccessToken(Employee employee) {
        Instant currentInstant = Instant.now();
        Date issuedAt = Date.from(currentInstant);
        Date expiration = Date.from(currentInstant.plusSeconds(ACCESS_EXPIRATION));

        return Jwts.builder()
                .setSubject(employee.getEmployeeId().toString())
                .claim("role", employee.getAccount().getRole())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(ACCESS_SECRET))
                .compact();
    }

    public String generateRefreshToken(Employee employee) {
        Instant currentInstant = Instant.now();
        Date issuedAt = Date.from(currentInstant);
        Date expiration = Date.from(currentInstant.plusSeconds(REFRESH_EXPIRATION));

        return Jwts.builder()
                .setSubject(employee.getEmployeeId().toString())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSigningKey(REFRESH_SECRET))
                .compact();
    }

    // -------------------- VALIDATION --------------------
    public Jws<Claims> verifyAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(ACCESS_SECRET))
                .build()
                .parseClaimsJws(token);
    }

    public Jws<Claims> verifyRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(REFRESH_SECRET))
                .build()
                .parseClaimsJws(token);
    }

    // -------------------- EXTRACT DATA --------------------
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public List<String> extractRoles(String token) {
        Claims claims = extractAllClaims(token);
        Object rolesObj = claims.get("roles");
        if (rolesObj instanceof List<?>) {
            return ((List<?>) rolesObj).stream()
                    .map(Object::toString)
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(ACCESS_SECRET))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSigningKey(String secret) {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public boolean isTokenInvalidated(Long employeeId, Date tokenIssuedAt) {
        String key = "user:%s:tokens:invalidated_before".formatted(employeeId);
        Instant invalidatedBefore = redisService.get(key, Instant.class);

        if (invalidatedBefore == null) {
            return false;
        }

        return tokenIssuedAt.toInstant().isBefore(invalidatedBefore);
    }
}
