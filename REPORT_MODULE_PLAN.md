# REPORT MODULE IMPLEMENTATION PLAN

## Tổng quan
Plan này mô tả chi tiết việc hoàn thiện module Report bao gồm: Repository, DTOs, Service, Controller, Mapper, và Exception handling theo conventions của Blog module.

## Cấu trúc hiện tại
- ✅ Entity: `Report`, `ReportReason` (đã có)
- ✅ Enum: `ReportStatus`, `ReportType` (đã có)
- ✅ Constants: `ReportConstants`, `InteractionMessageConstants`, `InteractionErrorCodeConstants` (đã có)
- ❌ Repository: `ReportRepository` (chưa có)
- ❌ DTOs: Request/Response DTOs (chưa có)
- ❌ Service: `ReportService` interface và implementation (chưa có)
- ❌ Controller: `ReportController` (chưa có)
- ❌ Mapper: `ReportMapper` (chưa có)

---

## PHASE 1: Repository Layer

### 1.1. ReportRepository
**File**: `src/main/java/com/se/hub/modules/interaction/repository/ReportRepository.java`

**Methods cần có**:
```java
@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    // Tìm reports theo target
    Page<Report> findByTargetTypeAndTargetId(TargetType targetType, String targetId, Pageable pageable);
    
    // Tìm reports theo status
    Page<Report> findByStatus(ReportStatus status, Pageable pageable);
    
    // Tìm reports theo reporter
    Page<Report> findByReporterId(String reporterId, Pageable pageable);
    
    // Tìm reports theo target và status
    Page<Report> findByTargetTypeAndTargetIdAndStatus(
        TargetType targetType, String targetId, ReportStatus status, Pageable pageable);
    
    // Đếm reports theo target
    long countByTargetTypeAndTargetId(TargetType targetType, String targetId);
    
    // Đếm reports theo status
    long countByStatus(ReportStatus status);
    
    // Kiểm tra xem user đã report target này chưa
    boolean existsByReporterIdAndTargetTypeAndTargetId(
        String reporterId, TargetType targetType, String targetId);
    
    // Tìm report của user cho một target cụ thể
    Optional<Report> findByReporterIdAndTargetTypeAndTargetId(
        String reporterId, TargetType targetType, String targetId);
}
```

**Lưu ý**:
- Sử dụng `Profile` entity thay vì `User`
- Sử dụng `TargetType` enum
- Sử dụng `ReportStatus` enum
- Thêm `@Query` nếu cần custom queries

---

## PHASE 2: DTOs Layer

### 2.1. CreateReportRequest
**File**: `src/main/java/com/se/hub/modules/interaction/dto/request/CreateReportRequest.java`

**Fields**:
```java
@NotBlank
@Pattern(regexp = "BLOG|QUESTION|COURSE|LESSON|COMMENT|EXAM|PRACTICAL_EXAM|DOCUMENT")
String targetType;

@NotBlank
@Size(max = ReportConstants.TARGET_ID_MAX_LENGTH)
String targetId;

@NotNull
@NotEmpty
@Valid
List<ReportReasonRequest> reasons; // Danh sách lý do report
```

**Nested DTO - ReportReasonRequest**:
```java
@NotNull
ReportType reportType;

@Size(max = ReportConstants.DESCRIPTION_MAX_LENGTH)
String description; // Optional
```

**Validation**:
- `targetType`: Phải là một trong các giá trị TargetType enum
- `targetId`: Không được blank, max length 36
- `reasons`: Phải có ít nhất 1 reason, mỗi reason phải có reportType

### 2.2. UpdateReportRequest
**File**: `src/main/java/com/se/hub/modules/interaction/dto/request/UpdateReportRequest.java`

**Fields**:
```java
@NotNull
ReportStatus status; // Chỉ admin/staff mới được update status
```

**Lưu ý**: Chỉ dùng cho admin/staff để update status của report

### 2.3. ReportResponse
**File**: `src/main/java/com/se/hub/modules/interaction/dto/response/ReportResponse.java`

**Fields**:
```java
String id;
String reporterId;
String reporterName;
String reporterAvatar;
String targetType;
String targetId;
ReportStatus status;
List<ReportReasonResponse> reasons;
String createDate;
String updatedDate;
```

**Nested DTO - ReportReasonResponse**:
```java
String id;
ReportType reportType;
String description;
```

### 2.4. ReportSummaryResponse (Optional - cho dashboard)
**File**: `src/main/java/com/se/hub/modules/interaction/dto/response/ReportSummaryResponse.java`

**Fields**:
```java
long totalReports;
long pendingReports;
long approvedReports;
long rejectedReports;
long resolvedReports;
Map<ReportType, Long> reportsByType; // Thống kê theo loại report
```

---

## PHASE 3: Mapper Layer

### 3.1. ReportMapper
**File**: `src/main/java/com/se/hub/modules/interaction/mapper/ReportMapper.java`

**Methods**:
```java
@Mapper(componentModel = "spring")
public interface ReportMapper {
    // Map CreateReportRequest -> Report entity
    @Mapping(target = "reporter", ignore = true)
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "reasons", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createDate", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedDate", ignore = true)
    @Mapping(target = "updateBy", ignore = true)
    Report toReport(CreateReportRequest request);
    
    // Map Report entity -> ReportResponse
    @Mapping(target = "reporterId", source = "reporter.id")
    @Mapping(target = "reporterName", source = "reporter.fullName")
    @Mapping(target = "reporterAvatar", source = "reporter.avtUrl")
    @Mapping(target = "targetType", expression = "java(report.getTargetType().name())")
    @Mapping(target = "status", expression = "java(report.getStatus().name())")
    @Mapping(target = "reasons", source = "reasons")
    ReportResponse toReportResponse(Report report);
    
    // Map ReportReason entity -> ReportReasonResponse
    @Mapping(target = "reportType", expression = "java(reason.getReportType().name())")
    ReportReasonResponse toReportReasonResponse(ReportReason reason);
    
    // Map List<ReportReason> -> List<ReportReasonResponse>
    List<ReportReasonResponse> toReportReasonResponseList(List<ReportReason> reasons);
    
    // Map List<Report> -> List<ReportResponse>
    List<ReportResponse> toReportResponseList(List<Report> reports);
}
```

**Lưu ý**:
- Sử dụng MapStruct với `componentModel = "spring"`
- Ignore các fields không cần map từ request
- Map enum sang String cho response

---

## PHASE 4: Service Layer

### 4.1. ReportService Interface
**File**: `src/main/java/com/se/hub/modules/interaction/service/api/ReportService.java`

**Methods**:
```java
public interface ReportService {
    /**
     * Tạo report mới
     * Virtual Thread: Sử dụng @Transactional với blocking I/O operations
     */
    ReportResponse createReport(CreateReportRequest request);
    
    /**
     * Lấy report theo ID
     * Virtual Thread: Sử dụng blocking I/O operation
     */
    ReportResponse getById(String reportId);
    
    /**
     * Lấy tất cả reports với pagination
     * Virtual Thread: Sử dụng blocking I/O operations
     */
    PagingResponse<ReportResponse> getReports(PagingRequest request);
    
    /**
     * Lấy reports theo target
     * Virtual Thread: Sử dụng blocking I/O operations
     */
    PagingResponse<ReportResponse> getReportsByTarget(
        String targetType, String targetId, PagingRequest request);
    
    /**
     * Lấy reports theo status
     * Virtual Thread: Sử dụng blocking I/O operations
     */
    PagingResponse<ReportResponse> getReportsByStatus(
        ReportStatus status, PagingRequest request);
    
    /**
     * Lấy reports của current user
     * Virtual Thread: Sử dụng blocking I/O operations
     */
    PagingResponse<ReportResponse> getMyReports(PagingRequest request);
    
    /**
     * Update report status (chỉ admin/staff)
     * Virtual Thread: Sử dụng @Transactional với blocking I/O operations
     */
    ReportResponse updateReportStatus(String reportId, UpdateReportRequest request);
    
    /**
     * Xóa report (chỉ admin hoặc chính reporter)
     * Virtual Thread: Sử dụng @Transactional với blocking I/O operations
     */
    void deleteReport(String reportId);
    
    /**
     * Kiểm tra xem current user đã report target này chưa
     * Virtual Thread: Sử dụng blocking I/O operation
     */
    boolean hasUserReported(String targetType, String targetId);
    
    /**
     * Lấy report summary (cho dashboard - chỉ admin/staff)
     * Virtual Thread: Sử dụng blocking I/O operations
     */
    ReportSummaryResponse getReportSummary();
}
```

### 4.2. ReportServiceImpl
**File**: `src/main/java/com/se/hub/modules/interaction/service/impl/ReportServiceImpl.java`

**Dependencies**:
- `ReportRepository`
- `ReportMapper`
- `ProfileRepository` (để lấy Profile từ userId)
- `AuthUtils` (để lấy current user)

**Business Logic**:

1. **createReport**:
   - Validate targetType và targetId
   - Kiểm tra xem user đã report target này chưa (nếu có thì throw exception hoặc update)
   - Tạo Report entity với status = PENDING
   - Tạo các ReportReason entities từ request
   - Set reporter từ current user
   - Save và return response

2. **getById**:
   - Tìm report theo ID
   - Throw `InteractionException` nếu không tìm thấy
   - Return mapped response

3. **getReports**:
   - Lấy tất cả reports với pagination
   - Map và return

4. **getReportsByTarget**:
   - Validate targetType
   - Query reports theo targetType và targetId
   - Map và return

5. **getReportsByStatus**:
   - Query reports theo status
   - Map và return

6. **getMyReports**:
   - Lấy current user ID từ AuthUtils
   - Query reports theo reporterId
   - Map và return

7. **updateReportStatus**:
   - Kiểm tra quyền (chỉ admin/staff)
   - Tìm report theo ID
   - Update status
   - Save và return

8. **deleteReport**:
   - Kiểm tra quyền (admin hoặc chính reporter)
   - Tìm report theo ID
   - Delete

9. **hasUserReported**:
   - Lấy current user ID
   - Check exists trong database

10. **getReportSummary**:
    - Chỉ admin/staff mới được truy cập
    - Aggregate counts theo status và reportType
    - Return summary

**Lưu ý**:
- Sử dụng `@Transactional` cho write operations
- Thêm virtual thread comments
- Sử dụng `InteractionException` cho errors
- Thêm debug logging
- Validate permissions với `AuthUtils`

---

## PHASE 5: Controller Layer

### 5.1. ReportController
**File**: `src/main/java/com/se/hub/modules/interaction/controller/ReportController.java`

**Endpoints**:

1. **POST /reports**
   - Tạo report mới
   - Authentication: Required (USER, ADMIN, STAFF)
   - Request: `CreateReportRequest`
   - Response: `ReportResponse`

2. **GET /reports**
   - Lấy tất cả reports với pagination
   - Authentication: Required (ADMIN, STAFF only)
   - Query params: page, size, field, direction
   - Response: `PagingResponse<ReportResponse>`

3. **GET /reports/{reportId}**
   - Lấy report theo ID
   - Authentication: Required
   - Response: `ReportResponse`

4. **GET /reports/target/{targetType}/{targetId}**
   - Lấy reports theo target
   - Authentication: Required (ADMIN, STAFF only)
   - Query params: page, size, field, direction
   - Response: `PagingResponse<ReportResponse>`

5. **GET /reports/status/{status}**
   - Lấy reports theo status
   - Authentication: Required (ADMIN, STAFF only)
   - Query params: page, size, field, direction
   - Response: `PagingResponse<ReportResponse>`

6. **GET /reports/my-reports**
   - Lấy reports của current user
   - Authentication: Required
   - Query params: page, size, field, direction
   - Response: `PagingResponse<ReportResponse>`

7. **PUT /reports/{reportId}/status**
   - Update report status
   - Authentication: Required (ADMIN, STAFF only)
   - Request: `UpdateReportRequest`
   - Response: `ReportResponse`

8. **DELETE /reports/{reportId}**
   - Xóa report
   - Authentication: Required (ADMIN hoặc chính reporter)
   - Response: Success message

9. **GET /reports/check/{targetType}/{targetId}**
   - Kiểm tra xem current user đã report target này chưa
   - Authentication: Required
   - Response: `{ "hasReported": boolean }`

10. **GET /reports/summary**
    - Lấy report summary (dashboard)
    - Authentication: Required (ADMIN, STAFF only)
    - Response: `ReportSummaryResponse`

**Lưu ý**:
- Extends `BaseController`
- Sử dụng `success()` method
- Thêm `@Slf4j`, `@Validated`
- Sử dụng `ResponseCode` constants
- Sử dụng `InteractionMessageConstants` cho messages
- Sử dụng `PaginationConstants` cho pagination
- Thêm `@PreAuthorize` cho authorization (nếu cần)

---

## PHASE 6: Exception Handling

### 6.1. Cập nhật InteractionErrorCode
**File**: `src/main/java/com/se/hub/modules/interaction/exception/InteractionErrorCode.java`

**Thêm các error codes**:
```java
REPORT_ALREADY_EXISTS(MessageCodeConstant.E001_VALIDATION_ERROR, 
    InteractionMessageConstants.REPORT_ALREADY_EXISTS_MESSAGE, HttpStatus.BAD_REQUEST),
REPORT_TARGET_NOT_FOUND(MessageCodeConstant.E002_NOT_FOUND, 
    InteractionMessageConstants.REPORT_TARGET_NOT_FOUND_MESSAGE, HttpStatus.NOT_FOUND),
REPORT_REASON_REQUIRED(MessageCodeConstant.E001_VALIDATION_ERROR, 
    InteractionMessageConstants.REPORT_REASON_REQUIRED_MESSAGE, HttpStatus.BAD_REQUEST),
REPORT_STATUS_UPDATE_FORBIDDEN(MessageCodeConstant.E004_FORBIDDEN, 
    InteractionMessageConstants.REPORT_STATUS_UPDATE_FORBIDDEN_MESSAGE, HttpStatus.FORBIDDEN),
REPORT_DELETE_FORBIDDEN(MessageCodeConstant.E004_FORBIDDEN, 
    InteractionMessageConstants.REPORT_DELETE_FORBIDDEN_MESSAGE, HttpStatus.FORBIDDEN);
```

### 6.2. Cập nhật InteractionMessageConstants
**File**: `src/main/java/com/se/hub/modules/interaction/constant/InteractionMessageConstants.java`

**Thêm các messages**:
```java
// Error Messages
public static final String REPORT_ALREADY_EXISTS_MESSAGE = "You have already reported this target";
public static final String REPORT_TARGET_NOT_FOUND_MESSAGE = "Report target not found";
public static final String REPORT_REASON_REQUIRED_MESSAGE = "At least one report reason is required";
public static final String REPORT_STATUS_UPDATE_FORBIDDEN_MESSAGE = "You are not allowed to update report status";
public static final String REPORT_DELETE_FORBIDDEN_MESSAGE = "You are not allowed to delete this report";

// API Response Messages
public static final String API_REPORT_CREATED_SUCCESS = "Report created successfully";
public static final String API_REPORT_RETRIEVED_ALL_SUCCESS = "Retrieved all reports successfully";
public static final String API_REPORT_RETRIEVED_BY_ID_SUCCESS = "Retrieved report by ID successfully";
public static final String API_REPORT_RETRIEVED_BY_TARGET_SUCCESS = "Retrieved reports by target successfully";
public static final String API_REPORT_RETRIEVED_BY_STATUS_SUCCESS = "Retrieved reports by status successfully";
public static final String API_REPORT_RETRIEVED_MY_REPORTS_SUCCESS = "Retrieved my reports successfully";
public static final String API_REPORT_UPDATED_SUCCESS = "Report updated successfully";
public static final String API_REPORT_DELETED_SUCCESS = "Report deleted successfully";
public static final String API_REPORT_CHECK_SUCCESS = "Report check completed successfully";
public static final String API_REPORT_SUMMARY_RETRIEVED_SUCCESS = "Report summary retrieved successfully";
```

### 6.3. Cập nhật InteractionErrorCodeConstants
**File**: `src/main/java/com/se/hub/modules/interaction/constant/InteractionErrorCodeConstants.java`

**Thêm các error code constants**:
```java
public static final String REPORT_ALREADY_EXISTS = "REPORT_ALREADY_EXISTS";
public static final String REPORT_TARGET_NOT_FOUND = "REPORT_TARGET_NOT_FOUND";
public static final String REPORT_REASON_REQUIRED = "REPORT_REASON_REQUIRED";
public static final String REPORT_STATUS_UPDATE_FORBIDDEN = "REPORT_STATUS_UPDATE_FORBIDDEN";
public static final String REPORT_DELETE_FORBIDDEN = "REPORT_DELETE_FORBIDDEN";
```

---

## PHASE 7: Validation & Cleanup

### 7.1. Kiểm tra và cập nhật
- ✅ Kiểm tra tất cả imports
- ✅ Kiểm tra naming conventions
- ✅ Kiểm tra code style (theo Blog module)
- ✅ Thêm virtual thread comments
- ✅ Thêm `@Transactional` cho write operations
- ✅ Thêm logging (debug, error)
- ✅ Kiểm tra validation annotations
- ✅ Kiểm tra constants usage (không hardcode)
- ✅ Kiểm tra exception handling
- ✅ Kiểm tra equals/hashCode cho entities (đã có)

### 7.2. Testing Checklist
- [ ] Test create report
- [ ] Test get report by ID
- [ ] Test get all reports (pagination)
- [ ] Test get reports by target
- [ ] Test get reports by status
- [ ] Test get my reports
- [ ] Test update report status (admin/staff)
- [ ] Test delete report (admin và reporter)
- [ ] Test check if user reported
- [ ] Test get report summary
- [ ] Test validation errors
- [ ] Test permission errors
- [ ] Test duplicate report prevention

---

## Tổng kết Files cần tạo/cập nhật

### Files mới cần tạo (11 files):
1. `ReportRepository.java`
2. `CreateReportRequest.java`
3. `UpdateReportRequest.java`
4. `ReportResponse.java`
5. `ReportReasonRequest.java` (nested trong CreateReportRequest hoặc riêng)
6. `ReportReasonResponse.java` (nested trong ReportResponse hoặc riêng)
7. `ReportSummaryResponse.java` (optional)
8. `ReportMapper.java`
9. `ReportService.java`
10. `ReportServiceImpl.java`
11. `ReportController.java`

### Files cần cập nhật (3 files):
1. `InteractionErrorCode.java` - Thêm error codes
2. `InteractionMessageConstants.java` - Thêm messages
3. `InteractionErrorCodeConstants.java` - Thêm error code constants

---

## Code Style & Conventions

### Tuân theo Blog module:
- ✅ Extends `BaseController`
- ✅ Sử dụng `success()` method
- ✅ Sử dụng `AuthUtils` cho current user
- ✅ Sử dụng constants (không hardcode)
- ✅ Sử dụng `InteractionException` cho errors
- ✅ Virtual thread comments
- ✅ `@Transactional` cho write operations
- ✅ `@Slf4j` cho logging
- ✅ `@FieldDefaults` với Lombok
- ✅ Validation annotations
- ✅ MapStruct cho mapping

### Security:
- ✅ Kiểm tra quyền với `@PreAuthorize` hoặc `AuthUtils`
- ✅ Chỉ admin/staff mới được xem tất cả reports
- ✅ Chỉ admin/staff mới được update status
- ✅ Admin hoặc chính reporter mới được delete report

---

## Notes

1. **Duplicate Report Prevention**: 
   - Có thể cho phép user report nhiều lần với lý do khác nhau
   - Hoặc chỉ cho phép 1 report per user per target (cần quyết định business logic)

2. **Report Status Flow**:
   - PENDING -> APPROVED/REJECTED -> RESOLVED
   - Cần validate status transitions

3. **Report Reasons**:
   - Một report có thể có nhiều reasons
   - Mỗi reason có type và optional description

4. **Performance**:
   - Sử dụng virtual threads cho high concurrency
   - Index database cho các queries thường dùng (targetType, targetId, status, reporterId)

