package org.admin.ecommerce.Admin.Portal.service;

import org.admin.ecommerce.Admin.Portal.model.TblProducts;
import org.admin.ecommerce.Admin.Portal.model.TblSubCategory;
import org.admin.ecommerce.Admin.Portal.repository.ProductRepository;
import org.admin.ecommerce.Admin.Portal.repository.SubCategoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SubCategoryRepository subCategoryRepository;

    public Page<TblProducts> getProductListData(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findByIsDeletedFalse(pageable);
    }

    public List<TblSubCategory> getAllSubCategory() {
        return subCategoryRepository.getAllRecordNonDeleted();
    }

    public Optional<TblProducts> getProductByProductName(String productName) {
        return productRepository.findByProductNameAndIsDeletedFalse(productName);
    }

    public TblProducts saveProducts(TblProducts products, Long subCategoryId){
        try {
            Optional<TblSubCategory> subCategory = subCategoryRepository.findById(subCategoryId);
            if (subCategory.isEmpty()) {
                logger.error("Product with ID " + subCategoryId + "Not found.");
                return null;
            }
            TblSubCategory tblSubCategory = subCategory.get();
            products.setSubCategoryId(tblSubCategory);
            products.setProductName(products.getProductName());
            products.setDescription(products.getDescription());
            products.setPrice(products.getPrice());
            products.setDiscount(products.getDiscount());
            products.setQuantity(products.getQuantity());
            products.setImage(products.getImage());
            products.setCreatedAt(Instant.now().toEpochMilli());
            products.setIsDeleted(false);

            return productRepository.save(products);
        } catch (Exception e) {
            logger.error("Error saving products: " , e);
            return null;
        }
    }

    public TblProducts updateProduct(Long productId, TblProducts updatedProductData, Long newSubCategoryId ){
        try {
            Optional<TblProducts> existingProducts = productRepository.findById(productId);
            if (existingProducts.isEmpty()) {
                logger.warn("Product with ID " + productId + " not found for Update.");
                return null;
            }
            TblProducts products = existingProducts.get();

            TblSubCategory subCategoryToSet = null;
            if (newSubCategoryId == null) {
                Optional<TblSubCategory> subCategory = subCategoryRepository.findById(newSubCategoryId);
                if (subCategory.isEmpty()) {
                    logger.warn("New Sub-Category with ID " + newSubCategoryId + "not found. Product will retain old Sub-Category.");
                } else {
                    subCategoryToSet = subCategory.get();
                }
            }

            if (updatedProductData.getProductName() != null && !updatedProductData.getProductName().trim().isEmpty()) {
                products.setProductName(updatedProductData.getProductName());
            }

            if (subCategoryToSet != null) {
                products.setSubCategoryId(subCategoryToSet);
            }

            products.setProductName(updatedProductData.getProductName());
            products.setDescription(updatedProductData.getDescription());
            products.setPrice(updatedProductData.getPrice());
            products.setDiscount(updatedProductData.getDiscount());
            products.setImage(updatedProductData.getImage());
            products.setQuantity(updatedProductData.getQuantity());
            products.setUpdatedAt(Instant.now().toEpochMilli());

            return productRepository.save(products);
        }catch (IllegalArgumentException e){
            throw e;
        } catch (Exception e) {
            logger.error("Error updating products with ID" + productId, e);
            return null;
        }
    }

    public Optional<TblProducts> getProductById(Long id){
        return productRepository.findById(id);
    }

    public void deleteProduct(Long id){
        Optional<TblProducts> productsOptional = productRepository.findById(id);
        if (productsOptional.isPresent()) {
            TblProducts products = productsOptional.get();
            products.setIsDeleted(true);
            productRepository.save(products);
        }else {
            throw new IllegalArgumentException("Product  not found id: " + id);
        }
    }
}
