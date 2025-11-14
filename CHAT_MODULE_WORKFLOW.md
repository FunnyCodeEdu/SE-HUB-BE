# Chat Module Workflow Documentation

## Tổng quan

Module chat sử dụng **REST API** cho các thao tác CRUD và **Socket.IO** cho real-time messaging. Dữ liệu được lưu trong **MongoDB** (conversations, messages) và **Redis** (sessions).

---

## 🔄 Workflow chính

### 1. **User Connection (WebSocket)**

#### Flow:
```
Client → Socket.IO Connect (với JWT token) 
  → SocketHandler.onConnect()
    → Extract userId từ JWT
    → Save session vào Redis
    → Auto-join tất cả conversations của user
```

#### Chi tiết:
1. **Client kết nối**: `ws://server:8099?token=<JWT_TOKEN>`
2. **SocketHandler.onConnect()**:
   - Extract `userId` từ JWT token (dùng `JwtDecoder` từ SE-HUB-BE config)
   - Lưu `userId` vào client metadata: `client.set("userId", userId)`
   - **SessionService.saveSession()**: Lưu session vào Redis
     - Key: `user:session:{userId}` (Set chứa sessionIds)
     - Heartbeat key: `user:session:heartbeat:{userId}:{sessionId}` (TTL 5 phút)
   - **Auto-join rooms**: Tự động join tất cả conversations của user
     - Query MongoDB: `findByParticipantIdsContains(userId, PageRequest.of(0, 100))`
     - Join mỗi room: `client.joinRoom("conversation:{conversationId}")`

#### Heartbeat:
- Client gửi event `heartbeat` mỗi 5 phút
- **SocketHandler.onHeartbeat()**: Update heartbeat timestamp trong Redis
- Scheduled task `cleanupStaleSessions()` chạy mỗi phút để cleanup stale sessions

---

### 2. **Tạo Conversation**

#### Flow:
```
POST /api/conversations
  → ConversationController.createConversation()
    → ConversationService.createConversation()
      → Validate participants (min 2, max 100)
      → Generate participantsHash (SHA-256)
      → Find or Create với retry mechanism
        → Save vào MongoDB
      → Map to Response (fetch Profile info)
```

#### Chi tiết:
1. **Request**: `CreateConversationRequest` với `type` (DIRECT/GROUP) và `participantIds`
2. **Validation**:
   - Tự động thêm `currentUserId` nếu chưa có
   - DIRECT: phải có đúng 2 participants
   - GROUP: 2-100 participants
3. **Generate Hash**:
   - Sort participantIds → `userId1_userId2_userId3`
   - SHA-256 hash → `participantsHash` (unique index)
4. **Find or Create với Retry**:
   - Tìm conversation theo `participantsHash`
   - Nếu không có → tạo mới
   - Nếu `DuplicateKeyException` (concurrent creation):
     - Retry tối đa 3 lần với exponential backoff (100ms, 200ms, 300ms)
     - Sau mỗi retry, tìm lại conversation
5. **Response**: Map với Profile info (username, fullName, avatarUrl)

#### Database:
- **MongoDB Collection**: `conversation`
- **Fields**: `conversationId`, `type`, `participantsHash` (unique), `participants` (List<ParticipantInfo>), `createdDate`, `modifiedDate`

---

### 3. **Gửi Message**

#### Flow:
```
POST /api/messages
  → ChatMessageController.createMessage()
    → ChatMessageService.createMessage()
      → Validate participant
      → Save message vào MongoDB
      → Update conversation.modifiedDate
      → Broadcast to room (Socket.IO)
      → Return response
```

#### Chi tiết:
1. **Request**: `CreateChatMessageRequest` với `conversationId` và `message`
2. **Validation**:
   - Kiểm tra user là participant của conversation
   - Validate message length (max 5000 chars)
3. **Save Message**:
   - Tạo `ChatMessage` với `senderId`, `conversationId`, `message`, `createDate`
   - Save vào MongoDB collection `chat-message`
4. **Update Conversation**: Update `modifiedDate` để sort conversations
5. **Broadcast to Room**:
   ```java
   // Lấy tất cả participants
   List<String> participantIds = conversation.getParticipants()
   
   // Lấy tất cả active sessions từ Redis
   Set<String> sessionIds = participantIds.stream()
       .map(sessionService::getSessions)  // Redis lookup
       .flatMap(Set::stream)
       .collect(Collectors.toSet())
   
   // Broadcast đến room
   String roomName = "conversation:" + conversationId
   socketIOServer.getRoomOperations(roomName)
       .getClients()
       .forEach(client -> {
           if (sessionIds.contains(client.getSessionId())) {
               // Map message với Profile info
               ChatMessageResponse response = mapper.toChatMessageResponse(message)
               response.setIsMe(client.getUserId() == senderId)
               client.sendEvent("chat_message", response)
           }
       })
   ```
6. **Response**: `ChatMessageResponse` với Profile info của sender

#### Database:
- **MongoDB Collection**: `chat-message`
- **Fields**: `messageId`, `conversationId` (indexed), `senderId`, `message`, `createDate` (indexed)

---

### 4. **Lấy Messages (Pagination)**

#### Flow:
```
GET /api/messages?conversationId=xxx&page=1&pageSize=50&beforeDate=...
  → ChatMessageController.getMessages()
    → ChatMessageService.getMessages()
      → Validate participant
      → Query MongoDB (page-based hoặc cursor-based)
      → Map to Response (fetch Profile info)
```

#### Chi tiết:
1. **Page-based Pagination**:
   - Query: `findByConversationIdOrderByCreateDateDesc(conversationId, pageable)`
   - Sort: `createDate DESC` (mới nhất trước)
2. **Cursor-based Pagination** (nếu có `beforeDate`):
   - Query: `findByConversationIdAndCreateDateBeforeOrderByCreateDateDesc(conversationId, beforeDate, pageable)`
   - Dùng cho infinite scroll
3. **Response**: `PagingResponse<ChatMessageResponse>` với Profile info

---

### 5. **Lấy Conversations**

#### Flow:
```
GET /api/conversations/mine?page=1&pageSize=20
  → ConversationController.getMyConversations()
    → ConversationService.getConversations()
      → Query MongoDB (pagination)
      → Map to Response (fetch Profile info cho participants)
```

#### Chi tiết:
1. **Query**: `findByParticipantIdsContains(currentUserId, pageable)`
2. **Response**: `PagingResponse<ConversationResponse>` với:
   - `participants`: List<ParticipantInfoResponse> (fetch từ Profile)
   - `conversationName`: 
     - DIRECT: tên của participant khác
     - GROUP: "User1, User2 and 3 others" (nếu > 3 participants)

---

### 6. **User Disconnection**

#### Flow:
```
Client Disconnect
  → SocketHandler.onDisconnect()
    → Remove session từ Redis
    → Log disconnect
```

#### Chi tiết:
1. **SessionService.removeSession()**:
   - Remove sessionId từ Redis Set: `user:session:{userId}`
   - Delete heartbeat key: `user:session:heartbeat:{userId}:{sessionId}`
2. **Auto-leave rooms**: Socket.IO tự động remove client khỏi rooms

---

## 🔑 Key Components

### **Session Management (Redis)**
- **Session Storage**: `user:session:{userId}` → Set<sessionId>
- **Heartbeat**: `user:session:heartbeat:{userId}:{sessionId}` → timestamp (TTL 5 phút)
- **TTL**: 24 giờ cho session, 5 phút cho heartbeat

### **Room Management (Socket.IO)**
- **Room Name**: `conversation:{conversationId}`
- **Auto-join**: Khi connect, tự động join tất cả conversations
- **Manual join/leave**: Client có thể emit `join_room`/`leave_room` events

### **Profile Sync**
- **Mapper Pattern**: `ChatMessageMapper` và `ConversationMapper` fetch Profile info
- **Batch Fetch**: ConversationMapper batch fetch tất cả participants để tránh N+1 queries
- **Fallback**: Nếu Profile không tìm thấy → "Unknown User"

---

## 📊 Data Flow Diagram

```
┌─────────┐
│ Client  │
└────┬────┘
     │
     ├─── REST API ───────────────────────────────┐
     │                                             │
     │  POST /conversations                        │
     │  → ConversationService                     │
     │  → MongoDB (save conversation)              │
     │  → Response (with Profile info)            │
     │                                             │
     │  POST /messages                             │
     │  → ChatMessageService                       │
     │  → MongoDB (save message)                   │
     │  → Socket.IO (broadcast to room)            │
     │  → Response                                 │
     │                                             │
     │  GET /messages                              │
     │  → ChatMessageService                       │
     │  → MongoDB (query with pagination)         │
     │  → Response (with Profile info)            │
     │                                             │
     └─── Socket.IO ──────────────────────────────┤
          │                                        │
          │  Connect (with JWT)                   │
          │  → SocketHandler.onConnect()          │
          │  → Save session (Redis)                │
          │  → Auto-join rooms                    │
          │                                        │
          │  Heartbeat (every 5 min)              │
          │  → Update heartbeat (Redis)           │
          │                                        │
          │  Receive chat_message event            │
          │  ← Broadcast from room                │
          │                                        │
          │  Disconnect                            │
          │  → Remove session (Redis)             │
          │                                        │
          └────────────────────────────────────────┘
```

---

## 🔄 Concurrency Handling

### **Conversation Creation**
- **Retry Mechanism**: 3 retries với exponential backoff
- **Duplicate Detection**: Dùng `participantsHash` (unique index) để detect duplicate
- **Race Condition**: Nếu 2 users cùng tạo conversation → một sẽ tìm thấy conversation đã tạo

### **Message Broadcasting**
- **Room-based**: Chỉ broadcast đến clients trong room (không scan tất cả clients)
- **Session Validation**: Chỉ broadcast đến active sessions (có trong Redis)

---

## 🚀 Performance Optimizations

1. **Room-based Broadcasting**: Thay vì `getAllClients()`, dùng `getRoomOperations(roomName).getClients()`
2. **Pagination**: Hỗ trợ page-based và cursor-based để tránh load quá nhiều data
3. **Profile Batch Fetch**: ConversationMapper batch fetch tất cả participants
4. **Indexes**: MongoDB indexes trên `conversationId`, `createDate`, `participantsHash`
5. **Redis Caching**: Sessions cached trong Redis với TTL

---

## 🔐 Security

1. **JWT Authentication**: Socket.IO connection yêu cầu JWT token
2. **Participant Validation**: Chỉ participants mới có thể gửi/nhận messages
3. **Room Isolation**: Mỗi conversation là một room riêng biệt

---

## 📝 Notes

- **Virtual Threads**: Tất cả blocking I/O operations (MongoDB, Redis) chạy trên virtual threads
- **Transactional**: Write operations (`@Transactional`) để đảm bảo consistency
- **Error Handling**: Broadcast errors không throw exception (message đã được save)

