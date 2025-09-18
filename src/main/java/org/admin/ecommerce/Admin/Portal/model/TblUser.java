package org.admin.ecommerce.Admin.Portal.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "tbl_User")
public class TblUser implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    @NotBlank(message = "firstName cannot be empty")
    private  String fullName;

    @Column(name = "username", unique = true, nullable = false)
    @NotBlank(message = "userName cannot be empty")
    private String username;

    @Column(name="email", unique = true, nullable = false)
    @NotEmpty(message = "Email cannot be empty")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Invalid email address format.")
    String email;


    @Column(name="password")
    String password;

    @Transient
    String confirmPassword;

    @Column(name="user_role")
    private String role;

    @Column(name="mobile", unique = true, nullable = false)
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10}$", message = "Please provide valid 10 digits mobile number.")
    String mobile;

    @Column(name="is_deleted")
    Boolean isDeleted;

    @Column(name="login_otp")
    String otp;

    @Column(name="login_otp_generatedAt")
    LocalDateTime otpGeneratedAt;

    private boolean isVerified;
    private String webSecurityToken;
    private String address;
    private String city;
    private String zipCode;


    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }


    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.role == null) {
            return List.of();
        }
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role));
    }

    public boolean isOtpValid() {
        return this.otp != null && this.otpGeneratedAt != null &&
                LocalDateTime.now().isBefore(this.otpGeneratedAt.plusMinutes(2));
    }

    @Override
    public String getUsername() {
        return mobile;
    }
}
