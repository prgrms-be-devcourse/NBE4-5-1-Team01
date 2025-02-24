package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.admin.dto.CreateItemReq;
import com.team1.beanstore.domain.admin.dto.UpdateItemInfoReq;
import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import com.team1.beanstore.global.dto.PageDto;
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

    private final ProductMapper productMapper;
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

    @Transactional
    public Map<String, Long> createItem(CreateItemReq reqBody) {
        Product product = productRepository.save(productMapper.toEntity(reqBody));
        return Map.of("id", product.getId());
    }

    @Transactional
    public Map<String, Long> modifyItem(long id, UpdateItemInfoReq reqBody) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 상품"));

        product.updateInfo(reqBody);
        return Map.of("id", id);
    }

    @Transactional
    public Map<String, String> modifyItemImage(long id, String imageUrl) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 상품"));
        product.updateImageUrl(imageUrl);
        return Map.of("imageUrl", imageUrl);
    }

    public Optional<Product> getLatestItem() {
        return productRepository.findTopByDeletedAtIsNullOrderByIdDesc();
    }

    @Transactional
    public PageDto<ProductResponse> getListedItems(int page, int pageSize, SearchKeywordType keywordType, String keyword, String sort) {
        Sort.Direction direction = "ASC".equalsIgnoreCase(sort) ? Sort.Direction.ASC : Sort.Direction.DESC;
        PageRequest pageRequest = PageRequest.of(page - 1, pageSize, Sort.by(direction, "name"));

        String likeKeyword = "%" + keyword + "%";
        if (SearchKeywordType.id == keywordType) {
            Page<ProductResponse> mapperAll = productRepository.findAllByDeletedAtIsNull(pageRequest).map(ProductResponse::from);
            return new PageDto<>(mapperAll);
        } else if (SearchKeywordType.name == keywordType) {
            Page<ProductResponse> mapperName = productRepository.findByNameLikeAndDeletedAtIsNull(likeKeyword, pageRequest).map(ProductResponse::from);
            return new PageDto<>(mapperName);
        } else if (SearchKeywordType.description == keywordType) {
            Page<ProductResponse> mapperDescription = productRepository.findByDescriptionLikeAndDeletedAtIsNull(likeKeyword, pageRequest).map(ProductResponse::from);
            return new PageDto<>(mapperDescription);
        }

        ProductCategory categoryEnum;
        try {
            categoryEnum = ProductCategory.valueOf(keyword.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ServiceException("400-1", "잘못된 검색타입: " + keyword);
        }

        Page<ProductResponse> mapperCategory = productRepository.findByCategory(categoryEnum, pageRequest).map(ProductResponse::from);
        return new PageDto<>(mapperCategory);
    }

    @Transactional(readOnly = true)
    public ProductResponse getItem(Long id) {
        Product product = productRepository.findById(id).orElseThrow(
                () -> new ServiceException("404-1", "존재하지 않는 글")
        );
        if (product.getDeletedAt() != null) {
            throw new ServiceException("404-1", "존재하지 않는 글");
        }
        return ProductResponse.from(product);
    }


    @Transactional()
    public Map<String, Long> deleteItem(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ServiceException("404-1", "존재하지 않는 상품"));
        product.delete();
        Long deletedId = product.getId();
        return Map.of("id", deletedId);
    }
}