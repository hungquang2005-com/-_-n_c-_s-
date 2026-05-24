package com.hung_gamingshop.service;

import com.hung_gamingshop.model.User;
import com.hung_gamingshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(String fullName, String username, String email, String password, String phone) {
        String normalizedUsername = username == null ? "" : username.trim();
        String normalizedEmail = email == null ? "" : email.trim();

        if (normalizedUsername.isBlank()) {
            throw new RuntimeException("Ten dang nhap khong duoc de trong!");
        }
        if (normalizedEmail.isBlank()) {
            throw new RuntimeException("Email khong duoc de trong!");
        }
        if (userRepository.existsByUsername(normalizedUsername)) {
            throw new RuntimeException("Ten dang nhap da duoc su dung!");
        }
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new RuntimeException("Email da duoc su dung!");
        }

        User user = new User();
        user.setFullName(fullName);
        user.setUsername(normalizedUsername);
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setRole(User.Role.USER);
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
}
