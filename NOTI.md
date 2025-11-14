Dưới đây là **tài liệu hoàn chỉnh – bản chuẩn best-practice**, viết như một **technical design document (TDD)** cho dự án SE HUB (giống StackOverflow).
Không có code, chỉ **kiến trúc – tư duy – quy trình – flow hoạt động – chuẩn hệ thống lớn**.

---

# 📘 **SE HUB — Notification System Design Document**

**Tech:** Java Spring Boot 21 • PostgreSQL • Redis • WebSocket
**Goal:** Realtime, scalable, event-driven notification system

---

# 1. 🎯 **Mục tiêu**

Hệ thống Notification hỗ trợ thông báo realtime cho toàn bộ sản phẩm SE HUB:

* Mention: User A nhắc đến User B
* Post Reaction: Ai đó like/comment bài của bạn
* Blog Moderation: Blog được duyệt
* Achievement: Mốc thành tựu
* System Announcement
* Các loại thông báo mở rộng không giới hạn

Yêu cầu:

* Realtime
* Chính xác, không mất thông báo
* Trạng thái UNREAD/READ
* Hiệu năng cao, chịu tải lớn
* Dễ dàng mở rộng, không phá kiến trúc modulith

---

# 2. 🧠 **Tư duy thiết kế**

## 2.1. Notification **không** thuộc về nghiệp vụ

➡ Các module **không tự gửi noti**, chỉ **phát Event**.
➡ Notification Module **duy nhất** xử lý noti.

Ví dụ:

* CommentModule → emit `MentionEvent`
* BlogModule → emit `BlogApprovedEvent`
* ReactionModule → emit `PostLikedEvent`

Điều này đảm bảo:

* Tách biệt trách nhiệm
* Dễ mở rộng
* Không tạo vòng phụ thuộc

---

## 2.2. Event-driven Architecture

Sơ đồ ý tưởng:

```
User Action → Business Module → Emit Event
                        ↓
                 Notification Service
                        ↓
          (DB Storage + Redis Cache + WebSocket)
                        ↓
                   Frontend Realtime
```

Cách này giống mô hình của:

* StackOverflow
* Reddit
* Facebook
* Discord

---

## 2.3. Hai lớp dữ liệu Notification

**1. Notification** = sự kiện chung
**2. UserNotification** = thông báo gửi cho từng người

Một sự kiện có thể tạo nhiều UserNotification.

---

## 2.4. Redis gia tăng hiệu năng

Redis dùng để:

* Cache danh sách noti mới nhất (recent list)
* Cache unread_count
* Pub/Sub realtime
* Tạm chứa dữ liệu để gom nhóm nhiều sự kiện (aggregation)

---

## 2.5. WebSocket để realtime

Hệ thống phải hỗ trợ đẩy notification tức thời khi người nhận đang online.

Không dùng WebSocket → UX giảm 40%.

---

# 3. 🏗 **Kiến trúc hệ thống**

## 3.1. Tổng quan kiến trúc

```
           ┌─────────────────────────────┐
           │   Business Modules           │
           │(Post, Comment, Blog, Like…)  │
           └──────────────┬──────────────┘
                          │ Emit Event
                          ▼
               ┌─────────────────────────┐
               │   Notification Service   │
               │ - Listen event           │
               │ - Apply template         │
               │ - Generate notification  │
               └─────────┬───────┬───────┘
                         │       │
                         ▼       ▼
             ┌────────────────┐  ┌────────────────────────┐
             │   PostgreSQL    │  │         Redis          │
             │ notification     │  │ unread_count          │
             │ user_notification│  │ recent_list           │
             └────────────────┘  │ pub/sub                │
                                  └───────────┬────────────┘
                                              ▼
                                   ┌────────────────────┐
                                   │   WebSocket Server  │
                                   └───────────┬────────┘
                                               ▼
                                       Frontend UI
```

---

# 4. 🧱 **Thành phần hệ thống**

## 4.1. Notification Module

Chịu trách nhiệm:

* Nhận Event
* Xác định Template
* Render nội dung
* Tạo Notification + UserNotification
* Ghi Redis
* Đẩy WebSocket

Module tách biệt hoàn toàn khỏi các module khác.

---

## 4.2. PostgreSQL (Source of Truth)

Lưu:

* Sự kiện chung
* Các thông báo của từng user
* Trạng thái READ/UNREAD
* Template noti
* Setting noti của từng người

---

## 4.3. Redis (Performance Layer)

Chứa:

* `notif:unread:user:{id}`
* `notif:recent:user:{id}`
* Pub/Sub channel: `notif:channel:user:{id}`
* Aggregation: `notif:agg:*`

---

## 4.4. WebSocket Server

* Lắng nghe Redis Pub/Sub
* Đẩy realtime tới FE ngay khi có noti mới
* Dùng STOMP, raw WS hoặc SSE tùy kiến trúc

---

# 5. 🔁 **Flow hoạt động chính**

## 5.1. Flow “Tạo Notification mới”

(Chuẩn doanh nghiệp lớn)

```
User thực hiện hành động (comment / like / approve blog)
          │
          ▼
Module nghiệp vụ xử lý logic
          │
          ▼
Module phát Event (e.g., MentionEvent, PostLikedEvent)
          │
          ▼
Notification Service nhận Event
          │
   ┌────────────┬────────────┬──────────────┐
   │            │             │              │
Xác định loại   Lấy Template  Tạo Notification chung
nội dung        phù hợp       (event-level)
          │
          ▼
Tạo UserNotification (mỗi user nhận 1 record)
          │
   ┌────────────┬────────────┬──────────────┐
   │            │             │              │
Ghi DB        Ghi Redis      Publish qua Redis PubSub
(UNREAD)      (recent list)  (notif:channel:user:{id})
              (INCR unread)
          │
          ▼
WebSocket Server
          │
          ▼
Frontend hiển thị realtime
```

---

## 5.2. Flow “User mở danh sách thông báo”

```
User mở menu Notification
        │
        ▼
Check Redis recent_list của user
        │
        ├── Có: trả về ngay (0–5ms)
        └── Không: load từ DB → nạp vào Redis
```

---

## 5.3. Flow “User đọc thông báo”

```
User click xem 1 notification
        │
        ▼
DB:
  - status = READ
Redis:
  - DECR unread_count
  - Cập nhật recent_list (đánh dấu READ)
```

---

## 5.4. Flow Aggregation (gom nhiều sự kiện)

Ví dụ: 10 người like bài viết trong 1 phút.

```
LikeEvent → Redis Set (agg)
        │
Worker cron 30–60s:
        │
        ▼
Nếu Set >= 2 người
→ Gom lại 1 Notification duy nhất
→ Tạo 1 UserNotification
```

---

# 6. ⚙️ **Quản lý trạng thái Notification**

## 6.1. Các trạng thái

* UNREAD (mặc định)
* READ
* ARCHIVED (ẩn)
* DELETED (xóa)

## 6.2. Các thao tác

* Mark as read (1 item)
* Mark all as read
* Clear all (archive)
* Hide (delete logic)

---

# 7. 🗂 **Notification Template (Best Practice)**

Template dùng để:

* Chuẩn hóa nội dung
* Dễ translate đa ngôn ngữ
* Dễ chỉnh sửa nội dung mà không sửa code

Ví dụ:

```
MENTION  
BLOG_APPROVED  
POST_LIKED  
ACHIEVEMENT_UNLOCKED  
FOLLOWED_YOU  
SYSTEM_ANNOUNCEMENT  
```

---

# 8. 🧪 **Quy trình kiểm thử**

### Test logic:

* Mention với nhiều user
* Like spam
* Blog được duyệt
* Achievement

### Test trạng thái:

* UNREAD → READ
* MARK ALL READ
* DELETE
* ARCHIVE

### Test Redis:

* unread_count chính xác
* recent_list hoạt động đúng
* Pub/Sub

### Test Realtime:

* User online nhận push
* User offline → vẫn lưu DB + Redis

---

# 9. 🛠 **Vận hành & bảo trì**

## 9.1. Cron jobs hằng ngày

* Sync unread_count giữa Redis ↔ DB
* Trim recent_list giữ 50–100 item
* Xoá noti cũ > 6 tháng
* Xoá các record archived lâu ngày

## 9.2. Logs cần ghi

* Thời gian xử lý event
* Số noti tạo ra
* WebSocket delivery success/fail

## 9.3. Giám sát (Monitoring)

* Redis latency
* DB query slow log
* WebSocket connections per node
* Push message throughput

---

# 10. 📌 **Tóm tắt — Những điểm quan trọng nhất**

### ✔ Hệ thống Notification phải là **event-driven**

### ✔ Notification Service tách hoàn toàn khỏi các module khác

### ✔ PostgreSQL = dữ liệu bền vững

### ✔ Redis = hiệu năng và realtime

### ✔ WebSocket = trải nghiệm realtime

### ✔ Template + Setting = mở rộng linh hoạt

### ✔ Aggregation = xử lý spam like/comment

### ✔ Cron cleanup = tránh phình dữ liệu

---

# Nếu bạn muốn tiếp tục:

Tôi có thể viết thêm các phần:

* **PlantUML Architecture Diagram**
* **PlantUML Sequence cho Mention / Like / Blog Approved**
* **Checklist để implement theo từng sprint**
* **Tài liệu API Specification dạng OpenAPI**

Chỉ cần bạn nói:
👉 *"Vẽ sơ đồ PlantUML tổng thể"*
hoặc
👉 *"Viết spec API cho Notification"*
