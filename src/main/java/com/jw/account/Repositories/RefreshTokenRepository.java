package com.jw.account.repositories;

import com.jw.account.entities.RefreshTokenEntity;
import com.jw.account.entities.Users;
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