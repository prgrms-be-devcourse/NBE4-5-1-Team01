package com.team1.beanstore.domain.admin;

import com.team1.beanstore.global.Rq;
import com.team1.beanstore.global.dto.Empty;
import com.team1.beanstore.global.dto.RsData;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/GCcoffee/admin")
@RequiredArgsConstructor
@Tag(name = "관리자 인증 API", description = "관리자 인증 후 토큰을 주는 API")
public class AdminAuthController {

    private final PasswordEncoder passwordEncoder;
    private final AuthTokenService authTokenService;
    private final Rq rq;


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

        // 비밀번호가 틀리면 오류 처리
        if (!passwordEncoder.matches(password, encodedPassword)) {
            return new RsData<>(
                    "401-1",
                    "비밀번호가 틀렸습니다."
            );
        }

        // accessToken, refreshToken 발급
        String accessToken = authTokenService.genAccessToken();
        String refreshToken = authTokenService.genRefreshToken();

        // 쿠키 및 헤더 설정
        rq.setLogin(accessToken, refreshToken);

        return new RsData<>(
                "200-1",
                "로그인 성공",
                accessToken
        );


    }

    @Operation(summary = "관리자 로그아웃", description = "로그아웃 시 쿠키 삭제")
    @DeleteMapping("/logout")
    public RsData<Empty> logout() {
        rq.removeCookie("accessToken");
        rq.removeCookie("refreshToken");

        return new RsData<>(
                "200-1",
                "로그아웃 되었습니다."
        );
    }

}