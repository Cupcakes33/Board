# Repository Layer 사용 시나리오

## UserRepository

- **로그인**: username으로 사용자를 조회한다 → `findByUsername(String username)`
- **회원가입 - username 중복 검증**: username이 이미 존재하는지 확인한다 → `existsByUsername(String username)`
- **회원가입 - email 중복 검증**: email이 이미 존재하는지 확인한다 → `existsByEmail(String email)`

---

## PostRepository

- **게시글 목록 조회 (최신순, 페이징)**: 최신 게시글 10개씩 조회한다 → `findAllByOrderByCreatedAtDesc(Pageable pageable)`
- **특정 사용자의 게시글 조회**: 특정 사용자가 작성한 게시글을 조회한다 → `findByAuthorIdOrderByCreatedAtDesc(Long authorId, Pageable pageable)`
- **게시글 검색 (제목 키워드)**: 제목에 키워드가 포함된 게시글을 검색한다 → `findByTitleContainingOrderByCreatedAtDesc(String keyword, Pageable pageable)`
- **게시글 상세 조회 (작성자 포함)**: 게시글과 작성자 정보를 함께 조회한다 (N+1 방지) → `findByIdWithAuthor(Long id)`
- **게시글 상세 조회 (모든 정보 포함)**: 게시글, 작성자, 댓글, 댓글 작성자를 한 번에 조회한다 (N+1 방지) → `findByIdWithDetails(Long id)`

---

## CommentRepository

- **게시글의 댓글 목록 조회**: 특정 게시글의 모든 댓글을 작성 시간 순으로 조회한다 → `findByPostIdOrderByCreatedAtAsc(Long postId)`
- **댓글 목록 조회 (작성자 포함)**: 댓글과 작성자 정보를 함께 조회한다 (N+1 방지) → `findByPostIdWithAuthor(Long postId)`
- **게시글의 댓글 개수 조회**: 특정 게시글의 댓글 개수를 조회한다 → `countByPostId(Long postId)`

---

## AttachmentRepository

- **게시글의 첨부파일 목록 조회**: 특정 게시글의 모든 첨부파일을 조회한다 → `findByPostId(Long postId)`
- **파일 다운로드**: 저장된 파일명으로 첨부파일 정보를 조회한다 → `findByStoredFilename(String storedFilename)`
- **게시글의 첨부파일 개수 조회**: 특정 게시글의 첨부파일 개수를 조회한다 → `countByPostId(Long postId)`
