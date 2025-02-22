package com.team1.beanstore;

import com.team1.beanstore.domain.admin.AuthTokenService;
import com.team1.beanstore.domain.product.ProductRepository;
import com.team1.beanstore.domain.product.ProductService;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AuthTokenService authTokenService;

    @Autowired
    private ProductService productService;

    private String token;

    @BeforeEach
    void setUp() {
        //login
        token = authTokenService.genToken();


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
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 조회 성공"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].name").value("에티오피아 예가체프"));
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
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 조회 성공"))
                .andExpect(jsonPath("$.data.content.length()").value(2)); // 2개까지만 가져오는지 확인
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
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 조회 성공"))
                .andExpect(jsonPath("$.data.content[0].name").value("과테말라 안티구아"));
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
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 조회 성공"))
                .andExpect(jsonPath("$.data.content").isArray());
    }


    //------
    private ResultActions writeRequest(
            String name,
            int price,
            String imageUrl,
            int inventory,
            String description,
            String category
    ) throws Exception {
        return mockMvc
                .perform(
                        post("/GCcoffee/admin/item")
                                .header("Authorization", "Bearer " + token)
                                .content("""
                                        {
                                            "name": "%s",
                                            "price": %d,
                                            "imageUrl": "%s",
                                            "inventory": %d,
                                            "description": "%s",
                                            "category": "%s"
                                        }
                                        """
                                        .formatted(name, price, imageUrl, inventory, description, category)
                                        .stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
    }

    private ResultActions updateRequest(
            long id,
            String name,
            int price,
            String imageUrl,
            int inventory,
            String description,
            String category
    ) throws Exception {
        return mockMvc
                .perform(
                        patch("/GCcoffee/admin/item/%d".formatted(id))
                                .header("Authorization", "Bearer " + token)
                                .content("""
                                        {
                                            "name": "%s",
                                            "price": %d,
                                            "imageUrl": "%s",
                                            "inventory": %d,
                                            "description": "%s",
                                            "category": "%s"
                                        }
                                        """
                                        .formatted(name, price, imageUrl, inventory, description, category)
                                        .stripIndent())
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());
    }

    private ResultActions deleteRequest(long postId) throws Exception {
        return mockMvc
                .perform(
                        delete("/GCcoffee/admin/delete/%d".formatted(postId))
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print());
    }

    private ResultActions itemRequest(long productId) throws Exception {
        return mockMvc
                .perform(
                        get("/GCcoffee/admin/item/%d".formatted(productId))
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print());

    }

    @Test
    @DisplayName("글 다건 조회")
    void items1() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/GCcoffee/admin/items")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())

                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[1].name").value("다즐링 홍차"))
                .andExpect(jsonPath("$.data.items[1].price").value(10000))
                .andExpect(jsonPath("$.data.items[1].imageUrl").value("darjeeling.jpg"))
                .andExpect(jsonPath("$.data.items[1].inventory").value(10))
                .andExpect(jsonPath("$.data.items[1].description").value("세계 3대 홍차 중 하나로, 깊은 풍미를 자랑"))
                .andExpect(jsonPath("$.data.items[1].category").value("TEA"));
    }

    @Test
    @DisplayName("글 다건 조회 - 페이징")
    void items2() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/GCcoffee/admin/items")
                                .param("page", "2")
                                .param("pageSize", "1")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())

                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].name").value("다즐링 홍차"))
                .andExpect(jsonPath("$.data.items[0].price").value(10000))
                .andExpect(jsonPath("$.data.items[0].imageUrl").value("darjeeling.jpg"))
                .andExpect(jsonPath("$.data.items[0].inventory").value(10))
                .andExpect(jsonPath("$.data.items[0].description").value("세계 3대 홍차 중 하나로, 깊은 풍미를 자랑"))
                .andExpect(jsonPath("$.data.items[0].category").value("TEA"));
    }

    @Test
    @DisplayName("글 다건 조회 - 검색 - 이름")
    void items3() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/GCcoffee/admin/items")
                                .param("keywordType", "name")
                                .param("keyword", "과테말라 안티구아")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())

                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].name").value("과테말라 안티구아"))
                .andExpect(jsonPath("$.data.items[0].price").value(14000))
                .andExpect(jsonPath("$.data.items[0].imageUrl").value("guatemala.jpg"))
                .andExpect(jsonPath("$.data.items[0].inventory").value(10))
                .andExpect(jsonPath("$.data.items[0].description").value("스모키한 향과 묵직한 바디감을 자랑하는 원두"))
                .andExpect(jsonPath("$.data.items[0].category").value("HAND_DRIP"));
    }

    @Test
    @DisplayName("글 다건 조회 - 검색 - 설명")
    void items4() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/GCcoffee/admin/items")
                                .param("keywordType", "description")
                                .param("keyword", "묵직")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())

                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].name").value("과테말라 안티구아"))
                .andExpect(jsonPath("$.data.items[0].price").value(14000))
                .andExpect(jsonPath("$.data.items[0].imageUrl").value("guatemala.jpg"))
                .andExpect(jsonPath("$.data.items[0].inventory").value(10))
                .andExpect(jsonPath("$.data.items[0].description").value("스모키한 향과 묵직한 바디감을 자랑하는 원두"))
                .andExpect(jsonPath("$.data.items[0].category").value("HAND_DRIP"));
    }

    @Test
    @DisplayName("글 다건 조회 - 검색 - 카테고리")
    void items5() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/GCcoffee/admin/items")
                                .param("keywordType", "category")
                                .param("keyword", "TEA")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isOk())

                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data.items").isArray())
                .andExpect(jsonPath("$.data.items[0].name").value("다즐링 홍차"))
                .andExpect(jsonPath("$.data.items[0].price").value(10000))
                .andExpect(jsonPath("$.data.items[0].imageUrl").value("darjeeling.jpg"))
                .andExpect(jsonPath("$.data.items[0].inventory").value(10))
                .andExpect(jsonPath("$.data.items[0].description").value("세계 3대 홍차 중 하나로, 깊은 풍미를 자랑"))
                .andExpect(jsonPath("$.data.items[0].category").value("TEA"));
    }

    @Test
    @DisplayName("글 다건 조회 - 검색 - 잘못된 카테고리")
    void items6() throws Exception {
        ResultActions resultActions = mockMvc
                .perform(
                        get("/GCcoffee/admin/items")
                                .param("keywordType", "category")
                                .param("keyword", "CLOTHES")
                                .header("Authorization", "Bearer " + token)
                )
                .andDo(print());

        resultActions
                .andExpect(status().isBadRequest())

                .andExpect(handler().methodName("getItems"))
                .andExpect(jsonPath("$.code").value("400-1"))
                .andExpect(jsonPath("$.msg").value("잘못된 검색타입: CLOTHES"));
    }



    @Test
    @DisplayName("상품 단건 조회")
    void item1() throws Exception {

        Product product = productService.getLatestItem().get();
        long id = product.getId();

        ResultActions resultActions = itemRequest(id);

        resultActions
                .andExpect(status().isOk())

                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("상품 조회 성공"))
                .andExpect(jsonPath("$.data.name").exists());
    }

    @Test
    @DisplayName("상품 단건 조회 - 없는 글 조회")
    void item2() throws Exception {

        long productId = 10000;

        ResultActions resultActions = itemRequest(productId);

        resultActions
                .andExpect(status().isNotFound())

                .andExpect(handler().methodName("getItem"))
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 글"));
    }

    @Test
    @DisplayName("상품 생성")
    void write1() throws Exception {

        String name = "새로운 상품명";
        String description = "새로운 상품 설명";
        int price = 10000;
        int inventory = 100;
        String imageUrl = "http://example.com/image.jpg";
        String category = "티";

        ResultActions resultActions = writeRequest(name, price, imageUrl, inventory, description, category);

        resultActions
                .andExpect(status().isCreated())

                .andExpect(handler().methodName("createItem"))
                .andExpect(jsonPath("$.code").value("201-1"))
                .andExpect(jsonPath("$.msg").value("상품 등록 성공"))
                .andExpect(jsonPath("$.data.id").exists());
    }


    @Test
    @DisplayName("상품 생성 실패 - 빈 이름")
    void write2() throws Exception {

        String name = "";
        String description = "새로운 상품 설명";
        int price = 10000;
        int inventory = 100;
        String imageUrl = "http://example.com/image.jpg";
        String category = "티";

        ResultActions resultActions = writeRequest(name, price, imageUrl, inventory, description, category);

        resultActions
                .andExpect(status().isBadRequest())

                .andExpect(handler().methodName("createItem"));
    }

    @Test
    @DisplayName("상품 생성 실패 - 빈 설명")
    void write3() throws Exception {

        String name = "새로운 상품명";
        String description = "";
        int price = 10000;
        int inventory = 100;
        String imageUrl = "http://example.com/image.jpg";
        String category = "티";

        ResultActions resultActions = writeRequest(name, price, imageUrl, inventory, description, category);

        resultActions
                .andExpect(status().isBadRequest())

                .andExpect(handler().methodName("createItem"));
    }

    @Test
    @DisplayName("상품 생성 실패 - 가격 0원")
    void write4() throws Exception {

        String name = "새로운 상품명";
        String description = "새로운 상품 설명";
        int price = 0;
        int inventory = 100;
        String imageUrl = "http://example.com/image.jpg";
        String category = "티";

        ResultActions resultActions = writeRequest(name, price, imageUrl, inventory, description, category);

        resultActions
                .andExpect(status().isBadRequest())

                .andExpect(handler().methodName("createItem"));
    }

    @Test
    @DisplayName("상품 생성 실패 - 잘못된 카테고리")
    void write5() throws Exception {

        String name = "새로운 상품명";
        String description = "새로운 상품 설명";
        int price = 10000;
        int inventory = 100;
        String imageUrl = "http://example.com/image.jpg";
        String category = "허브";

        ResultActions resultActions = writeRequest(name, price, imageUrl, inventory, description, category);

        resultActions
                .andExpect(status().isBadRequest())

                .andExpect(handler().methodName("createItem"));
    }

    @Test
    @DisplayName("상품 생성 실패 - 잘못된 이미지 URL")
    void write6() throws Exception {

        String name = "새로운 상품명";
        String description = "새로운 상품 설명";
        int price = 10000;
        int inventory = 100;
        String imageUrl = "";
        String category = "티";

        ResultActions resultActions = writeRequest(name, price, imageUrl, inventory, description, category);

        resultActions
                .andExpect(status().isBadRequest())

                .andExpect(handler().methodName("createItem"));
    }


    @Test
    @DisplayName("상품 수정")
    void update1() throws Exception {
        String name = "수정된 상품명";
        String description = "수정된 상품 설명";
        int price = 15000;
        int inventory = 150;
        String imageUrl = "http://example.com/image.jpg";
        String category = "티";

        Product product = productService.getLatestItem().get();
        long id = product.getId();
        ResultActions resultActions = updateRequest(id, name, price, imageUrl, inventory, description, category);

        resultActions
                .andExpect(status().isOk())

                .andExpect(handler().methodName("modifyItem"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 상품 수정 성공".formatted(id)));

        Product updatedProduct = productRepository.findById(id).get();

        assertThat(updatedProduct.getName()).isEqualTo(name);
        assertThat(updatedProduct.getDescription()).isEqualTo(description);
        assertThat(updatedProduct.getPrice()).isEqualTo(price);
        assertThat(updatedProduct.getInventory()).isEqualTo(inventory);
        assertThat(updatedProduct.getImageUrl()).isEqualTo(imageUrl);
        assertThat(updatedProduct.getCategory()).isEqualTo(ProductCategory.TEA);
    }

    @Test
    @DisplayName("상품 수정 실패 - 존재하지 않는 상품")
    void update2() throws Exception {
        long id = 100;
        String name = "수정된 상품명";
        String description = "수정된 상품 설명";
        int price = 15000;
        int inventory = 150;
        String imageUrl = "http://example.com/image.jpg";
        String category = "티";


        ResultActions resultActions = updateRequest(id, name, price, imageUrl, inventory, description, category);

        resultActions
                .andExpect(status().isNotFound())

                .andExpect(handler().methodName("modifyItem"))
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 상품"));
    }

    @Test
    @DisplayName("상품 삭제")
    void delete1() throws Exception {
        Product product = productService.getLatestItem().get();
        long id = product.getId();

        ResultActions resultActions = deleteRequest(id);

        resultActions
                .andExpect(status().isOk())

                .andExpect(handler().methodName("deleteItem"))
                .andExpect(jsonPath("$.code").value("200-1"))
                .andExpect(jsonPath("$.msg").value("%d번 상품 삭제 성공".formatted(id)));
    }

    @Test
    @DisplayName("상품 삭제 실패 - 존재하지 않는 상품")
    void delete2() throws Exception {
        long id = 1000;

        ResultActions resultActions = deleteRequest(id);

        resultActions
                .andExpect(status().isNotFound())

                .andExpect(handler().methodName("deleteItem"))
                .andExpect(jsonPath("$.code").value("404-1"))
                .andExpect(jsonPath("$.msg").value("존재하지 않는 상품"));
    }

}