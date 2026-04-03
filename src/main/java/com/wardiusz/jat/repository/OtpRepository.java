package com.wardiusz.jat.repository;

import com.wardiusz.jat.entity.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpToken, Long> {

    Optional<OtpToken> findByEmail(String email);

    void deleteAllByExpiresAtBefore(LocalDateTime expiresAtBefore);

}
