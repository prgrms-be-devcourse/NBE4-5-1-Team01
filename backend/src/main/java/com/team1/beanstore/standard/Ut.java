package com.team1.beanstore.standard;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

public class Ut {
    public static class Jwt {
        public static String createToken(String keyString, int expireSeconds, Map<String, Object> claims) {
            Date issuedAt = new Date();
            Date expiration = new Date(issuedAt.getTime() + 1000L * expireSeconds);

            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

            return Jwts.builder()
                    .claims(claims)
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .signWith(secretKey)
                    .compact();
        }

        public static boolean isValidToken(String keyString, String token) {
            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

                Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parse(token);
            } catch (Exception e) {
                return false;
            }

            return true;
        }


        public static Map<String, Object> getPayload(String keyString, String token) {
            SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

            return (Map<String, Object>) Jwts
                    .parser()
                    .verifyWith(secretKey)
                    .build()
                    .parse(token)
                    .getPayload();
        }

        public static boolean isExpired(String keyString, String token) {
            try {
                SecretKey secretKey = Keys.hmacShaKeyFor(keyString.getBytes());

                Date expiration = Jwts.parser()
                        .verifyWith(secretKey)
                        .build()
                        .parseSignedClaims(token)
                        .getPayload()
                        .getExpiration();

                return expiration.before(new Date()); // 🔥 현재 시간보다 만료 시간이 이전이면 만료됨

            } catch (Exception e) {
                return true; // 🔥 만료된 토큰이거나 잘못된 토큰일 경우 true 반환
            }
        }
    }
}
