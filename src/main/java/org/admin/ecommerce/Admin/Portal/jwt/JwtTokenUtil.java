package org.admin.ecommerce.Admin.Portal.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Jwts;


@Component
public class JwtTokenUtil {

//    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;
//
//
//    public String generateToken(String email, String role) {
//        return Jwts.builder()
//                .setSubject(email)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new  Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .compact();
//    }
//
//    public String extractEmail(String token) {
//        return getClaims(token).getSubject();
//    }
//
//    public boolean isTokenValid(String token, UserDetails userDetails) {
//        try {
//            getClaims(token);
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    private Claims getClaims(String token) {
//        return Jwts.parserBuilder()
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//    }


    @Value("${application.security.jwt.secret-key}")
    private String secretKey;

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, userDetails, jwtExpiration);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                // --- THIS IS THE CRUCIAL PART ---
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // <--- ENSURE THIS LINE IS PRESENT AND CORRECT
                .compact();
    }

    // --- Your existing methods ---
    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // <-- This calls extractAllClaims
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // This is where the parsing happens.
        // It expects a signed token because you've set a signing key.
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // <--- This key is used to verify the signature
                .build()
                .parseClaimsJws(token) // <--- parseClaimsJws explicitly expects a JWS (JSON Web Signature), i.e., a signed token
                .getBody();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String email = extractEmail(token);
        return (email.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
