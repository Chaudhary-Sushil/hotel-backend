package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.dto.OAuth2UserInfo;
import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            String provider,
            Map<String, Object> attributes) {

        switch (provider.toLowerCase()) {

            case "google":
                return OAuth2UserInfo.builder()
                        .email((String) attributes.get("email"))
                        .firstName((String) attributes.get("given_name"))
                        .lastName((String) attributes.get("family_name"))
                        .provider("google")
                        .build();

            case "github":
                // GitHub might return null email
                // if user has private email setting
                Object emailObj = attributes.get("email");
                String email = emailObj != null ?
                        emailObj.toString() :
                        attributes.get("login") + "@github.com"; // fallback

                String githubName = (String) attributes.get("name");
                String login = (String) attributes.get("login");
                String displayName = githubName != null ? githubName : login;
                String[] nameParts = displayName.split(" ", 2);

                return OAuth2UserInfo.builder()
                        .email(email)
                        .firstName(nameParts[0])
                        .lastName(nameParts.length > 1 ? nameParts[1] : "")
                        .provider("github")
                        .build();

            default:
                throw new RuntimeException("Unsupported OAuth2 provider: " + provider);
        }
    }
}