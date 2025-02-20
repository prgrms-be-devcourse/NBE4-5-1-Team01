package com.team1.beanstore.domain.order;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Map;

public record OrderRequest(
        @Email(message = "올바른 이메일 형식을 입력하세요.")
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email,

        @NotBlank(message = "주소는 필수 입력값입니다.")
        String address,

        @NotBlank(message = "우편번호는 필수 입력값입니다.")
        String zipCode,

        @NotEmpty(message = "주문할 상품 목록이 비어 있을 수 없습니다.")
        Map<Long, Integer> productQuantities
) {}
