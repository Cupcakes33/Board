package com.my.board.service;

import com.my.board.dto.post.PostCreateRequest;
import com.my.board.dto.post.PostListResponse;
import com.my.board.dto.post.PostResponse;
import com.my.board.dto.post.PostUpdateRequest;
import com.my.board.entity.Post;
import com.my.board.entity.User;
import com.my.board.exception.ResourceNotFoundException;
import com.my.board.exception.UnauthorizedException;
import com.my.board.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final UserService userService;

    @Transactional
    public PostResponse createPost(PostCreateRequest request, Long authorId) {
        User author = userService.findUserEntityById(authorId);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .viewCount(0L)
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    @Transactional
    public PostResponse createPost(PostCreateRequest request, String username) {
        User author = userService.findUserEntityByUsername(username);

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .author(author)
                .viewCount(0L)
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponse.from(savedPost);
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse getPostByIdWithViewIncrement(Long id) {
        Post post = postRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        post.incrementViewCount();
        return PostResponse.from(post);
    }

    public PostResponse getPostWithDetails(Long id) {
        Post post = postRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
        return PostResponse.from(post);
    }

    public Page<PostListResponse> getAllPosts(Pageable pageable) {
        return postRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(PostListResponse::from);
    }

    public Page<PostListResponse> getPostsByAuthor(Long authorId, Pageable pageable) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(authorId, pageable)
                .map(PostListResponse::from);
    }

    public Page<PostListResponse> searchPostsByTitle(String keyword, Pageable pageable) {
        return postRepository.findByTitleContainingOrderByCreatedAtDesc(keyword, pageable)
                .map(PostListResponse::from);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, Long userId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("게시글을 수정할 권한이 없습니다.");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return PostResponse.from(post);
    }

    @Transactional
    public PostResponse updatePost(Long postId, PostUpdateRequest request, String username) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("게시글을 수정할 권한이 없습니다.");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());

        return PostResponse.from(post);
    }

    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    @Transactional
    public void deletePost(Long postId, String username) {
        Post post = postRepository.findByIdWithAuthor(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", postId));

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("게시글을 삭제할 권한이 없습니다.");
        }

        postRepository.delete(post);
    }

    public Post findPostEntityById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "id", id));
    }
}
