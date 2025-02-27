package com.team1.beanstore.domain.product;

import com.team1.beanstore.domain.product.entity.Product;
import com.team1.beanstore.domain.product.entity.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.id IN :ids AND p.deletedAt IS NULL")
    List<Product> findByIds(@Param("ids") List<Long> ids);

    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.deletedAt IS NULL")
    Page<Product> findByCategory(@Param("category") ProductCategory category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.deletedAt IS NULL")
    Page<Product> searchByName(@Param("keyword") String keyword, Pageable pageable);

    Page<Product> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Product> findByNameLikeAndDeletedAtIsNull(String likeKeyword, Pageable pageable);

    Page<Product> findByDescriptionLikeAndDeletedAtIsNull(String likeKeyword, Pageable pageable);

    Optional<Product> findTopByDeletedAtIsNullOrderByIdDesc();
}