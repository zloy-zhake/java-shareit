package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.UserNotValidException;
import ru.practicum.shareit.user.dto.NewUserRequestDto;
import ru.practicum.shareit.user.dto.UpdateUserRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("test")
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addUser_ValidData_CreatesUser() {
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setName("Test User");
        requestDto.setEmail("test@example.com");

        UserDto result = userService.addUser(requestDto);

        assertNotNull(result);
        assertEquals("Test User", result.getName());
        assertEquals("test@example.com", result.getEmail());
    }

    @Test
    void addUser_InvalidEmail_ThrowsException() {
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setName("Test User");
        requestDto.setEmail("invalid-email");

        assertThrows(UserNotValidException.class, () -> userService.addUser(requestDto));
    }

    @Test
    void addUser_NullName_ThrowsException() {
        NewUserRequestDto requestDto = new NewUserRequestDto();
        requestDto.setName(null);
        requestDto.setEmail("test@example.com");

        assertThrows(UserNotValidException.class, () -> userService.addUser(requestDto));
    }

    @Test
    void getUserById_ExistingUser_ReturnsUser() {
        User user = new User();
        user.setName("Existing User");
        user.setEmail("existing@example.com");
        userRepository.save(user);

        UserDto result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals("Existing User", result.getName());
        assertEquals("existing@example.com", result.getEmail());
    }

    @Test
    void getUserById_NonExistingUser_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> userService.getUserById(99999));
    }

    @Test
    void updateUser_ValidData_UpdatesUser() {
        User user = new User();
        user.setName("Original Name");
        user.setEmail("original@example.com");
        userRepository.save(user);

        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName("Updated Name");

        UserDto result = userService.updateUser(user.getId(), updateDto);

        assertNotNull(result);
        assertEquals("Updated Name", result.getName());
        assertEquals("original@example.com", result.getEmail());
    }

    @Test
    void updateUser_NonExistingUser_ThrowsException() {
        UpdateUserRequestDto updateDto = new UpdateUserRequestDto();
        updateDto.setName("Updated Name");

        assertThrows(NoSuchElementException.class, () -> userService.updateUser(99999, updateDto));
    }

    @Test
    void deleteUser_ExistingUser_DeletesSuccessfully() {
        User user = new User();
        user.setName("To Delete");
        user.setEmail("delete@example.com");
        userRepository.save(user);

        assertDoesNotThrow(() -> userService.deleteUser(user.getId()));
        assertFalse(userRepository.findById(user.getId()).isPresent());
    }
}
