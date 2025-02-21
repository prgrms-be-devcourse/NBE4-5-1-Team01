package com.team1.beanstore.domain.admin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/GCcoffee/admin")
@RequiredArgsConstructor
public class AdminAuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;


    @Value("${admin.password}")
    private String encodedPassword;

    record LoginReqBody(@NonNull String password) {
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReqBody body) {
        String password = body.password();

        // 비밀번호가 맞으면 토큰 발급
        if (passwordEncoder.matches(password, encodedPassword)) {
            // JWT 발급
            String jwtToken = authTokenService.genToken();

            return ResponseEntity.ok(
                    Map.of(
                            "status", 200,
                            "msg", "로그인 성공",
                            "token", jwtToken
                    )
            );
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", 401,
                        "msg", "비밀번호가 틀렸습니다."
                ));
    }

}
