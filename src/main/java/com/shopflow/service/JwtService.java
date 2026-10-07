package com.shopflow.service;

import com.shopflow.config.AppProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {

    private final AppProperties appProperties;

    public JwtService(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String generateToken(String email){
        SecretKey jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(appProperties.getJwtSecret()));
        return Jwts.builder()
                .subject(email).signWith(jwtSecret)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + appProperties.getJwtExpirationMs()))
                .compact();
    }

    public String extractEmail(String token){
        SecretKey jwtSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(appProperties.getJwtSecret()));
        return Jwts.parser().verifyWith(jwtSecret).build().parseSignedClaims(token).getPayload().getSubject();
    }

    public Boolean isTokenValid(String token){
        try{
            extractEmail(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
}
