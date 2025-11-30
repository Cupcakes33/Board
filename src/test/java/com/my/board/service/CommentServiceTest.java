package com.my.board.service;

import com.my.board.dto.comment.CommentCreateRequest;
import com.my.board.dto.comment.CommentResponse;
import com.my.board.dto.comment.CommentUpdateRequest;
import com.my.board.dto.post.PostCreateRequest;
import com.my.board.dto.post.PostResponse;
import com.my.board.dto.user.UserCreateRequest;
import com.my.board.dto.user.UserResponse;
import com.my.board.exception.ResourceNotFoundException;
import com.my.board.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private UserResponse testUser;
    private PostResponse testPost;

    @BeforeEach
    void setUp() {
        UserCreateRequest userRequest = UserCreateRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
        testUser = userService.createUser(userRequest);

        PostCreateRequest postRequest = PostCreateRequest.builder()
                .title("테스트 게시글")
                .content("테스트 내용")
                .build();
        testPost = postService.createPost(postRequest, testUser.getId());
    }

    @Test
    @DisplayName("댓글 생성 성공")
    void createComment_Success() {
        // given
        CommentCreateRequest request = CommentCreateRequest.builder()
                .content("테스트 댓글")
                .build();

        // when
        CommentResponse response = commentService.createComment(testPost.getId(), request, testUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContent()).isEqualTo("테스트 댓글");
        assertThat(response.getPostId()).isEqualTo(testPost.getId());
        assertThat(response.getAuthor().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("게시글별 댓글 목록 조회 성공")
    void getCommentsByPostId_Success() {
        // given
        for (int i = 1; i <= 3; i++) {
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("테스트 댓글 " + i)
                    .build();
            commentService.createComment(testPost.getId(), request, testUser.getId());
        }

        // when
        List<CommentResponse> comments = commentService.getCommentsByPostId(testPost.getId());

        // then
        assertThat(comments).hasSize(3);
    }

    @Test
    @DisplayName("댓글 수정 성공")
    void updateComment_Success() {
        // given
        CommentCreateRequest createRequest = CommentCreateRequest.builder()
                .content("원래 댓글")
                .build();
        CommentResponse created = commentService.createComment(testPost.getId(), createRequest, testUser.getId());

        CommentUpdateRequest updateRequest = CommentUpdateRequest.builder()
                .content("수정된 댓글")
                .build();

        // when
        CommentResponse updated = commentService.updateComment(created.getId(), updateRequest, testUser.getId());

        // then
        assertThat(updated.getContent()).isEqualTo("수정된 댓글");
    }

    @Test
    @DisplayName("다른 사용자가 댓글 수정 시 예외 발생")
    void updateComment_Unauthorized_ThrowsException() {
        // given
        CommentCreateRequest createRequest = CommentCreateRequest.builder()
                .content("원래 댓글")
                .build();
        CommentResponse created = commentService.createComment(testPost.getId(), createRequest, testUser.getId());

        UserCreateRequest otherUserRequest = UserCreateRequest.builder()
                .username("otheruser")
                .password("password123")
                .email("other@example.com")
                .build();
        UserResponse otherUser = userService.createUser(otherUserRequest);

        CommentUpdateRequest updateRequest = CommentUpdateRequest.builder()
                .content("수정된 댓글")
                .build();

        // when & then
        assertThatThrownBy(() -> commentService.updateComment(created.getId(), updateRequest, otherUser.getId()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    void deleteComment_Success() {
        // given
        CommentCreateRequest createRequest = CommentCreateRequest.builder()
                .content("삭제할 댓글")
                .build();
        CommentResponse created = commentService.createComment(testPost.getId(), createRequest, testUser.getId());

        // when
        commentService.deleteComment(created.getId(), testUser.getId());

        // then
        assertThatThrownBy(() -> commentService.getCommentById(created.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("댓글 개수 조회 성공")
    void getCommentCountByPostId_Success() {
        // given
        for (int i = 1; i <= 3; i++) {
            CommentCreateRequest request = CommentCreateRequest.builder()
                    .content("테스트 댓글 " + i)
                    .build();
            commentService.createComment(testPost.getId(), request, testUser.getId());
        }

        // when
        long count = commentService.getCommentCountByPostId(testPost.getId());

        // then
        assertThat(count).isEqualTo(3);
    }
}
