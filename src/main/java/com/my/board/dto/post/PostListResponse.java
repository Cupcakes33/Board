package com.my.board.dto.post;

import com.my.board.dto.user.UserResponse;
import com.my.board.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostListResponse {

    private Long id;
    private String title;
    private Long viewCount;
    private String authorUsername;
    private int commentCount;
    private LocalDateTime createdAt;

    public static PostListResponse from(Post post) {
        return PostListResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .viewCount(post.getViewCount())
                .authorUsername(post.getAuthor().getUsername())
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .createdAt(post.getCreatedAt())
                .build();
    }
}
