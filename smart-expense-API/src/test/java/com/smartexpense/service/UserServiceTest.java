package com.smartexpense.service;

import com.smartexpense.dto.UserDTO;
import com.smartexpense.entity.User;
import com.smartexpense.exception.ResourceNotFoundException;
import com.smartexpense.repository.UserRepository;
import com.smartexpense.security.JwtUtil;                    // ← ADD import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;                                 // ← ADD THIS

    @InjectMocks
    private UserService userService;

    private User mockUser;
    private UserDTO mockUserDTO;

    @BeforeEach
    void setUp() {
        mockUser = User.builder()
                .id(1L)
                .name("Thill")
                .email("thill@gmail.com")
                .password("$2a$10$hashedpassword")
                .build();

        mockUserDTO = UserDTO.builder()
                .name("Thill")
                .email("thill@gmail.com")
                .password("123456")
                .build();
    }

    @Test
    void registerUser_Success() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserDTO result = userService.registerUser(mockUserDTO);

        assertNotNull(result);
        assertEquals("Thill", result.getName());
        assertEquals("thill@gmail.com", result.getEmail());
        assertEquals("***", result.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder, times(1)).encode("123456");
    }

    @Test
    void registerUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail("thill@gmail.com")).thenReturn(true);

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(mockUserDTO));

        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserDTO result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("thill@gmail.com", result.getEmail());
    }

    @Test
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(99L));
    }

    // ✅ FIXED — login now returns String (JWT token)
    @Test
    void login_Success() {
        when(userRepository.findByEmail("thill@gmail.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("123456", "$2a$10$hashedpassword"))
                .thenReturn(true);
        when(jwtUtil.generateToken("thill@gmail.com"))
                .thenReturn("mocked.jwt.token");             // ← mock token

        String token = userService.login("thill@gmail.com", "123456"); // ← String

        assertNotNull(token);
        assertEquals("mocked.jwt.token", token);
    }

    @Test
    void login_WrongPassword_ThrowsException() {
        when(userRepository.findByEmail("thill@gmail.com"))
                .thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongpass", "$2a$10$hashedpassword"))
                .thenReturn(false);

        assertThrows(IllegalArgumentException.class,
                () -> userService.login("thill@gmail.com", "wrongpass"));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);
        doNothing().when(userRepository).deleteById(1L);

        assertDoesNotThrow(() -> userService.deleteUser(1L));
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_NotFound_ThrowsException() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class,
                () -> userService.deleteUser(99L));
    }
}
