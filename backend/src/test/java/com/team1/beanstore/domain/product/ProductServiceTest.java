package com.team1.beanstore.domain.product;


import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
@AutoConfigureMockMvc
class ProductServiceTest {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long productId1;
    private Long productId2;
    private Long deletedProductId;

    @BeforeEach
    void setUp() {
        Product product1 = productRepository.saveAndFlush(Product.builder()
                .name("에티오피아 예가체프")
                .description("과일 향과 부드러운 산미가 특징인 원두")
                .price(12000)
                .imageUrl("ethiopia.jpg")
                .inventory(10)
                .category(ProductCategory.HAND_DRIP)
                .build());

        Product product2 = productRepository.saveAndFlush(Product.builder()
                .name("콜롬비아 수프리모")
                .description("견과류와 초콜릿 향이 감도는 원두")
                .price(13000)
                .imageUrl("colombia.jpg")
                .inventory(5)
                .category(ProductCategory.DECAF)
                .build());

        // 논리 삭제된 상품 저장
        Product deletedProduct = productRepository.saveAndFlush(Product.builder()
                .name("삭제된 상품")
                .description("이 상품은 논리 삭제되었습니다.")
                .price(15000)
                .imageUrl("deleted.jpg")
                .inventory(3)
                .category(ProductCategory.HAND_DRIP)
                .deletedAt(LocalDateTime.now())
                .build());

        this.productId1 = product1.getId();
        this.productId2 = product2.getId();
        this.deletedProductId = deletedProduct.getId();
    }

    @Test
    @DisplayName("카테고리별 상품 조회 성공")
    void getProductsByCategory_Success() {
        // given
        int page = 0;
        int pageSize = 10;
        String sort = "asc";

        // when
        Page<ProductResponse> result = productService.getProductsByCategory(ProductCategory.HAND_DRIP, page, pageSize, sort);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
                .extracting(ProductResponse::id)
                .contains(productId1)
                .doesNotContain(productId2);

        assertThat(result.getContent()).allMatch(product -> product.category().equals(ProductCategory.HAND_DRIP));
    }

    @Test
    @DisplayName("논리 삭제된 상품은 조회되지 않음")
    void getProductsByCategory_ExcludeDeletedProducts() {
        // given
        int page = 0;
        int pageSize = 10;
        String sort = "asc";

        // when
        Page<ProductResponse> result = productService.getProductsByCategory(ProductCategory.HAND_DRIP, page, pageSize, sort);

        // then
        assertThat(result.getContent())
                .extracting(ProductResponse::id)
                .doesNotContain(deletedProductId);
    }

    @Test
    @DisplayName("상품 이름으로 검색 성공")
    void searchProductsByName_Success() {
        // given
        int page = 0;
        int pageSize = 10;
        String sort = "asc";
        String searchKeyword = "예가체프";

        // when
        Page<ProductResponse> result = productService.searchProductsByName(searchKeyword, page, pageSize, sort);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
                .extracting(ProductResponse::id)
                .contains(productId1)
                .doesNotContain(productId2);

        assertThat(result.getContent()).allMatch(product -> product.name().contains(searchKeyword));
    }

    @Test
    @DisplayName("논리 삭제된 상품은 검색되지 않음")
    void searchProductsByName_ExcludeDeletedProducts() {
        // given
        int page = 0;
        int pageSize = 10;
        String sort = "asc";
        String searchKeyword = "삭제된 상품";

        // when
        Page<ProductResponse> result = productService.searchProductsByName(searchKeyword, page, pageSize, sort);

        // then
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 카테고리 조회 시 빈 결과 반환")
    void getProductsByCategory_NoResults() {
        // given
        int page = 0;
        int pageSize = 10;
        String sort = "asc";

        // when
        Page<ProductResponse> result = productService.getProductsByCategory(ProductCategory.TEA, page, pageSize, sort);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("상품 이름 검색 시 부분 문자열 검색 가능")
    void searchProductsByName_PartialMatch() {
        // given
        int page = 0;
        int pageSize = 10;
        String sort = "asc";
        String partialKeyword = "체프";

        // when
        Page<ProductResponse> result = productService.searchProductsByName(partialKeyword, page, pageSize, sort);

        // then
        assertThat(result).isNotEmpty();
        assertThat(result.getContent())
                .extracting(ProductResponse::name)
                .anyMatch(name -> name.contains(partialKeyword));
    }

    @Test
    @DisplayName("카테고리별 상품 조회 시 페이지네이션이 정상적으로 동작")
    void getProductsByCategory_Pagination() {
        // given
        int page = 0;
        int pageSize = 1;
        String sort = "asc";

        // when
        Page<ProductResponse> result = productService.getProductsByCategory(ProductCategory.HAND_DRIP, page, pageSize, sort);

        // then
        assertThat(result).hasSize(1);
    }
}