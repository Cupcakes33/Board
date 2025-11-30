package com.my.board.dto.attachment;

import com.my.board.entity.Attachment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentResponse {

    private Long id;
    private String originalFilename;
    private String storedFilename;
    private Long fileSize;
    private String contentType;
    private Long postId;
    private LocalDateTime createdAt;

    public static AttachmentResponse from(Attachment attachment) {
        return AttachmentResponse.builder()
                .id(attachment.getId())
                .originalFilename(attachment.getOriginalFilename())
                .storedFilename(attachment.getStoredFilename())
                .fileSize(attachment.getFileSize())
                .contentType(attachment.getContentType())
                .postId(attachment.getPost().getId())
                .createdAt(attachment.getCreatedAt())
                .build();
    }
}
