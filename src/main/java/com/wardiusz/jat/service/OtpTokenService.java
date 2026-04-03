package com.wardiusz.jat.service;

import com.wardiusz.jat.entity.OtpToken;

public interface OtpTokenService {

    OtpToken generateAndSendOTP(String email);

    void sendOtp(String email, String code);

    void deleteAllExpiredOtps();

    boolean validateOtp(String email, String otp);

}
