package com.jw.accounts.repositories;

import com.jw.accounts.entities.RefreshTokenEntity;
import com.jw.accounts.entities.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByRefreshToken(String token);

    @Modifying
    int deleteByUser(Users user);

}