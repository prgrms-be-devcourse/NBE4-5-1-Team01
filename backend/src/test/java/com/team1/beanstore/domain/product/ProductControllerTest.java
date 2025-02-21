package com.team1.beanstore.domain.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        productService.createItem("테스트 상품", 10000, "http://example.com/image.jpg", 10, "테스트 설명", ProductCategory.TEA);
    }


    private void checkProduct(ResultActions resultActions, Product product) throws Exception {

        resultActions
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.id").value(product.getId()))
                .andExpect(jsonPath("$.data.name").value(product.getName()))
                .andExpect(jsonPath("$.data.price").value(product.getPrice()))
                .andExpect(jsonPath("$.data.imageUrl").value(product.getImageUrl()))
                .andExpect(jsonPath("$.data.inventory").value(product.getInventory()))
                .andExpect(jsonPath("$.data.description").value(product.getDescription()))
                .andExpect(jsonPath("$.data.category").value(product.getCategory()));
    }


    private ResultActions writeRequest(
            String name,
            int price,
            String imageUrl,
            int inventory,
            String description,
            String category
    ) throws Exception {
        return mvc
                .perform(
                        post("/GCcoffee/admin/item")
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

        Product product = productService.getLatestItem()
                .orElseThrow(() -> new IllegalStateException("상품이 존재하지 않습니다."));

        resultActions
                .andExpect(status().isOk())
                .andExpect(handler().handlerType(ProductController.class))
                .andExpect(handler().methodName("createItem"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value(product.getId()));

    }

}
