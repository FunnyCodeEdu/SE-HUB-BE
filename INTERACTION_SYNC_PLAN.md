# PLAN ĐỒNG BỘ CODE INTERACTION MODULE

## MỤC TIÊU
Đồng bộ code từ `interactionmanagement` (src B) sang `interaction` (src A), follow theo code convention và style của `blog` (src C).

## PHÂN TÍCH HIỆN TRẠNG

### Source A (interaction - cần update)
- ✅ Đã có: Comment entity, CommentController, CommentService, CommentRepository, CommentMapper
- ❌ Chưa có: Reaction, Report, ReportReason entities và các service/controller tương ứng
- ❌ Enum: Thiếu ReactionType, ReportStatus, ReportType
- ❌ Constant: Thiếu các constant cho Reaction, Report

### Source B (interactionmanagement - nguồn để sync)
- ✅ Có đầy đủ: Comment, Reaction, Report, ReportReason entities
- ✅ Có: CommentController, ReactionController
- ✅ Có: CommentService, ReactionService
- ✅ Có: Các enum: ReactionType, ReportStatus, ReportType, TargetType, BaseTargetType
- ✅ Có: Constants: CommentConstant, ReportConstant, InteractionMessageConstant, InteractionMessageCodeConstant

### Source C (blog - template để follow)
- ✅ Code style: Sử dụng BaseController, BaseEntity
- ✅ Exception: Custom exception (BlogException extends AppException)
- ✅ Constant: Tách riêng BlogConstants, BlogMessageConstants, BlogErrorCodeConstants
- ✅ Controller: Extends BaseController, sử dụng success() method
- ✅ Service: Sử dụng @Transactional, virtual thread comments
- ✅ Entity: Sử dụng @Table, @Column với columnDefinition, validation annotations

## CÁC TASK CẦN THỰC HIỆN

### PHASE 1: ENTITY & ENUM

#### Task 1.1: Cập nhật Comment Entity
**File:** `src/main/java/com/se/hub/modules/interaction/entity/Comment.java`
- ✅ Giữ nguyên cấu trúc hiện tại (đã đúng với src B)
- ✅ Đảm bảo sử dụng Profile thay vì User (theo src A)
- ✅ Giữ validation annotations theo style src C

#### Task 1.2: Tạo Reaction Entity
**File mới:** `src/main/java/com/se/hub/modules/interaction/entity/Reaction.java`
- Copy từ src B, adapt:
  - Đổi User → Profile
  - Thêm @Table, @Column với columnDefinition
  - Thêm validation annotations
  - Sử dụng TargetType enum thay vì String

#### Task 1.3: Tạo Report Entity
**File mới:** `src/main/java/com/se/hub/modules/interaction/entity/Report.java`
- Copy từ src B, adapt:
  - Đổi User → Profile
  - Thêm @Table, @Column với columnDefinition
  - Thêm validation annotations
  - Sử dụng TargetType enum

#### Task 1.4: Tạo ReportReason Entity
**File mới:** `src/main/java/com/se/hub/modules/interaction/entity/ReportReason.java`
- Copy từ src B, adapt:
  - Thêm @Table, @Column với columnDefinition
  - Thêm validation annotations

#### Task 1.5: Tạo/Cập nhật Enum
**Files:**
- `src/main/java/com/se/hub/modules/interaction/enums/ReactionType.java` (mới)
- `src/main/java/com/se/hub/modules/interaction/enums/ReportStatus.java` (mới)
- `src/main/java/com/se/hub/modules/interaction/enums/ReportType.java` (mới)
- `src/main/java/com/se/hub/modules/interaction/enums/TargetType.java` (cập nhật - thêm các giá trị từ src B nếu thiếu)

### PHASE 2: CONSTANT

#### Task 2.1: Cập nhật CommentConstants
**File:** `src/main/java/com/se/hub/modules/interaction/constant/CommentConstants.java`
- Thêm các constant còn thiếu từ src B
- Đảm bảo format theo style src C (TABLE_, COL_, etc.)

#### Task 2.2: Tạo ReactionConstants
**File mới:** `src/main/java/com/se/hub/modules/interaction/constant/ReactionConstants.java`
- Tạo theo pattern BlogConstants
- Định nghĩa TABLE_REACTION, các COL_*, các giá trị validation

#### Task 2.3: Tạo ReportConstants
**File mới:** `src/main/java/com/se/hub/modules/interaction/constant/ReportConstants.java`
- Tạo theo pattern BlogConstants
- Định nghĩa TABLE_REPORT, các COL_*

#### Task 2.4: Tạo InteractionMessageConstants
**File mới:** `src/main/java/com/se/hub/modules/interaction/constant/InteractionMessageConstants.java`
- Copy từ src B, adapt theo style BlogMessageConstants
- Tách message constants cho Comment, Reaction, Report

#### Task 2.5: Tạo InteractionErrorCodeConstants
**File mới:** `src/main/java/com/se/hub/modules/interaction/constant/InteractionErrorCodeConstants.java`
- Tạo theo pattern BlogErrorCodeConstants
- Định nghĩa các error code constants

### PHASE 3: REPOSITORY

#### Task 3.1: Tạo ReactionRepository
**File mới:** `src/main/java/com/se/hub/modules/interaction/repository/ReactionRepository.java`
- Copy từ src B, adapt:
  - Đổi User → Profile
  - Đổi package name

#### Task 3.2: Tạo ReportRepository
**File mới:** `src/main/java/com/se/hub/modules/interaction/repository/ReportRepository.java`
- Tạo mới với các method cần thiết
- Follow pattern từ CommentRepository

### PHASE 4: DTO

#### Task 4.1: Tạo ReactionToggleResult
**File mới:** `src/main/java/com/se/hub/modules/interaction/dto/response/ReactionToggleResult.java`
- Copy từ src B, giữ nguyên structure

#### Task 4.2: Kiểm tra và cập nhật các DTO hiện có
**Files:**
- `CreateCommentRequest.java` - đảm bảo đúng với src B
- `UpdateCommentRequest.java` - đảm bảo đúng với src B
- `CommentResponse.java` - đảm bảo đúng với src B

### PHASE 5: SERVICE

#### Task 5.1: Cập nhật CommentService Interface
**File:** `src/main/java/com/se/hub/modules/interaction/service/api/CommentService.java`
- Đảm bảo có đầy đủ methods từ src B
- Follow naming convention từ BlogService

#### Task 5.2: Cập nhật CommentServiceImpl
**File:** `src/main/java/com/se/hub/modules/interaction/service/impl/CommentServiceImpl.java`
- Sync logic từ src B
- Adapt: User → Profile, UserUtilService → AuthUtils
- Thêm virtual thread comments
- Sử dụng AppException thay vì custom exception (nếu chưa có InteractionException)
- Follow pattern BlogServiceImpl

#### Task 5.3: Tạo ReactionService Interface
**File mới:** `src/main/java/com/se/hub/modules/interaction/service/api/ReactionService.java`
- Copy từ src B, adapt package name
- Follow naming convention từ BlogService

#### Task 5.4: Tạo ReactionServiceImpl
**File mới:** `src/main/java/com/se/hub/modules/interaction/service/impl/ReactionServiceImpl.java`
- Copy từ src B, adapt:
  - User → Profile
  - UserUtilService → AuthUtils
  - UserStatsService → ProfileProgressService (nếu cần)
  - Thêm virtual thread comments
  - Sử dụng AppException/InteractionException
  - Follow pattern BlogServiceImpl

### PHASE 6: CONTROLLER

#### Task 6.1: Cập nhật CommentController
**File:** `src/main/java/com/se/hub/modules/interaction/controller/CommentController.java`
- Extends BaseController (nếu chưa)
- Sử dụng success() method từ BaseController
- Cập nhật API responses theo style BlogController
- Thêm ResponseCode constants
- Thêm InteractionMessageConstants cho messages
- Đảm bảo có đầy đủ endpoints từ src B

#### Task 6.2: Tạo ReactionController
**File mới:** `src/main/java/com/se/hub/modules/interaction/controller/ReactionController.java`
- Copy từ src B, adapt:
  - Extends BaseController
  - Sử dụng success() method
  - Đổi package name
  - Follow style BlogController
  - Sử dụng InteractionMessageConstants

### PHASE 7: EXCEPTION HANDLING

#### Task 7.1: Tạo InteractionException
**File mới:** `src/main/java/com/se/hub/modules/interaction/exception/InteractionException.java`
- Copy pattern từ BlogException
- Extends AppException

#### Task 7.2: Tạo InteractionErrorCode
**File mới:** `src/main/java/com/se/hub/modules/interaction/exception/InteractionErrorCode.java`
- Copy pattern từ BlogErrorCode
- Định nghĩa các error codes: COMMENT_NOT_FOUND, REACTION_ERROR, etc.

### PHASE 8: MAPPER

#### Task 8.1: Cập nhật CommentMapper
**File:** `src/main/java/com/se/hub/modules/interaction/mapper/CommentMapper.java`
- Đảm bảo mapping đúng với entity mới
- Follow pattern BlogMapper

#### Task 8.2: Tạo ReactionMapper (nếu cần)
**File mới:** `src/main/java/com/se/hub/modules/interaction/mapper/ReactionMapper.java`
- Tạo nếu cần mapping DTO cho Reaction

### PHASE 9: VALIDATION & CLEANUP

#### Task 9.1: Kiểm tra imports
- Đảm bảo tất cả imports đúng package
- User → Profile
- UserUtilService → AuthUtils

#### Task 9.2: Kiểm tra naming convention
- Constants: UPPER_SNAKE_CASE
- Classes: PascalCase
- Methods: camelCase
- Packages: lowercase

#### Task 9.3: Kiểm tra code style
- Sử dụng @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true) cho service/controller
- Sử dụng @RequiredArgsConstructor
- Thêm @Slf4j cho service/controller
- Thêm virtual thread comments cho service methods

## CODE STYLE CHECKLIST

### Entity
- [ ] Extends BaseEntity
- [ ] Sử dụng @Table(name = Constants.TABLE_*)
- [ ] Sử dụng @Column với columnDefinition
- [ ] Validation annotations (@NotNull, @NotBlank, @Size)
- [ ] Sử dụng Profile thay vì User
- [ ] @FieldDefaults(level = AccessLevel.PRIVATE)

### Controller
- [ ] Extends BaseController
- [ ] Sử dụng success() method
- [ ] @RequiredArgsConstructor
- [ ] @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
- [ ] @Slf4j
- [ ] @Tag cho Swagger
- [ ] @Operation, @ApiResponses cho mỗi endpoint
- [ ] Sử dụng ResponseCode constants
- [ ] Sử dụng MessageConstants cho messages

### Service
- [ ] Interface trong package `service/api/`
- [ ] Implementation trong package `service/impl/`
- [ ] @Service annotation
- [ ] @RequiredArgsConstructor
- [ ] @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
- [ ] @Slf4j
- [ ] @Transactional cho write operations
- [ ] Virtual thread comments
- [ ] Sử dụng AppException/InteractionException
- [ ] Logging với log.debug/log.error

### Repository
- [ ] Extends JpaRepository
- [ ] @Repository annotation
- [ ] Method naming: findBy*, countBy*, existsBy*

### Constant
- [ ] Tách riêng: Constants, MessageConstants, ErrorCodeConstants
- [ ] private constructor để prevent instantiation
- [ ] Naming: TABLE_*, COL_*, *_MAX_LENGTH, etc.

### Exception
- [ ] Extends AppException
- [ ] Có InteractionErrorCode enum
- [ ] toErrorCode() method

## LƯU Ý QUAN TRỌNG

1. **User → Profile**: Tất cả references đến User entity phải đổi thành Profile
2. **UserUtilService → AuthUtils**: Sử dụng AuthUtils.getCurrentUserId() thay vì UserUtilService
3. **Package naming**: `com.se.hub.modules.interaction.*` (không phải `interactionmanagement`)
4. **BaseController**: Luôn extends BaseController và dùng success() method
5. **Constants**: Không hardcode, luôn dùng constants
6. **Exception**: Sử dụng AppException hoặc InteractionException, không throw RuntimeException
7. **Virtual Threads**: Thêm comments về virtual thread best practices
8. **Validation**: Sử dụng Jakarta validation annotations
9. **Logging**: Sử dụng SLF4J với format: `ClassName_methodName_Description`

## THỨ TỰ THỰC HIỆN

1. Phase 1: Entity & Enum (nền tảng)
2. Phase 2: Constant (cần cho entity)
3. Phase 3: Repository (cần cho service)
4. Phase 4: DTO (cần cho controller)
5. Phase 5: Service (cần cho controller)
6. Phase 6: Controller (expose API)
7. Phase 7: Exception (xử lý lỗi)
8. Phase 8: Mapper (nếu cần)
9. Phase 9: Validation & Cleanup (hoàn thiện)

## FILES CẦN TẠO MỚI

1. `entity/Reaction.java`
2. `entity/Report.java`
3. `entity/ReportReason.java`
4. `enums/ReactionType.java`
5. `enums/ReportStatus.java`
6. `enums/ReportType.java`
7. `constant/ReactionConstants.java`
8. `constant/ReportConstants.java`
9. `constant/InteractionMessageConstants.java`
10. `constant/InteractionErrorCodeConstants.java`
11. `repository/ReactionRepository.java`
12. `repository/ReportRepository.java`
13. `dto/response/ReactionToggleResult.java`
14. `service/api/ReactionService.java`
15. `service/impl/ReactionServiceImpl.java`
16. `controller/ReactionController.java`
17. `exception/InteractionException.java`
18. `exception/InteractionErrorCode.java`

## FILES CẦN CẬP NHẬT

1. `entity/Comment.java` (kiểm tra và đảm bảo đúng)
2. `enums/TargetType.java` (thêm giá trị nếu thiếu)
3. `constant/CommentConstants.java` (thêm constants nếu thiếu)
4. `service/api/CommentService.java` (đảm bảo đầy đủ methods)
5. `service/impl/CommentServiceImpl.java` (sync logic từ src B)
6. `controller/CommentController.java` (cập nhật style theo src C)
7. `mapper/CommentMapper.java` (kiểm tra mapping)

---

**Lưu ý:** Plan này cần được review và approve trước khi bắt đầu implement.

