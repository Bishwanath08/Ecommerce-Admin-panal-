package org.admin.ecommerce.Admin.Portal.repository;

import org.admin.ecommerce.Admin.Portal.model.TblProducts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<TblProducts, Long> {

    Page<TblProducts> findAll(Pageable pageable);

    @Query("SELECT p FROM TblProducts p WHERE p.isDeleted = false")
    Page<TblProducts> findByIsDeletedFalse(Pageable pageable);

    Optional<TblProducts> findByProductNameAndIsDeletedFalse(String productName);

}
