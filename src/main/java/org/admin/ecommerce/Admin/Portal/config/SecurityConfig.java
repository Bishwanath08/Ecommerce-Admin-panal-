package org.admin.ecommerce.Admin.Portal.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.admin.ecommerce.Admin.Portal.model.TblAdmin;
import org.admin.ecommerce.Admin.Portal.repository.AdminAuthRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final MD5PasswordEncoder md5PasswordEncoder;
    private final AdminAuthRepository adminAuthRepository;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws  Exception {
        httpSecurity
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/verifyOtp", "/dashboard", "/css/**", "/js/**", "/images/**", "/templates/**", "/static/**" , "/uploads/**").permitAll()
                        .requestMatchers("/category/**", "/subCategory/**", "/product/**").permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler())
                )

                .authenticationProvider(authenticationProvider(userDetailsService()))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return md5PasswordEncoder;
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, authException) -> {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + authException.getMessage());
        };
    }


    @Bean
    public AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            TblAdmin admin = adminAuthRepository.findByEmail(email);

            if (admin == null) {
                throw new UsernameNotFoundException("User not found with email: " + email);
            }

            return new User(
                    admin.getEmail(),
                    admin.getPassword(),
                    admin.getAuthorities()
            );
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
