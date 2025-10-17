package com.my.board.entity;

import com.my.board.entity.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "attachments",
    indexes = {
        @Index(name = "idx_post_id", columnList = "post_id"),
        @Index(name = "idx_stored_filename", columnList = "stored_filename")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "post")
public class Attachment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFilename;

    @Column(nullable = false, unique = true)
    private String storedFilename;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    private Long fileSize;

    @Column(nullable = false)
    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

}
