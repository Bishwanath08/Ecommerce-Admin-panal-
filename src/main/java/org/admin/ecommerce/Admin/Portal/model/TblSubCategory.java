package org.admin.ecommerce.Admin.Portal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
@Table(name = "tbl_subCategory")
public class TblSubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "subcategoryTitle", unique = true, nullable = false)
    @NotBlank(message = "subCategory cannot be empty")
    private String subCategoryTitle;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private TblCategory categoryId;

    @Column(name = "is_Deleted")
    private Boolean isDeleted;

    @Column(name = "created_at")
    private Long createdAt;

    @Column(name = "updated_at")
    private Long updatedAt;


}
