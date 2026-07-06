package com.example.Hotel_Management_System.config;

import com.example.Hotel_Management_System.dto.OAuth2UserInfo;
import com.example.Hotel_Management_System.entity.Guest;
import com.example.Hotel_Management_System.entity.Role;
import com.example.Hotel_Management_System.repository.GuestRepository;
import com.example.Hotel_Management_System.service.JwtService;
import com.example.Hotel_Management_System.service.OAuth2UserInfoFactory;
import com.example.Hotel_Management_System.service.RefreshTokenService;
import com.example.Hotel_Management_System.service.UserDetailsServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final GuestRepository guestRepository;
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.oauth2.redirect-uri}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // get provider name (google or GitHub)
        String provider = authentication.getAuthorities()
                .toString().contains("SCOPE_email") ? "google" : "github";

        // extract user info
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory
                .getOAuth2UserInfo(provider, oAuth2User.getAttributes());

        // find or create guest
        Guest guest = guestRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> createNewGuest(userInfo));

        // generate JWT token
        var userDetails = userDetailsService
                .loadUserByUsername(guest.getEmail());
        var accessToken = jwtService.generateToken(userDetails);
        var refreshToken = refreshTokenService
                .createRefreshToken(guest.getEmail());

        // redirect to frontend with tokens
        String targetUrl = redirectUri
                + "?token=" + accessToken
                + "&refreshToken=" + refreshToken.getToken();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private Guest createNewGuest(OAuth2UserInfo userInfo) {
        Guest guest = Guest.builder()
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .email(userInfo.getEmail())
                .password("")                    // no password for OAuth2 users
                .phoneNumber("null")                 // can update later
                .passportNumber("null")              // can update later
                .citizenshipNumber("null")           // can update later
                .nationality("null")                 // can update later
                .address("null")                     // can update later
                .role(Role.ROLE_GUEST)           // always guest
                .build();

        return guestRepository.save(guest);
    }
}