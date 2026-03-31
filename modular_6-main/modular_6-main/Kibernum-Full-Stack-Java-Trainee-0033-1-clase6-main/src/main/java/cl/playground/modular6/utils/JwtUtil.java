package cl.playground.modular6.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {

    private final String SECRET = "123151747241874281289512321412321";

    public String generateToken(String email) {
        return Jwts.builder()
            .subject(email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Token válido por 10 horas
            .signWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
            .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(SECRET.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }

}
