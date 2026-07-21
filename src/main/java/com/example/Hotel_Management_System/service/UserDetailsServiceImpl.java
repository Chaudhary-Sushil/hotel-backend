package com.example.Hotel_Management_System.service;

import com.example.Hotel_Management_System.repository.UserRepository;
import com.example.Hotel_Management_System.entity.Staff;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // fetch user from DB by email
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole().name()));
        if (user instanceof Staff staff) {
            if (staff.getPermissions() != null) {
                staff.getPermissions().forEach(permission -> {
                    authorities.add(new SimpleGrantedAuthority(permission.name()));
                });
            }
        }

        // wrap our User entity into Spring Security's UserDetails format
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
