package com.smartexpense.service;

import com.smartexpense.dto.UserDTO;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ResourceNotFoundException;
import com.smartexpense.repository.UserRepository;
import com.smartexpense.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Transactional(rollbackFor = Exception.class)
    public UserDTO registerUser(UserDTO dto) {
        validatePassword(dto.getPassword());            // ← validate first
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already registered: " + dto.getEmail());
        }
        User user = User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .build();
        return toDTO(userRepository.save(user));
    }

    @Transactional(readOnly = true)
    public String login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with email: " + email));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return jwtUtil.generateToken(email);
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
        return toDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUser(Long id, UserDTO dto) {
        validatePassword(dto.getPassword());            // ← validate on update too
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + id));
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        return toDTO(userRepository.save(user));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 8)
            throw new IllegalArgumentException(
                    "Password must be at least 8 characters.");
        if (!password.matches(".*[A-Z].*"))
            throw new IllegalArgumentException(
                    "Password must contain at least one uppercase letter.");
        if (!password.matches(".*[a-z].*"))
            throw new IllegalArgumentException(
                    "Password must contain at least one lowercase letter.");
        if (!password.matches(".*[0-9].*"))
            throw new IllegalArgumentException(
                    "Password must contain at least one number.");
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*"))
            throw new IllegalArgumentException(
                    "Password must contain at least one special character (!@#$%...).");
    }

    private UserDTO toDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .password("***")
                .build();
    }
}