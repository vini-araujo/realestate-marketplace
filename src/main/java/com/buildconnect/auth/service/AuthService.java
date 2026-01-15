package com.buildconnect.auth.service;

import com.buildconnect.auth.dto.AuthDto;
import com.buildconnect.org.model.*;
import com.buildconnect.org.repository.MembershipRepository;
import com.buildconnect.org.repository.OrganizationRepository;
import com.buildconnect.org.repository.UserRepository;
import com.buildconnect.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final MembershipRepository membershipRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Transactional
    public AuthDto.AuthResponse register(AuthDto.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }

        // Create User
        var user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();
        userRepository.save(user);

        // Create Organization
        var org = Organization.builder()
                .name(request.getOrgName())
                .type(request.getOrgType())
                .build();
        organizationRepository.save(org);

        // Create Membership
        var role = request.getRole() != null ? request.getRole() : Role.ORG_ADMIN;
        var membership = Membership.builder()
                .id(new MembershipId(user.getId(), org.getId()))
                .user(user)
                .organization(org)
                .role(role)
                .build();
        membershipRepository.save(membership);

        // Generate Token
        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var token = jwtService.generateToken(userDetails);

        return AuthDto.AuthResponse.builder()
                .token(token)
                .build();
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));
        var userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        var token = jwtService.generateToken(userDetails);
        return AuthDto.AuthResponse.builder()
                .token(token)
                .build();
    }
}
