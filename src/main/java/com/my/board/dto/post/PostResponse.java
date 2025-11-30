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
public class PostResponse {

    private Long id;
    private String title;
    private String content;
    private Long viewCount;
    private UserResponse author;
    private int commentCount;
    private int attachmentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponse from(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .viewCount(post.getViewCount())
                .author(UserResponse.from(post.getAuthor()))
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .attachmentCount(post.getAttachments() != null ? post.getAttachments().size() : 0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
