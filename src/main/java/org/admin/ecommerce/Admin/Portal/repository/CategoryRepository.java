package org.admin.ecommerce.Admin.Portal.repository;

import org.admin.ecommerce.Admin.Portal.model.TblCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface CategoryRepository extends JpaRepository<TblCategory, Long> {

    Page<TblCategory> findAll (Pageable pageable);

    @Query("SELECT p FROM TblCategory p WHERE p.isDeleted = false ORDER BY p.title ASC")
    Page<TblCategory> findByIsDeletedFalse(Pageable pageable);

    @Query("SELECT e FROM TblCategory e WHERE e.isDeleted = false")
    List<TblCategory> getAllRecordNonDeleted();

    TblCategory findByTitle(String title);

    Optional<TblCategory> findById(TblCategory categoryId);
}
