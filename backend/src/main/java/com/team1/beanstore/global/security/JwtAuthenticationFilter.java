package com.team1.beanstore.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.beanstore.domain.admin.AuthTokenService;
import com.team1.beanstore.global.Rq;
import com.team1.beanstore.global.dto.Empty;
import com.team1.beanstore.global.dto.RsData;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenService authTokenService;
    private final Rq rq;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getAccessToken();

        if (token == null) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authTokenService.isValid(token) && !authTokenService.isExpiredToken(token)) {
            setAuthentication(token);
            filterChain.doFilter(request, response);
            return;
        }

        String refreshToken = rq.getCookie("refreshToken");
        if (refreshToken != null && authTokenService.isValid(refreshToken)) {

            String newAccessToken = authTokenService.genAccessToken();
            rq.addCookie("accessToken", newAccessToken);
            setAuthentication(newAccessToken);

            filterChain.doFilter(request, response);
            return;
        }


        sendErrorResponse(response, "401-1", "세션이 만료되었습니다. 다시 로그인해주세요.");
    }

    private void setAuthentication(String token) {
        String role = authTokenService.getRoleFromToken(token);
        if (role.equals("admin")) {
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            "admin",
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

            SecurityContextHolder.getContext().setAuthentication(auth);
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.equals("/GCcoffee/admin/login") || !path.startsWith("/GCcoffee/admin/");
    }

    private String getAccessToken() {
        String bearerToken = rq.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return rq.getCookie("accessToken");
    }

    private void sendErrorResponse(HttpServletResponse response, String code, String message) throws IOException {
        RsData<Empty> errorResponse = new RsData<>(code, message);
        response.setStatus(errorResponse.getStatusCode());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }
}
