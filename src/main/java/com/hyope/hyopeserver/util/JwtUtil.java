package com.hyope.hyopeserver.util;

import com.hyope.hyopeserver.entity.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.Keys;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey(){ return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));}

    public String generateToken(String loginId, Role role){
        return Jwts.builder()
                .subject(loginId)
                .claim("role", role.name())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    public String extractLoginId(String token){
        if(!validateToken(token)){
            throw new IllegalArgumentException("Invalid or expired JWT token");
        }
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e){
            log.warn("JWT expired: {}", e.getMessage());
        } catch (MalformedJwtException e){
            log.warn("JWT malformed: {}", e.getMessage());
        } catch (SignatureException e){
            log.warn("JWT signature invalid: {}", e.getMessage());
        } catch (UnsupportedJwtException e){
            log.warn("JWT unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e){
            log.warn("JWT claims empty: {}", e.getMessage());
        } catch (JwtException e){
            log.warn("JWT invalid: {}", e.getMessage());
        }

        return false;
    }
}
