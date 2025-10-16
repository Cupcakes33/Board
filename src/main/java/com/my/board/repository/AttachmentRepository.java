package com.my.board.repository;

import com.my.board.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    List<Attachment> findByPostId(Long postId);

    Optional<Attachment> findByStoredFilename(String storedFilename);

    long countByPostId(Long postId);
}
