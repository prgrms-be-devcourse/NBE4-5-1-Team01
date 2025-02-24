package com.team1.beanstore.global;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
import jakarta.servlet.http.Cookie;

@Component
@RequiredArgsConstructor
@RequestScope
public class Rq {
    private final HttpServletResponse response;
    private final HttpServletRequest request;


    public void addCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);

        cookie.setDomain("localhost");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setAttribute("SameSite", "Strict");
        response.addCookie(cookie);
    }

    public String getHeader(String name) {
        return request.getHeader(name);
    }

    public String getCookie(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie.getValue();
            }
        }

        return null;
    }

    public void setHeader(String name, String value) {
        response.setHeader(name, value);
    }

    public void setLogin(String accessToken, String refreshToken) {
        addCookie("accessToken", accessToken);
        setHeader("Authorization", "Bearer " + accessToken);
        addCookie("refreshToken", refreshToken);
    }
}
