package com.jw.accounts.security;

import com.jw.accounts.models.JwtAuthenticatedProfile;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    public JwtAuthorizationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.replace("Bearer ", "");

        if (jwtUtils.validateToken(token)) {
            Claims claims = jwtUtils.getClaimsFromToken(token);
            String username = claims.getSubject();
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();

            List<String> rolesList = (List<String>) claims.get("roles");

            for (String role : rolesList) {
                authorities.add(new SimpleGrantedAuthority(role));
            }

            JwtAuthenticatedProfile authenticatedProfile = new JwtAuthenticatedProfile(username, authorities, token);
            SecurityContextHolder.getContext().setAuthentication(authenticatedProfile);
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"status\": 401, \"error\":\"Unauthorized\",\"message\":\"Invalid token. Please provide a valid token for authentication.\"}");
        }
    }

}
