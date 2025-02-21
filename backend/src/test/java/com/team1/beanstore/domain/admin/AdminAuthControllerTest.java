package com.team1.beanstore.domain.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.beanstore.standard.Ut;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class AdminAuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${custom.jwt.secret-key}")
    private String keyString;

    @Value("${custom.jwt.expire-seconds}")
    private int expireSeconds;

    @Test
    @DisplayName("로그인 성공 + JWT 검증")
    void login1() throws Exception {
        String password = "6863";

        String requestBody = """
                {
                    "password": "%s"
                }
                """.formatted(password).trim().stripIndent();


        ResultActions resultActions = mvc.perform(post("/GCcoffee/admin/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print());

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(AdminAuthController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.msg").value("로그인 성공"))
                .andExpect(jsonPath("$.token").exists());

        String jsonResponse = resultActions.andReturn().getResponse().getContentAsString();
        Map<String, Object> responseMap = objectMapper.readValue(jsonResponse, Map.class);
        String token = (String) responseMap.get("token");

        assertThat(token).isNotNull();
        assertThat(token.split("\\.")).hasSize(3);

        boolean isValid = Ut.Jwt.isValidToken(keyString, token);
        assertThat(isValid).isTrue();

        Map<String, Object> payload = Ut.Jwt.getPayload(keyString, token);
        assertThat(payload).isNotNull();
        assertThat(payload.get("role")).isEqualTo("admin");
    }

    @Test
    @DisplayName("로그인 실패")
    void login2() throws Exception {
        String password = "1234";

        String requestBody = """
                {
                    "password": "%s"
                }
                """.formatted(password).trim().stripIndent();


        ResultActions resultActions = mvc.perform(post("/GCcoffee/admin/login")
                        .contentType("application/json")
                        .content(requestBody))
                .andDo(print());

        resultActions
                .andExpect(status().isUnauthorized())
                .andExpect(handler().handlerType(AdminAuthController.class))
                .andExpect(handler().methodName("login"))
                .andExpect(jsonPath("$.status").value("401"))
                .andExpect(jsonPath("$.msg").value("비밀번호가 틀렸습니다."));
        ;
    }
}