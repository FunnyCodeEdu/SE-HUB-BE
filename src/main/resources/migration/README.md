# Database Migration Scripts

## Migration: Add Blog Approval and Reactions

**File:** `add_blog_approval_and_reactions.sql`

**Date:** 2025-11-15

**Description:** 
- Thêm cột `is_approved` vào bảng `blogs`
- Tạo bảng `blog_reactions` để lưu trữ like/dislike

### Cách chạy migration:

#### Option 1: Chạy trực tiếp trên PostgreSQL
```bash
psql -U postgres -d sehub -f src/main/resources/migration/add_blog_approval_and_reactions.sql
```

#### Option 2: Chạy qua psql interactive
```bash
psql -U postgres -d sehub
\i src/main/resources/migration/add_blog_approval_and_reactions.sql
```

#### Option 3: Copy và paste vào psql
```bash
psql -U postgres -d sehub
# Sau đó copy nội dung file và paste vào
```

### Lưu ý:
- Script sử dụng `IF NOT EXISTS` nên an toàn khi chạy nhiều lần
- Nếu muốn approve tất cả blog hiện có, uncomment dòng cuối trong script
- Đảm bảo database connection string đúng trong `.env` hoặc `application.properties`

