package com.itcjx.socialplatform.util;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenUtil {

    // 密钥
    @Value("${jwt.secret}")
    private String secret;
    // 过期时间， 24 小时
    private long expiration = 86400000; // 毫秒

    // 从token中获取用户名
    public String extractUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 生成token
    public String generateToken(String username) {
        return Jwts.builder()
                //.setClaims(claims)
                .setSubject(username)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, Base64.getDecoder().decode(secret))
                .compact();
    }

    // 验证token是否有效
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(Base64.getDecoder().decode(secret)).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    //解析JWT令牌
    public String parseJWT(String jwt) {
        return Jwts.parser()
                .setSigningKey(Base64.getDecoder().decode(secret))
                .parseClaimsJws(jwt)
                .getBody()
                .getSubject();
    }
}
