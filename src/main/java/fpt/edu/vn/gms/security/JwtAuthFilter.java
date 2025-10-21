package fpt.edu.vn.gms.security;

import fpt.edu.vn.gms.service.AccountDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final AccountDetailsService accountDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Không có token thì bỏ qua, cho phép tiếp tục filter chain
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        if (!jwtUtils.validateJwtToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Lấy username & roles từ JWT (không cần query DB)
        String username = jwtUtils.extractUsername(token);
        List<String> roles = jwtUtils.extractRoles(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Convert roles -> GrantedAuthorities
            var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(username, null, authorities);
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Gắn thông tin user vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
