package com.expandapis.testtask.service;

import com.expandapis.testtask.dto.UserCredentialsRequestDto;
import com.expandapis.testtask.model.AppUser;
import com.expandapis.testtask.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    public void addUser(UserCredentialsRequestDto userCredentialsDto) {
        if (userRepository.existsUserByUsername(userCredentialsDto.username())) {
            throw new IllegalArgumentException(String.format("User with username %s already exists.", userCredentialsDto.username()));
        }
        AppUser userToAdd = new AppUser();
        userToAdd.setUsername(userCredentialsDto.username());
        userToAdd.setPassword(passwordEncoder.encode(userCredentialsDto.password()));
        userRepository.save(userToAdd);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<AppUser> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isPresent()){
            return new org.springframework.security.core.userdetails.User(optionalUser.get().getUsername(), optionalUser.get().getPassword(),
                    true, true, true, true, new HashSet<>());
        }
        throw new UsernameNotFoundException(String.format("User with username %s is not found", username));
    }
}