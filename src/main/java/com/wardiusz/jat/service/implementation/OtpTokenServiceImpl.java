package com.wardiusz.jat.service.implementation;

import com.wardiusz.jat.entity.OtpToken;
import com.wardiusz.jat.repository.OtpRepository;
import com.wardiusz.jat.service.OtpTokenService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OtpTokenServiceImpl implements OtpTokenService {

    @Value("${spring.mail.username}")
    private String otpSender;

    private final JavaMailSender mailSender;

    private final OtpRepository otpRepository;

    @Override
    public OtpToken generateAndSendOTP(String email) {
        Optional<OtpToken> exOtpToken = otpRepository.findByEmail(email).stream().findFirst();

        exOtpToken.ifPresent(otpRepository::delete);

        int number = 100000 + new SecureRandom().nextInt(900000);

        String code = String.valueOf(number);

        OtpToken otpToken = OtpToken.builder()
                .otp(code)
                .email(email)
                .expiresAt(LocalDateTime.now().plusSeconds(300))
                .build();

        sendOtp(email, code);

        return otpRepository.save(otpToken);
    }

    @Override
    public void sendOtp(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(otpSender);
            helper.setTo(email);
            helper.setSubject("Your verification code");
            helper.setText(buildOtpEmail(code), true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error while sending mail");
        }
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

    private String buildOtpEmail(String code) {
        return """
                    <div style="background:#f4f4f5;padding:40px 20px;font-family:Arial,sans-serif;">
                      <div style="max-width:480px;margin:0 auto;background:#ffffff;border-radius:12px;overflow:hidden;border:1px solid #e4e4e7;">
                
                        <div style="background:#18181b;padding:32px;text-align:center;">
                          <div style="display:inline-block;background:#e8911a;border-radius:8px;padding:8px 18px;">
                            <span style="color:#fff;font-size:18px;font-weight:700;letter-spacing:1px;">JobApplicationTracker</span>
                          </div>
                        </div>
                
                        <div style="padding:36px 40px 28px;">
                          <p style="margin:0 0 6px;font-size:22px;font-weight:700;color:#18181b;">Verify your email</p>
                          <p style="margin:0 0 28px;font-size:15px;color:#71717a;line-height:1.6;">
                            Enter the code below to confirm your account. It expires in <strong style="color:#18181b;">5 minutes</strong>.
                          </p>
                          <div style="background:#f4f4f5;border-radius:10px;padding:24px;text-align:center;margin-bottom:28px;border:1px dashed #d4d4d8;">
                            <span style="font-size:36px;font-weight:800;letter-spacing:10px;color:#18181b;font-family:monospace;">%s</span>
                          </div>
                          <p style="margin:0;font-size:13px;color:#a1a1aa;text-align:center;">
                            Didn't request this? You can safely ignore this email.
                          </p>
                        </div>
                
                        <div style="border-top:1px solid #f4f4f5;padding:20px 40px;background:#fafafa;">
                          <p style="margin:0;font-size:12px;color:#a1a1aa;text-align:center;">
                            This is an automated message. Please do not reply.
                          </p>
                        </div>
                
                      </div>
                    </div>
                """.formatted(code);
    }
}
