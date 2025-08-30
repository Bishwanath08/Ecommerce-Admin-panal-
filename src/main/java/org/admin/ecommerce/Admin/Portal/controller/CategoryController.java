package org.admin.ecommerce.Admin.Portal.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.admin.ecommerce.Admin.Portal.jwt.JwtTokenUtil;
import org.admin.ecommerce.Admin.Portal.model.TblAdmin;
import org.admin.ecommerce.Admin.Portal.model.TblCategory;
import org.admin.ecommerce.Admin.Portal.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/category")
public class CategoryController extends  BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @GetMapping("/list")
    public String listCategory(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "25") int size,
                               @RequestParam(defaultValue = "title") String sortBy,
                               @RequestParam(defaultValue = "desc") String sortDir,
                               Model model, @CookieValue(value = "jwtToken", required = false) String jwtToken) {
        try {
            Page<TblCategory> categoryListPage = categoryService.getCategoryListData(page, size, sortBy, sortDir );
            getBasicDetails(model, jwtToken);
            model.addAttribute("categoryList" , categoryListPage );
            model.addAttribute("newCategory", new TblCategory());
            model.addAttribute("title", "Category List");
            return "category's/category_list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching category: " + e.getMessage());
            return "error_page";
        }
    }

    
    @GetMapping("/add") 
    public String getAddCategoryPage(Model model, @ModelAttribute("newCategory") TblCategory newCategory,
                                     @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken){
        try {
            getBasicDetails(model, jwtToken);
            model.addAttribute("newCategory", newCategory);
            return "category's/category_add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching permissions: " + e.getMessage());
            return "error_page";
        }
    }
    
    @PostMapping("/add")
    public String saveCategory(HttpServletRequest request,@ModelAttribute("newCategory") TblCategory newCategory,
                               Model model, RedirectAttributes redirectAttributes,
                               @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {

        try {

            getBasicDetails(model, jwtToken);


            TblCategory existingCategory = categoryService.getCategoryByTitle(newCategory.getTitle());
            if (existingCategory != null) {
                model.addAttribute("error", "A category with the same title already exists.");
                model.addAttribute("newCategory", newCategory);
                return "category's/category_add";
            }
            TblCategory saveCategory = categoryService.saveCategory(newCategory );
            if (saveCategory != null && saveCategory.getId() != null && saveCategory.getId() > 0) {
                redirectAttributes.addFlashAttribute("message", "Category successfully created.");
                return "redirect:/category/list";
            } else {
                model.addAttribute("error", "Something went wrong. Category not created.");
                return "category's/category_add";
            }
        }catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("newCategory", newCategory);
        }
        return "category's/category_add";
    }


    @GetMapping("/update/{id}")
    public String getEditCategoryPage(@PathVariable Long id, Model model,
                                      @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            getBasicDetails(model, jwtToken);
            Optional<TblCategory> categoryOptional = categoryService.getCategoryById(id);
            if (categoryOptional.isEmpty()) {
                model.addAttribute("error" ," Category not found. ");
                return "category's/category_list";
            }
            TblCategory category = categoryOptional.get();
            model.addAttribute("newCategory", category);

            return "category's/category_update";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching Category's :" + e.getMessage());
            return "category's/category_list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateCategory(@PathVariable("id") Long id,
                                TblCategory updatedCategory,@ModelAttribute("newCategory") TblCategory newCategory,
                                 Model model, RedirectAttributes redirectAttributes,
                                 @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            getBasicDetails(model, jwtToken);

            Optional<TblCategory> existingCategoryOptional = categoryService.getCategoryById(id);
            if (existingCategoryOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Category with ID " + id + " not found.");
                return "redirect:/category/list";
            }
            TblCategory categoryToUpdate = existingCategoryOptional.get();

            TblCategory categoryWithSameTitle = categoryService.getCategoryByTitle(updatedCategory.getTitle());
            if (categoryWithSameTitle != null && !categoryWithSameTitle.getId().equals(id)) {
                model.addAttribute("error", "A category with the same title already exists.");
                model.addAttribute("newCategory", updatedCategory);
                return "category's/category_update";
            }
            TblCategory savedCategory = categoryService.updateCategory(id, categoryToUpdate);
            categoryService.updateCategory(id, newCategory);
            if (savedCategory != null && savedCategory.getId() != null) {
                redirectAttributes.addFlashAttribute("message", "Category '" + savedCategory.getTitle() + "' updated successfully.");
                return "redirect:/category/list";
            } else {
                model.addAttribute("error" , "Failed to update category. Please try again.");
                model.addAttribute("newCategory", updatedCategory);
                return "category's/category_update";
            }
        }catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("newCategory", updatedCategory);
            return "category's/category_update";
        }
    }


        @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id, Model model,
                                 @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            getBasicDetails(model, jwtToken);
            categoryService.deleteCategory(id);
            model.addAttribute("message", "Category successfully deleted.");
            Page<TblCategory> categoriesList = categoryService.getCategoryListData(0, 10, "title", "asc");
            model.addAttribute("categoryList", categoriesList);
            model.addAttribute("title", "Category List");
            model.addAttribute("newCategory", new TblCategory());
            return "category's/category_list";
        } catch (Exception e) {
            model.addAttribute("error", "Error deleting  category: " + e.getMessage());
            return "category's/category_list";
        }
    }
}
