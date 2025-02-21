package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


    public Map<String, Long> modifyItem(
            long id,
            String name,
            int price,
            String image_url,
            int inventory,
            String description,
            ProductCategory category
    ) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 상품"));
        product.updateProduct( name, price, image_url, inventory, description, category);
        return Map.of("id", id);
    }


    public Optional<Product> getLatestItem() {
        return productRepository.findTopByOrderByIdDesc();
    }



    public Page<ProductResponse> getListedItems(int page, int pageSize, SearchKeywordType keywordType, String keyword, String sort) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(direction, "name"));

        String likeKeyword = "%" + keyword + "%";
        if (SearchKeywordType.id == keywordType) {
            return productRepository.findAll(pageRequest).map(ProductResponse::from);
        } else if (SearchKeywordType.name == keywordType) {
            return productRepository.findByNameLike(likeKeyword, pageRequest).map(ProductResponse::from);
        } else if (SearchKeywordType.description == keywordType) {
            return productRepository.findByDescriptionLike(likeKeyword, pageRequest).map(ProductResponse::from);
        }

        ProductCategory categoryEnum;
        try {
            categoryEnum = ProductCategory.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("400-1", "잘못된 검색타입: " + keyword);
        }

        return productRepository.findByCategory(categoryEnum, pageRequest).map(ProductResponse::from);
    }


    public ProductResponse getItem(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글")
        );
        return ProductResponse.from(product);
    }


    @Transactional()
    public Map<String, Long> deleteItem(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 상품")
        );
        product.delete();
        Long deletedId = product.getId();
        return Map.of("id", deletedId);

    }
}