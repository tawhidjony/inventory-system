package com.inventory.system.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtils {
    private final String SECRET_STRING = "577565f151a44f1534b17bee22e6816a34c3decce31eace521f8dc60ca573aad";

    private SecretKey getSignKey(){
        return Keys.hmacShaKeyFor(SECRET_STRING.getBytes());
    }

    // ১. ইউজারের ইনফরমেশন থেকে টোকেন জেনারেট করা
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // ১০ ঘণ্টা মেয়াদ
                .signWith(getSignKey()) // JJWT 0.12+ এ অ্যালগরিদম অটো ডিটেক্ট করে
                .compact();
    }

    // ২. টোকেন থেকে ইউজারনেম বের করা
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // ৩. টোকেন থেকে যেকোনো ক্লেন (Claim) বের করা
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // ৫. টোকেন এক্সপায়ার্ড হয়ে গেছে কিনা চেক করা
    public boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    // ৬. টোকেন ভ্যালিড কিনা চেক করা
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
