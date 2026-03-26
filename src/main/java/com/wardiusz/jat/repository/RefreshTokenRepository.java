package com.wardiusz.jat.repository;

import com.wardiusz.jat.model.entity.RefreshToken;
import com.wardiusz.jat.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    @Transactional
    void deleteByUser(User user); // for login

    @Transactional
    void deleteByToken(String token); // for logout

}