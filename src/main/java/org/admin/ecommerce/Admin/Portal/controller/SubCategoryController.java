package org.admin.ecommerce.Admin.Portal.controller;

import jakarta.validation.Valid;
import org.admin.ecommerce.Admin.Portal.model.TblCategory;
import org.admin.ecommerce.Admin.Portal.model.TblSubCategory;
import org.admin.ecommerce.Admin.Portal.service.CategoryService;
import org.admin.ecommerce.Admin.Portal.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subCategory")
public class SubCategoryController extends BaseController {

    @Autowired
    private SubCategoryService subCategoryService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    public String getSubCategory(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "50") int size,
                                 @RequestParam(defaultValue = "id") String sortBy,
                                 @RequestParam(defaultValue = "asc") String sortDir,
                                 Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            Page<TblSubCategory> subCategoryList = subCategoryService.getSubCategoryListData(page, size, sortBy, sortDir);

            getBasicDetails(model, jwtToken);
            model.addAttribute("newCategory", new TblSubCategory());
            model.addAttribute("subCategoryList", subCategoryList);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

            return "sub-category/sub-category_list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching Sub-Category list: " + e.getMessage());
            return "error_page";
        }
    }

    @GetMapping("/add")
    public String getAddSubCategoryPage(Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            getBasicDetails(model, jwtToken);
            model.addAttribute("newCategory", new TblCategory());
            model.addAttribute("newSubCategory", new TblSubCategory());
            List<TblCategory> categoryList = subCategoryService.getAllCategory();
            model.addAttribute("categoryList", categoryList);

            return "sub-category/sub-category_add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching sub-category: " + e.getMessage());
            return "error_page";
        }
    }

    @PostMapping("/add")
    public String saveSubCategory(@Valid @ModelAttribute("newSubCategory") TblSubCategory newSubCategory,
                                  BindingResult result, @RequestParam(value = "categoryId", required = false) Long categoryId,
                                  RedirectAttributes redirectAttributes, Model model,
                                  @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {


        try {
            getBasicDetails(model, jwtToken);

            if (result.hasErrors()) {
                model.addAttribute("error", "Please correct the errors below.");
                addCommonAttributes(model, newSubCategory, subCategoryService, categoryService);
                return "sub-category/sub-category_add";
            }
            if (newSubCategory.getSubCategoryTitle() == null || newSubCategory.getSubCategoryTitle().trim().isEmpty()) {
                model.addAttribute("error", "Sub Category Title is required.");
                addCommonAttributes(model, newSubCategory, subCategoryService, categoryService);
                return "sub-category/sub-category_add";
            }

            if (categoryId == null || categoryId == 0) {
                model.addAttribute("error", "Select Category is required.");
                addCommonAttributes(model, newSubCategory, subCategoryService, categoryService);
                return "sub-category/sub-category_add";
            }

            TblSubCategory existingSubCategory = subCategoryService.getSubCategoryByTitle(newSubCategory.getSubCategoryTitle());
            if (existingSubCategory != null) {
                model.addAttribute("error", "A SubCategory with the same title already exists.");
                addCommonAttributes(model, newSubCategory, subCategoryService, categoryService);
                return "sub-category/sub-category_add";
            }

            addCommonAttributes(model, newSubCategory, subCategoryService, categoryService);



            TblSubCategory saveSubCategory = subCategoryService.saveSubCategory(newSubCategory, categoryId);

            if (saveSubCategory != null && saveSubCategory.getId() != null && saveSubCategory.getId() > 0) {
                redirectAttributes.addFlashAttribute("message", "Sub-Category successfully created. ");
                return "redirect:/subCategory/list";

            }else {
                model.addAttribute("error", "Something went wrong. Sub-Category not created.");
                return "sub-category/sub-category_add";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("newSubCategory", newSubCategory);
        }
        return "sub-category/sub-category_add";
    }


    @GetMapping("/update/{id}")
    public String showUpdateSubCategoryForm(@PathVariable("id") Long id, Model model,
                                            @CookieValue(value = "jwtToken" , defaultValue = "Guest") String jwtToken){
        try {
            getBasicDetails(model, jwtToken);

            Optional<TblSubCategory> subCategory = subCategoryService.getSubCategoryById(id);
            if (subCategory.isEmpty()) {
                model.addAttribute("error", "Sub-Category not found");
                return "redirect:/subCategory/list";
            }

            TblSubCategory subCategory1 = subCategory.get();
            model.addAttribute("newSubCategory", subCategory1);
            model.addAttribute("categoryList", subCategoryService.getAllCategory());
            return "sub-category/sub-category_update";
        }catch (Exception e) {
            model.addAttribute("error", "Error loading sub-category for update: " + e.getMessage());
            return "redirect:/subCategory/list";
        }
    }


    @PostMapping("/update/{id}")
    public String updateSubCategory (@PathVariable("id") Long id,
                                     @Valid @ModelAttribute("newSubCategory") TblSubCategory updatedSubCategoryData,
                                     BindingResult result,
                                     @RequestParam(value = "categoryId", required = false) TblCategory newCategoryId,
                                     RedirectAttributes redirectAttributes, Model model,
                                     @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            getBasicDetails(model, jwtToken);

            if (result.hasErrors()) {
                model.addAttribute("error", "Please correct the error below.");
                model.addAttribute("categoryList", subCategoryService.getAllCategory());
                return "sub-category/sub-category_update";
            }

            if (newCategoryId == null) {
                model.addAttribute("error", "Category is required.");
                model.addAttribute("categoryList", subCategoryService.getAllCategory());
                return "sub-category/sub-category_update";
            }

            TblSubCategory existingSubCategoryWithSameTitle = subCategoryService.getSubCategoryByTitle(updatedSubCategoryData.getSubCategoryTitle());
            if (existingSubCategoryWithSameTitle != null && !existingSubCategoryWithSameTitle.getId().equals(id)) {
                model.addAttribute("error", "A SubCategory with the same title already exists.");
                model.addAttribute("categoryList", subCategoryService.getAllCategory());
                return "sub-category/sub-category_update";
            }

            TblSubCategory updateSubCategory = subCategoryService.updateSubCategory(id, updatedSubCategoryData,newCategoryId);

            if (updateSubCategory != null) {
                redirectAttributes.addFlashAttribute("message", "Sub-Category successfully updated.");
                return "redirect:/subCategory/list";
            } else {
                model.addAttribute("error", "Failed to update Sub-Category. It might not exist.");
                model.addAttribute("categoryList", subCategoryService.getAllCategory());
                return "sub-category/sub-category_update";
            }

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
            model.addAttribute("categoryList", subCategoryService.getAllCategory());
            return "sub-category/sub-category_update";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteSubCategory(@PathVariable Long id, Model model,
                                 @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            getBasicDetails(model, jwtToken);
            subCategoryService.deleteSubCategory(id);
            model.addAttribute("message", "Sub Category successfully deleted.");
            Page<TblSubCategory> subCategoriesList = subCategoryService.getSubCategoryListData(0, 10, "title", "asc");
            model.addAttribute("categoryList", subCategoriesList);
            model.addAttribute("title", "Category List");
            model.addAttribute("newCategory", new TblSubCategory());
            return "sub-category/sub-category_list";

        } catch (Exception e) {
            model.addAttribute("error", "Error deleting  subCategory: " + e.getMessage());
            return "sub-category/sub-category_list";
        }
    }
}
