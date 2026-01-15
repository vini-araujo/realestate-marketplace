package com.buildconnect.security;

import com.buildconnect.org.model.Membership;
import com.buildconnect.org.model.User;
import com.buildconnect.org.repository.MembershipRepository;
import com.buildconnect.org.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // MVP Assumption: User belongs to exactly one org.
        List<Membership> memberships = membershipRepository.findByUser_Id(user.getId());
        if (memberships.isEmpty()) {
            // Should not happen in valid state, but handle gracefully
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPasswordHash(),
                    Collections.emptyList());
        }

        Membership membership = memberships.get(0);
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + membership.getRole().name());

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                Collections.singletonList(authority));
    }
}
