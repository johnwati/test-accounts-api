package com.jw.account.services;

import com.jw.account.entities.RefreshTokenEntity;
import com.jw.account.entities.Roles;
import com.jw.account.entities.Users;
import com.jw.account.DTO.LoginRequestDTO;
import com.jw.account.DTO.RegisterRequestDTO;
import com.jw.account.exceptions.BadRequestException;
import com.jw.account.exceptions.ForbiddenException;
import com.jw.account.exceptions.TokenRefreshException;
import com.jw.account.exceptions.UserNotFoundException;
import com.jw.account.repositories.RefreshTokenRepository;
import com.jw.account.repositories.RolesRepository;
import com.jw.account.repositories.UsersRepository;
import com.jw.account.response.AuthResponse;
import com.jw.account.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class authenticationServiceTest2 {

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private RolesRepository rolesRepository;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testRegisterWithValidData() {
        // Prepare test data
        RegisterRequestDTO registerDTO = new RegisterRequestDTO();
        registerDTO.setUsername("john");
        registerDTO.setPassword("password");
        registerDTO.setEmail("john@example.com");

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(registerDTO.getPassword());

        Users registeredUser = new Users();
        registeredUser.setId(1L);
        registeredUser.setUsername(registerDTO.getUsername());
        registeredUser.setPassword(encodedPassword);
        registeredUser.setEmail(registerDTO.getEmail());
        Roles role = new Roles();
        role.setName("ROLE_ADMIN");
        Roles role1 = new Roles();
        role1.setName("ROLE_MEMBER");
        Set<Roles> roles = new HashSet<>();
        roles.add(role);
        roles.add(role1);
        registeredUser.setRoles(roles);

        // Mock the repository methods
        when(usersRepository.findByUsername(anyString())).thenReturn(null);
        when(usersRepository.save(any(Users.class))).thenReturn(registeredUser);
        when(rolesRepository.findByName(anyString())).thenReturn(role, role1);
        when(jwtUtils.generateJwtToken(any(Users.class))).thenReturn("jwt-token");
        when(jwtUtils.createRefreshToken(any(Users.class))).thenReturn(new RefreshTokenEntity());
//        when(registeredUser.getRoles()).thenReturn(roles);

        // Call the service method
        AuthResponse result = authenticationService.register(registerDTO);

        // Assertions
        assertNotNull(result);
        assertEquals("jwt-token", result.getToken());
        assertEquals("john", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
        assertEquals(2, result.getRoles().size());
    }

    @Test
    void testRegisterWithExistingUsername() {
        // Prepare test data
        RegisterRequestDTO registerDTO = new RegisterRequestDTO();
        registerDTO.setUsername("john");
        registerDTO.setPassword("password");
        registerDTO.setEmail("john@example.com");

        Users existingUser = new Users();

        // Mock the repository methods
        when(usersRepository.findByUsername(anyString())).thenReturn(existingUser);

        // Assertions
        assertThrows(BadRequestException.class, () -> authenticationService.register(registerDTO));
    }

    @Test
    void testLoginWithNonExistingUsername() {
        // Prepare test data
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("john");
        loginRequestDTO.setPassword("password");

        // Mock the repository methods
        when(usersRepository.findByUsername(anyString())).thenReturn(null);

        // Assertions
        assertThrows(UserNotFoundException.class, () -> authenticationService.login(loginRequestDTO));
    }

    @Test
    void testLoginWithInvalidPassword() {
        // Prepare test data
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("john");
        loginRequestDTO.setPassword("password");

        Users existingUser = new Users();
        existingUser.setPassword("$2a$10$J5Y2V5y.yN/z.3YfU8hGU.YrsUEN0jMZMQgHNr2uxa3h8FfiWzJlm"); // Encoded password

        // Mock the repository methods
        when(usersRepository.findByUsername(anyString())).thenReturn(existingUser);

        // Assertions
        assertThrows(ForbiddenException.class, () -> authenticationService.login(loginRequestDTO));
    }

    @Test
    void testLogoutWithValidRefreshToken() {
        // Prepare test data
        String refreshToken = "refresh-token";
        RefreshTokenEntity existingRefreshToken = new RefreshTokenEntity();

        // Mock the repository methods
        when(refreshTokenRepository.findByRefreshToken(anyString())).thenReturn(Optional.of(existingRefreshToken));
        when(refreshTokenRepository.findById(anyLong())).thenReturn(Optional.of(existingRefreshToken));

        // Call the service method
        boolean result = authenticationService.logout(refreshToken);

        // Assertions
        assertTrue(result);
    }

    @Test
    void testLogoutWithInvalidRefreshToken() {
        // Prepare test data
        String refreshToken = "refresh-token";

        // Mock the repository methods
        when(refreshTokenRepository.findByRefreshToken(anyString())).thenReturn(Optional.empty());

        // Call the service method
        boolean result = authenticationService.logout(refreshToken);

        // Assertions
        assertFalse(result);
    }

    @Test
    void testRefreshTokenWithExpiredToken() {
        // Prepare test data
        String refreshToken = "refresh-token";
        RefreshTokenEntity existingRefreshToken = new RefreshTokenEntity();

        // Mock the repository methods
        when(refreshTokenRepository.findByRefreshToken(anyString())).thenReturn(Optional.of(existingRefreshToken));
        when(jwtUtils.verifyExpiration(any(RefreshTokenEntity.class))).thenThrow(TokenRefreshException.class);

        // Assertions
        assertThrows(TokenRefreshException.class, () -> authenticationService.refreshToken(refreshToken));
    }

    @Test
    void testRefreshTokenWithInvalidToken() {
        // Prepare test data
        String refreshToken = "refresh-token";

        // Mock the repository methods
        when(refreshTokenRepository.findByRefreshToken(anyString())).thenReturn(Optional.empty());

        // Assertions
        assertThrows(TokenRefreshException.class, () -> authenticationService.refreshToken(refreshToken));
    }
}
