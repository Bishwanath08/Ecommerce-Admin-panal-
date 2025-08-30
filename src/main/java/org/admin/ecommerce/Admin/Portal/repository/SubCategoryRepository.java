package org.admin.ecommerce.Admin.Portal.repository;

import org.admin.ecommerce.Admin.Portal.model.TblCategory;
import org.admin.ecommerce.Admin.Portal.model.TblSubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface SubCategoryRepository extends JpaRepository<TblSubCategory, Long> {

    Optional<TblSubCategory> findById(@Param("id") Long id);

    Optional<TblSubCategory> findById(TblSubCategory newSubCategoryId);

    @Query("SELECT sc FROM TblSubCategory sc JOIN FETCH sc.categoryId WHERE sc.isDeleted = false")
    Page<TblSubCategory> getAllRecord(Pageable pageable);

    @Query("SELECT e FROM TblSubCategory e WHERE e.isDeleted = false")
    List<TblSubCategory> getAllRecordNonDeleted();


    TblSubCategory findBySubCategoryTitle(String subCategoryTitle);

}
