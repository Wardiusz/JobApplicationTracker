package com.wardiusz.jat.repository;

import com.wardiusz.jat.entity.RefreshToken;
import com.wardiusz.jat.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

    @Transactional
    void deleteByUser(User user);

    @Transactional
    void deleteByToken(String token);

}