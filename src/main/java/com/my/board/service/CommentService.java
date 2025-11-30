package com.my.board.service;

import com.my.board.dto.comment.CommentCreateRequest;
import com.my.board.dto.comment.CommentResponse;
import com.my.board.dto.comment.CommentUpdateRequest;
import com.my.board.entity.Comment;
import com.my.board.entity.Post;
import com.my.board.entity.User;
import com.my.board.exception.ResourceNotFoundException;
import com.my.board.exception.UnauthorizedException;
import com.my.board.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;

    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, Long authorId) {
        Post post = postService.findPostEntityById(postId);
        User author = userService.findUserEntityById(authorId);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }

    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest request, String username) {
        Post post = postService.findPostEntityById(postId);
        User author = userService.findUserEntityByUsername(username);

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return CommentResponse.from(savedComment);
    }

    public CommentResponse getCommentById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", id));
        return CommentResponse.from(comment);
    }

    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostIdWithAuthor(postId).stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    public long getCommentCountByPostId(Long postId) {
        return commentRepository.countByPostId(postId);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("댓글을 수정할 권한이 없습니다.");
        }

        comment.setContent(request.getContent());
        return CommentResponse.from(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteComment(Long commentId, String username) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "id", commentId));

        if (!comment.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
    }
}
