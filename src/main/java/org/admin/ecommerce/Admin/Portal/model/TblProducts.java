package org.admin.ecommerce.Admin.Portal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "tbl_products")
public class TblProducts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "subCategory_id")
    private TblSubCategory subCategoryId;


    @NotBlank(message = "Product name cannot be blank")
    @Size(min = 3, max = 255, message = "Product name must be between 3 to 255 characters")
    @Column(name = "product_name")
    private String productName;

    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0.01")
    @Digits(integer = 10, fraction = 2, message = "Invalid price format")
    @Column(name = "price")
    private Double price;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

    @NotNull(message = "Discount is required.")
    @Min(value = 0, message = "Discount cannot be negative")
    @Max(value = 100, message = "Discount cannot exceed 100%")
    private Double discount;

//    private Double discountPercentage;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;

    @Size(max = 255, message = "Image URL cannot exceed 255 characters")
    @NotNull(message = "Image cannot be null")
    private String image;

    @Transient
    public Double getDiscountPrice() {
        if (price == null || discount == null || discount < 0 || discount > 100) {
            return price;
        }
        return price * (1 - (discount / 100.0));
    }

    @Transient
    public Boolean isOnSale() {
        return discount != null && discount > 0 && discount<= 100;
    }

}
