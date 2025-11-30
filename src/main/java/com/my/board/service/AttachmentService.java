package com.my.board.service;

import com.my.board.dto.attachment.AttachmentResponse;
import com.my.board.entity.Attachment;
import com.my.board.entity.Post;
import com.my.board.exception.ResourceNotFoundException;
import com.my.board.exception.UnauthorizedException;
import com.my.board.repository.AttachmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AttachmentService {

    private final AttachmentRepository attachmentRepository;
    private final PostService postService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public AttachmentResponse uploadFile(Long postId, MultipartFile file, Long userId) throws IOException {
        Post post = postService.findPostEntityById(postId);

        if (!post.getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("파일을 업로드할 권한이 없습니다.");
        }

        return saveFile(post, file);
    }

    @Transactional
    public AttachmentResponse uploadFile(Long postId, MultipartFile file, String username) throws IOException {
        Post post = postService.findPostEntityById(postId);

        if (!post.getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("파일을 업로드할 권한이 없습니다.");
        }

        return saveFile(post, file);
    }

    private AttachmentResponse saveFile(Post post, MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String storedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath);

        Attachment attachment = Attachment.builder()
                .originalFilename(originalFilename)
                .storedFilename(storedFilename)
                .filePath(filePath.toString())
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .post(post)
                .build();

        Attachment savedAttachment = attachmentRepository.save(attachment);
        return AttachmentResponse.from(savedAttachment);
    }

    public AttachmentResponse getAttachmentById(Long id) {
        Attachment attachment = attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", id));
        return AttachmentResponse.from(attachment);
    }

    public Attachment getAttachmentEntityById(Long id) {
        return attachmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", id));
    }

    public AttachmentResponse getAttachmentByStoredFilename(String storedFilename) {
        Attachment attachment = attachmentRepository.findByStoredFilename(storedFilename)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "storedFilename", storedFilename));
        return AttachmentResponse.from(attachment);
    }

    public List<AttachmentResponse> getAttachmentsByPostId(Long postId) {
        return attachmentRepository.findByPostId(postId).stream()
                .map(AttachmentResponse::from)
                .collect(Collectors.toList());
    }

    public long getAttachmentCountByPostId(Long postId) {
        return attachmentRepository.countByPostId(postId);
    }

    public byte[] downloadFile(Long attachmentId) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));

        Path filePath = Paths.get(attachment.getFilePath());
        return Files.readAllBytes(filePath);
    }

    @Transactional
    public void deleteAttachment(Long attachmentId, Long userId) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));

        if (!attachment.getPost().getAuthor().getId().equals(userId)) {
            throw new UnauthorizedException("파일을 삭제할 권한이 없습니다.");
        }

        deleteFileFromStorage(attachment);
        attachmentRepository.delete(attachment);
    }

    @Transactional
    public void deleteAttachment(Long attachmentId, String username) throws IOException {
        Attachment attachment = attachmentRepository.findById(attachmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Attachment", "id", attachmentId));

        if (!attachment.getPost().getAuthor().getUsername().equals(username)) {
            throw new UnauthorizedException("파일을 삭제할 권한이 없습니다.");
        }

        deleteFileFromStorage(attachment);
        attachmentRepository.delete(attachment);
    }

    private void deleteFileFromStorage(Attachment attachment) throws IOException {
        Path filePath = Paths.get(attachment.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
    }
}
