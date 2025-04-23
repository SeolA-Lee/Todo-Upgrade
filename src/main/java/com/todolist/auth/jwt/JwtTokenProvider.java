package com.todolist.auth.jwt;

import com.todolist.auth.user.CustomUserService;
import com.todolist.entity.Member;
import com.todolist.exception.UnauthorizedException;
import com.todolist.auth.jwt.dto.JwtToken;
import com.todolist.auth.user.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final long JWT_TOKEN_VALID = 1000 * 60 * 60; // 1시간

    private final Key key;
    private final CustomUserService customUserService;

    /**
     * application-secret.yml에서 secret 값을 가져와서 key에 저장
     * @param secretKey
     */
    public JwtTokenProvider(@Value("${jwt.secret}") String secretKey, CustomUserService customUserService) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.customUserService = customUserService;
    }

    /**
     * Member 정보를 가지고 AccessToken, RefreshToken을 생성
     */
    public JwtToken createToken(Object payload) {

        Member member = (Member) payload;

        long now = new Date().getTime();

        // Access Token 생성
        String accessToken = Jwts.builder()
                .claim("id", member.getId())
                .claim("email", member.getEmail())
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
    public UsernamePasswordAuthenticationToken getAuthentication(String token) {
        Claims claims = parseClaims(token); // JWT 토큰 복호화
        Long memberId = claims.get("id", Long.class);
        CustomUserDetails userDetails = customUserService.loadUserByUserId(memberId);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
                    .setSigningKey(key.getEncoded())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.warn(e.getMessage());
            throw new UnauthorizedException();
        }
    }
}
