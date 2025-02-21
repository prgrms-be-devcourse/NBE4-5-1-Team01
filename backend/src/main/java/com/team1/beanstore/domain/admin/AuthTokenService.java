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

    public String genToken() {
        return Ut.Jwt.createToken(
                keyString,
                expireSeconds,
                Map.of("role", "admin")
        );
    }

    public String getRoleFromToken(String token) {
        Map<String, Object> payload = Ut.Jwt.getPayload(keyString, token);
        return (String) payload.get("role");
    }
}
