package org.admin.ecommerce.Admin.Portal.service;

import lombok.RequiredArgsConstructor;
import org.admin.ecommerce.Admin.Portal.jwt.JwtTokenUtil;
import org.admin.ecommerce.Admin.Portal.model.TblAdmin;
import org.admin.ecommerce.Admin.Portal.repository.AdminAuthRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.admin.ecommerce.Admin.Portal.config.MD5PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminAuthRepository adminAuthRepository;
    private final MD5PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;


    public String generateOTP(int length, TblAdmin admin) {
        String digits = "0123456789";
        StringBuilder otp = new StringBuilder(length);
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            otp.append(digits.charAt(random.nextInt(digits.length())));
        }

        String generateOtp = "1111";
        admin.setLoginOtp(generateOtp);
        admin.setOtpGeneratedAt(String.valueOf(LocalDateTime.now()));
        admin.setVerified(false);

        return "1111";
    }


//    public String login(String email, String password) {
//
//        try {
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//            if (passwordEncoder.matches(password.replaceAll("\\s", ""), userDetails.getPassword())) {
//
//                TblAdmin admin = adminAuthRepository.findByEmail(email);
//
//                if (admin != null) {
//                    String otp = generateOTP(4, admin);
//                    adminAuthRepository.save(admin);
//                    return otp;
//                } else {
//                    System.err.println("User details found but TblAdmin not found for email: " + email);
//                    return null;
//                }
//            } else {
//                return null;
//            }
//
//        } catch (BadCredentialsException e) {
//            System.err.println("Login failed for " + email + ": Invalid credentials.");
//            return null;
//        } catch (Exception e) {
//            System.err.println("Login failed for " + email + ": An unexpected error occurred: " + e.getMessage());
//            e.printStackTrace();
//            return null;
//        }
//    }

    public String login(String email, String password) {
        TblAdmin admin = adminAuthRepository.findByEmail(email);
        if (admin != null) {

            if (passwordEncoder.matches(password.replaceAll("\\s", ""), admin.getPassword())) {
                String otp = generateOTP(4, admin);
                adminAuthRepository.save(admin);
                return otp;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }


    public String verifyOtp(String email, String otp) {

        System.out.println("VerifyOtp called with email: " + email + ", otp: " + otp);
        TblAdmin admin = adminAuthRepository.findByEmail(email);

        if (admin != null) {
            System.out.println("TblAdmin found: " + admin.getEmail());
            String cleanedOtp = otp.trim();

            if (admin.getLoginOtp() != null && admin.getLoginOtp().equals(cleanedOtp)) {
                System.out.println("OTP is valid");

                try {
                    UserDetails userDetails = loadAdminByEmail(email);

                    System.out.println("Generating JWT token for Admin: " + email);
                    String jwt = jwtTokenUtil.generateToken(userDetails);
                    System.out.println("JWT token generated: " + jwt);
                    admin.setWebSecurityToken(jwt);


                    System.out.println("OTP cleared for admin: " + email);
                    admin.setLoginOtp(null);
                    adminAuthRepository.save(admin);
                    System.out.println("OTP cleared for user: " + email);

                    return jwt;
                } catch (Exception e) {
                    System.err.println("Exception during JWT generation: " + e.getMessage());
                    e.printStackTrace();
                    return null;
                }
            } else {
                System.out.println("OTP is invalid");
            }
        } else {
            System.out.println("TblAdmin not found for email: " + email);
        }

        return null;
    }


    public UserDetails loadAdminByEmail(String email) {
        TblAdmin admin = adminAuthRepository.findByEmail(email);
        if (admin != null) {

            return new User(admin.getEmail(), admin.getPassword(), new ArrayList<>());

        }
        return null;
    }

    public TblAdmin getUsersDetails(String email) {
        if (email == null) {

            return null;
        }
        return adminAuthRepository.findByEmail(email);
    }
}