package com.wardiusz.jat.controller;

import com.wardiusz.jat.dto.request.LoginRequest;
import com.wardiusz.jat.dto.request.OtpRequest;
import com.wardiusz.jat.dto.request.RegisterRequest;
import com.wardiusz.jat.entity.RefreshToken;
import com.wardiusz.jat.entity.User;
import com.wardiusz.jat.security.CookieUtil;
import com.wardiusz.jat.security.JwtTokenProvider;
import com.wardiusz.jat.service.AuthService;
import com.wardiusz.jat.service.OtpTokenService;
import com.wardiusz.jat.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AuthService authService;
    @Mock
    private OtpTokenService otpTokenService;
    @Mock
    private RefreshTokenService refreshTokenService;
    @Mock
    private CookieUtil cookieUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void resendOtp_shouldSendOtpAndReturnOk() {
        OtpRequest request = new OtpRequest("123456", "user@example.com");

        ResponseEntity<?> response = authController.resendOtp(request);

        verify(otpTokenService).generateAndSendOTP("user@example.com");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void verifyOtp_shouldActivateUserWhenOtpIsValid() {
        OtpRequest request = new OtpRequest("123456", "user@example.com");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(otpTokenService.validateOtp("user@example.com", "123456")).thenReturn(true);

        ResponseEntity<?> result = authController.verifyOtp(request, response);

        verify(authService).activateUser("user@example.com");
        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    void verifyOtp_shouldReturnRequestTimeoutWhenOtpIsInvalid() {
        OtpRequest request = new OtpRequest("123456", "user@example.com");
        MockHttpServletResponse response = new MockHttpServletResponse();
        when(otpTokenService.validateOtp("user@example.com", "123456")).thenReturn(false);

        ResponseEntity<?> result = authController.verifyOtp(request, response);

        verify(authService, never()).activateUser("user@example.com");
        assertEquals(HttpStatus.REQUEST_TIMEOUT, result.getStatusCode());
    }

    @Test
    void login_shouldSetCookiesAndReturnNoContent() {
        LoginRequest request = new LoginRequest("username", "password");
        MockHttpServletResponse response = new MockHttpServletResponse();
        RefreshToken refreshToken = RefreshToken.builder().token("refresh-token").build();
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "access-token").build();
        ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", "refresh-token").build();

        when(authService.login(request)).thenReturn("access-token");
        when(refreshTokenService.createRefreshToken("username")).thenReturn(refreshToken);
        when(cookieUtil.createAccessTokenCookie("access-token")).thenReturn(accessCookie);
        when(cookieUtil.createRefreshTokenCookie("refresh-token")).thenReturn(refreshCookie);

        ResponseEntity<Void> result = authController.login(request, response);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertEquals("access_token=access-token", response.getHeaders(HttpHeaders.SET_COOKIE).get(0));
        assertEquals("refresh_token=refresh-token", response.getHeaders(HttpHeaders.SET_COOKIE).get(1));
    }

    @Test
    void register_shouldRegisterAndSendOtpAndReturnCreated() {
        RegisterRequest request = new RegisterRequest("username", "secret", "user@example.com", null);

        ResponseEntity<Void> result = authController.register(request);

        verify(authService).register(request);
        verify(otpTokenService).generateAndSendOTP("user@example.com");
        assertEquals(HttpStatus.CREATED, result.getStatusCode());
    }

    @Test
    void refreshToken_shouldReturnUnauthorizedWhenCookieMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        ResponseEntity<Void> result = authController.refreshToken(request, response);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        verify(refreshTokenService, never()).verifyRefreshToken(org.mockito.ArgumentMatchers.anyString());
    }

    @Test
    void refreshToken_shouldIssueNewAccessTokenWhenRefreshCookieExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("refresh_token", "refresh-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        User user = User.builder().username("username").build();
        RefreshToken refreshToken = RefreshToken.builder().user(user).build();
        ResponseCookie accessCookie = ResponseCookie.from("access_token", "new-access-token").build();

        when(refreshTokenService.verifyRefreshToken("refresh-token")).thenReturn(refreshToken);
        when(jwtTokenProvider.generateAccessToken("username")).thenReturn("new-access-token");
        when(cookieUtil.createAccessTokenCookie("new-access-token")).thenReturn(accessCookie);

        ResponseEntity<Void> result = authController.refreshToken(request, response);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertTrue(response.getHeaders(HttpHeaders.SET_COOKIE).contains("access_token=new-access-token"));
    }

    @Test
    void logout_shouldClearCookiesAndDeleteRefreshTokenWhenAccessCookieExists() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("access_token", "jwt-token"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseCookie clearedAccessCookie = ResponseCookie.from("access_token", "").maxAge(0).build();
        ResponseCookie clearedRefreshCookie = ResponseCookie.from("refresh_token", "").maxAge(0).build();

        when(cookieUtil.clearAccessTokenCookie()).thenReturn(clearedAccessCookie);
        when(cookieUtil.clearRefreshTokenCookie()).thenReturn(clearedRefreshCookie);
        when(jwtTokenProvider.getUsername("jwt-token")).thenReturn("username");

        ResponseEntity<Void> result = authController.logout(request, response);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(refreshTokenService).deleteByUser("username");
        assertNotNull(response.getHeaders(HttpHeaders.SET_COOKIE));
        assertEquals(2, response.getHeaders(HttpHeaders.SET_COOKIE).size());
    }

    @Test
    void logout_shouldClearCookiesWithoutDeletingRefreshTokenWhenAccessCookieMissing() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ResponseCookie clearedAccessCookie = ResponseCookie.from("access_token", "").maxAge(0).build();
        ResponseCookie clearedRefreshCookie = ResponseCookie.from("refresh_token", "").maxAge(0).build();

        when(cookieUtil.clearAccessTokenCookie()).thenReturn(clearedAccessCookie);
        when(cookieUtil.clearRefreshTokenCookie()).thenReturn(clearedRefreshCookie);

        ResponseEntity<Void> result = authController.logout(request, response);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(refreshTokenService, never()).deleteByUser(org.mockito.ArgumentMatchers.anyString());
    }
}
