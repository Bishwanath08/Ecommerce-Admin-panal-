package org.admin.ecommerce.Admin.Portal.service;

import org.admin.ecommerce.Admin.Portal.model.TblCategory;
import org.admin.ecommerce.Admin.Portal.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public Page<TblCategory> getCategoryListData(int page, int size, String sortBy, String sortDir){
        Sort sort = Sort.by(sortBy);
        if (sortDir.equalsIgnoreCase("desc")){
            sort = sort.descending();
        }
        Pageable pageable = PageRequest.of(page, size, sort);
        return categoryRepository.findByIsDeletedFalse(pageable);
    }

    public Optional<TblCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }
    public TblCategory getCategoryByTitle(String categoryTitle) {
        return categoryRepository.findByTitle(categoryTitle);
    }

    public  TblCategory saveCategory(TblCategory category) {
        if (category.getId() == null) {
            category.setCreatedAt(Instant.now().toEpochMilli());
            category.setIsDeleted(false);

        }else {
            Optional<TblCategory> existingOptional = categoryRepository.findById(category.getId());
            if (existingOptional.isPresent()) {
                TblCategory existingCat = existingOptional.get();
                existingCat.setTitle(category.getTitle());
                return  categoryRepository.save(existingCat);
            }else {
                throw new RuntimeException("Category not found for update with ID:" + category.getId());
            }
        }
        return categoryRepository.save(category);
    }

    public TblCategory updateCategory(Long id, TblCategory category){
        Optional<TblCategory> existingCategoryOptional = categoryRepository.findById(category.getId());
        if (existingCategoryOptional.isEmpty()){
            throw new IllegalArgumentException("category with ID " + category.getId() + "not found.");
        }
        TblCategory existingCategory = existingCategoryOptional.get();
        existingCategory.setTitle(category.getTitle());
        existingCategory.setUpdatedAt(Instant.now().toEpochMilli());
       categoryRepository.save(existingCategory);

        return existingCategory;
    }

    public void deleteCategory(Long id) {
        Optional<TblCategory> categoryToDelete = categoryRepository.findById(id);
        if (categoryToDelete.isPresent()) {
            TblCategory category = categoryToDelete.get();
            category.setIsDeleted(true);
            categoryRepository.save(category);
        }else {
            throw new IllegalArgumentException("category with ID " + id + "not found");
        }
    }

}
