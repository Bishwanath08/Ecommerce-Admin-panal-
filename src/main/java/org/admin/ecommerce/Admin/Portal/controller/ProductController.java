package org.admin.ecommerce.Admin.Portal.controller;

import jakarta.validation.Valid;
import org.admin.ecommerce.Admin.Portal.model.TblProducts;
import org.admin.ecommerce.Admin.Portal.model.TblSubCategory;
import org.admin.ecommerce.Admin.Portal.service.ProductService;
import org.admin.ecommerce.Admin.Portal.service.SubCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

@Controller
@RequestMapping("/product")
public class ProductController extends BaseController {

    private static final String UPLOAD_DIRECTORY = "static/uploads";
    private final List<String> uploadedImageNames = new ArrayList<>();


    @Autowired
    private ProductService productService;

    @Autowired
    private SubCategoryService subCategoryService;

    @GetMapping("/list")
    public String getProduct(@RequestParam(defaultValue = "0") int page,
                             @RequestParam(defaultValue = "50") int size,
                             Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            Page<TblProducts> productsList = productService.getProductListData(page, size);

            getBasicDetails(model, jwtToken);
            model.addAttribute("newSubCategory", new TblProducts());
            model.addAttribute("productsList", productsList);
            model.addAttribute("currentPage", page);
            model.addAttribute("pageSize", size);

            return "products/products_list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching Products list: " + e.getMessage());
            return "error_page";
        }
    }

    @GetMapping("/add")

    public String getAddProductPage(Model model, @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            getBasicDetails(model, jwtToken);
            model.addAttribute("newSubCategory", new TblSubCategory());
            model.addAttribute("newProduct", new TblProducts());
            model.addAttribute("uploads", uploadedImageNames);
            List<TblSubCategory> subCategoryList = productService.getAllSubCategory();
            model.addAttribute("subCategoryList", subCategoryList);

            return "products/product_add";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error fetching Products: " + e.getMessage());
            return "error_page";

        }
    }


    @PostMapping("/add")
    public String saveProduct(@Valid @ModelAttribute("newProduct") TblProducts newProduct,
                              BindingResult result,
                              @RequestParam(value = "subCategoryId", required = false) Long subCategoryId,
                              @RequestParam("image") MultipartFile image,
                              RedirectAttributes redirectAttributes, Model model,
                              @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {

        try {

            getBasicDetails(model, jwtToken);
            addCommonAttributesForProductForm(model, newProduct, productService);


            if (image != null && !image.isEmpty()) {
                String contentType = image.getContentType();
                Set<String> allowedContentTypes = new HashSet<>(Arrays.asList(MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE));

                if (!allowedContentTypes.contains(contentType)) {
                    model.addAttribute("error", "Invalid file type. Only JPEG and PNG images are allowed.");
                    return "products/product_add";
                }

                try {
                    String originalFileName = image.getOriginalFilename();
                    String fileName = UUID.randomUUID() + "_" + originalFileName;
                    File filePath = new File(Paths.get(UPLOAD_DIRECTORY, fileName).toString());

                    if (!Files.exists(Paths.get(UPLOAD_DIRECTORY))) {
                        Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
                    }


                    image.transferTo(filePath.toPath());
                    uploadedImageNames.add(fileName);

                    newProduct.setImage(fileName);

                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("error ", "Error uploading image: " + e.getMessage());
                    return "products/product_add";
                }
            } else {
                model.addAttribute("error", "Product  image is required.");
                return "products/product_add";
            }
            if (subCategoryId == null || subCategoryId == 0) {
                model.addAttribute("error", "Sub-Category is required.");
                return "products/product_add";
            }
            Optional<TblProducts> existingProduct = productService.getProductByProductName(newProduct.getProductName());
            if (existingProduct.isPresent()) {
                model.addAttribute("error", "A Product with the same name already exists.");
                return "products/product_add";
            }
            TblProducts saveProduct = productService.saveProducts(newProduct, subCategoryId);
            if (saveProduct != null && saveProduct.getId() != null && saveProduct.getId() > 0) {
                redirectAttributes.addFlashAttribute("message", "Products successfully created.");
                return "redirect:/product/list";

            } else {
                model.addAttribute("error", "Something went wrong. Products not created.");
                return "products/product_add";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "An Unexpected error occurred: " + e.getMessage());
            model.addAttribute("newProduct", newProduct);
        }
        return "products/product_add";
    }

    @GetMapping("/update/{id}")
    public String showUpdateProductFrom(@PathVariable("id") Long id, Model model,
                                        @CookieValue(value = "jwtToken" , defaultValue = "Guest") String jwtToken){
        try {
            getBasicDetails(model, jwtToken);

            Optional<TblProducts> products = productService.getProductById(id);
            if (products.isEmpty()) {
                model.addAttribute("error", "Product not found! ");
                return "redirect:/product/list";
            }
            TblProducts tblProducts = products.get();
            model.addAttribute("newProduct", tblProducts);
            model.addAttribute("subCategoryList", productService.getAllSubCategory());
            return "products/product_update";
        } catch (Exception e) {
            model.addAttribute("error" ,  "Error loading products for update: " + e.getMessage());
            return "redirect:/product/list";
        }
    }

    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable("id") Long id,
                                @Valid @ModelAttribute("newProduct") TblProducts updatedProductData,
                                BindingResult result,
                                @RequestParam(value = "subCategoryId", required = false) Long newSubCategoryId,
                                @RequestParam("image") MultipartFile image,
                                RedirectAttributes redirectAttributes, Model model,
                                @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {

        try {
            getBasicDetails(model, jwtToken);
            addCommonAttributesForProductForm(model, updatedProductData, productService);


            if (image != null && !image.isEmpty()) {
                String contentType = image.getContentType();
                Set<String> allowedContentTypes = new HashSet<>(Arrays.asList(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE));
                if (!allowedContentTypes.contains(contentType)) {
                    model.addAttribute("error", "Invalid file type. Only JPEG and PNG images are allowed.");
                    return "products/product_update";
                }
                try {
                    String originalFileName = image.getOriginalFilename();
                    String newUploadedFileName = UUID.randomUUID() +  "_" + originalFileName;
                    File filePath = new File(Paths.get(UPLOAD_DIRECTORY, newUploadedFileName).toString());

                    if (!Files.exists(Paths.get(UPLOAD_DIRECTORY))) {
                        Files.createDirectories(Paths.get(UPLOAD_DIRECTORY));
                    }

                    image.transferTo(filePath.toPath());
                    uploadedImageNames.add(newUploadedFileName);

                    updatedProductData.setImage(newUploadedFileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    model.addAttribute("error", "Error Uploading image: " + e.getMessage());
                    return "products/product_update";
                }
            }else {
                model.addAttribute("error", "Product image is required.");
                return "products/product_update";
            }
            if (newSubCategoryId == null || newSubCategoryId == 0) {
                model.addAttribute("error", "Sub-Category is required.");
                return "products/product_update";
            }

            TblProducts updatedProduct = productService.updateProduct(id, updatedProductData, newSubCategoryId);

            if (updatedProduct != null && updatedProduct.getId() != null && updatedProduct.getId() > 0) {
                redirectAttributes.addFlashAttribute("message", "Product successfully updated.");
                return "redirect:/product/list";
            } else {
                model.addAttribute("error", "Failed to update Product. It might not exist or the selected sub-category is invalid. ");
                return "products/product_update";
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error","An unexpected error occurred: "  + e.getMessage());
            model.addAttribute("newProduct", updatedProductData);
            return "products/product_update";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {

        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("message", "Product deleted successfully!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete product: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to delete product: " + e.getMessage());
        }
        return "redirect:/product/list";
    }
}
