package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.ProductCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}