package com.example.Rider.security;

import com.example.Rider.model.user.User;
import com.example.Rider.model.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {


    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailAndIsDeleted(email, "NO").orElse(null);

        if (user == null) {
            throw new UsernameNotFoundException("User Not Found with email : " + email);
        } else {
            return new UserPrinciple(user);
        }
    }
}
