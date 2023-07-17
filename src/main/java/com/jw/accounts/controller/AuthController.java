package com.jw.accounts.controller;

import com.jw.accounts.entities.RefreshTokenEntity;
import com.jw.accounts.response.AuthResponse;
import com.jw.accounts.dto.LoginRequestDTO;
import com.jw.accounts.response.RefreshTokenResponse;
import com.jw.accounts.dto.RegisterRequestDTO;
import com.jw.accounts.exceptions.BadRequestException;
import com.jw.accounts.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public AuthResponse register(@RequestBody @Valid RegisterRequestDTO request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return authenticationService.register(request);
    }
    @PostMapping("/login")
    public AuthResponse login(@RequestBody @Valid LoginRequestDTO request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new BadRequestException(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return authenticationService.login(request);
    }

    @PostMapping("/refreshtoken")
    public RefreshTokenResponse refreshtoken(@Valid @RequestBody RefreshTokenEntity request) {
        String requestRefreshToken = request.getRefreshToken();
        return authenticationService.refreshToken(requestRefreshToken);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenEntity request){
        if(!authenticationService.logout(request.getRefreshToken()))
            return ResponseEntity.badRequest().body("Sorry, it seems that you are unable to log out at the moment. Please try again!");
        return ResponseEntity.ok().body("You have been successfully logged out.");
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/roles")
    public ResponseEntity<List<String>> roles(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        List<String> roles = new ArrayList<>();
        for (GrantedAuthority authority : authorities) {
            String grantedAuthorityAuthority = authority.getAuthority();
            roles.add(grantedAuthorityAuthority);
        }

        return ResponseEntity.ok().body(roles);
    }

}
