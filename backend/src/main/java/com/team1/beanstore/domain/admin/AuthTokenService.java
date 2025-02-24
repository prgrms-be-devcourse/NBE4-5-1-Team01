package com.team1.beanstore.domain.admin;

import com.team1.beanstore.standard.Ut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthTokenService {

    @Value("${custom.jwt.secret-key}")
    private String keyString;

    @Value("${custom.jwt.expire-seconds}")
    private int expireSeconds;

    @Value("${custom.jwt.refresh-expire-seconds}")
    private int REFRESH_TOKEN_EXPIRATION;

    public String genAccessToken() {
        return Ut.Jwt.createToken(
                keyString,
                expireSeconds,
                Map.of("role", "admin")
        );
    }

    public String genRefreshToken() {
        return Ut.Jwt.createToken(
                keyString,
                REFRESH_TOKEN_EXPIRATION,
                Map.of("type", "refresh", "role", "admin")
        );
    }

    public String getRoleFromToken(String token) {
        Map<String, Object> payload = Ut.Jwt.getPayload(keyString, token);
        return (String) payload.get("role");
    }

    public boolean isExpiredToken(String token) {
        return Ut.Jwt.isExpired(keyString, token);
    }

    public boolean isValid(String token) {
        return Ut.Jwt.isValidToken(keyString, token);
    }
}