package com.my.board.service;

import com.my.board.dto.post.PostCreateRequest;
import com.my.board.dto.post.PostListResponse;
import com.my.board.dto.post.PostResponse;
import com.my.board.dto.post.PostUpdateRequest;
import com.my.board.dto.user.UserCreateRequest;
import com.my.board.dto.user.UserResponse;
import com.my.board.exception.ResourceNotFoundException;
import com.my.board.exception.UnauthorizedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class PostServiceTest {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    private UserResponse testUser;

    @BeforeEach
    void setUp() {
        UserCreateRequest userRequest = UserCreateRequest.builder()
                .username("testuser")
                .password("password123")
                .email("test@example.com")
                .build();
        testUser = userService.createUser(userRequest);
    }

    @Test
    @DisplayName("게시글 생성 성공")
    void createPost_Success() {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();

        // when
        PostResponse response = postService.createPost(request, testUser.getId());

        // then
        assertThat(response).isNotNull();
        assertThat(response.getTitle()).isEqualTo("테스트 제목");
        assertThat(response.getContent()).isEqualTo("테스트 내용");
        assertThat(response.getViewCount()).isEqualTo(0L);
        assertThat(response.getAuthor().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("게시글 조회 성공")
    void getPostById_Success() {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        PostResponse created = postService.createPost(request, testUser.getId());

        // when
        PostResponse found = postService.getPostById(created.getId());

        // then
        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(created.getId());
        assertThat(found.getTitle()).isEqualTo("테스트 제목");
    }

    @Test
    @DisplayName("게시글 조회 시 조회수 증가")
    void getPostByIdWithViewIncrement_Success() {
        // given
        PostCreateRequest request = PostCreateRequest.builder()
                .title("테스트 제목")
                .content("테스트 내용")
                .build();
        PostResponse created = postService.createPost(request, testUser.getId());

        // when
        PostResponse found = postService.getPostByIdWithViewIncrement(created.getId());

        // then
        assertThat(found.getViewCount()).isEqualTo(1L);
    }

    @Test
    @DisplayName("존재하지 않는 게시글 조회 시 예외 발생")
    void getPostById_NotFound_ThrowsException() {
        // when & then
        assertThatThrownBy(() -> postService.getPostById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("게시글 목록 조회 성공")
    void getAllPosts_Success() {
        // given
        for (int i = 1; i <= 5; i++) {
            PostCreateRequest request = PostCreateRequest.builder()
                    .title("테스트 제목 " + i)
                    .content("테스트 내용 " + i)
                    .build();
            postService.createPost(request, testUser.getId());
        }

        // when
        Page<PostListResponse> posts = postService.getAllPosts(PageRequest.of(0, 10));

        // then
        assertThat(posts.getContent()).hasSize(5);
    }

    @Test
    @DisplayName("제목으로 게시글 검색 성공")
    void searchPostsByTitle_Success() {
        // given
        PostCreateRequest request1 = PostCreateRequest.builder()
                .title("검색 테스트")
                .content("내용")
                .build();
        PostCreateRequest request2 = PostCreateRequest.builder()
                .title("다른 제목")
                .content("내용")
                .build();
        postService.createPost(request1, testUser.getId());
        postService.createPost(request2, testUser.getId());

        // when
        Page<PostListResponse> posts = postService.searchPostsByTitle("검색", PageRequest.of(0, 10));

        // then
        assertThat(posts.getContent()).hasSize(1);
        assertThat(posts.getContent().get(0).getTitle()).contains("검색");
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updatePost_Success() {
        // given
        PostCreateRequest createRequest = PostCreateRequest.builder()
                .title("원래 제목")
                .content("원래 내용")
                .build();
        PostResponse created = postService.createPost(createRequest, testUser.getId());

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        // when
        PostResponse updated = postService.updatePost(created.getId(), updateRequest, testUser.getId());

        // then
        assertThat(updated.getTitle()).isEqualTo("수정된 제목");
        assertThat(updated.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    @DisplayName("다른 사용자가 게시글 수정 시 예외 발생")
    void updatePost_Unauthorized_ThrowsException() {
        // given
        PostCreateRequest createRequest = PostCreateRequest.builder()
                .title("원래 제목")
                .content("원래 내용")
                .build();
        PostResponse created = postService.createPost(createRequest, testUser.getId());

        UserCreateRequest otherUserRequest = UserCreateRequest.builder()
                .username("otheruser")
                .password("password123")
                .email("other@example.com")
                .build();
        UserResponse otherUser = userService.createUser(otherUserRequest);

        PostUpdateRequest updateRequest = PostUpdateRequest.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .build();

        // when & then
        assertThatThrownBy(() -> postService.updatePost(created.getId(), updateRequest, otherUser.getId()))
                .isInstanceOf(UnauthorizedException.class);
    }

    @Test
    @DisplayName("게시글 삭제 성공")
    void deletePost_Success() {
        // given
        PostCreateRequest createRequest = PostCreateRequest.builder()
                .title("삭제할 제목")
                .content("삭제할 내용")
                .build();
        PostResponse created = postService.createPost(createRequest, testUser.getId());

        // when
        postService.deletePost(created.getId(), testUser.getId());

        // then
        assertThatThrownBy(() -> postService.getPostById(created.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
