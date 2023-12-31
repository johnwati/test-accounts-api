package com.jw.accounts.services;

import com.jw.accounts.dto.LoginRequestDTO;
import com.jw.accounts.dto.RegisterRequestDTO;
import com.jw.accounts.entities.RefreshTokenEntity;
import com.jw.accounts.entities.Users;
import com.jw.accounts.entities.Roles;
import com.jw.accounts.exceptions.BadRequestException;
import com.jw.accounts.exceptions.ForbiddenException;
import com.jw.accounts.exceptions.TokenRefreshException;
import com.jw.accounts.exceptions.UserNotFoundException;
import com.jw.accounts.repositories.RefreshTokenRepository;
import com.jw.accounts.repositories.RolesRepository;
import com.jw.accounts.repositories.UsersRepository;
import com.jw.accounts.response.AuthResponse;
import com.jw.accounts.response.RefreshTokenResponse;
import com.jw.accounts.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AuthenticationService {
    @Autowired
    private UsersRepository usersRepository;


    @Autowired
    private RolesRepository rolesRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public AuthResponse register(RegisterRequestDTO registerDTO){
        if (usersRepository.findByUsername(registerDTO.getUsername()) != null) {
            throw new BadRequestException("Sorry, this username is already taken. Please choose a different one.");
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            Users registeredUser = new Users();
            registeredUser.setUsername(registerDTO.getUsername());
            registeredUser.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
            registeredUser.setEmail(registerDTO.getEmail());
            Roles role = rolesRepository.findByName("ROLE_ADMIN");
            Roles role1 = rolesRepository.findByName("ROLE_MEMBER");
            Set<Roles> roles = new HashSet<>();
            roles.add(role);
            roles.add(role1);
            registeredUser.setRoles(roles);

            usersRepository.save(registeredUser);

            String token = jwtUtils.generateJwtToken(registeredUser);
            RefreshTokenEntity refreshToken = jwtUtils.createRefreshToken(registeredUser);

            List<String> roleNames = new ArrayList<>();
            for (Roles roles1 : registeredUser.getRoles()) {
                String name = roles1.getName();
                roleNames.add(name);
            }

            return new AuthResponse(token,refreshToken.getRefreshToken(),registeredUser.getId(),registeredUser.getUsername(),registeredUser.getEmail(), roleNames);
        }
    }

    public AuthResponse login(LoginRequestDTO loginRequestDTO) {
        Users loginUser = usersRepository.findByUsername(loginRequestDTO.getUsername());
        if (loginUser == null) {
            throw new UserNotFoundException("The user account you are trying to access does not exist.");
        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), loginUser.getPassword())) {
            throw new ForbiddenException("The password you entered is incorrect. Please try again.");
        }

        String token = jwtUtils.generateJwtToken(loginUser);
        RefreshTokenEntity refreshToken = jwtUtils.createRefreshToken(loginUser);

        List<String> roleNames = new ArrayList<>();
        for (Roles roles : loginUser.getRoles()) {
            String name = roles.getName();
            roleNames.add(name);
        }

        return new AuthResponse(token,refreshToken.getRefreshToken(),loginUser.getId(),loginUser.getUsername(),loginUser.getEmail(), roleNames);
    }

    public boolean logout(String token){
        RefreshTokenEntity toDeletetoken = refreshTokenRepository.findByRefreshToken(token).orElse(null);
        if(toDeletetoken != null) {
            refreshTokenRepository.delete(toDeletetoken);
            return refreshTokenRepository.findById(toDeletetoken.getId()).isEmpty();
        }
        return false;
    }

    public RefreshTokenResponse refreshToken(String token){
        return refreshTokenRepository.findByRefreshToken(token)
                .map(jwtUtils::verifyExpiration)
                .map(RefreshTokenEntity::getUser)
                .map(user -> {
                    String jwtToken = jwtUtils.generateJwtToken(user);
                    return new RefreshTokenResponse(jwtToken, token);
                })
                .orElseThrow(() -> new TokenRefreshException("Refresh token is not valid or expired!"));
    }
}
