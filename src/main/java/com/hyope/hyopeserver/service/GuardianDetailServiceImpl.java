package com.hyope.hyopeserver.service;

import com.hyope.hyopeserver.entity.Guardian;
import com.hyope.hyopeserver.repository.GuardianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GuardianDetailServiceImpl implements UserDetailsService {
    private final GuardianRepository guardianRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException{
        Guardian guardian = guardianRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + loginId));

        return User.withUsername(guardian.getLoginId())
                .password(guardian.getPasswordHash())
                .authorities(new SimpleGrantedAuthority("ROLE_" + guardian.getRole().name()))
                .build();
    }
}
