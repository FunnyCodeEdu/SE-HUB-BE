# Chat Module - SSE Implementation Documentation

## Tổng quan

Module chat đã được chuyển đổi từ Socket.IO sang Server-Sent Events (SSE) để đơn giản hóa kiến trúc và phù hợp với lưu lượng chat cường độ thấp. SSE cung cấp kết nối real-time một chiều từ server đến client, đủ cho hầu hết các use case chat.

## Thay đổi chính

### ✅ Đã thêm
- **ChatSseService**: Service quản lý kết nối SSE cho chat
- **ChatSseController**: Endpoint để client subscribe vào chat stream
- **NewChatMessageEvent**: Event được publish khi có tin nhắn mới
- **ChatEventHandler**: Xử lý event và gửi tin nhắn qua SSE + thông báo

### ❌ Đã xóa
- **SocketHandler**: Socket.IO handler (không còn cần)
- **SocketEvent**: Socket event constants
- **SessionService**: Session management cho Socket.IO
- **UnifiedWebSocketConfig**: WebSocket configuration
- **netty-socketio dependency**: Dependency Socket.IO trong pom.xml

### 🔄 Đã cập nhật
- **ChatMessageServiceImpl**: Sử dụng ApplicationEventPublisher thay vì Socket.IO
- **NotificationType**: Thêm `MESSAGE_RECEIVED` cho thông báo tin nhắn

## Kiến trúc mới

```
Client                 Backend                          Redis
  |                       |                               |
  |-- SSE Subscribe ----> |                               |
  |<--- Connected --------|                               |
  |                       |                               |
  |-- POST /messages ---> |                               |
  |                       |-- Save to DB                  |
  |                       |-- Publish Event               |
  |                       |   NewChatMessageEvent         |
  |                       |                               |
  |                       |-- ChatEventHandler            |
  |                       |   |-- Publish to Redis ------>|
  |                       |   |   chat_messages           |
  |                       |   |                           |
  |                       |   |-- Publish to Redis ------>|
  |                       |       notifications           |
  |                       |                               |
  |                       |<-- Redis Listener <-----------|
  |<--- SSE Message ------|                               |
  |                       |                               |
```

## API Endpoints

### 1. Subscribe to Chat SSE
**Endpoint:** `GET /chat/subscribe`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Accept: text/event-stream
```

**Response:** SSE Stream

**Events:**
- `connected`: Sự kiện kết nối ban đầu
- `chat_message`: Tin nhắn chat mới
- `:keep-alive`: Comment keep-alive (mỗi 30s)

### 2. Subscribe to Notification SSE
**Endpoint:** `GET /notifications/subscribe`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Accept: text/event-stream
```

**Response:** SSE Stream

**Events:**
- `connected`: Sự kiện kết nối ban đầu
- `notification`: Thông báo mới (bao gồm thông báo tin nhắn)
- `:keep-alive`: Comment keep-alive (mỗi 30s)

### 3. Send Chat Message
**Endpoint:** `POST /chat/messages`

**Headers:**
```
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

**Request Body:**
```json
{
  "conversationId": "conv_123",
  "message": "Hello!"
}
```

**Response:**
```json
{
  "messageId": "msg_456",
  "conversationId": "conv_123",
  "isMe": true,
  "message": "Hello!",
  "sender": {
    "userId": "user_789",
    "username": "john_doe",
    "fullName": "John Doe",
    "avatar": "https://..."
  },
  "createDate": "2025-11-24T10:30:00Z"
}
```

## Frontend Implementation

### JavaScript (Vanilla)

```javascript
// 1. Kết nối đến Chat SSE
const chatEventSource = new EventSource('/chat/subscribe', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

// Xử lý kết nối thành công
chatEventSource.addEventListener('connected', (event) => {
  console.log('Connected to chat stream:', JSON.parse(event.data));
});

// Xử lý tin nhắn chat mới
chatEventSource.addEventListener('chat_message', (event) => {
  const message = JSON.parse(event.data);
  console.log('New chat message:', message);
  
  // Cập nhật UI
  displayChatMessage(message);
});

// Xử lý lỗi
chatEventSource.onerror = (error) => {
  console.error('SSE error:', error);
  // SSE tự động reconnect
};

// 2. Kết nối đến Notification SSE (để nhận thông báo tin nhắn)
const notificationEventSource = new EventSource('/notifications/subscribe', {
  headers: {
    'Authorization': `Bearer ${token}`
  }
});

notificationEventSource.addEventListener('notification', (event) => {
  const notification = JSON.parse(event.data);
  
  if (notification.notificationType === 'MESSAGE_RECEIVED') {
    // Hiển thị thông báo tin nhắn mới
    showNotificationBadge(notification);
  }
});

// 3. Gửi tin nhắn
async function sendMessage(conversationId, message) {
  const response = await fetch('/chat/messages', {
    method: 'POST',
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    },
    body: JSON.stringify({
      conversationId: conversationId,
      message: message
    })
  });
  
  return await response.json();
}

// 4. Cleanup khi rời trang
window.addEventListener('beforeunload', () => {
  chatEventSource.close();
  notificationEventSource.close();
});
```

### React

```javascript
import { useEffect, useState } from 'react';

function ChatComponent({ conversationId, token }) {
  const [messages, setMessages] = useState([]);
  const [eventSource, setEventSource] = useState(null);

  useEffect(() => {
    // Kết nối SSE
    const es = new EventSource('/chat/subscribe', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    es.addEventListener('connected', (event) => {
      console.log('Connected:', JSON.parse(event.data));
    });

    es.addEventListener('chat_message', (event) => {
      const message = JSON.parse(event.data);
      if (message.conversationId === conversationId) {
        setMessages(prev => [...prev, message]);
      }
    });

    es.onerror = (error) => {
      console.error('SSE error:', error);
    };

    setEventSource(es);

    // Cleanup
    return () => {
      es.close();
    };
  }, [conversationId, token]);

  const sendMessage = async (text) => {
    const response = await fetch('/chat/messages', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        conversationId: conversationId,
        message: text
      })
    });

    const data = await response.json();
    // Message sẽ được thêm vào danh sách qua SSE event
  };

  return (
    <div>
      {messages.map(msg => (
        <div key={msg.messageId} className={msg.isMe ? 'my-message' : 'their-message'}>
          <strong>{msg.sender.fullName}</strong>: {msg.message}
        </div>
      ))}
    </div>
  );
}
```

### Vue.js

```javascript
<template>
  <div class="chat-container">
    <div v-for="message in messages" :key="message.messageId"
         :class="message.isMe ? 'my-message' : 'their-message'">
      <strong>{{ message.sender.fullName }}</strong>: {{ message.message }}
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      messages: [],
      eventSource: null
    };
  },
  
  mounted() {
    this.connectSSE();
  },
  
  beforeUnmount() {
    if (this.eventSource) {
      this.eventSource.close();
    }
  },
  
  methods: {
    connectSSE() {
      this.eventSource = new EventSource('/chat/subscribe', {
        headers: {
          'Authorization': `Bearer ${this.$store.state.token}`
        }
      });

      this.eventSource.addEventListener('connected', (event) => {
        console.log('Connected:', JSON.parse(event.data));
      });

      this.eventSource.addEventListener('chat_message', (event) => {
        const message = JSON.parse(event.data);
        if (message.conversationId === this.conversationId) {
          this.messages.push(message);
        }
      });
    },
    
    async sendMessage(text) {
      await fetch('/chat/messages', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${this.$store.state.token}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          conversationId: this.conversationId,
          message: text
        })
      });
    }
  }
};
</script>
```

## Đặc điểm kỹ thuật

### Keep-Alive Mechanism
- SSE gửi comment keep-alive mỗi 30 giây để giữ kết nối
- Trình duyệt tự động reconnect nếu mất kết nối

### Multi-Device Support
- Một user có thể có nhiều kết nối SSE đồng thời (multi-tab, multi-device)
- Tất cả devices nhận tin nhắn real-time

### Redis Pub/Sub
- Chat messages được publish qua Redis channel `chat_messages`
- Notifications được publish qua Redis channel `notifications`
- Hỗ trợ horizontal scaling với multiple server instances

### Event Flow
1. User A gửi tin nhắn
2. Backend lưu tin nhắn vào database
3. Backend publish `NewChatMessageEvent`
4. `ChatEventHandler` xử lý event:
   - Publish tin nhắn đến Redis channel `chat_messages` cho recipients
   - Publish thông báo đến Redis channel `notifications` cho recipients
5. `ChatSseService` nhận từ Redis và gửi đến clients qua SSE
6. `SseService` (notification) nhận từ Redis và gửi thông báo đến clients

## Lưu ý quan trọng

### Browser Compatibility
- SSE được hỗ trợ bởi tất cả trình duyệt hiện đại
- Không hỗ trợ IE (nhưng IE đã EOL)

### CORS Configuration
- Đảm bảo server cho phép SSE từ frontend domain
- Headers cần thiết: `Access-Control-Allow-Origin`, `Access-Control-Allow-Credentials`

### Authentication
- JWT token được gửi qua Authorization header
- Token được validate khi establish SSE connection

### Error Handling
- SSE tự động reconnect khi mất kết nối
- Backend tự động cleanup dead connections

### Performance
- SSE timeout: Infinite (0L) - dựa vào cleanup callbacks
- Keep-alive interval: 30 giây
- Không giới hạn số lượng concurrent connections (tùy thuộc server resources)

## Troubleshooting

### Client không nhận được tin nhắn
1. Kiểm tra kết nối SSE trong DevTools Network tab
2. Verify JWT token còn valid
3. Check server logs để xem có errors không

### Kết nối bị đứt liên tục
1. Check network stability
2. Verify proxy/load balancer configuration (một số proxy không hỗ trợ SSE tốt)
3. Xem xét tăng timeout settings

### Tin nhắn bị duplicate
1. Đảm bảo chỉ có một EventSource instance cho mỗi user
2. Cleanup EventSource properly khi component unmount

## So sánh với Socket.IO

| Feature | Socket.IO (Cũ) | SSE (Mới) |
|---------|---------------|-----------|
| Bidirectional | ✅ | ❌ (chỉ server->client) |
| Real-time | ✅ | ✅ |
| Auto-reconnect | ✅ | ✅ |
| Browser support | ✅ | ✅ (trừ IE) |
| Complexity | 🔴 Cao | 🟢 Thấp |
| Dependencies | netty-socketio | Built-in Spring |
| Port required | Separate | Same as HTTP |
| Overhead | Cao | Thấp |

## Migration Guide

### Từ Socket.IO
```javascript
// Cũ - Socket.IO
const socket = io('ws://localhost:9092/chat', {
  auth: { token: token }
});

socket.on('chat_message', (data) => {
  console.log(data);
});

socket.emit('join_room', conversationId);

// Mới - SSE
const eventSource = new EventSource('/chat/subscribe', {
  headers: { 'Authorization': `Bearer ${token}` }
});

eventSource.addEventListener('chat_message', (event) => {
  const data = JSON.parse(event.data);
  console.log(data);
});

// Không cần join room - tự động nhận tin nhắn theo userId
```

## Support

Để được hỗ trợ, vui lòng liên hệ team backend hoặc tạo issue trên repository.

