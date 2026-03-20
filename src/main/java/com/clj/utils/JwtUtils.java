package com.clj.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import static com.clj.constants.JwtConstants.EXPIRE_TIME;
import static com.clj.constants.JwtConstants.TOKEN_SECRET;


@Component
public class JwtUtils {
    private static final Key KEY = Keys.hmacShaKeyFor(TOKEN_SECRET.getBytes(StandardCharsets.UTF_8));

    public static String createToken(Long userId,String role) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("role", role)  //  添加角色信息
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME))
                .signWith(KEY)
                .compact();
    }

    public static Long getUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(KEY).build()
                    .parseClaimsJws(token).getBody();
            return Long.valueOf(claims.getSubject());
        } catch (Exception e) {
            return null;
        }
    }

    public static String getClaim(String token, String key) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get(key, String.class);
        } catch (Exception e) {
            return null;
        }
    }


    public static Date getExpiration(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(KEY)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
        } catch (JwtException e) {
            return null;
        }
    }

}

