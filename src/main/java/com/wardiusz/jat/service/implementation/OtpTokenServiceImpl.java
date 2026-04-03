package com.wardiusz.jat.service.implementation;

import com.wardiusz.jat.entity.OtpToken;
import com.wardiusz.jat.entity.User;
import com.wardiusz.jat.repository.OtpRepository;
import com.wardiusz.jat.repository.UserRepository;
import com.wardiusz.jat.service.OtpTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class OtpTokenServiceImpl implements OtpTokenService {

    @Value("${app.mail.otp.expiration}")
    private int OTP_EXPIRATION;

    private JavaMailSender mailSender;
    private final OtpRepository otpRepository;
    private final UserRepository userRepository;

    @Override
    public OtpToken generateAndSendOTP(String email) {
        otpRepository.deleteAll(otpRepository.findByEmail(email).stream().collect(Collectors.toList()));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email not found"));

        int number = 100000 + new SecureRandom().nextInt(999999);

        String code = String.valueOf(number);

        OtpToken otpToken = OtpToken.builder()
                .otp(code)
                .email(user.getEmail())
                .expiresAt(LocalDateTime.now().plusSeconds(OTP_EXPIRATION))
                .build();

        sendOtp(email, code);

        return otpRepository.save(otpToken);
    }

    public void sendOtp(String email, String code) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("OTP Token");
        message.setText("OTP Token: " + code);

        mailSender.send(message);
    }

    @Override
    public boolean validateOtp(String email, String otp) {
        OtpToken otpToken = otpRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("OTP not found"));

        if (otpToken.isExpired()) {
            otpRepository.delete(otpToken);
            throw new RuntimeException("OTP token is expired");
        }
        if (!otpToken.getOtp().equals(otp)) {
            throw new RuntimeException("Invalid OTP");
        }

        otpToken.setUsed(true);

        otpRepository.save(otpToken);

        return true;
    }

    @Scheduled(fixedRate = 60000)
    @Override
    public void deleteAllExpiredOtps() {
        otpRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }
}
