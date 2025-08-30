package org.admin.ecommerce.Admin.Portal.controller;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.admin.ecommerce.Admin.Portal.config.MD5PasswordEncoder;
import org.admin.ecommerce.Admin.Portal.dto.LoginRequest;
import org.admin.ecommerce.Admin.Portal.dto.OTPVerificationRequest;
import org.admin.ecommerce.Admin.Portal.jwt.JwtTokenUtil;
import org.admin.ecommerce.Admin.Portal.model.TblAdmin;
import org.admin.ecommerce.Admin.Portal.service.AdminAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.SignatureException;


@Controller
@RequiredArgsConstructor
public class AdminAuthController  extends  BaseController{

    @Autowired
    private AdminAuthService adminAuthService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private MD5PasswordEncoder passwordEncoder;


    @GetMapping("/")
    public String showLogin(Model model){
        model.addAttribute("loginRequest", new LoginRequest());
        return "login";
    }

    @PostMapping("/login")
    public String ProcessLogin(@Valid @ModelAttribute("loginRequest") LoginRequest loginRequest, BindingResult result, Model model, HttpServletResponse response){

        if (result.hasErrors()) {
            return "login";
        }

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();
        String otp = adminAuthService.login(email, password);

        if (otp != null) {
            model.addAttribute("email", email);
            model.addAttribute("generatedOtp", otp);
            model.addAttribute("otpVerificationRequest", new OTPVerificationRequest());
            return "verifyOtp";
        }else {
            model.addAttribute("error", "Invalid email or password.");
            return "login";
        }
    }

    @PostMapping("/verifyOtp")
    public String verifyOtp(HttpServletRequest request,
                            @ModelAttribute("otpVerificationRequest") OTPVerificationRequest otpVerificationRequest,
                            @RequestParam("email") String email,
                            Model model,
                            HttpServletResponse response) {

        String jwt = adminAuthService.verifyOtp(email, otpVerificationRequest.getOtp());

        if (jwt != null) {
            Cookie jwtCookie = new Cookie("jwtToken", jwt);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            response.addCookie(jwtCookie);

            request.getSession().setAttribute("Authorization", "Bearer" + jwt);
            response.setHeader("Authorization", "Bearer " + jwt);

            return "redirect:/dashboard";
        }else {
            model.addAttribute("email", email);
            model.addAttribute("otpVerificationRequest", new OTPVerificationRequest());
            model.addAttribute("error", "Invalid OTP");
            return "verifyOtp";
        }
    }



    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model,
                            @CookieValue(value = "jwtToken", defaultValue = "Guest") String jwtToken) {
        try {
            String email = jwtTokenUtil.extractEmail(jwtToken);
            TblAdmin admin = adminAuthService.getUsersDetails(email);

            model.addAttribute("name", admin.getName());
            model.addAttribute("email", admin.getEmail());
            model.addAttribute("role", admin.getRole());
            return "dashboard";
        } catch (Exception e) {
            // Log the full stack trace to your server console
            System.err.println("Error accessing dashboard: " + e.getMessage());
            e.printStackTrace();

            model.addAttribute("error", "An error occurred: " + e.getMessage());
            return "error_page";
        }
    }

}
