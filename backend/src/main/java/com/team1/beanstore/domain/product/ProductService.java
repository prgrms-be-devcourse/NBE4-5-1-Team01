package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(ProductCategory category, int page, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(page, pageSize,
                Sort.by(sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "id"));

        return productRepository.findByCategory(category, pageable)
                .map(ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProductsByName(String keyword, int page, int pageSize, String sort) {
        Pageable pageable = PageRequest.of(page, pageSize,
                Sort.by(sort.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC, "id"));

        return productRepository.searchByName(keyword, pageable)
                .map(ProductResponse::from);
    }


    public Map<String, Long> createItem(
            String name,
            int price,
            String image_url,
            int inventory,
            String description,
            ProductCategory category
    ) {
        Product product = productRepository.save(
                Product.builder()
                        .name(name)
                        .price(price)
                        .imageUrl(image_url)
                        .inventory(inventory)
                        .description(description)
                        .category(category)
                        .build()
        );
        return Map.of("id", product.getId());
    }


    public Long modifyItem(
            long id,
            String name,
            int price,
            String image_url,
            int inventory,
            String description,
            ProductCategory category
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("상품이 존재하지 않습니다."));
        product.updateProduct( name, price, image_url, inventory, description, category);
        return id;
    }


    public Optional<Product> getLatestItem() {
        return productRepository.findTopByOrderByIdDesc();
    }



    public Page<ProductResponse> getListedItems(int page, int pageSize, SearchKeywordType keywordType, String keyword, String sort) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(direction, "name"));

        String likeKeyword = "%" + keyword + "%";

        if (SearchKeywordType.name == keywordType) {
            return productRepository.findByNameLike(likeKeyword, pageRequest).map(ProductResponse::from);
        } else if (SearchKeywordType.description == keywordType) {
            return productRepository.findByDescriptionLike(likeKeyword, pageRequest).map(ProductResponse::from);
        }

        ProductCategory categoryEnum;
        try {
            categoryEnum = ProductCategory.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("잘못된 카테고리입니다: " + keyword);
        }

        return productRepository.findByCategory(categoryEnum, pageRequest).map(ProductResponse::from);
    }


    public ProductResponse getItem(Long id) {
        Product product = productRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        //프로덕트가 안 존재한다면
        if (product == null) {
            throw new IllegalArgumentException("상품이 존재하지 않습니다.");
        }
        return ProductResponse.from(product);
    }


    @Transactional()
    public Long deleteItem(Long id) {
        Product product = productRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        product.delete();
        return product.getId();
    }
}