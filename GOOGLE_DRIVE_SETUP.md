# Hướng dẫn Setup Google Drive API

## Bước 1: Tạo Google Cloud Project

1. Truy cập [Google Cloud Console](https://console.cloud.google.com/)
2. Đăng nhập bằng tài khoản Google của bạn
3. Tạo project mới hoặc chọn project có sẵn:
   - Click vào dropdown project ở top bar
   - Click "New Project"
   - Đặt tên project (ví dụ: "SE-HUB")
   - Click "Create"

## Bước 2: Enable Google Drive API

1. Trong Google Cloud Console, vào **APIs & Services** > **Library**
2. Tìm kiếm "Google Drive API"
3. Click vào "Google Drive API"
4. Click nút **Enable** để kích hoạt API

## Bước 3: Tạo OAuth 2.0 Credentials

1. Vào **APIs & Services** > **Credentials**
2. Click **+ CREATE CREDENTIALS** > **OAuth client ID**
3. Nếu chưa có OAuth consent screen, bạn sẽ được yêu cầu cấu hình:
   - **User Type**: Chọn "External" (hoặc "Internal" nếu dùng Google Workspace)
   - Click **Create**
   - **App name**: Nhập tên ứng dụng (ví dụ: "SE-HUB")
   - **User support email**: Chọn email của bạn
   - **Developer contact information**: Nhập email của bạn
   - Click **Save and Continue**
   - **Scopes**: Click **Save and Continue** (có thể bỏ qua)
   - **Test users**: Thêm email của bạn vào danh sách test users, click **Save and Continue**
   - Click **Back to Dashboard**

4. Tạo OAuth Client ID:
   - **Application type**: Chọn "Web application"
   - **Name**: Đặt tên (ví dụ: "SE-HUB Web Client")
   - **Authorized redirect URIs**: Thêm:
     ```
     http://localhost:8080/api/drive/callback
     ```
     (Hoặc URL callback của bạn nếu khác)
   - Click **Create**
   - **QUAN TRỌNG**: Copy và lưu lại:
     - **Client ID** (sẽ dùng cho `GOOGLE_DRIVE_CLIENT_ID`)
     - **Client secret** (sẽ dùng cho `GOOGLE_DRIVE_CLIENT_SECRET`)
     - **Project ID** (tìm ở top bar hoặc trong Project Settings)

## Bước 4: Cấu hình trong Application

### Cách 1: Sử dụng file .env (Khuyến nghị)

1. Tạo file `.env` trong thư mục root của project (cùng cấp với `pom.xml`)

2. Thêm các biến môi trường:
```properties
# Google Drive Configuration
GOOGLE_DRIVE_CLIENT_ID=your_client_id_here
GOOGLE_DRIVE_CLIENT_SECRET=your_client_secret_here
GOOGLE_DRIVE_PROJECT_ID=your_project_id_here
GOOGLE_DRIVE_TOKENS_DIR=tokens
GOOGLE_DRIVE_APPLICATION_NAME=SE-HUB
```

3. Thay thế các giá trị:
   - `your_client_id_here`: Client ID từ bước 3
   - `your_client_secret_here`: Client secret từ bước 3
   - `your_project_id_here`: Project ID từ Google Cloud Console

### Cách 2: Sử dụng trực tiếp trong application.properties

Mở file `src/main/resources/application.properties` và cập nhật:

```properties
#============================ Google Drive Configuration
google.drive.client_id=your_client_id_here
google.drive.client_secret=your_client_secret_here
google.drive.project_id=your_project_id_here
google.drive.tokens.dir=tokens
google.drive.application.name=SE-HUB
```

**Lưu ý**: Không commit file `.env` hoặc `application.properties` có chứa credentials vào Git!

## Bước 5: Tạo thư mục tokens

1. Tạo thư mục `tokens` trong thư mục root của project (cùng cấp với `pom.xml`)
2. Thư mục này sẽ lưu trữ OAuth tokens sau lần đầu xác thực

## Bước 6: Chạy ứng dụng và xác thực lần đầu

1. Chạy ứng dụng Spring Boot:
```bash
mvn spring-boot:run
```

2. Khi ứng dụng khởi động, Google Drive API sẽ tự động mở browser để xác thực:
   - Chọn tài khoản Google của bạn
   - Cho phép ứng dụng truy cập Google Drive
   - Sau khi xác thực thành công, tokens sẽ được lưu vào thư mục `tokens`

3. **Lưu ý**: 
   - Lần đầu tiên cần xác thực qua browser
   - Các lần sau sẽ tự động sử dụng tokens đã lưu
   - Nếu tokens hết hạn, sẽ tự động refresh

## Bước 7: Kiểm tra

1. Gửi request upload document:
```bash
POST http://localhost:8080/api/documents
Content-Type: multipart/form-data

file: [chọn file]
courseId: [course_id]
documentName: [tên document]
```

2. Nếu thành công, file sẽ được upload lên Google Drive và có quyền xem công khai

## Troubleshooting

### Lỗi: "Redirect URI mismatch"
- Kiểm tra lại **Authorized redirect URIs** trong OAuth credentials
- Đảm bảo có: `http://localhost:8080/Callback`

### Lỗi: "Access blocked: This app's request is invalid"
- Kiểm tra OAuth consent screen đã được cấu hình đúng
- Đảm bảo email của bạn đã được thêm vào test users

### Lỗi: "The application has not been verified"
- Nếu đang ở chế độ testing, bạn có thể bỏ qua cảnh báo này
- Click "Advanced" > "Go to [App Name] (unsafe)" để tiếp tục

### Lỗi: "Invalid credentials"
- Kiểm tra lại Client ID và Client Secret trong `.env` hoặc `application.properties`
- Đảm bảo không có khoảng trắng thừa

### File không có quyền xem công khai
- Kiểm tra method `setPublicViewPermission()` trong `GoogleDriveService`
- Đảm bảo OAuth scope bao gồm `https://www.googleapis.com/auth/drive`

## Security Best Practices

1. **Không commit credentials vào Git**:
   - Thêm `.env` vào `.gitignore`
   - Sử dụng environment variables trong production

2. **Sử dụng Service Account cho Production** (tùy chọn):
   - Tạo Service Account trong Google Cloud Console
   - Download JSON key file
   - Sử dụng Service Account thay vì OAuth 2.0 cho server-to-server communication

3. **Restrict API access**:
   - Chỉ enable Google Drive API cho project cần thiết
   - Sử dụng IAM để giới hạn quyền truy cập

## Tham khảo

- [Google Drive API Documentation](https://developers.google.com/drive/api)
- [OAuth 2.0 Setup Guide](https://developers.google.com/identity/protocols/oauth2)
- [Google Cloud Console](https://console.cloud.google.com/)

