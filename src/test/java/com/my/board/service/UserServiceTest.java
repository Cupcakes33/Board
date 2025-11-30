package com.my.board.service;

import com.my.board.dto.user.UserCreateRequest;
import com.my.board.dto.user.UserResponse;
import com.my.board.entity.User;
import com.my.board.exception.DuplicateResourceException;
import com.my.board.exception.ResourceNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Test
    @DisplayName("사용자 생성 성공")
    void createUser_Success() {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();

        // when
        UserResponse response = userService.createUser(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getRole()).isEqualTo(User.Role.USER);
    }

    @Test
    @DisplayName("중복 사용자명으로 생성 시 예외 발생")
    void createUser_DuplicateUsername_ThrowsException() {
        // given
        UserCreateRequest request1 = UserCreateRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test1@example.com")
                .build();

        UserCreateRequest request2 = UserCreateRequest.builder()
                .username("testuser")
                .password("password456")
                .email("test2@example.com")
                .build();

        userService.createUser(request1);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request2))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("중복 이메일로 생성 시 예외 발생")
    void createUser_DuplicateEmail_ThrowsException() {
        // given
        UserCreateRequest request1 = UserCreateRequest.builder()
                .username("testuser1")
                .password("password123")
                .email("test@example.com")
                .build();

        UserCreateRequest request2 = UserCreateRequest.builder()
                .username("testuser2")
                .password("password456")
                .email("test@example.com")
                .build();

        userService.createUser(request1);

        // when & then
        assertThatThrownBy(() -> userService.createUser(request2))
                .isInstanceOf(DuplicateResourceException.class);
    }

    @Test
    @DisplayName("ID로 사용자 조회 성공")
    void getUserById_Success() {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
        UserResponse created = userService.createUser(request);

        // when
        UserResponse found = userService.getUserById(created.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("존재하지 않는 ID로 조회 시 예외 발생")
    void getUserById_NotFound_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> userService.getUserById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("사용자명으로 조회 성공")
    void getUserByUsername_Success() {
        // given
        UserCreateRequest request = UserCreateRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
        userService.createUser(request);

        // when
        UserResponse found = userService.getUserByUsername("testuser");

        // then
        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("testuser");
    }
}
