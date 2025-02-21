package com.team1.beanstore.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.NonNull;

import java.util.Map;

public record OrderRequest(
        @Schema(description = "주문자 이메일", example = "user@example.com")
        @Email(message = "올바른 이메일 형식을 입력하세요.")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        @NonNull String email,

        @Schema(description = "배송 주소", example = "경기도 피카츄 라이츄로 진화")
        @NotBlank(message = "주소는 필수 입력값입니다.")
        @NonNull String address,

        @Schema(description = "우편번호", example = "12345")
        @NotBlank(message = "우편번호는 필수 입력값입니다.")
        @NonNull String zipCode,

        @Schema(description = "주문 상품 목록 (상품 ID : 수량)")
        @NotEmpty(message = "주문할 상품 목록이 비어 있을 수 없습니다.")
        @NonNull Map<Long, Integer> productQuantities
) {}