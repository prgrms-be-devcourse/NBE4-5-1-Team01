package com.team1.beanstore.domain.admin;

import com.team1.beanstore.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/GCcoffee/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 인증 API", description = "관리자 인증 후 토큰을 주는 API")
public class AdminAuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;


    @Value("${admin.password}")
    private String encodedPassword;

    public record LoginReqBody(@NonNull String password) {
    }

    @PostMapping("/login")
    @Operation(summary = "관리자 로그인", description = "관리자가 로그인하여 JWT 를 발급받습니다.")
    @ApiResponse(responseCode = "200", description = "로그인 성공")
    @ApiResponse(responseCode = "401", description = "비밀번호가 틀렸습니다.")
    public RsData<String> login(@Valid @RequestBody LoginReqBody body) {
        String password = body.password();

        // 비밀번호가 맞으면 토큰 발급
        if (passwordEncoder.matches(password, encodedPassword)) {
            // JWT 발급
            String jwtToken = authTokenService.genToken();

            return new RsData<>(
                    "200-1",
                    "로그인 성공",
                    jwtToken
            );
        }

        return new RsData<>(
                "401-1",
                "비밀번호가 틀렸습니다."
        );
    }

}
