package org.admin.ecommerce.Admin.Portal.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OTPVerificationRequest {
    private String email;
    private String otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
