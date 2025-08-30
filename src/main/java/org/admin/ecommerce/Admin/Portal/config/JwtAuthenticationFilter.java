package org.admin.ecommerce.Admin.Portal.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.admin.ecommerce.Admin.Portal.jwt.JwtTokenUtil;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private JwtTokenUtil jwtTokenUtil;
    private UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        if (request.getServletPath().startsWith("/login") || request.getServletPath().startsWith("/verifyOtp") || request.getServletPath().startsWith("/dashboard")){
//            filterChain.doFilter(request, response);
//        }
        final String authHeader = (String) request.getSession().getAttribute("Authorization");
        final String jwt;
        final String adminEmail;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//            response.sendRedirect("/");
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);

        try {
            adminEmail = jwtTokenUtil.extractEmail(jwt);


            if (adminEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(adminEmail);


                if (jwtTokenUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid or expired JWT token: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }


}
