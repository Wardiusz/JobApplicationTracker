package com.wardiusz.jat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Table(name = "otp_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class OtpToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String otp;

    @Column
    private String email;

    @Column
    private boolean used = false;

    @DateTimeFormat
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @DateTimeFormat
    @Column(updatable = false)
    private LocalDateTime expiresAt;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

}
