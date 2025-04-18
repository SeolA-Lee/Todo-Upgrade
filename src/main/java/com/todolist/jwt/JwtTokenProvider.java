package com.todolist.jwt;

import com.todolist.exception.UnauthorizedException;
import com.todolist.jwt.dto.JwtToken;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final long JWT_TOKEN_VALID = 1000 * 60 * 60; // 1시간

    private final Key key;

    /**
     * application-secret.yml에서 secret 값을 가져와서 key에 저장
     * @param secretKey
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    /**
     * Member 정보를 가지고 AccessToken, RefreshToken을 생성
     */
    public JwtToken createToken(Authentication authentication) {
        // 권한 가져오기
        String auth = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", auth)
                .setExpiration(new Date(now + JWT_TOKEN_VALID))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder()
                .setExpiration(new Date(now + JWT_TOKEN_VALID))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new JwtToken(accessToken, refreshToken);
    }

    /**
     * JWT 토믄을 복호화하여 토큰에 들어 있는 정보를 꺼내는 메서드
     */
    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token); // JWT 토큰 복호화

        if (claims.get("auth") == null) {
            throw new UnauthorizedException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("Invalid JWT Token: {}", e.getMessage());
        }
        return false;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn(e.getMessage());
            throw new UnauthorizedException();
        }
    }
}
