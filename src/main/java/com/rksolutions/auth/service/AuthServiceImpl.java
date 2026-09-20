package com.rksolutions.auth.service;

import com.rksolutions.auth.dto.LoginRequest;
import com.rksolutions.auth.dto.LoginResponse;
import com.rksolutions.auth.dto.OtpRequest;
import com.rksolutions.auth.dto.OtpVerifyRequest;
import com.rksolutions.auth.dto.RefreshTokenRequest;
import com.rksolutions.auth.dto.RegisterRequest;
import com.rksolutions.auth.dto.UserRegisterResponse;
import com.rksolutions.auth.security.CustomUserDetails;
import com.rksolutions.auth.security.JwtService;
import com.rksolutions.common.entity.Role;
import com.rksolutions.common.enums.RoleName;
import com.rksolutions.common.enums.UserStatus;
import com.rksolutions.common.exception.BadRequestException;
import com.rksolutions.common.exception.ResourceNotFoundException;
import com.rksolutions.common.exception.UnauthorizedException;
import com.rksolutions.common.repository.RoleRepository;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.jwt.secret}")
    private String jwtSecret;

    private final Map<String, OtpEntry> otpStore = new ConcurrentHashMap<>();

    private static class OtpEntry {
        final String otp;
        final LocalDateTime expiresAt;
        final String name;
        final String password;
        final RoleName roleName;

        OtpEntry(String otp, LocalDateTime expiresAt, String name,
                 String password, RoleName roleName) {
            this.otp = otp;
            this.expiresAt = expiresAt;
            this.name = name;
            this.password = password;
            this.roleName = roleName;
        }
    }

    @PostConstruct
    public void initRoles() {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName).orElseGet(() -> {
                Role role = new Role();
                role.setName(roleName);
                return roleRepository.save(role);
            });
        }
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmailOrPhone(request.getEmailOrPhone(), request.getEmailOrPhone())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email or phone: " + request.getEmailOrPhone()));

        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new UnauthorizedException("User account is not active. Please contact admin.");
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            user.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            throw new UnauthorizedException("Invalid credentials");
        }

        CustomUserDetails userDetails = CustomUserDetails.fromUser(user);
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);

        return toLoginResponse(user, accessToken, refreshToken);
    }

    @Override
    public void sendOtp(OtpRequest request) {
        String phoneOrEmail = request.getPhoneOrEmail();
        Optional<User> existingUser = userRepository.findByEmailOrPhone(phoneOrEmail, phoneOrEmail);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getStatus() == UserStatus.ACTIVE) {
                String otp = generateOtp();
                otpStore.put(phoneOrEmail, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5),
                        null, null, null));
                sendOtpNotification(user.getEmail(), user.getPhone(), otp);
                return;
            }
            throw new BadRequestException("User already exists but not active. Status: " + user.getStatus());
        }

        String otp = generateOtp();
        otpStore.put(phoneOrEmail, new OtpEntry(otp, LocalDateTime.now().plusMinutes(5),
                null, null, RoleName.ROLE_USER));
        sendOtpNotification(null, phoneOrEmail, otp);
    }

    @Override
    @Transactional
    public LoginResponse verifyOtp(OtpVerifyRequest request) {
        String phoneOrEmail = request.getPhoneOrEmail();
        OtpEntry entry = otpStore.get(phoneOrEmail);

        if (entry == null) {
            throw new BadRequestException("OTP not found. Please request OTP first.");
        }

        if (LocalDateTime.now().isAfter(entry.expiresAt)) {
            otpStore.remove(phoneOrEmail);
            throw new BadRequestException("OTP has expired. Please request a new one.");
        }

        if (!entry.otp.equals(request.getOtp())) {
            throw new BadRequestException("Invalid OTP.");
        }

        otpStore.remove(phoneOrEmail);

        Optional<User> existingUser = userRepository.findByEmailOrPhone(phoneOrEmail, phoneOrEmail);

        if (existingUser.isPresent()) {
            User user = existingUser.get();
            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new UnauthorizedException("User account is not active.");
            }
            CustomUserDetails userDetails = CustomUserDetails.fromUser(user);
            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);
            return toLoginResponse(user, accessToken, refreshToken);
        }

        // New user registration via OTP
        if (entry.name == null || entry.password == null) {
            if (request.getName() != null && request.getPassword() != null) {
                User newUser = new User();
                if (phoneOrEmail.contains("@")) {
                    newUser.setEmail(phoneOrEmail);
                    newUser.setPhone(phoneOrEmail);
                } else {
                    newUser.setPhone(phoneOrEmail);
                    newUser.setEmail(phoneOrEmail);
                }
                newUser.setName(request.getName());
                newUser.setPassword(passwordEncoder.encode(request.getPassword()));
                newUser.setStatus(UserStatus.PENDING);

                Role role = roleRepository.findByName(entry.roleName)
                        .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + entry.roleName));
                newUser.setRoles(Set.of(role));

                User savedUser = userRepository.save(newUser);

                LoginResponse response = new LoginResponse();
                response.setName(savedUser.getName());
                response.setEmail(savedUser.getEmail());
                response.setPhone(savedUser.getPhone());
                response.setRole(entry.roleName.name());
                response.setMessage("Registration successful. Please wait for admin approval.");
                return response;
            }
        }

        throw new BadRequestException("User not found. Provide name and password to register, or ensure user exists.");
    }

    @Override
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();
        String email;

        try {
            email = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new UnauthorizedException("Invalid or expired refresh token.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));

        CustomUserDetails userDetails = CustomUserDetails.fromUser(user);

        if (jwtService.isTokenExpired(refreshToken)) {
            throw new UnauthorizedException("Refresh token has expired.");
        }

        String accessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);
        return toLoginResponse(user, accessToken, newRefreshToken);
    }

    @Override
    public void logout(String token) {
        // Stateless JWT - tokens are invalidated client-side
        // In production, consider a token blacklist (Redis) for immediate revocation
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }

    private void sendOtpNotification(String email, String phone, String otp) {
        System.out.println("=== OTP Notification ===");
        if (email != null) {
            System.out.println("To Email: " + email);
        }
        if (phone != null) {
            System.out.println("To Phone: " + phone);
        }
        System.out.println("OTP: " + otp);
        System.out.println("========================");
    }

    private LoginResponse toLoginResponse(User user, String accessToken, String refreshToken) {
        String roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.joining(","));

        LoginResponse response = new LoginResponse();
        response.setAccessToken(accessToken);
        response.setRefreshToken(refreshToken);
        response.setTokenType("Bearer");
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(roleNames);
        return response;
    }

    @Override
    @Transactional
    public UserRegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BadRequestException("Phone already registered: " + request.getPhone());
        }

        User user = new User();
        user.setName(request.getName());
        user.setPhone(request.getPhone());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(UserStatus.PENDING);

        Role role = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));
        user.setRoles(Set.of(role));

       

        User savedUser = userRepository.save(user);

        UserRegisterResponse response = new UserRegisterResponse();
        response.setId(savedUser.getId());
        response.setName(savedUser.getName());
        response.setEmail(savedUser.getEmail());
        response.setPhone(savedUser.getPhone());
        response.setRole("ROLE_USER");
        response.setStatus(savedUser.getStatus());
        response.setMessage("Registration successful. Please wait for admin approval.");
        return response;
    }
}
