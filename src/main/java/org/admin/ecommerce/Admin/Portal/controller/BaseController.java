package org.admin.ecommerce.Admin.Portal.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.admin.ecommerce.Admin.Portal.jwt.JwtTokenUtil;
import org.admin.ecommerce.Admin.Portal.model.TblAdmin;
import org.admin.ecommerce.Admin.Portal.model.TblCategory;
import org.admin.ecommerce.Admin.Portal.model.TblProducts;
import org.admin.ecommerce.Admin.Portal.model.TblSubCategory;
import org.admin.ecommerce.Admin.Portal.service.AdminAuthService;
import org.admin.ecommerce.Admin.Portal.service.CategoryService;
import org.admin.ecommerce.Admin.Portal.service.ProductService;
import org.admin.ecommerce.Admin.Portal.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class BaseController {

//    private static final String UPLOAD_DIRECTORY = "static/uploads";

    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private AdminAuthService adminAuthService;

    public void getBasicDetails(Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        String email = jwtTokenUtil.extractEmail(jwtToken);
        TblAdmin admin = adminAuthService.getUsersDetails(email);
        model.addAttribute("name", admin.getName());
        model.addAttribute("email", admin.getEmail());
        model.addAttribute("role", admin.getRole());

    }

    public TblAdmin getLoggedAdminViaRequest(HttpServletRequest request, Model model, @CookieValue(value = "jwtToken",
            defaultValue = "Guest") String jwtToken) {
        String email = jwtTokenUtil.extractEmail(jwtToken);
        TblAdmin admin = adminAuthService.getUsersDetails(email);
        return admin;
    }


    void addCommonAttributes(Model model, @Valid TblSubCategory newSubCategory, SubCategoryService subCategoryService, CategoryService categoryService) {
        model.addAttribute("newSubCategory", newSubCategory);
        List<TblCategory> categoryList = subCategoryService.getAllCategory();
        model.addAttribute("categoryList", categoryList);

    }

     void addCommonAttributesForProductForm(Model model, TblProducts newProduct, ProductService productService) {
        model.addAttribute("newProduct", newProduct);
        List<TblSubCategory> subCategoryList = productService.getAllSubCategory();
        model.addAttribute("subCategoryList", subCategoryList);

    }

//    protected void deleteFileIfNecessary(String fileName){
//        if (fileName != null && !fileName.isEmpty()) {
//            try {
//                Files.deleteIfExists(Paths.get(UPLOAD_DIRECTORY, fileName));
//            } catch (Exception fileEx) {
//                System.out.println("Failed to delete file: " + fileName + " _ " + fileEx.getMessage());
//            }
//        }
//    }
}
