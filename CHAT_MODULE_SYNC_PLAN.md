# 📋 Chat Module Synchronization Plan

**Source:** `chat-service/` → **Target:** `modules/chat/`  
**Focus:** WebSocket, Redis, Entity Relationships (Bỏ qua User/Authentication)  
**Date:** 2024

---

## 🎯 Mục tiêu

Đồng bộ chat-service vào SE-HUB-BE project với các cải thiện:
1. ✅ Room-based broadcasting (thay vì getAllClients)
2. ✅ Pagination cho message retrieval
3. ✅ Concurrency handling khi create conversation
4. ✅ Group chat support (hash và participant list)
5. ✅ Heartbeat/cleanup session Redis
6. ✅ Validation DTO (participantIds size ≥2)
7. ✅ Profile sync mechanism

---

## 📁 Cấu trúc Module Chat

```
modules/chat/
├── constant/
│   ├── ChatConstants.java              (table names, column names, definitions)
│   ├── ChatMessageConstants.java       (message constants)
│   ├── ChatErrorCodeConstants.java    (error code constants)
│   ├── SocketEvent.java                (Socket.IO event names)
│   └── RedisKeys.java                  (Redis key patterns)
├── entity/
│   ├── Conversation.java               (MongoDB document)
│   ├── ChatMessage.java                (MongoDB document)
│   └── ParticipantInfo.java            (embedded document)
├── enums/
│   └── ConversationType.java           (DIRECT, GROUP)
├── repository/
│   ├── ConversationRepository.java     (MongoDB repository)
│   └── ChatMessageRepository.java      (MongoDB repository)
├── dto/
│   ├── request/
│   │   ├── CreateConversationRequest.java
│   │   ├── CreateChatMessageRequest.java
│   │   └── GetMessagesRequest.java     (với pagination)
│   └── response/
│       ├── ConversationResponse.java
│       ├── ChatMessageResponse.java
│       └── ParticipantInfoResponse.java
├── mapper/
│   ├── ConversationMapper.java         (MapStruct)
│   └── ChatMessageMapper.java          (MapStruct)
├── service/
│   ├── api/
│   │   ├── ConversationService.java
│   │   ├── ChatMessageService.java
│   │   └── SessionService.java
│   └── impl/
│       ├── ConversationServiceImpl.java
│       ├── ChatMessageServiceImpl.java
│       └── SessionServiceImpl.java
├── controller/
│   ├── ConversationController.java
│   └── ChatMessageController.java
├── config/
│   ├── NettySocketIOConfig.java        (Socket.IO configuration)
│   └── SocketHandler.java               (Socket.IO event handlers)
├── exception/
│   ├── ChatErrorCode.java               (Error code enum)
│   └── ChatException.java               (Custom exception)
└── package-info.java
```

---

## 🔄 Phase 1: Setup & Constants (Day 1)

### 1.1 Tạo Constants Files

**File:** `constant/ChatConstants.java`
```java
public class ChatConstants {
    // MongoDB Collections
    public static final String COLLECTION_CONVERSATION = "conversation";
    public static final String COLLECTION_CHAT_MESSAGE = "chat-message";
    
    // Field Definitions
    public static final String CONVERSATION_TYPE_DEFINITION = "VARCHAR(20)";
    public static final String PARTICIPANTS_HASH_DEFINITION = "VARCHAR(64)";
    public static final String MESSAGE_DEFINITION = "TEXT";
    
    // Validation
    public static final int MIN_PARTICIPANTS = 2;
    public static final int MAX_PARTICIPANTS = 100; // For group chat
    public static final int MESSAGE_MAX_LENGTH = 5000;
}
```

**File:** `constant/ChatMessageConstants.java`
```java
public class ChatMessageConstants {
    // API Response Messages
    public static final String API_MESSAGE_CREATED_SUCCESS = "Message created successfully";
    public static final String API_MESSAGES_RETRIEVED_SUCCESS = "Messages retrieved successfully";
    public static final String API_CONVERSATION_CREATED_SUCCESS = "Conversation created successfully";
    public static final String API_CONVERSATIONS_RETRIEVED_SUCCESS = "Conversations retrieved successfully";
    
    // Error Messages
    public static final String CONVERSATION_NOT_FOUND_MESSAGE = "Conversation not found";
    public static final String NOT_PARTICIPANT_MESSAGE = "You are not a participant of this conversation";
    public static final String CONVERSATION_ALREADY_EXISTS_MESSAGE = "Conversation already exists";
    public static final String INVALID_PARTICIPANT_COUNT_MESSAGE = "Conversation must have at least 2 participants";
    public static final String MESSAGE_TOO_LONG_MESSAGE = "Message exceeds maximum length";
}
```

**File:** `constant/ChatErrorCodeConstants.java`
```java
public class ChatErrorCodeConstants {
    public static final String CONVERSATION_NOT_FOUND = "CONVERSATION_NOT_FOUND";
    public static final String NOT_PARTICIPANT = "NOT_PARTICIPANT";
    public static final String CONVERSATION_ALREADY_EXISTS = "CONVERSATION_ALREADY_EXISTS";
    public static final String INVALID_PARTICIPANT_COUNT = "INVALID_PARTICIPANT_COUNT";
    public static final String MESSAGE_TOO_LONG = "MESSAGE_TOO_LONG";
    public static final String SESSION_NOT_FOUND = "SESSION_NOT_FOUND";
}
```

**File:** `constant/SocketEvent.java` (giữ nguyên từ chat-service)
**File:** `constant/RedisKeys.java` (giữ nguyên từ chat-service)

### 1.2 Tạo Exception Files

**File:** `exception/ChatErrorCode.java`
- Pattern giống `InteractionErrorCode`
- Include: `CONVERSATION_NOT_FOUND`, `NOT_PARTICIPANT`, `CONVERSATION_ALREADY_EXISTS`, `INVALID_PARTICIPANT_COUNT`, `MESSAGE_TOO_LONG`, `SESSION_NOT_FOUND`

**File:** `exception/ChatException.java`
- Extend từ `AppException`
- Pattern giống `InteractionException`

### 1.3 Tạo Enum

**File:** `enums/ConversationType.java`
```java
public enum ConversationType {
    DIRECT,  // 1-on-1 chat
    GROUP     // Group chat
}
```

---

## 🔄 Phase 2: Entities & Repositories (Day 1-2)

### 2.1 Entity: ParticipantInfo

**File:** `entity/ParticipantInfo.java`
- **Thay đổi:** Bỏ `@Data`, dùng `@Getter/@Setter`
- **Thay đổi:** Chỉ lưu `userId` (không lưu username, fullname)
- **Lý do:** Profile info sẽ fetch từ Profile entity khi cần, tránh data inconsistency

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantInfo {
    String userId;  // Only store userId, fetch profile when needed
}
```

### 2.2 Entity: Conversation

**File:** `entity/Conversation.java`
- **Thay đổi:** Thêm enum `ConversationType`
- **Thay đổi:** Thêm validation cho participant count
- **Thay đổi:** Composite index cho participantsHash + type
- **Thay đổi:** Bỏ `@Data`, dùng `@Getter/@Setter`

```java
@Document(collection = ChatConstants.COLLECTION_CONVERSATION)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Conversation {
    @MongoId
    String conversationId;
    
    @Indexed
    ConversationType type;  // DIRECT or GROUP
    
    @Indexed(unique = true)
    String participantsHash;
    
    List<ParticipantInfo> participants;  // Only userIds
    
    Instant createdDate;
    Instant modifiedDate;
}
```

### 2.3 Entity: ChatMessage

**File:** `entity/ChatMessage.java`
- **Thay đổi:** Chỉ lưu `senderId` (không lưu full ParticipantInfo)
- **Thay đổi:** Composite index cho conversationId + createDate
- **Thay đổi:** Bỏ `@Data`, dùng `@Getter/@Setter`

```java
@Document(collection = ChatConstants.COLLECTION_CHAT_MESSAGE)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessage {
    @MongoId
    String messageId;
    
    @Indexed
    String conversationId;
    
    @Indexed
    String senderId;  // Only userId, fetch profile when needed
    
    String message;
    
    @Indexed
    Instant createDate;
}
```

### 2.4 Repository: ConversationRepository

**File:** `repository/ConversationRepository.java`
- **Thay đổi:** Thêm method với pagination
- **Thay đổi:** Thêm method tìm conversation by type
- **Thay đổi:** Optimize query với projection

```java
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {
    Optional<Conversation> findByParticipantsHash(String participantsHash);
    
    @Query("{'participants.userId' : ?0}")
    Page<Conversation> findByParticipantIdsContains(String userId, Pageable pageable);
    
    @Query("{'participants.userId' : ?0, 'type' : ?1}")
    Page<Conversation> findByParticipantIdsContainsAndType(String userId, ConversationType type, Pageable pageable);
    
    boolean existsByParticipantsHash(String participantsHash);
}
```

### 2.5 Repository: ChatMessageRepository

**File:** `repository/ChatMessageRepository.java`
- **Thay đổi:** Thêm pagination support
- **Thay đổi:** Thêm cursor-based pagination cho real-time chat
- **Thay đổi:** Thêm method count messages

```java
@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    Page<ChatMessage> findByConversationIdOrderByCreateDateDesc(String conversationId, Pageable pageable);
    
    // Cursor-based pagination for real-time chat
    List<ChatMessage> findByConversationIdAndCreateDateBeforeOrderByCreateDateDesc(
        String conversationId, Instant beforeDate, Pageable pageable);
    
    long countByConversationId(String conversationId);
}
```

---

## 🔄 Phase 3: DTOs & Mappers (Day 2)

### 3.1 Request DTOs

**File:** `dto/request/CreateConversationRequest.java`
- **Thay đổi:** Validation `participantIds.size() >= 2`
- **Thay đổi:** Thêm `ConversationType`
- **Thay đổi:** Validation không cho phép self-conversation

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateConversationRequest {
    @NotNull(message = "Conversation type is required")
    ConversationType type;
    
    @NotNull(message = "Participant IDs are required")
    @Size(min = ChatConstants.MIN_PARTICIPANTS, 
          max = ChatConstants.MAX_PARTICIPANTS,
          message = ChatErrorCodeConstants.INVALID_PARTICIPANT_COUNT)
    List<String> participantIds;
}
```

**File:** `dto/request/CreateChatMessageRequest.java`
- **Thay đổi:** Thêm validation message length
- **Thay đổi:** Thêm validation conversationId format

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateChatMessageRequest {
    @NotBlank(message = "Conversation ID is required")
    String conversationId;
    
    @NotBlank(message = "Message cannot be blank")
    @Size(max = ChatConstants.MESSAGE_MAX_LENGTH,
          message = ChatErrorCodeConstants.MESSAGE_TOO_LONG)
    String message;
}
```

**File:** `dto/request/GetMessagesRequest.java` (NEW)
```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetMessagesRequest {
    @NotBlank(message = "Conversation ID is required")
    String conversationId;
    
    // Pagination
    int page = 1;
    int pageSize = 50;
    String sortField = "createDate";
    String sortDirection = "DESC";
    
    // Optional: Cursor-based pagination
    Instant beforeDate;  // For cursor-based pagination
}
```

### 3.2 Response DTOs

**File:** `dto/response/ParticipantInfoResponse.java` (NEW)
- Fetch từ Profile entity khi cần

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ParticipantInfoResponse {
    String userId;
    String username;      // From Profile
    String fullName;      // From Profile
    String avatarUrl;     // From Profile
}
```

**File:** `dto/response/ChatMessageResponse.java`
- **Thay đổi:** Dùng `ParticipantInfoResponse` thay vì `ParticipantInfo`
- **Thay đổi:** Thêm `senderInfo` (fetch từ Profile)

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageResponse {
    String messageId;
    String conversationId;
    Boolean isMe;
    String message;
    ParticipantInfoResponse sender;  // Fetched from Profile
    Instant createDate;
}
```

**File:** `dto/response/ConversationResponse.java`
- **Thay đổi:** Dùng `ParticipantInfoResponse`
- **Thay đổi:** Thêm `lastMessage` (optional)
- **Thay đổi:** Thêm `unreadCount` (optional)

```java
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationResponse {
    String conversationId;
    ConversationType type;
    String conversationName;  // For DIRECT: other participant name, for GROUP: group name
    List<ParticipantInfoResponse> participants;  // Fetched from Profile
    Instant createdDate;
    Instant modifiedDate;
    
    // Optional fields
    ChatMessageResponse lastMessage;
    Long unreadCount;
}
```

### 3.3 Mappers

**File:** `mapper/ConversationMapper.java`
- Map Conversation → ConversationResponse
- Fetch Profile info cho participants
- Map CreateConversationRequest → Conversation

**File:** `mapper/ChatMessageMapper.java`
- Map ChatMessage → ChatMessageResponse
- Fetch Profile info cho sender
- Map CreateChatMessageRequest → ChatMessage

---

## 🔄 Phase 4: Services - Core Logic (Day 3-4)

### 4.1 SessionService với Heartbeat

**File:** `service/impl/SessionServiceImpl.java`
- **Cải thiện:** Thêm heartbeat mechanism
- **Cải thiện:** Thêm cleanup stale sessions
- **Cải thiện:** Batch operations

```java
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SessionServiceImpl implements SessionService {
    StringRedisTemplate redisTemplate;
    
    private static final Duration SESSION_TTL = Duration.ofHours(24);
    private static final Duration HEARTBEAT_INTERVAL = Duration.ofMinutes(5);
    
    @Override
    public void saveSession(String userId, String sessionId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        redisTemplate.opsForSet().add(key, sessionId);
        redisTemplate.expire(key, SESSION_TTL);
        
        // Save heartbeat timestamp
        String heartbeatKey = RedisKeys.USER_SESSION_HEARTBEAT_KEY + userId + ":" + sessionId;
        redisTemplate.opsForValue().set(heartbeatKey, String.valueOf(Instant.now().toEpochMilli()), HEARTBEAT_INTERVAL);
    }
    
    @Override
    public void updateHeartbeat(String userId, String sessionId) {
        String heartbeatKey = RedisKeys.USER_SESSION_HEARTBEAT_KEY + userId + ":" + sessionId;
        redisTemplate.opsForValue().set(heartbeatKey, String.valueOf(Instant.now().toEpochMilli()), HEARTBEAT_INTERVAL);
    }
    
    @Override
    public Set<String> getSessions(String userId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        return redisTemplate.opsForSet().members(key);
    }
    
    @Override
    public void removeSession(String userId, String sessionId) {
        String key = RedisKeys.USER_SESSION_KEY + userId;
        redisTemplate.opsForSet().remove(key, sessionId);
        
        // Remove heartbeat
        String heartbeatKey = RedisKeys.USER_SESSION_HEARTBEAT_KEY + userId + ":" + sessionId;
        redisTemplate.delete(heartbeatKey);
    }
    
    @Scheduled(fixedRate = 60000) // Every minute
    public void cleanupStaleSessions() {
        // Cleanup sessions without heartbeat
        // Implementation: Scan all heartbeat keys, remove expired ones
    }
}
```

**File:** `constant/RedisKeys.java` (UPDATE)
```java
public class RedisKeys {
    public static final String USER_SESSION_KEY = "user:session:";
    public static final String USER_SESSION_HEARTBEAT_KEY = "user:session:heartbeat:";
    public static final String CONVERSATION_ROOM_KEY = "conversation:room:";  // For room-based broadcasting
}
```

### 4.2 ConversationService với Concurrency & Group Chat

**File:** `service/impl/ConversationServiceImpl.java`
- **Cải thiện:** Retry mechanism khi duplicate hash
- **Cải thiện:** Support group chat (nhiều hơn 2 participants)
- **Cải thiện:** Fetch Profile info cho participants
- **Cải thiện:** Transaction handling

```java
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ConversationServiceImpl implements ConversationService {
    ConversationRepository conversationRepository;
    ConversationMapper conversationMapper;
    ProfileRepository profileRepository;  // Fetch Profile info
    
    @Override
    @Transactional
    public ConversationResponse createConversation(CreateConversationRequest request) {
        log.debug("ConversationServiceImpl_createConversation_Creating conversation for user: {}", AuthUtils.getCurrentUserId());
        
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Validate participant count
        List<String> participantIds = new ArrayList<>(request.getParticipantIds());
        if (!participantIds.contains(currentUserId)) {
            participantIds.add(currentUserId);
        }
        
        if (participantIds.size() < ChatConstants.MIN_PARTICIPANTS) {
            throw ChatErrorCode.INVALID_PARTICIPANT_COUNT.toException();
        }
        
        // Prevent self-conversation for DIRECT type
        if (request.getType() == ConversationType.DIRECT && participantIds.size() != 2) {
            throw ChatErrorCode.INVALID_PARTICIPANT_COUNT.toException();
        }
        
        // Generate hash
        String participantsHash = generateParticipantHash(participantIds);
        
        // Try to find existing conversation with retry mechanism
        Conversation conversation = findOrCreateConversation(participantsHash, request, participantIds, currentUserId);
        
        return conversationMapper.toConversationResponse(conversation, currentUserId);
    }
    
    private Conversation findOrCreateConversation(String participantsHash, 
                                                  CreateConversationRequest request,
                                                  List<String> participantIds,
                                                  String currentUserId) {
        int maxRetries = 3;
        int retryCount = 0;
        
        while (retryCount < maxRetries) {
            try {
                // Try to find existing
                Optional<Conversation> existing = conversationRepository.findByParticipantsHash(participantsHash);
                if (existing.isPresent()) {
                    return existing.get();
                }
                
                // Create new conversation
                List<ParticipantInfo> participants = participantIds.stream()
                    .map(userId -> ParticipantInfo.builder().userId(userId).build())
                    .toList();
                
                Conversation newConversation = Conversation.builder()
                    .type(request.getType())
                    .participantsHash(participantsHash)
                    .participants(participants)
                    .createdDate(Instant.now())
                    .modifiedDate(Instant.now())
                    .build();
                
                return conversationRepository.save(newConversation);
                
            } catch (DuplicateKeyException e) {
                // Retry if duplicate key (concurrent creation)
                retryCount++;
                if (retryCount >= maxRetries) {
                    log.error("ConversationServiceImpl_findOrCreateConversation_Failed after {} retries", maxRetries);
                    throw ChatErrorCode.CONVERSATION_ALREADY_EXISTS.toException();
                }
                try {
                    Thread.sleep(100 * retryCount); // Exponential backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw ChatErrorCode.CONVERSATION_ALREADY_EXISTS.toException();
                }
            }
        }
        
        throw ChatErrorCode.CONVERSATION_ALREADY_EXISTS.toException();
    }
    
    @Override
    public PagingResponse<ConversationResponse> getConversations(PagingRequest request) {
        String currentUserId = AuthUtils.getCurrentUserId();
        
        Pageable pageable = PageRequest.of(
            request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
            request.getPageSize(),
            PagingUtil.createSort(request)
        );
        
        Page<Conversation> conversations = conversationRepository.findByParticipantIdsContains(currentUserId, pageable);
        
        return PagingResponse.<ConversationResponse>builder()
            .currentPage(conversations.getNumber())
            .totalPages(conversations.getTotalPages())
            .pageSize(conversations.getSize())
            .totalElement(conversations.getTotalElements())
            .data(conversations.getContent().stream()
                .map(conv -> conversationMapper.toConversationResponse(conv, currentUserId))
                .toList())
            .build();
    }
    
    private String generateParticipantHash(List<String> userIds) {
        // Sort to ensure consistent hash
        List<String> sorted = new ArrayList<>(userIds);
        Collections.sort(sorted);
        
        String raw = String.join("_", sorted);
        
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Hex.encodeHexString(hash);
        } catch (Exception e) {
            log.error("ConversationServiceImpl_generateParticipantHash_Error generating hash", e);
            throw ChatErrorCode.GENERATION_ERROR.toException();
        }
    }
}
```

### 4.3 ChatMessageService với Room-based Broadcasting & Pagination

**File:** `service/impl/ChatMessageServiceImpl.java`
- **Cải thiện:** Room-based broadcasting (thay vì getAllClients)
- **Cải thiện:** Pagination cho messages
- **Cải thiện:** Fetch Profile info cho sender
- **Cải thiện:** Transaction handling

```java
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {
    ConversationRepository conversationRepository;
    ChatMessageRepository chatMessageRepository;
    ChatMessageMapper chatMessageMapper;
    ProfileRepository profileRepository;
    SocketIOServer socketIOServer;
    SessionService sessionService;
    
    @Override
    @Transactional
    public ChatMessageResponse createMessage(CreateChatMessageRequest request) {
        log.debug("ChatMessageServiceImpl_createMessage_Creating message for conversation: {}", request.getConversationId());
        
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Validate participant
        Conversation conversation = validateParticipant(request.getConversationId(), currentUserId);
        
        // Create message
        ChatMessage message = ChatMessage.builder()
            .conversationId(request.getConversationId())
            .senderId(currentUserId)
            .message(request.getMessage())
            .createDate(Instant.now())
            .build();
        
        message = chatMessageRepository.save(message);
        
        // Update conversation modified date
        conversation.setModifiedDate(Instant.now());
        conversationRepository.save(conversation);
        
        // Broadcast to room (room-based broadcasting)
        broadcastToRoom(request.getConversationId(), message);
        
        // Fetch Profile info for response
        return chatMessageMapper.toChatMessageResponse(message, currentUserId);
    }
    
    @Override
    public PagingResponse<ChatMessageResponse> getMessages(GetMessagesRequest request) {
        log.debug("ChatMessageServiceImpl_getMessages_Fetching messages for conversation: {}", request.getConversationId());
        
        String currentUserId = AuthUtils.getCurrentUserId();
        
        // Validate participant
        validateParticipant(request.getConversationId(), currentUserId);
        
        // Pagination
        Pageable pageable = PageRequest.of(
            request.getPage() - GlobalVariable.PAGE_SIZE_INDEX,
            request.getPageSize(),
            Sort.by(Sort.Direction.fromString(request.getSortDirection()), request.getSortField())
        );
        
        Page<ChatMessage> messages;
        
        // Cursor-based pagination if beforeDate provided
        if (request.getBeforeDate() != null) {
            messages = chatMessageRepository.findByConversationIdAndCreateDateBeforeOrderByCreateDateDesc(
                request.getConversationId(), request.getBeforeDate(), pageable);
        } else {
            messages = chatMessageRepository.findByConversationIdOrderByCreateDateDesc(
                request.getConversationId(), pageable);
        }
        
        return PagingResponse.<ChatMessageResponse>builder()
            .currentPage(messages.getNumber())
            .totalPages(messages.getTotalPages())
            .pageSize(messages.getSize())
            .totalElement(messages.getTotalElements())
            .data(messages.getContent().stream()
                .map(msg -> chatMessageMapper.toChatMessageResponse(msg, currentUserId))
                .toList())
            .build();
    }
    
    /**
     * Room-based broadcasting - chỉ broadcast đến clients trong room
     * Virtual Thread Best Practice: Uses synchronous blocking I/O operations.
     */
    private void broadcastToRoom(String conversationId, ChatMessage message) {
        // Get all participants
        Conversation conversation = conversationRepository.findById(conversationId)
            .orElseThrow(() -> ChatErrorCode.CONVERSATION_NOT_FOUND.toException());
        
        List<String> participantIds = conversation.getParticipants().stream()
            .map(ParticipantInfo::getUserId)
            .toList();
        
        // Get all active sessions for participants
        Set<String> sessionIds = participantIds.stream()
            .map(sessionService::getSessions)
            .filter(Objects::nonNull)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
        
        // Broadcast to room (room-based)
        String roomName = "conversation:" + conversationId;
        
        socketIOServer.getRoomOperations(roomName).getClients().forEach(client -> {
            if (sessionIds.contains(client.getSessionId().toString())) {
                ChatMessageResponse response = chatMessageMapper.toChatMessageResponse(message, 
                    client.get(UserConstants.USER_ID).toString());
                client.sendEvent(SocketEvent.CHAT_MESSAGE, response);
            }
        });
    }
    
    private Conversation validateParticipant(String conversationId, String userId) {
        return conversationRepository.findById(conversationId)
            .orElseThrow(() -> ChatErrorCode.CONVERSATION_NOT_FOUND.toException())
            .getParticipants().stream()
            .filter(p -> p.getUserId().equals(userId))
            .findAny()
            .orElseThrow(() -> ChatErrorCode.NOT_PARTICIPANT.toException());
    }
}
```

---

## 🔄 Phase 5: Socket.IO Configuration (Day 4)

### 5.1 SocketHandler với Room Management

**File:** `config/SocketHandler.java`
- **Cải thiện:** Auto join room khi connect
- **Cải thiện:** Heartbeat handling
- **Cải thiện:** Sử dụng AuthUtils thay vì AuthenticationService

```java
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SocketHandler {
    SessionService sessionService;
    
    @OnConnect
    public void onConnect(SocketIOClient client) {
        try {
            String userId = extractUserIdFromToken(client);
            if (userId != null) {
                client.set(UserConstants.USER_ID, userId);
                sessionService.saveSession(userId, client.getSessionId().toString());
                
                // Join all user's conversations
                joinUserConversations(client, userId);
                
                log.info("SocketHandler_onConnect_Client connected: {} for user: {}", client.getSessionId(), userId);
            } else {
                log.warn("SocketHandler_onConnect_Authentication failed for client: {}", client.getSessionId());
                client.disconnect();
            }
        } catch (Exception e) {
            log.error("SocketHandler_onConnect_Error handling connect", e);
            client.disconnect();
        }
    }
    
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        try {
            String userId = (String) client.get(UserConstants.USER_ID);
            if (userId != null) {
                sessionService.removeSession(userId, client.getSessionId().toString());
                log.info("SocketHandler_onDisconnect_Client disconnected: {} for user: {}", client.getSessionId(), userId);
            }
        } catch (Exception e) {
            log.error("SocketHandler_onDisconnect_Error handling disconnect", e);
        }
    }
    
    @OnEvent(SocketEvent.HEARTBEAT)
    public void onHeartbeat(SocketIOClient client) {
        String userId = (String) client.get(UserConstants.USER_ID);
        if (userId != null) {
            sessionService.updateHeartbeat(userId, client.getSessionId().toString());
        }
    }
    
    @OnEvent(SocketEvent.JOIN_ROOM)
    public void onJoinRoom(SocketIOClient client, String conversationId) {
        String userId = (String) client.get(UserConstants.USER_ID);
        if (userId != null) {
            String roomName = "conversation:" + conversationId;
            client.joinRoom(roomName);
            log.debug("SocketHandler_onJoinRoom_User {} joined room: {}", userId, roomName);
        }
    }
    
    @OnEvent(SocketEvent.LEAVE_ROOM)
    public void onLeaveRoom(SocketIOClient client, String conversationId) {
        String roomName = "conversation:" + conversationId;
        client.leaveRoom(roomName);
        log.debug("SocketHandler_onLeaveRoom_User left room: {}", roomName);
    }
    
    private String extractUserIdFromToken(SocketIOClient client) {
        // Extract from JWT token in handshake
        // Use AuthUtils pattern or JWT decoder
        // Implementation similar to chat-service but using SE-HUB-BE JWT config
    }
    
    private void joinUserConversations(SocketIOClient client, String userId) {
        // Fetch user's conversations and join rooms
        // Implementation: Get conversations from repository, join each room
    }
}
```

### 5.2 NettySocketIOConfig

**File:** `config/NettySocketIOConfig.java`
- **Cải thiện:** CORS configuration từ properties
- **Cải thiện:** Port configuration từ properties
- **Cải thiện:** Proper bean lifecycle management

```java
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class NettySocketIOConfig {
    SocketHandler socketHandler;
    
    @Value("${socketio.port:8099}")
    int socketIOPort;
    
    @Value("${socketio.cors.origins:*}")
    String corsOrigins;
    
    @Bean
    public SocketIOServer socketIOServer() {
        Configuration configuration = new Configuration();
        configuration.setPort(socketIOPort);
        configuration.setOrigin(corsOrigins);
        
        SocketIOServer server = new SocketIOServer(configuration);
        server.addListeners(socketHandler);
        
        return server;
    }
    
    @EventListener(ApplicationReadyEvent.class)
    public void startSocketServer() {
        socketIOServer().start();
        log.info("NettySocketIOConfig_startSocketServer_Socket.IO server started on port: {}", socketIOPort);
    }
    
    @PreDestroy
    public void stopSocketServer() {
        socketIOServer().stop();
        log.info("NettySocketIOConfig_stopSocketServer_Socket.IO server stopped");
    }
}
```

---

## 🔄 Phase 6: Controllers (Day 5)

### 6.1 ConversationController

**File:** `controller/ConversationController.java`
- Extend `BaseController`
- Sử dụng `GenericResponse`
- Swagger annotations
- Validation

```java
@Slf4j
@Tag(name = "Conversation Management", description = "APIs for managing conversations")
@RequestMapping("/conversations")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ConversationController extends BaseController {
    ConversationService conversationService;
    
    @PostMapping
    @Operation(summary = "Create conversation", description = "Create a new conversation (DIRECT or GROUP)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ResponseCode.OK_200, description = ChatMessageConstants.API_CONVERSATION_CREATED_SUCCESS),
        @ApiResponse(responseCode = ResponseCode.BAD_REQUEST_400, description = ChatMessageConstants.API_BAD_REQUEST),
        @ApiResponse(responseCode = ResponseCode.INTERNAL_ERROR_500, description = ChatMessageConstants.API_INTERNAL_ERROR)
    })
    public ResponseEntity<GenericResponse<ConversationResponse>> createConversation(
            @Valid @RequestBody CreateConversationRequest request) {
        log.debug("ConversationController_createConversation_Creating conversation");
        ConversationResponse response = conversationService.createConversation(request);
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }
    
    @GetMapping("/mine")
    @Operation(summary = "Get my conversations", description = "Get all conversations for current user with pagination")
    public ResponseEntity<GenericResponse<PagingResponse<ConversationResponse>>> getMyConversations(
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false, defaultValue = BaseFieldConstant.CREATE_DATE) String field,
            @RequestParam(required = false, defaultValue = PaginationConstants.DESC) String direction) {
        log.debug("ConversationController_getMyConversations_Fetching conversations for user");
        PagingRequest request = PagingRequest.builder()
            .page(page)
            .pageSize(size)
            .sortRequest(new SortRequest(direction, field))
            .build();
        return success(conversationService.getConversations(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }
}
```

### 6.2 ChatMessageController

**File:** `controller/ChatMessageController.java`
- Extend `BaseController`
- Pagination support
- Swagger annotations

```java
@Slf4j
@Tag(name = "Chat Message Management", description = "APIs for managing chat messages")
@RequestMapping("/messages")
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Validated
public class ChatMessageController extends BaseController {
    ChatMessageService chatMessageService;
    
    @PostMapping
    @Operation(summary = "Create message", description = "Create a new chat message")
    public ResponseEntity<GenericResponse<ChatMessageResponse>> createMessage(
            @Valid @RequestBody CreateChatMessageRequest request) {
        log.debug("ChatMessageController_createMessage_Creating message");
        ChatMessageResponse response = chatMessageService.createMessage(request);
        return success(response, MessageCodeConstant.M002_CREATED, MessageConstant.CREATED);
    }
    
    @GetMapping
    @Operation(summary = "Get messages", description = "Get messages for a conversation with pagination")
    public ResponseEntity<GenericResponse<PagingResponse<ChatMessageResponse>>> getMessages(
            @RequestParam("conversationId") String conversationId,
            @RequestParam(value = PaginationConstants.PARAM_PAGE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE) int page,
            @RequestParam(value = PaginationConstants.PARAM_SIZE, required = false, defaultValue = PaginationConstants.DEFAULT_PAGE_SIZE) int size,
            @RequestParam(required = false) Instant beforeDate) {
        log.debug("ChatMessageController_getMessages_Fetching messages for conversation: {}", conversationId);
        GetMessagesRequest request = GetMessagesRequest.builder()
            .conversationId(conversationId)
            .page(page)
            .pageSize(size)
            .beforeDate(beforeDate)
            .build();
        return success(chatMessageService.getMessages(request), MessageCodeConstant.M005_RETRIEVED, MessageConstant.RETRIEVED);
    }
}
```

---

## 🔄 Phase 7: Profile Sync Mechanism (Day 5-6)

### 7.1 Profile Sync trong Mapper

**File:** `mapper/ChatMessageMapper.java`
- Fetch Profile info khi map
- Cache Profile info để tránh N+1 queries

```java
@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    
    @Mapping(target = "sender", expression = "java(fetchParticipantInfo(message.getSenderId()))")
    ChatMessageResponse toChatMessageResponse(ChatMessage message, String currentUserId);
    
    default ParticipantInfoResponse fetchParticipantInfo(String userId) {
        // Fetch from ProfileRepository
        // Cache if needed
    }
}
```

### 7.2 Profile Sync trong ConversationMapper

**File:** `mapper/ConversationMapper.java`
- Fetch Profile info cho tất cả participants
- Batch fetch để tránh N+1 queries

```java
@Mapper(componentModel = "spring")
public interface ConversationMapper {
    
    @Mapping(target = "participants", expression = "java(fetchParticipantsInfo(conversation.getParticipants(), currentUserId))")
    @Mapping(target = "conversationName", expression = "java(getConversationName(conversation, currentUserId))")
    ConversationResponse toConversationResponse(Conversation conversation, String currentUserId);
    
    default List<ParticipantInfoResponse> fetchParticipantsInfo(List<ParticipantInfo> participants, String currentUserId) {
        // Batch fetch from ProfileRepository
        // Return list of ParticipantInfoResponse
    }
    
    default String getConversationName(Conversation conversation, String currentUserId) {
        // For DIRECT: return other participant name
        // For GROUP: return group name or first few participant names
    }
}
```

---

## 🔄 Phase 8: Testing & Validation (Day 6-7)

### 8.1 Unit Tests
- Service layer tests
- Mapper tests
- Repository tests

### 8.2 Integration Tests
- Controller tests
- Socket.IO tests
- Redis session tests

### 8.3 Performance Tests
- Load testing cho broadcasting
- Pagination performance
- Concurrent conversation creation

---

## 📝 Implementation Checklist

### Phase 1: Setup & Constants
- [ ] Create `ChatConstants.java`
- [ ] Create `ChatMessageConstants.java`
- [ ] Create `ChatErrorCodeConstants.java`
- [ ] Create `SocketEvent.java`
- [ ] Create `RedisKeys.java` (updated)
- [ ] Create `ChatErrorCode.java`
- [ ] Create `ChatException.java`
- [ ] Create `ConversationType.java`

### Phase 2: Entities & Repositories
- [ ] Update `ParticipantInfo.java` (chỉ lưu userId)
- [ ] Update `Conversation.java` (thêm type, bỏ @Data)
- [ ] Update `ChatMessage.java` (chỉ lưu senderId, bỏ @Data)
- [ ] Update `ConversationRepository.java` (thêm pagination)
- [ ] Update `ChatMessageRepository.java` (thêm pagination, cursor-based)

### Phase 3: DTOs & Mappers
- [ ] Create `CreateConversationRequest.java` (validation)
- [ ] Create `CreateChatMessageRequest.java` (validation)
- [ ] Create `GetMessagesRequest.java` (pagination)
- [ ] Create `ParticipantInfoResponse.java`
- [ ] Update `ChatMessageResponse.java`
- [ ] Update `ConversationResponse.java`
- [ ] Create `ConversationMapper.java`
- [ ] Create `ChatMessageMapper.java`

### Phase 4: Services
- [ ] Update `SessionServiceImpl.java` (heartbeat, cleanup)
- [ ] Update `ConversationServiceImpl.java` (concurrency, group chat)
- [ ] Update `ChatMessageServiceImpl.java` (room-based broadcasting, pagination)

### Phase 5: Socket.IO
- [ ] Update `SocketHandler.java` (room management, heartbeat)
- [ ] Update `NettySocketIOConfig.java` (config từ properties)

### Phase 6: Controllers
- [ ] Create `ConversationController.java` (BaseController pattern)
- [ ] Create `ChatMessageController.java` (BaseController pattern)

### Phase 7: Profile Sync
- [ ] Implement Profile fetch trong mappers
- [ ] Add caching nếu cần

### Phase 8: Testing
- [ ] Unit tests
- [ ] Integration tests
- [ ] Performance tests

---

## 🔧 Configuration Updates

### application.properties
```properties
# Socket.IO Configuration
socketio.port=8099
socketio.cors.origins=http://localhost:5173,https://yourdomain.com

# Chat Configuration
chat.message.max-length=5000
chat.participants.min=2
chat.participants.max=100
chat.session.ttl-hours=24
chat.session.heartbeat-interval-minutes=5
```

### pom.xml
- Đảm bảo có dependencies: `netty-socketio`, `spring-boot-starter-data-mongodb`, `spring-boot-starter-data-redis`

---

## 📊 Summary

**Total Files to Create/Update:** ~35 files  
**Estimated Time:** 6-7 days  
**Key Improvements:**
1. ✅ Room-based broadcasting (performance)
2. ✅ Pagination (scalability)
3. ✅ Concurrency handling (reliability)
4. ✅ Group chat support (functionality)
5. ✅ Heartbeat/cleanup (reliability)
6. ✅ Validation (security)
7. ✅ Profile sync (data consistency)

---

**Next Steps:** Review plan → Start Phase 1 implementation

