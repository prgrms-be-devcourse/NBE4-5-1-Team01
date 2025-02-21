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
    }
}
