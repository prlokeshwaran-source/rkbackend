package com.rksolutions.auth.service;

import com.rksolutions.auth.security.CustomUserDetails;
import com.rksolutions.common.entity.Role;
import com.rksolutions.common.enums.RoleName;
import com.rksolutions.common.enums.UserStatus;
import com.rksolutions.common.repository.RoleRepository;
import com.rksolutions.user.entity.User;
import com.rksolutions.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrPhone(emailOrPhone, emailOrPhone)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email or phone: " + emailOrPhone));

        return CustomUserDetails.fromUser(user);
    }
}
