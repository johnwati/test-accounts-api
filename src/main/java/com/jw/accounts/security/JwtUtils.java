package com.jw.accounts.security;

import com.jw.accounts.entities.RefreshTokenEntity;
import com.jw.accounts.entities.Roles;
import com.jw.accounts.entities.Users;
import com.jw.accounts.exceptions.TokenRefreshException;
import com.jw.accounts.repositories.RefreshTokenRepository;
import com.jw.accounts.repositories.UsersRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component
public class JwtUtils {
    @Autowired
    UsersRepository usersRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;
    @Value("${jwt.secret}")
    private String JWT_SECRET;

    @Value("${jwt.expiration_ms}")
    private int JWT_EXPIRATION_MS;
    @Value("${jwt.refresh.token.expiration_ms}")
    private int REFRESH_TOKEN_EXPIRATION_MS;

    public String generateJwtToken(Users user) {
        List<String> roleNames = user.getRoles().stream()
                .map(Roles::getName)
                .toList();

        return Jwts.builder()
                .setSubject((user.getUsername()))
                .claim("roles", roleNames)
                .claim("email", user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public RefreshTokenEntity createRefreshToken(Users user) {
        RefreshTokenEntity refreshToken = new RefreshTokenEntity();

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshTokenEntity verifyExpiration(RefreshTokenEntity token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }
}
