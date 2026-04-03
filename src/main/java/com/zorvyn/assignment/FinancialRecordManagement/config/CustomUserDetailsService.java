package com.zorvyn.assignment.FinancialRecordManagement.config;

import com.zorvyn.assignment.FinancialRecordManagement.entities.User;
import com.zorvyn.assignment.FinancialRecordManagement.exception.BadRequestException;
import com.zorvyn.assignment.FinancialRecordManagement.exception.ResourceNotFoundException;
import com.zorvyn.assignment.FinancialRecordManagement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username).orElseThrow(() ->
                new ResourceNotFoundException("User not found with the current username"));

        if (Boolean.FALSE.equals(user.getStatus()) || Boolean.TRUE.equals(user.getIsDeleted())) {
            throw new BadRequestException("Account is inactive or has been deleted.");
        }

        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(String.valueOf(user.getRole()))
        );


        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
