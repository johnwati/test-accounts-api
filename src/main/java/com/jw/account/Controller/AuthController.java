package com.jw.account.Controller;

import com.jw.account.Entities.RefreshTokenEntity;
import com.jw.account.Response.AuthResponse;
import com.jw.account.DTO.LoginRequestDTO;
import com.jw.account.Response.RefreshTokenResponse;
import com.jw.account.DTO.RegisterRequestDTO;
import com.jw.account.Exceptions.BadRequestException;
import com.jw.account.Services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

        List<String> roles = authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(roles);
    }

}
