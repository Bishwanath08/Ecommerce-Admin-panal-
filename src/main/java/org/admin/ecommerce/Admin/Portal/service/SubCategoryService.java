package org.admin.ecommerce.Admin.Portal.service;

import org.admin.ecommerce.Admin.Portal.model.TblCategory;
import org.admin.ecommerce.Admin.Portal.model.TblSubCategory;
import org.admin.ecommerce.Admin.Portal.repository.CategoryRepository;
import org.admin.ecommerce.Admin.Portal.repository.SubCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;


@Service
public class SubCategoryService {

    private static final Logger logger = LoggerFactory.getLogger(SubCategoryService.class);

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<TblSubCategory> getSubCategoryListData(int page, int size, String sortBy, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return subCategoryRepository.getAllRecord(pageable);
    }

    public List<TblCategory> getAllCategory() {
        return categoryRepository.getAllRecordNonDeleted();
    }

    public TblSubCategory getSubCategoryByTitle(String subCategoryTitle) {
        return subCategoryRepository.findBySubCategoryTitle(subCategoryTitle);
    }

    public TblSubCategory saveSubCategory(TblSubCategory subCategory, Long categoryId) {
        try {
            Optional<TblCategory> categoryOptional = categoryRepository.findById(categoryId);
            if (categoryOptional.isEmpty()) {
                logger.error("Category with ID " + categoryId + "Not found.");
                return null;
            }
            TblCategory category = categoryOptional.get();
            subCategory.setCategoryId(category);
            subCategory.setSubCategoryTitle(subCategory.getSubCategoryTitle());
            subCategory.setCreatedAt(Instant.now().toEpochMilli());
            subCategory.setIsDeleted(false);

            return subCategoryRepository.save(subCategory);
        } catch (Exception e) {
            logger.error("Error saving subCategory: ", e);
            return null;
        }
    }

    public TblSubCategory updateSubCategory(Long subCategoryId, TblSubCategory updatedSubCategoryData, TblCategory newCategoryId) {
        try {
            Optional<TblSubCategory> existingSubCategory = subCategoryRepository.findById(subCategoryId);

            if (existingSubCategory.isEmpty()) {
                logger.warn("SubCategory with ID " + subCategoryId + "not found for Update.");
                return null;
            }
            TblSubCategory subCategory = existingSubCategory.get();

            TblCategory newCategory = null;
            if (newCategoryId == null) {
                Optional<TblCategory> categoryOptional = categoryRepository.findById(newCategoryId);
                if (categoryOptional.isEmpty()) {
                    logger.warn("New Category with ID " + newCategoryId + "not found. Sub-category will retain old category.");
                } else {
                    newCategory = categoryOptional.get();
                }
            }

            if (updatedSubCategoryData.getSubCategoryTitle() != null && !updatedSubCategoryData.getSubCategoryTitle().trim().isEmpty()) {
                subCategory.setSubCategoryTitle(updatedSubCategoryData.getSubCategoryTitle().trim());
            }

            if (newCategory != null) {
                subCategory.setCategoryId(newCategoryId);

            }

            subCategory.setUpdatedAt(Instant.now().toEpochMilli());
            subCategory.setCategoryId(subCategory.getCategoryId());

            return subCategoryRepository.save(subCategory);
        } catch (Exception e) {
            logger.error("Error updating sub-category with ID" + subCategoryId, e);
            return null;
        }
    }

    public Optional<TblSubCategory> getSubCategoryById(Long id) {
        return subCategoryRepository.findById(id);
    }

    public void deleteSubCategory(Long id) {
        Optional<TblSubCategory> subCategoryToDelete = subCategoryRepository.findById(id);
        if (subCategoryToDelete.isPresent()) {
            TblSubCategory subCategory =  subCategoryToDelete.get();
            subCategory.setIsDeleted(true);
            subCategoryRepository.save(subCategory);
        }else {
            throw new IllegalArgumentException("Sub Category with ID " + id + "not found");
        }
    }
}
