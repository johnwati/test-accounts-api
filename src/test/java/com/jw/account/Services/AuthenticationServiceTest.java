package com.jw.account.services;

import com.jw.account.DTO.LoginRequestDTO;
import com.jw.account.DTO.RegisterRequestDTO;
import com.jw.account.entities.RefreshTokenEntity;
import com.jw.account.entities.Roles;
import com.jw.account.entities.Users;
import com.jw.account.exceptions.BadRequestException;
import com.jw.account.exceptions.TokenRefreshException;
import com.jw.account.exceptions.UserNotFoundException;
import com.jw.account.repositories.RefreshTokenRepository;
import com.jw.account.repositories.RolesRepository;
import com.jw.account.repositories.UsersRepository;
import com.jw.account.response.RefreshTokenResponse;
import com.jw.account.security.JwtUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UsersRepository mockUsersRepository;
    @Mock
    private RefreshTokenRepository mockRefreshTokenRepository;
    @Mock
    private RolesRepository mockRolesRepository;
    @Mock
    private JwtUtils mockJwtUtils;

    @InjectMocks
    private AuthenticationService authenticationServiceUnderTest;

    @Test
    void testRegister_ThrowsBadRequestException() {
        // Setup
        final RegisterRequestDTO registerDTO = new RegisterRequestDTO();
        registerDTO.setUsername("username");
        registerDTO.setPassword("password");
        registerDTO.setEmail("email");

        // Configure UsersRepository.findByUsername(...).
        final Users users = new Users();
        users.setId(0L);
        users.setUsername("username");
        users.setEmail("email");
        users.setPassword("password");
        final Roles roles = new Roles();
        roles.setName("name");
        users.setRoles(Set.of(roles));
        when(mockUsersRepository.findByUsername("username")).thenReturn(users);

        // Run the test
        assertThatThrownBy(() -> authenticationServiceUnderTest.register(registerDTO))
                .isInstanceOf(BadRequestException.class);
    }


    @Test
    void testLogin_UsersRepositoryReturnsNull() {
        // Setup
        final LoginRequestDTO loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setUsername("username");
        loginRequestDTO.setPassword("password");

        when(mockUsersRepository.findByUsername("username")).thenReturn(null);

        // Run the test
        assertThatThrownBy(() -> authenticationServiceUnderTest.login(loginRequestDTO))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void testLogout() {
        // Setup
        // Configure RefreshTokenRepository.findByRefreshToken(...).
        final RefreshTokenEntity refreshToken1 = new RefreshTokenEntity();
        refreshToken1.setId(0L);
        final Users user = new Users();
        user.setId(0L);
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("password");
        final Roles roles = new Roles();
        roles.setName("name");
        user.setRoles(Set.of(roles));
        refreshToken1.setUser(user);
        refreshToken1.setRefreshToken("refreshToken");
        final Optional<RefreshTokenEntity> refreshToken = Optional.of(refreshToken1);
        when(mockRefreshTokenRepository.findByRefreshToken("token")).thenReturn(refreshToken);

        // Configure RefreshTokenRepository.findById(...).
        final RefreshTokenEntity refreshToken3 = new RefreshTokenEntity();
        refreshToken3.setId(0L);
        final Users user1 = new Users();
        user1.setId(0L);
        user1.setUsername("username");
        user1.setEmail("email");
        user1.setPassword("password");
        final Roles roles1 = new Roles();
        roles1.setName("name");
        user1.setRoles(Set.of(roles1));
        refreshToken3.setUser(user1);
        refreshToken3.setRefreshToken("refreshToken");
        final Optional<RefreshTokenEntity> refreshToken2 = Optional.of(refreshToken3);
        when(mockRefreshTokenRepository.findById(0L)).thenReturn(refreshToken2);

        // Run the test
        final boolean result = authenticationServiceUnderTest.logout("token");

        // Verify the results
        assertThat(result).isFalse();

        // Confirm RefreshTokenRepository.delete(...).
        final RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(0L);
        final Users user2 = new Users();
        user2.setId(0L);
        user2.setUsername("username");
        user2.setEmail("email");
        user2.setPassword("password");
        final Roles roles2 = new Roles();
        roles2.setName("name");
        user2.setRoles(Set.of(roles2));
        entity.setUser(user2);
        entity.setRefreshToken("refreshToken");
        verify(mockRefreshTokenRepository).delete(entity);
    }

    @Test
    void testLogout_RefreshTokenRepositoryFindByRefreshTokenReturnsAbsent() {
        // Setup
        when(mockRefreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.empty());

        // Run the test
        final boolean result = authenticationServiceUnderTest.logout("token");

        // Verify the results
        assertThat(result).isFalse();
    }

    @Test
    void testLogout_RefreshTokenRepositoryFindByIdReturnsAbsent() {
        // Setup
        // Configure RefreshTokenRepository.findByRefreshToken(...).
        final RefreshTokenEntity refreshToken1 = new RefreshTokenEntity();
        refreshToken1.setId(0L);
        final Users user = new Users();
        user.setId(0L);
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("password");
        final Roles roles = new Roles();
        roles.setName("name");
        user.setRoles(Set.of(roles));
        refreshToken1.setUser(user);
        refreshToken1.setRefreshToken("refreshToken");
        final Optional<RefreshTokenEntity> refreshToken = Optional.of(refreshToken1);
        when(mockRefreshTokenRepository.findByRefreshToken("token")).thenReturn(refreshToken);

        when(mockRefreshTokenRepository.findById(0L)).thenReturn(Optional.empty());

        // Run the test
        final boolean result = authenticationServiceUnderTest.logout("token");

        // Verify the results
        assertThat(result).isTrue();

        // Confirm RefreshTokenRepository.delete(...).
        final RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(0L);
        final Users user1 = new Users();
        user1.setId(0L);
        user1.setUsername("username");
        user1.setEmail("email");
        user1.setPassword("password");
        final Roles roles1 = new Roles();
        roles1.setName("name");
        user1.setRoles(Set.of(roles1));
        entity.setUser(user1);
        entity.setRefreshToken("refreshToken");
        verify(mockRefreshTokenRepository).delete(entity);
    }

    @Test
    void testRefreshToken() {
        // Setup
        // Configure RefreshTokenRepository.findByRefreshToken(...).
        final RefreshTokenEntity refreshToken1 = new RefreshTokenEntity();
        refreshToken1.setId(0L);
        final Users user = new Users();
        user.setId(0L);
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("password");
        final Roles roles = new Roles();
        roles.setName("name");
        user.setRoles(Set.of(roles));
        refreshToken1.setUser(user);
        refreshToken1.setRefreshToken("refreshToken");
        final Optional<RefreshTokenEntity> refreshToken = Optional.of(refreshToken1);
        when(mockRefreshTokenRepository.findByRefreshToken("token")).thenReturn(refreshToken);

        // Configure JwtUtils.verifyExpiration(...).
        final RefreshTokenEntity refreshToken2 = new RefreshTokenEntity();
        refreshToken2.setId(0L);
        final Users user1 = new Users();
        user1.setId(0L);
        user1.setUsername("username");
        user1.setEmail("email");
        user1.setPassword("password");
        final Roles roles1 = new Roles();
        roles1.setName("name");
        user1.setRoles(Set.of(roles1));
        refreshToken2.setUser(user1);
        refreshToken2.setRefreshToken("refreshToken");
        final RefreshTokenEntity token = new RefreshTokenEntity();
        token.setId(0L);
        final Users user2 = new Users();
        user2.setId(0L);
        user2.setUsername("username");
        user2.setEmail("email");
        user2.setPassword("password");
        final Roles roles2 = new Roles();
        roles2.setName("name");
        user2.setRoles(Set.of(roles2));
        token.setUser(user2);
        token.setRefreshToken("refreshToken");
        when(mockJwtUtils.verifyExpiration(token)).thenReturn(refreshToken2);

        // Configure JwtUtils.generateJwtToken(...).
        final Users user3 = new Users();
        user3.setId(0L);
        user3.setUsername("username");
        user3.setEmail("email");
        user3.setPassword("password");
        final Roles roles3 = new Roles();
        roles3.setName("name");
        user3.setRoles(Set.of(roles3));
        when(mockJwtUtils.generateJwtToken(user3)).thenReturn("token");

        // Run the test
        final RefreshTokenResponse result = authenticationServiceUnderTest.refreshToken("token");

        // Verify the results
    }

    @Test
    void testRefreshToken_RefreshTokenRepositoryReturnsAbsent() {
        // Setup
        when(mockRefreshTokenRepository.findByRefreshToken("token")).thenReturn(Optional.empty());

        // Run the test
        assertThatThrownBy(() -> authenticationServiceUnderTest.refreshToken("token"))
                .isInstanceOf(TokenRefreshException.class);
    }
}
