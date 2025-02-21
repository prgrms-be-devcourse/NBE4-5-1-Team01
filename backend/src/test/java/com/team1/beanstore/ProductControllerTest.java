package com.team1.beanstore;

import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        productRepository.save(Product.builder()
                .name("에티오피아 예가체프")
                .description("과일 향과 부드러운 산미가 특징인 원두")
                .price(12000)
                .imageUrl("ethiopia.jpg")
                .inventory(15)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.save(Product.builder()
                .name("과테말라 안티구아")
                .description("스모키한 향과 묵직한 바디감을 자랑하는 원두")
                .price(14000)
                .imageUrl("guatemala.jpg")
                .inventory(10)
                .category(ProductCategory.HAND_DRIP)
                .build());

        productRepository.save(Product.builder()
                .name("콜롬비아 수프리모")
                .description("견과류와 초콜릿 향이 감도는 원두")
                .price(13000)
                .imageUrl("colombia.jpg")
                .inventory(20)
                .category(ProductCategory.DECAF)
                .build());

        productRepository.save(Product.builder()
                .name("다즐링 홍차")
                .description("세계 3대 홍차 중 하나로, 깊은 풍미를 자랑")
                .price(10000)
                .imageUrl("darjeeling.jpg")
                .inventory(10)
                .category(ProductCategory.TEA)
                .build());
    }

    @Test
    @DisplayName("카테고리별 상품 조회 API 성공 테스트")
    void getProductsByCategory_Success() throws Exception {
        mockMvc.perform(get("/GCcoffee/items")
                        .param("category", "HAND_DRIP")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("sort", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].name").value("에티오피아 예가체프"));
    }

    @Test
    @DisplayName("카테고리 없이 상품 조회 요청 시 실패 테스트")
    void getProductsByCategory_Failure_NoCategory() throws Exception {
        mockMvc.perform(get("/GCcoffee/items")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("sort", "asc"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("페이지네이션 동작 검증 - 2개씩 가져오기")
    void getProductsByCategory_Pagination() throws Exception {
        mockMvc.perform(get("/GCcoffee/items")
                        .param("category", "HAND_DRIP")
                        .param("page", "0")
                        .param("pageSize", "2")
                        .param("sort", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2)); // 2개까지만 가져오는지 확인
    }

    @Test
    @DisplayName("내림차순 정렬 확인")
    void getProductsByCategory_SortDescending() throws Exception {
        mockMvc.perform(get("/GCcoffee/items")
                        .param("category", "HAND_DRIP")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("sort", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("과테말라 안티구아"));
    }


    @Test
    @DisplayName("잘못된 카테고리 요청 시 400 Bad Request 반환")
    void getProductsByCategory_InvalidCategory() throws Exception {
        mockMvc.perform(get("/GCcoffee/items")
                        .param("category", "INVALID_CATEGORY")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("sort", "asc"))
                .andExpect(status().isBadRequest());
    }


    @Test
    @DisplayName("페이지 크기 생략 시 기본값 적용")
    void getProductsByCategory_DefaultPageSize() throws Exception {
        mockMvc.perform(get("/GCcoffee/items")
                        .param("category", "HAND_DRIP")
                        .param("page", "0")
                        .param("sort", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }
}