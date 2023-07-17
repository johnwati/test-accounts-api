package com.jw.account.Security;

import com.jw.account.Entities.RefreshToken;
import com.jw.account.Entities.Roles;
import com.jw.account.Entities.Users;
import com.jw.account.Exceptions.TokenRefreshException;
import com.jw.account.Repositories.RefreshTokenRepository;
import com.jw.account.Repositories.UsersRepository;
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

    public RefreshToken createRefreshToken(Users user) {
        RefreshToken refreshToken = new RefreshToken();

        refreshToken.setUser(user);
        refreshToken.setExpiryDate(Instant.now().plusMillis(REFRESH_TOKEN_EXPIRATION_MS));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());

        refreshToken = refreshTokenRepository.save(refreshToken);
        return refreshToken;
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException("Refresh token was expired. Please make a new signin request");
        }

        return token;
    }
}
