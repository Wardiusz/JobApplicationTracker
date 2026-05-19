package com.wardiusz.jat.service.implementation;

import com.wardiusz.jat.entity.OtpToken;
import com.wardiusz.jat.repository.OtpRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtpTokenServiceImplTest {

    @Mock
    private JavaMailSender mailSender;
    @Mock
    private OtpRepository otpRepository;

    @InjectMocks
    private OtpTokenServiceImpl otpTokenService;

    @Test
    void generateAndSendOtp_shouldDeleteExistingTokenAndSaveNewOne() {
        OtpToken existingToken = OtpToken.builder().email("user@example.com").otp("111111").build();
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(otpRepository.findByEmail("user@example.com")).thenReturn(Optional.of(existingToken));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(otpTokenService, "otpSender", "no-reply@example.com");

        ArgumentCaptor<OtpToken> savedTokenCaptor = ArgumentCaptor.forClass(OtpToken.class);
        when(otpRepository.save(savedTokenCaptor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        OtpToken result = otpTokenService.generateAndSendOTP("user@example.com");

        verify(otpRepository).delete(existingToken);
        verify(mailSender).send(any(MimeMessage.class));
        verify(otpRepository).save(any(OtpToken.class));
        assertNotNull(result);
        assertEquals("user@example.com", result.getEmail());
        assertEquals(6, savedTokenCaptor.getValue().getOtp().length());
        assertTrue(savedTokenCaptor.getValue().getExpiresAt().isAfter(LocalDateTime.now().plusMinutes(4)));
    }

    @Test
    void sendOtp_shouldSendEmail() {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        ReflectionTestUtils.setField(otpTokenService, "otpSender", "no-reply@example.com");

        otpTokenService.sendOtp("user@example.com", "123456");

        verify(mailSender).send(eq(mimeMessage));
    }

    @Test
    void sendOtp_shouldThrowRuntimeExceptionWhenMailFails() {
        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("mail error"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> otpTokenService.sendOtp("user@example.com", "123456"));

        assertEquals("Error while sending mail", exception.getMessage());
    }

    @Test
    void validateOtp_shouldThrowWhenOtpNotFound() {
        when(otpRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> otpTokenService.validateOtp("user@example.com", "123456"));

        assertEquals("OTP not found", exception.getMessage());
    }

    @Test
    void validateOtp_shouldThrowAndDeleteWhenExpired() {
        OtpToken expiredToken = OtpToken.builder()
                .email("user@example.com")
                .otp("123456")
                .expiresAt(LocalDateTime.now().minusSeconds(1))
                .build();
        when(otpRepository.findByEmail("user@example.com")).thenReturn(Optional.of(expiredToken));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> otpTokenService.validateOtp("user@example.com", "123456"));

        assertEquals("OTP token is expired", exception.getMessage());
        verify(otpRepository).delete(expiredToken);
    }

    @Test
    void validateOtp_shouldThrowWhenOtpIsInvalid() {
        OtpToken token = OtpToken.builder()
                .email("user@example.com")
                .otp("654321")
                .expiresAt(LocalDateTime.now().plusSeconds(30))
                .build();
        when(otpRepository.findByEmail("user@example.com")).thenReturn(Optional.of(token));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> otpTokenService.validateOtp("user@example.com", "123456"));

        assertEquals("Invalid OTP", exception.getMessage());
        verify(otpRepository, never()).save(any(OtpToken.class));
    }

    @Test
    void validateOtp_shouldMarkTokenAsUsedAndSaveWhenValid() {
        OtpToken token = OtpToken.builder()
                .email("user@example.com")
                .otp("123456")
                .expiresAt(LocalDateTime.now().plusSeconds(30))
                .used(false)
                .build();
        when(otpRepository.findByEmail("user@example.com")).thenReturn(Optional.of(token));

        boolean result = otpTokenService.validateOtp("user@example.com", "123456");

        assertTrue(result);
        assertTrue(token.isUsed());
        verify(otpRepository).save(token);
    }

    @Test
    void deleteAllExpiredOtps_shouldDeleteExpiredRecords() {
        otpTokenService.deleteAllExpiredOtps();

        verify(otpRepository).deleteAllByExpiresAtBefore(any(LocalDateTime.class));
    }
}
