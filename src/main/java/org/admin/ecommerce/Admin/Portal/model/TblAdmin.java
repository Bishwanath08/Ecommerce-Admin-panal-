package org.admin.ecommerce.Admin.Portal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "tbl_Admin")
public class TblAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, nullable = false)
    @NotBlank(message = "Name cannot be empty")
    private  String name;

    @Column(name="email", unique = true, nullable = false)
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address.")
    String email;


    @Column(name="password")
    String password;

    @Transient
    String confirmPassword;

    @Column(name="user_role")
    private String role;

    @Column(name="mobile", unique = true, nullable = false)
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Please provide valid mobile number.")
    String mobile;

    @Column(name="is_deleted")
    String isDeleted;

    @Column(name="login_otp")
    String loginOtp;

    @Column(name="login_otp_generatedAt")
    String OtpGeneratedAt;

    private boolean isVerified;

    @Transient
    private String webSecurityToken;

    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }
}
