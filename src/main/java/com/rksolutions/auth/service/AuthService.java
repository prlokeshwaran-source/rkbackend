package com.rksolutions.auth.service;

import com.rksolutions.auth.dto.LoginRequest;
import com.rksolutions.auth.dto.LoginResponse;
import com.rksolutions.auth.dto.OtpRequest;
import com.rksolutions.auth.dto.OtpVerifyRequest;
import com.rksolutions.auth.dto.RefreshTokenRequest;
import com.rksolutions.auth.dto.RegisterRequest;
import com.rksolutions.auth.dto.UserRegisterResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void sendOtp(OtpRequest request);

    LoginResponse verifyOtp(OtpVerifyRequest request);

    LoginResponse refreshToken(RefreshTokenRequest request);

    void logout(String token);

    UserRegisterResponse register(RegisterRequest request);
}
