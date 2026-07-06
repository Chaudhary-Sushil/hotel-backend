package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.dto.*;
import com.example.Hotel_Management_System.entity.*;
import com.example.Hotel_Management_System.exception.RefreshTokenExpiredException;
import com.example.Hotel_Management_System.exception.UserAlreadyExistsException;
import com.example.Hotel_Management_System.exception.UserNotFoundException;
import com.example.Hotel_Management_System.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final GuestRepository guestRepository;
    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlacklistService blacklistService;

    // To Register Guest
    public AuthResponse registerGuest(RegisterGuestRequest registerGuestRequest) {
        //check duplicate email
        if (guestRepository.findByEmail(registerGuestRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + registerGuestRequest.getEmail()
            );
        }
        var  guest = Guest.builder()
                .firstName(registerGuestRequest.getFirstName())
                .lastName(registerGuestRequest.getLastName())
                .email(registerGuestRequest.getEmail())
                .password(passwordEncoder.encode(registerGuestRequest.getPassword()))
                .phoneNumber(registerGuestRequest.getPhoneNumber())
                .passportNumber(registerGuestRequest.getPassportNumber())
                .citizenshipNumber(registerGuestRequest.getCitizenshipNumber())
                .nationality(registerGuestRequest.getNationality())
                .address(registerGuestRequest.getAddress())
                .role(Role.ROLE_GUEST)
                .build();

        guestRepository.save(guest);

        var userDetails = userDetailsService.loadUserByUsername(guest.getEmail());
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(guest.getEmail()); // ← added

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())    // ← added
                .build();
    }

    // To register Staff
    public AuthResponse registerStaff(RegisterStaffRequest registerStaffRequest) {
        if (staffRepository.findByEmail(registerStaffRequest.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + registerStaffRequest.getEmail()

            );
        }
        var staff = Staff.builder()
                .employeeId("EMP" + System.currentTimeMillis())
                .dateOfBirth(registerStaffRequest.getDateOfBirth())
                .firstName(registerStaffRequest.getFirstName())
                .lastName(registerStaffRequest.getLastName())
                .email(registerStaffRequest.getEmail())
                .password(passwordEncoder.encode(registerStaffRequest.getPassword()))
                .phoneNumber(registerStaffRequest.getPhoneNumber())
                .role(Role.valueOf(registerStaffRequest.getRole()))
                .build();

        staffRepository.save(staff);

        var userDetails = userDetailsService.loadUserByUsername(staff.getEmail());
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(staff.getEmail());

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }


    //Login

    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService.createRefreshToken(request.getEmail()); // ← added

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken.getToken())    // ← added
                .build();
    }



    // ─── REFRESH ─────────────────────────────────────────────────────
        public AuthResponse refresh(RefreshTokenRequest request) {

            // 1. find the refresh token in DB
            RefreshToken refreshToken = refreshTokenService.findByToken(request.getRefreshToken());

            // 2. check it hasn't expired
            refreshTokenService.verifyExpiration(refreshToken);

            // 3. generate new access token from the user attached to refresh token
            var userDetails = userDetailsService
                    .loadUserByUsername(refreshToken.getUser().getEmail());

            var newAccessToken = jwtService.generateToken(userDetails);

            return AuthResponse.builder()
                    .token(newAccessToken)
                    .refreshToken(refreshToken.getToken())   // same refresh token
                    .build();
        }

    public void logout(LogoutRequest request) {

        // 1. blacklist the access token immediately
        blacklistService.blacklistToken(request.getAccessToken());

        // 2. delete refresh token from DB — can't refresh anymore
        RefreshToken refreshToken = refreshTokenRepository
                .findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RefreshTokenExpiredException(
                        "Refresh token not found"
                ));

        refreshTokenService.deleteByUser(
                refreshToken.getUser().getEmail()
        );
    }

    // ─── CREATE STAFF (Admin only) ────────────────────────────────────
    public CreateStaffResponse createStaff(CreateStaffRequest request) {

        // check duplicate email
        if (staffRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException(
                    "Email already registered: " + request.getEmail()
            );
        }

        // auto generate temp password
        String tempPassword = generateTempPassword();

        // auto generate employeeId
        String employeeId = "EMP" + System.currentTimeMillis();

        // build staff
        var staff = Staff.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(tempPassword))
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .employeeId(employeeId)
                .role(Role.valueOf(request.getRole()))
                .build();

        staffRepository.save(staff);

        return CreateStaffResponse.builder()
                .id(staff.getId())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .email(staff.getEmail())
                .employeeId(employeeId)
                .role(request.getRole())
                .temporaryPassword(tempPassword)  // ← return once ✅
                .message("Staff created successfully! " +
                        "Share credentials securely.")
                .build();
    }


    public List<CreateStaffResponse> getAllStaff() {
        return staffRepository.findAll()
                .stream()
                .map(staff -> CreateStaffResponse.builder()
                        .id(staff.getId())
                        .firstName(staff.getFirstName())
                        .lastName(staff.getLastName())
                        .email(staff.getEmail())
                        .employeeId(staff.getEmployeeId())
                        .role(staff.getRole().name())
                        .build())
                .collect(Collectors.toList());
    }

    // ─── CHANGE PASSWORD ──────────────────────────────────────────────
    public String changePassword(
            String email,
            ChangePasswordRequest request) {

        // find user
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found"
                ));

        // verify current password
        if (!passwordEncoder.matches(
                request.getCurrentPassword(),
                user.getPassword())) {
            throw new BadCredentialsException(
                    "Current password is incorrect"
            );
        }

        // update password
        user.setPassword(
                passwordEncoder.encode(request.getNewPassword())
        );
        user.setFirstLogin(false);
        userRepository.save(user);

        return "Password changed successfully! ✅";
    }

    // ─── TEMP PASSWORD GENERATOR ──────────────────────────────────────
    private String generateTempPassword() {
        int randomNum = (int)(Math.random() * 900000) + 100000;
        return "Hotel@" + randomNum;
    }
}
